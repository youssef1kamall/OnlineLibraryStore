package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.services.OrderService;
import org.example.models.Order;

import java.text.NumberFormat;
import java.util.List;

public class OrderUI {
    private final OrderService orderService;
    private final ListView<String> orderListView = new ListView<>();
    private final Label totalAmountLabel = new Label("Total Amount: $0.00");
    private final int customerId;

    public OrderUI(int customerId, OrderService orderService) {
        this.customerId = customerId;
        this.orderService = orderService;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Order Management System");

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));

        Button cancelOrderButton = new Button("Cancel Order");
        Button trackOrderButton = new Button("Track Order");

        cancelOrderButton.setOnAction(e -> cancelOrder());
        trackOrderButton.setOnAction(e -> trackOrder());

        HBox buttonLayout = new HBox(15, cancelOrderButton, trackOrderButton);
        buttonLayout.setStyle("-fx-alignment: center;");

        orderListView.setPrefHeight(250);
        orderListView.setStyle("-fx-font-size: 14px;");

        mainLayout.getChildren().addAll(new Label("Your Orders:"), orderListView, totalAmountLabel, buttonLayout);

        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateOrderDisplay();
    }

    private void cancelOrder() {
        String selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to cancel.");
            return;
        }

        int orderId = extractOrderId(selectedOrder);

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Cancel Order");
        confirmationAlert.setHeaderText("Are you sure you want to cancel this order?");
        confirmationAlert.setContentText("Order ID: " + orderId);

        if (confirmationAlert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            boolean success = orderService.cancelOrder(orderId);
            if (success) {
                updateOrderDisplay();
                showAlert("Success", "Order has been cancelled.");
            } else {
                showAlert("Cancellation Failed", "Order not found, already delivered, or cannot be cancelled.");
            }
        }
    }

    private void trackOrder() {
        String selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to track.");
            return;
        }

        int orderId = extractOrderId(selectedOrder);
        String orderStatus = orderService.trackOrderStatus(orderId);

        if (orderStatus != null) {
            showAlert("Order Status", "Order ID: " + orderId + "\nStatus: " + orderStatus);
        } else {
            showAlert("Tracking Failed", "Unable to fetch the order status.");
        }
    }

    private void updateOrderDisplay() {
        orderListView.getItems().clear();

        List<Order> customerOrders = orderService.getOrdersByCustomerId(customerId);

        // Check if no orders are found
        if (customerOrders == null || customerOrders.isEmpty()) {
            orderListView.getItems().add("No orders found.");
            totalAmountLabel.setText("Total Amount: $0.00");
            return; // Exit the method early
        }

        double totalAmount = 0.0;

        for (Order order : customerOrders) {
            // Log the order details to verify data
            System.out.println("Order ID: " + order.getId() + ", Status: " + order.getStatus() +
                    ", Amount: " + order.getTotalAmount() + ", Address: " + order.getDeliveryAddress());

            String orderDisplay = String.format("Order ID: %d, Status: %s, Amount: $%.2f, Address: %s",
                    order.getId(), order.getStatus(), order.getTotalAmount(), order.getDeliveryAddress());
            orderListView.getItems().add(orderDisplay);
            totalAmount += order.getTotalAmount();
        }

        String formattedAmount = NumberFormat.getCurrencyInstance().format(totalAmount);
        totalAmountLabel.setText("Total Amount: " + formattedAmount);
    }


    private int extractOrderId(String orderString) {
        try {
            String[] parts = orderString.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("Order ID:")) {
                    return Integer.parseInt(part.split(":")[1].trim());
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid order string format. Error: " + e.getMessage());
        }
        throw new IllegalArgumentException("Order ID not found in the selected string.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
