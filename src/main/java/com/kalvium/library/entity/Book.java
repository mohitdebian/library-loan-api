package com.kalvium.library.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    private int copiesAvailable;

    private BigDecimal fineRatePerDay;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getCopiesAvailable() { return copiesAvailable; }
    public void setCopiesAvailable(int copiesAvailable) { this.copiesAvailable = copiesAvailable; }
    public BigDecimal getFineRatePerDay() { return fineRatePerDay; }
    public void setFineRatePerDay(BigDecimal fineRatePerDay) { this.fineRatePerDay = fineRatePerDay; }
}
