package org.example.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private int id;
    private int customerId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private List<CartItem> items;
    private String status;
    private String deliveryAddress;
    private LocalDateTime deliveredAt;

    // Default constructor
    public Order() {}

    // Constructor with parameters
    public Order(int id, int customerId, LocalDateTime orderDate, double totalAmount,
                 List<CartItem> items, String status, String deliveryAddress) {
        this.id = id; // ID is set externally by OrderService
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotalAmount(); // Recalculate total amount when items change
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    // Helper method to calculate the total amount of the order based on CartItem prices and quantities
    public void calculateTotalAmount() {
        if (items != null) {
            this.totalAmount = items.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        } else {
            this.totalAmount = 0.0;
        }
    }

    // Method to update order status (useful for admin updates)
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    // Method to check if the order is delivered
    public boolean isDelivered() {
        return this.deliveredAt != null;
    }

    @Override
    public String toString() {
        String itemsString = (items != null) ? items.stream()
                .map(CartItem::toString)
                .collect(Collectors.joining("; ")) : "[]";

        return String.format(
                "id=%d,customerId=%d,status=%s,orderDate=%s,totalAmount=%.2f,deliveryAddress=%s,deliveredAt=%s,items=[%s]",
                id, customerId, status, orderDate, totalAmount, deliveryAddress,
                deliveredAt != null ? deliveredAt : "null",
                itemsString
        );
    }
}
