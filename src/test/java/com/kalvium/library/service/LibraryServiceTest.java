package com.kalvium.library.service;

import com.kalvium.library.entity.Book;
import com.kalvium.library.entity.Loan;
import com.kalvium.library.entity.Reservation;
import com.kalvium.library.exception.LibraryException;
import com.kalvium.library.repository.BookRepository;
import com.kalvium.library.repository.LoanRepository;
import com.kalvium.library.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void borrowBook_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setCopiesAvailable(1);
        
        when(loanRepository.countByBorrowerIdAndStatus("user1", Loan.LoanStatus.ACTIVE)).thenReturn(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        Loan loan = libraryService.borrowBook(1L, "user1");

        assertNotNull(loan);
        assertEquals(Loan.LoanStatus.ACTIVE, loan.getStatus());
        assertEquals("user1", loan.getBorrowerId());
        assertEquals(0, book.getCopiesAvailable());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void borrowBook_MaxActiveLoansReached() {
        when(loanRepository.countByBorrowerIdAndStatus("user1", Loan.LoanStatus.ACTIVE)).thenReturn(3);

        assertThrows(LibraryException.class, () -> libraryService.borrowBook(1L, "user1"));
    }

    @Test
    void returnBook_SuccessOnTime() {
        Book book = new Book();
        book.setId(1L);
        book.setCopiesAvailable(0);
        book.setFineRatePerDay(new BigDecimal("10.0"));

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setDueDate(LocalDate.now().plusDays(1)); // Not late
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, Reservation.ReservationStatus.PENDING))
                .thenReturn(Collections.emptyList());
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan returnedLoan = libraryService.returnBook(1L);

        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(BigDecimal.ZERO, returnedLoan.getFineAmount());
        assertEquals(1, book.getCopiesAvailable());
        verify(bookRepository, times(1)).save(book);
    }
    
    @Test
    void returnBook_LateFineCalculated() {
        Book book = new Book();
        book.setId(1L);
        book.setCopiesAvailable(0);
        book.setFineRatePerDay(new BigDecimal("20.0"));

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setDueDate(LocalDate.now().minusDays(2)); // 2 days late
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, Reservation.ReservationStatus.PENDING))
                .thenReturn(Collections.emptyList());
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan returnedLoan = libraryService.returnBook(1L);

        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(new BigDecimal("40.0"), returnedLoan.getFineAmount()); // 2 * 20
    }

    @Test
    void returnBook_WithPendingReservation() {
        Book book = new Book();
        book.setId(1L);
        book.setCopiesAvailable(0);
        book.setFineRatePerDay(new BigDecimal("10.0"));

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setDueDate(LocalDate.now().plusDays(1));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setBorrowerId("user2");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, Reservation.ReservationStatus.PENDING))
                .thenReturn(Arrays.asList(reservation));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan returnedLoan = libraryService.returnBook(1L);

        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(0, book.getCopiesAvailable()); // Should NOT increment because of reservation
        assertEquals(Reservation.ReservationStatus.NOTIFIED, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
        verify(bookRepository, never()).save(book); // Book not updated (copies kept at 0)
    }

    @Test
    void reserveBook_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setCopiesAvailable(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reservationRepository.existsByBookAndBorrowerIdAndStatusIn(any(), any(), any())).thenReturn(false);
        when(reservationRepository.countByBorrowerIdAndStatusIn(any(), any())).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation reservation = libraryService.reserveBook(1L, "user1");

        assertNotNull(reservation);
        assertEquals(Reservation.ReservationStatus.PENDING, reservation.getStatus());
    }
}
