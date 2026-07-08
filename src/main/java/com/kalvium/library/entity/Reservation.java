package com.kalvium.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    private String borrowerId;

    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING, NOTIFIED, COMPLETED, CANCELLED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public String getBorrowerId() { return borrowerId; }
    public void setBorrowerId(String borrowerId) { this.borrowerId = borrowerId; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
