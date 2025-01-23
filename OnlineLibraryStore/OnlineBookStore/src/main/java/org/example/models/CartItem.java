package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CartItem {
    private int bookId;
    private String bookName;
    private int quantity;
    private double price;

    public CartItem() {}

    public CartItem(int bookId, String bookName, int quantity, double price) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @JsonIgnore
    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}