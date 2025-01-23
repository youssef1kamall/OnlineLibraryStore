package org.example.models;

import javafx.beans.property.*;

public class Book {
    private static int idCounter = 1; // Static counter for unique IDs
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty author;
    private final DoubleProperty price;
    private final IntegerProperty stock;
    private final StringProperty category;
    private final IntegerProperty popularity;
    private final StringProperty edition;
    private final StringProperty coverImage;

    // Constructor with auto-generated ID
    public Book(String title, String author, double price, int stock, String category, int popularity, String edition, String coverImage) {
        this.id = new SimpleIntegerProperty(idCounter++);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.category = new SimpleStringProperty(category);
        this.popularity = new SimpleIntegerProperty(popularity);
        this.edition = new SimpleStringProperty(edition);
        this.coverImage = new SimpleStringProperty(coverImage);
    }

    // Constructor with custom ID
    public Book(int id, String title, String author, double price, int stock, String category, int popularity, String edition, String coverImage) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.category = new SimpleStringProperty(category);
        this.popularity = new SimpleIntegerProperty(popularity);
        this.edition = new SimpleStringProperty(edition);
        this.coverImage = new SimpleStringProperty(coverImage);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty stockProperty() { return stock; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty popularityProperty() { return popularity; }
    public StringProperty editionProperty() { return edition; }
    public StringProperty coverImageProperty() { return coverImage; }

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public static void setIdCounter(int newCounter) { idCounter = newCounter; }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    public int getPopularity() { return popularity.get(); }
    public void setPopularity(int popularity) { this.popularity.set(popularity); }
    public String getEdition() { return edition.get(); }
    public void setEdition(String edition) { this.edition.set(edition); }
    public String getCoverImage() { return coverImage.get(); }
    public void setCoverImage(String coverImage) { this.coverImage.set(coverImage); }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", author='" + getAuthor() + '\'' +
                ", price=" + getPrice() +
                ", stock=" + getStock() +
                ", category='" + getCategory() + '\'' +
                ", popularity=" + getPopularity() +
                ", edition='" + getEdition() + '\'' +
                ", coverImage='" + getCoverImage() + '\'' +
                '}';
    }

    public void updateDetails(Book updatedBook) {
        this.setTitle(updatedBook.getTitle());
        this.setAuthor(updatedBook.getAuthor());
        this.setPrice(updatedBook.getPrice());
        this.setStock(updatedBook.getStock());
        this.setCategory(updatedBook.getCategory());
        this.setPopularity(updatedBook.getPopularity());
        this.setEdition(updatedBook.getEdition());
        this.setCoverImage(updatedBook.getCoverImage());
    }

}
