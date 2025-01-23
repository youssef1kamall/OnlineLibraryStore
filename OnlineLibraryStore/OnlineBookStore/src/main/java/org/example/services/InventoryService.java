package org.example.services;

import org.example.models.Book;
import org.example.models.Sale;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryService {
    private static final String SALES_FILE = "src/main/resources/sales.txt";

    protected final FileManager fileManager = new FileManager();
    private final List<Book> books;
    private final List<Sale> sales = new ArrayList<>();

    public InventoryService() {
        // Use FileManager to load books
        this.books = fileManager.loadBooksFromFile();
        loadSalesFromFile();
    }

    // Load sales from file
    private void loadSalesFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SALES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Sale sale = new Sale(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Double.parseDouble(parts[3])
                    );
                    sales.add(sale);
                }
            }
            System.out.println("Sales loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading sales file: " + e.getMessage());
        }
    }

    // Save sales to file
    private void saveSalesToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SALES_FILE))) {
            for (Sale sale : sales) {
                writer.write(String.format("%d,%s,%d,%.2f%n",
                        sale.getBookId(),
                        sale.getBookTitle(),
                        sale.getQuantitySold(),
                        sale.getTotalRevenue()));
            }
            System.out.println("Sales saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving sales file: " + e.getMessage());
        }
    }

    // Methods to handle sales and books
    public boolean recordSale(int bookId, int quantitySold) {
        boolean success = false;
        Optional<Book> book = books.stream().filter(b -> b.getId() == bookId).findFirst();
        if (book.isPresent()) {
            Book b = book.get();
            if (b.getStock() >= quantitySold) {
                b.setStock(b.getStock() - quantitySold);
                double totalRevenue = b.getPrice() * quantitySold;
                Sale sale = new Sale(bookId, b.getTitle(), quantitySold, totalRevenue);
                sales.add(sale);
                success = true;
            }
        }
        if (success) saveSalesToFile();
        return success;
    }

    public boolean addNewBook(Book book) {
        boolean success = books.add(book);
        if (success) {
            fileManager.saveBooksToFile(books);
        }
        return success;
    }

    public boolean deleteBook(int bookId) {
        boolean success = books.removeIf(book -> book.getId() == bookId);
        if (success) {
            fileManager.saveBooksToFile(books);
        }
        return success;
    }

    // Inventory monitoring: List books with low stock
    public List<Book> getBooksWithLowStock(int threshold) {
        return books.stream()
                .filter(b -> b.getStock() <= threshold)
                .collect(Collectors.toList());
    }

    // Statistics monitoring: Total revenue from all sales
    public double getTotalRevenue() {
        return sales.stream()
                .mapToDouble(Sale::getTotalRevenue)
                .sum();
    }

    // Statistics monitoring: Total quantity of a specific book sold
    public int getTotalQuantitySoldForBook(int bookId) {
        return sales.stream()
                .filter(sale -> sale.getBookId() == bookId)
                .mapToInt(Sale::getQuantitySold)
                .sum();
    }

    // Statistics monitoring: Most popular books (based on sales quantity)
    public List<Book> getMostPopularBooks() {
        return books.stream()
                .sorted((b1, b2) -> Integer.compare(
                        getTotalQuantitySoldForBook(b2.getId()),
                        getTotalQuantitySoldForBook(b1.getId())))
                .limit(5) // Get top 5 popular books
                .collect(Collectors.toList());
    }

    // Print statistics summary
    public void printStatistics() {
        System.out.println("Total revenue: " + getTotalRevenue());
        System.out.println("Most popular books:");
        getMostPopularBooks().forEach(book ->
                System.out.println(book.getTitle() + " - Total Sold: " + getTotalQuantitySoldForBook(book.getId())));
        System.out.println("Books with low stock:");
        getBooksWithLowStock(5).forEach(book ->
                System.out.println(book.getTitle() + " - Stock: " + book.getStock()));
    }
}
