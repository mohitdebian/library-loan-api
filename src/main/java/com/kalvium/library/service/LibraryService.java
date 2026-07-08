package com.kalvium.library.service;

import com.kalvium.library.entity.Book;
import com.kalvium.library.entity.Loan;
import com.kalvium.library.entity.Reservation;
import com.kalvium.library.exception.LibraryException;
import com.kalvium.library.repository.BookRepository;
import com.kalvium.library.repository.LoanRepository;
import com.kalvium.library.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int MAX_ACTIVE_RESERVATIONS = 3;
    private static final int LOAN_DURATION_DAYS = 14;

    public LibraryService(BookRepository bookRepository, LoanRepository loanRepository, ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Loan borrowBook(Long bookId, String borrowerId) {
        // Check active loans limit
        int activeLoans = loanRepository.countByBorrowerIdAndStatus(borrowerId, Loan.LoanStatus.ACTIVE);
        if (activeLoans >= MAX_ACTIVE_LOANS) {
            throw new LibraryException("Borrower has reached the maximum number of active loans.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new LibraryException("Book not found"));

        if (book.getCopiesAvailable() <= 0) {
            // Check if there's a reservation for this user that is NOTIFIED
            List<Reservation> userReservations = reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, Reservation.ReservationStatus.NOTIFIED);
            boolean isReservedForUser = userReservations.stream().anyMatch(r -> r.getBorrowerId().equals(borrowerId));
            
            if (!isReservedForUser) {
                throw new LibraryException("Book is not available for borrowing.");
            } else {
                // Fulfill reservation
                Reservation reservation = userReservations.stream()
                        .filter(r -> r.getBorrowerId().equals(borrowerId))
                        .findFirst().get();
                reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                reservationRepository.save(reservation);
            }
        } else {
            // Decrement copy
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            bookRepository.save(book);
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setBorrowerId(borrowerId);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_DURATION_DAYS));
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LibraryException("Loan not found"));

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new LibraryException("Loan is already returned.");
        }

        loan.setStatus(Loan.LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Calculate fine
        long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
        if (daysLate > 0) {
            BigDecimal fine = loan.getBook().getFineRatePerDay().multiply(BigDecimal.valueOf(daysLate));
            loan.setFineAmount(fine);
        } else {
            loan.setFineAmount(BigDecimal.ZERO);
        }

        Book book = loan.getBook();

        // Check for reservations
        List<Reservation> pendingReservations = reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, Reservation.ReservationStatus.PENDING);
        
        if (!pendingReservations.isEmpty()) {
            Reservation nextReservation = pendingReservations.get(0);
            nextReservation.setStatus(Reservation.ReservationStatus.NOTIFIED);
            reservationRepository.save(nextReservation);
            
            // Console output for notification
            System.out.println("NOTIFICATION: Book '" + book.getTitle() + "' is now available for borrower " + nextReservation.getBorrowerId());
            // Note: we do NOT increment copiesAvailable, keeping it locked for the notified user.
        } else {
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            bookRepository.save(book);
        }

        return loanRepository.save(loan);
    }

    @Transactional
    public Reservation reserveBook(Long bookId, String borrowerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new LibraryException("Book not found"));

        if (book.getCopiesAvailable() > 0) {
            throw new LibraryException("Book is available, no need to reserve.");
        }

        // Check if user already has this book reserved
        boolean alreadyReserved = reservationRepository.existsByBookAndBorrowerIdAndStatusIn(
                book, borrowerId, Arrays.asList(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.NOTIFIED));
        if (alreadyReserved) {
            throw new LibraryException("You have already reserved this book.");
        }

        // Check active reservations limit
        int activeReservations = reservationRepository.countByBorrowerIdAndStatusIn(
                borrowerId, Arrays.asList(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.NOTIFIED));
        if (activeReservations >= MAX_ACTIVE_RESERVATIONS) {
            throw new LibraryException("Borrower has reached the maximum number of active reservations.");
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setBorrowerId(borrowerId);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        return reservationRepository.save(reservation);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findByStatusAndDueDateBefore(Loan.LoanStatus.ACTIVE, LocalDate.now());
    }
}
