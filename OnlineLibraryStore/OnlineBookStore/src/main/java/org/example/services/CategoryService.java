package org.example.services;

import org.example.models.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private static final String FILE_PATH = "src/main/resources/categories.txt";
    private final List<Category> categories = new ArrayList<>();

    // Add a new category
    public void addCategory(String name) {
        categories.add(new Category(name));
        System.out.println("Category added: " + name);
    }

    // Remove a category by ID
    public boolean removeCategory(int categoryId) {
        return categories.removeIf(category -> category.getId() == categoryId);
    }

    // View all categories
    public List<Category> viewAllCategories() {
        return new ArrayList<>(categories);
    }

    // Load categories from a text file
    public void loadCategoriesFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        categories.add(new Category(name));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID format in line: " + line);
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
            System.out.println("Categories loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading categories file: " + e.getMessage());
        }
    }
}
