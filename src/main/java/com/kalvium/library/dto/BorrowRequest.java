package com.kalvium.library.dto;

public class BorrowRequest {
    private Long bookId;
    private String borrowerId;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBorrowerId() { return borrowerId; }
    public void setBorrowerId(String borrowerId) { this.borrowerId = borrowerId; }
}
