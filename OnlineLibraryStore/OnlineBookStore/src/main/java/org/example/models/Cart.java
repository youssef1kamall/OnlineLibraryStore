package org.example.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private static final String CART_FILE = "src/main/resources/cart.json";
    private List<CartItem> items;

    private static final Map<String, Cart> userCarts = new ConcurrentHashMap<>();

    private Cart() {
        this.items = new ArrayList<>();
    }

    public static Cart getInstance(String userId) {
        return userCarts.computeIfAbsent(userId, k -> new Cart());
    }

    public void addItem(CartItem item) {
        for (CartItem existingItem : items) {
            if (existingItem.getBookId() == item.getBookId()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                saveToFile();
                return;
            }
        }
        items.add(item);
        saveToFile();
    }

    public void removeItem(int bookId) {
        items.removeIf(item -> item.getBookId() == bookId);
        saveToFile();
    }

    public void updateQuantity(int bookId, int quantity) {
        for (CartItem item : items) {
            if (item.getBookId() == bookId) {
                item.setQuantity(quantity);
                saveToFile();
                return;
            }
        }
    }

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clear() {
        items.clear();
        saveToFile();
    }

    public List<CartItem> getItems() {
        return items;
    }

    private void saveToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(CART_FILE), userCarts);
        } catch (IOException e) {
            System.err.println("Error saving cart to file: " + e.getMessage());
        }
    }
}