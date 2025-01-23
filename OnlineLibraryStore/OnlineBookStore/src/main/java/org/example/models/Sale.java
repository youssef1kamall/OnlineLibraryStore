package org.example.models;

import java.time.LocalDate;

public class Sale {
    private static int idCounter = 1;
    private int id;
    private int bookId;
    private String bookTitle;
    private int quantitySold;
    private double totalRevenue;
    private LocalDate dateOfSale;

    public Sale(int bookId, String bookTitle, int quantitySold, double totalRevenue) {
        this.id = idCounter++;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
        this.dateOfSale = LocalDate.now();
    }

    // Getters and toString
    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public LocalDate getDateOfSale() {
        return dateOfSale;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", quantitySold=" + quantitySold +
                ", totalRevenue=" + totalRevenue +
                ", dateOfSale=" + dateOfSale +
                '}';
    }
}
