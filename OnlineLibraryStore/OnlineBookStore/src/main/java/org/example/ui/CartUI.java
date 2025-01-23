package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.models.CartItem;
import org.example.services.CartService;
import org.example.services.OrderService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CartUI {
    private final CartService cartService;
    private final OrderService orderService;
    private final ListView<String> cartListView = new ListView<>();
    private final Label totalPriceLabel = new Label("Total Price: $0.0");
    private final String customerId;

    public CartUI(String customerId) {
        this.cartService = new CartService();
        this.orderService = new OrderService();
        this.customerId = customerId;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cart Management System");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));

        Button removeItemButton = new Button("Remove Item");
        Button updateItemButton = new Button("Update Quantity");
        Button clearCartButton = new Button("Clear Cart");
        Button placeOrderButton = new Button("Place Order");

        removeItemButton.setOnAction(e -> removeItem());
        updateItemButton.setOnAction(e -> updateQuantity());
        clearCartButton.setOnAction(e -> clearCart());
        placeOrderButton.setOnAction(e -> placeOrder(primaryStage));

        HBox buttonLayout = new HBox(10, removeItemButton, updateItemButton, clearCartButton, placeOrderButton);

        cartListView.setPrefHeight(200);

        mainLayout.getChildren().addAll(new Label("Your Cart:"), cartListView, totalPriceLabel, buttonLayout);

        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCartDisplay();
    }

    private void removeItem() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select an item to remove.");
            return;
        }

        int bookId = extractBookId(selectedItem);
        if (bookId == -1) {
            showAlert("Error", "Unable to identify the selected item.");
            return;
        }

        if (showConfirmationAlert("Remove Item", "Are you sure you want to remove this item?", selectedItem)) {
            cartService.removeBookFromCart(customerId, bookId);
            updateCartDisplay();
        }
    }

    private void updateQuantity() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select an item to update.");
            return;
        }

        int bookId = extractBookId(selectedItem);
        if (bookId == -1) {
            showAlert("Error", "Unable to identify the selected item.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Enter New Quantity");
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int newQuantity = Integer.parseInt(input.trim());
                if (newQuantity <= 0) {
                    showAlert("Invalid Quantity", "Quantity must be greater than zero.");
                } else {
                    cartService.updateQuantity(customerId, bookId, newQuantity);
                    updateCartDisplay();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void clearCart() {
        if (showConfirmationAlert("Clear Cart", "Are you sure you want to clear the cart?", "This action cannot be undone.")) {
            cartService.clearCart(customerId);
            updateCartDisplay();
        }
    }

    private void placeOrder(Stage primaryStage) {
        if (cartService.getCartItems(customerId).isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add items before placing an order.");
            return;
        }

        TextInputDialog addressDialog = new TextInputDialog();
        addressDialog.setTitle("Delivery Address");
        addressDialog.setHeaderText("Enter Delivery Address");
        addressDialog.setContentText("Address:");

        addressDialog.showAndWait().ifPresent(address -> {
            try {
                List<CartItem> items = cartService.getCartItems(customerId);
                orderService.placeOrder(Integer.valueOf(customerId), items, address);
                cartService.clearCart(customerId);
                updateCartDisplay();
                showAlert("Order Placed", "Your order has been placed successfully.");
            } catch (IllegalArgumentException e) {
                showAlert("Order Error", e.getMessage());
            }
        });
    }

    private void updateCartDisplay() {
        cartListView.getItems().clear();
        cartService.getCartItems(customerId).forEach(item -> cartListView.getItems().add(item.toString()));
        totalPriceLabel.setText("Total Price: $" + cartService.calculateTotal(customerId));
    }

    private int extractBookId(String item) {
        // Regular expression to match the bookId value
        String regex = "bookId=(\\d+)";  // Match "bookId=" followed by digits

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(item);

        if (matcher.find()) {
            // Return the extracted bookId as an integer
            return Integer.parseInt(matcher.group(1));
        } else {
            System.out.println("Error: 'bookId' not found in item.");
            return -1;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }
}
