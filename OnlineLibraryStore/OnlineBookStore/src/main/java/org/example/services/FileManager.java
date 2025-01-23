package org.example.services;

import org.example.models.Book;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_PATH = "src/main/resources/books.txt"; // Moved to project root for simplicity

    public List<Book> loadBooksFromFile() {
        List<Book> books = new ArrayList<>();
        int maxId = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        double price = Double.parseDouble(parts[3].trim());
                        int stock = Integer.parseInt(parts[4].trim());
                        String category = parts[5].trim();
                        int popularity = Integer.parseInt(parts[6].trim());
                        String edition = parts[7].trim();
                        String coverImage = parts[8].trim();

                        books.add(new Book(id, title, author, price, stock, category, popularity, edition, coverImage));
                        maxId = Math.max(maxId, id);
                    } catch (NumberFormatException e) {
                        logError("Invalid number format in line: " + line, e);
                    }
                } else {
                    logError("Invalid book format: " + line, null);
                }
            }
        } catch (IOException e) {
            logError("Error reading books file", e);
        }

        Book.setIdCounter(maxId + 1);
        return books;
    }

    public void saveBooksToFile(List<Book> books) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Book book : books) {
                writer.write(String.format("%d,%s,%s,%.2f,%d,%s,%d,%s,%s%n",
                        book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(),
                        book.getStock(), book.getCategory(), book.getPopularity(),
                        book.getEdition(), book.getCoverImage()));
            }
        } catch (IOException e) {
            logError("Error writing books to file", e);
        }
    }

    private void logError(String message, Exception e) {
        System.err.println(message);
        if (e != null) {
            e.printStackTrace();
        }
    }
}
