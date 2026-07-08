package com.kalvium.library.controller;

import com.kalvium.library.dto.BorrowRequest;
import com.kalvium.library.dto.ReserveRequest;
import com.kalvium.library.entity.Book;
import com.kalvium.library.entity.Loan;
import com.kalvium.library.entity.Reservation;
import com.kalvium.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return new ResponseEntity<>(libraryService.addBook(book), HttpStatus.CREATED);
    }

    @PostMapping("/borrow")
    public ResponseEntity<Loan> borrowBook(@RequestBody BorrowRequest request) {
        return new ResponseEntity<>(libraryService.borrowBook(request.getBookId(), request.getBorrowerId()), HttpStatus.CREATED);
    }

    @PostMapping("/return/{loanId}")
    public ResponseEntity<Loan> returnBook(@PathVariable Long loanId) {
        return ResponseEntity.ok(libraryService.returnBook(loanId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Reservation> reserveBook(@RequestBody ReserveRequest request) {
        return new ResponseEntity<>(libraryService.reserveBook(request.getBookId(), request.getBorrowerId()), HttpStatus.CREATED);
    }

    @GetMapping("/loans/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(libraryService.getOverdueLoans());
    }
}
