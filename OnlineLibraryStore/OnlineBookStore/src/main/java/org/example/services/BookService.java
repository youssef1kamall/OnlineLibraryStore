package org.example.services;

import javafx.scene.control.Alert;
import org.example.models.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    protected final List<Book> books = new ArrayList<>();
    protected final FileManager fileManager = new FileManager();

    public void addBook(Book book) {
        reloadBooksIfNeeded();

        // Check for duplicate book
        boolean exists = books.stream().anyMatch(b ->
                b.getTitle().equalsIgnoreCase(book.getTitle()) &&
                        b.getAuthor().equalsIgnoreCase(book.getAuthor())
        );
        if (exists) {
            showAlert("Duplicate Book", "The book already exists!");
            return;
        }

        books.add(book);
        saveBooks();
    }

    public boolean removeBookById(int bookId) {
        reloadBooksIfNeeded();
        boolean removed = books.removeIf(book -> book.getId() == bookId);
        if (removed) {
            saveBooks();
        }
        return removed;
    }

    public boolean updateBook(Book updatedBook) {
        reloadBooksIfNeeded();

        for (Book book : books) {
            if (book.getId() == updatedBook.getId()) {
                // Update the book details
                book.updateDetails(updatedBook);
                saveBooks(); // Persist changes
                return true; // Update was successful
            }
        }

        return false; // No matching book found
    }


    public List<Book> viewAllBooks() {
        reloadBooksIfNeeded();
        return new ArrayList<>(books); // Defensive copy
    }

    public List<Book> searchBooks(String keyword) {
        reloadBooksIfNeeded();
        String lowerKeyword = keyword.toLowerCase();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword) ||
                        book.getAuthor().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    private void saveBooks() {
        try {
            fileManager.saveBooksToFile(books);
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save books: " + e.getMessage());
        }
    }

    private void reloadBooksIfNeeded() {
        if (books.isEmpty()) {
            books.addAll(fileManager.loadBooksFromFile());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
