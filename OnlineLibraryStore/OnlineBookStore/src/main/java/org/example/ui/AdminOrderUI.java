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

public class AdminOrderUI {
    private final OrderService orderService;
    private final ListView<String> orderListView = new ListView<>();
    private final Label totalAmountLabel = new Label("Total Amount: $0.00");

    public AdminOrderUI(OrderService orderService) {
        this.orderService = orderService;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Order Management");

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));

        Button confirmOrderButton = new Button("Confirm Order");
        Button cancelOrderButton = new Button("Cancel Order");

        confirmOrderButton.setOnAction(e -> confirmOrder());
        cancelOrderButton.setOnAction(e -> cancelOrder());

        HBox buttonLayout = new HBox(15, confirmOrderButton, cancelOrderButton);
        buttonLayout.setStyle("-fx-alignment: center;");

        orderListView.setPrefHeight(250);
        orderListView.setStyle("-fx-font-size: 14px;");

        mainLayout.getChildren().addAll(new Label("All Orders:"), orderListView, totalAmountLabel, buttonLayout);

        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateOrderDisplay();
    }

    private void confirmOrder() {
        String selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to confirm.");
            return;
        }

        int orderId = extractOrderId(selectedOrder);
        boolean success = orderService.confirmOrder(orderId);

        if (success) {
            updateOrderDisplay();
            showAlert("Success", "Order has been confirmed.");
        } else {
            showAlert("Confirmation Failed", "Order not found or already processed.");
        }
    }

    private void cancelOrder() {
        String selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to cancel.");
            return;
        }

        int orderId = extractOrderId(selectedOrder);
        boolean success = orderService.cancelOrder(orderId);

        if (success) {
            updateOrderDisplay();
            showAlert("Success", "Order has been cancelled.");
        } else {
            showAlert("Cancellation Failed", "Order not found or cannot be cancelled.");
        }
    }

    private void updateOrderDisplay() {
        orderListView.getItems().clear();

        List<Order> allOrders = orderService.getAllOrders();
        double totalAmount = 0.0;

        for (Order order : allOrders) {
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
