package org.example.services;

import org.example.models.Book;

import java.util.List;

public class BookServiceProxy extends BookService {
    private boolean isLoaded = false;

    @Override
    public List<Book> viewAllBooks() {
        reloadBooksIfNeeded();
        return super.viewAllBooks();
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        reloadBooksIfNeeded();
        return super.searchBooks(keyword);
    }

    @Override
    public void addBook(Book book) {
        // Reload books from file before adding a new book
        reloadBooksIfNeeded();
        super.addBook(book);
        // After adding, the list has changed, so mark isLoaded as false
        isLoaded = false;
    }

    @Override
    public boolean removeBookById(int bookId) {
        reloadBooksIfNeeded();
        boolean result = super.removeBookById(bookId);
        // After removing, the list has changed, so mark isLoaded as false
        isLoaded = false;
        return result;
    }

    @Override
    public boolean updateBook(Book updatedBook) {
        reloadBooksIfNeeded();
        boolean result = super.updateBook(updatedBook);
        // After updating, the list has changed, so mark isLoaded as false
        isLoaded = false;
        return result;
    }

    private void reloadBooksIfNeeded() {
        // Reload books only if they haven't been loaded yet
        if (!isLoaded) {
            reloadBooksFromFile();
        }
    }

    private void reloadBooksFromFile() {
        // Reload books from the file
        books.clear();
        books.addAll(fileManager.loadBooksFromFile());
        isLoaded = true; // Mark books as loaded
    }
}
