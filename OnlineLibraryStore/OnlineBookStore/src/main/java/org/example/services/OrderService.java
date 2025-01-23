package org.example.services;

import org.example.enums.OrderStatus;
import org.example.models.CartItem;
import org.example.models.Order;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
    private static final String ORDERS_FILE = "src/main/resources/orders.txt";
    private final List<Order> orders = new ArrayList<>();
    private static int orderIdCounter = 1; // Counter for order IDs

    // Constructor that loads orders from file
    public OrderService() {
        loadOrdersFromFile();
        orderIdCounter = orders.stream().mapToInt(Order::getId).max().orElse(0) + 1; // Initialize counter
    }

    // Admin Functionalities
    public void viewAllOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders available.");
        } else {
            orders.forEach(System.out::println);
        }
    }

    public boolean confirmOrder(int orderId) {
        Optional<Order> order = findOrderById(orderId);
        if (order.isPresent() && order.get().getStatus().equals(OrderStatus.PENDING.name())) {
            order.get().setStatus(OrderStatus.CONFIRMED.name());
            saveOrdersToFile();
            System.out.println("Order confirmed: " + order.get());
            return true;
        }
        System.out.println("Order not found or already processed.");
        return false;
    }

    public boolean cancelOrder(int orderId) {
        Optional<Order> order = findOrderById(orderId);
        if (order.isPresent() && !order.get().getStatus().equals(OrderStatus.DELIVERED.name())) {
            order.get().setStatus(OrderStatus.CANCELLED.name());
            saveOrdersToFile();
            System.out.println("Order cancelled: " + order.get());
            return true;
        }
        System.out.println("Order not found or cannot be cancelled.");
        return false;
    }

    // Customer Functionalities
    public Order placeOrder(int customerId, List<CartItem> items, String deliveryAddress) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot place an order with empty items.");
        }

        Order newOrder = new Order(
                generateOrderId(),
                customerId,
                LocalDateTime.now(),
                0.0,
                items,
                OrderStatus.PENDING.name(),
                deliveryAddress
        );
        newOrder.calculateTotalAmount();
        orders.add(newOrder);
        saveOrdersToFile();
        System.out.println("Order placed: " + newOrder);
        return newOrder;
    }

    public String trackOrderStatus(int orderId) {
        Optional<Order> order = findOrderById(orderId);
        return order.map(value -> "Order status: " + value.getStatus())
                .orElse("Order not found");
    }

    // Private Helper Methods
    private Optional<Order> findOrderById(int orderId) {
        return orders.stream().filter(order -> order.getId() == orderId).findFirst();
    }

    private void loadOrdersFromFile() {
        try {
            Path filePath = Paths.get(ORDERS_FILE);
            if (Files.exists(filePath)) {
                BufferedReader reader = Files.newBufferedReader(filePath);
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        Order order = deserializeOrder(line.trim());
                        orders.add(order);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing order line: " + line);
                        e.printStackTrace(); // Optional: for detailed debugging
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders from file: " + e.getMessage());
        }
    }

    private Order deserializeOrder(String orderString) {
        String[] parts = orderString.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid order format: " + orderString);
        }
        try {
            int id = Integer.parseInt(parts[0].split("=")[1]);
            int customerId = Integer.parseInt(parts[1].split("=")[1]);
            String status = parts[2].split("=")[1];
            LocalDateTime dateTime = LocalDateTime.parse(parts[3].split("=")[1], DateTimeFormatter.ISO_DATE_TIME);
            double totalAmount = Double.parseDouble(parts[4].split("=")[1]);
            String deliveryAddress = parts[5].split("=")[1];

            return new Order(id, customerId, dateTime, totalAmount, new ArrayList<>(), status, deliveryAddress);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing order: " + orderString, e);
        }
    }

    private void saveOrdersToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(ORDERS_FILE), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Order order : orders) {
                writer.write(serializeOrder(order) + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error saving orders to file: " + e.getMessage());
        }
    }

    private String serializeOrder(Order order) {
        return String.format(
                "id=%d,customerId=%d,status=%s,dateTime=%s,totalAmount=%.2f,deliveryAddress=%s",
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getOrderDate().format(DateTimeFormatter.ISO_DATE_TIME),
                order.getTotalAmount(),
                order.getDeliveryAddress()
        );
    }
    // New method added to resolve the issue
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders); // Return a copy of the orders list to avoid external modification
    }
    public List<Order> getOrdersByCustomerId(int customerId) {
        return orders.stream()
                .filter(order -> order.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    private int generateOrderId() {
        return orderIdCounter++;
    }
}
