package org.example.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.models.Book;
import org.example.models.CartItem;
import org.example.models.Order;
import org.example.models.Review;
import org.example.services.BookServiceProxy;
import org.example.services.CartService;
import org.example.services.OrderService;
import org.example.services.ReviewService;

import java.util.List;

public class BookUI {
    private final int customerId;
    private static final String ERROR_LOADING_BOOKS = "Failed to load books.";
    private static final String ERROR_SEARCH_BOOKS = "Failed to search books.";
    private static final String ALERT_INVALID_QUANTITY = "Please enter a valid quantity.";

    private final BookServiceProxy bookServiceProxy = new BookServiceProxy();
    private final CartService cartService = new CartService();
    private final ReviewService reviewService = new ReviewService();  // Assuming this service exists

    private final TableView<Book> bookTable = new TableView<>();
    private final TableView<CartItem> cartTable = new TableView<>();

    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cartData = FXCollections.observableArrayList();

    public BookUI(int customerId) {
        this.customerId = customerId;
    }


    private void openReviewDialog(Book selectedBook, int customerId) {
        // Create the review dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Review");
        dialog.setHeaderText("Review for " + selectedBook.getTitle());

        // Create dialog content (TextArea for review and ComboBox for rating)
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // TextArea for the review text
        TextArea reviewTextArea = new TextArea();
        reviewTextArea.setPromptText("Write your review here...");
        reviewTextArea.setWrapText(true);
        reviewTextArea.setMaxHeight(100);

        // ComboBox for the rating (1 to 5 stars)
        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setPromptText("Rating (1-5)");

        content.getChildren().addAll(new Label("Your Review:"), reviewTextArea, new Label("Rating:"), ratingComboBox);

        // Set the dialog's content
        dialog.getDialogPane().setContent(content);

        // Add the dialog's buttons
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Handle the submit button
        dialog.setResultConverter(buttonType -> {
            if (buttonType == submitButtonType) {
                String reviewText = reviewTextArea.getText().trim();
                Integer rating = ratingComboBox.getValue();

                if (reviewText.isEmpty() || rating == null) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill out both the review and rating.");
                    return null;
                }

                try {
                    // Call the addReview method with individual parameters
                    reviewService.addReview(selectedBook.getId(), customerId, reviewText, rating);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Your review has been added.");
                    refreshUI();  // Refresh UI to show the new review
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit review: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    public VBox createMainLayout() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        Label titleLabel = createTitleLabel("Book Management System");
        HBox controls = createControls();
        configureTable(bookTable, 400);
        configureTable(cartTable, 200);

        loadBookTableColumns();

        loadBooks();

        mainLayout.getChildren().addAll(titleLabel, controls, bookTable);
        return mainLayout;
    }

    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        return label;
    }

    private HBox createControls() {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10, 0, 10, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks(searchField.getText().trim()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart());

        Button viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> viewCart());

        // New "View Orders" button
        Button viewOrdersButton = new Button("View Orders");
        viewOrdersButton.setOnAction(e -> viewOrders());

        controls.getChildren().addAll(searchField, searchButton, addToCartButton, viewCartButton, viewOrdersButton);
        return controls;
    }

    private void viewOrders() {
        OrderService orderService = new OrderService();
        OrderUI orderUI = new OrderUI(customerId, orderService);
        Stage orderStage = new Stage();
        orderUI.start(orderStage);
    }

    private void viewCart() {
        int customerId = getCustomerId();  // Assuming this returns an int
        CartUI cartUI = new CartUI(String.valueOf(customerId));  // Convert customerId to String
        Stage cartStage = new Stage();
        cartUI.start(cartStage);
    }

    private void configureTable(TableView<?> table, double height) {
        table.setPrefHeight(height);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadBookTableColumns() {
        TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getId()));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(param -> param.getValue().titleProperty());

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(param -> param.getValue().authorProperty());

        TableColumn<Book, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPrice()));

        TableColumn<Book, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getStock()));

        // Add a column for the "View Reviews" button
        TableColumn<Book, Void> reviewColumn = new TableColumn<>("Reviews");
        reviewColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewReviewButton = new Button("View Reviews");

            {
                viewReviewButton.setOnAction(e -> {
                    Book selectedBook = getTableView().getItems().get(getIndex());
                    viewReviewsForBook(selectedBook.getId());
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewReviewButton);
                }
            }
        });

        TableColumn<Book, Void> addReviewColumn = new TableColumn<>("Add Review");
        addReviewColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addReviewButton = new Button("Add Review");

            {
                addReviewButton.setOnAction(e -> {
                    Book selectedBook = getTableView().getItems().get(getIndex());
                    openReviewDialog(selectedBook,customerId);  // Open the review dialog to add a new review
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addReviewButton);
                }
            }
        });

        bookTable.getColumns().addAll(idColumn, titleColumn, authorColumn, priceColumn, stockColumn, reviewColumn, addReviewColumn);
    }

    private void loadBooks() {
        try {
            bookData.clear();
            bookData.addAll(bookServiceProxy.viewAllBooks());
            bookTable.setItems(bookData);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", ERROR_LOADING_BOOKS + " " + e.getMessage());
        }
    }

    private void searchBooks(String keyword) {
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Search term cannot be empty.");
            return;
        }

        try {
            bookData.clear();
            bookData.addAll(bookServiceProxy.searchBooks(keyword));
            bookTable.setItems(bookData);

            if (bookData.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No books found for the search term: " + keyword);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", ERROR_SEARCH_BOOKS + " " + e.getMessage());
        }
    }

    private void handleAddToCart() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Book Selected", "Please select a book to add to cart.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add to Cart");
        dialog.setHeaderText("Enter quantity for: " + selectedBook.getTitle());
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(quantityStr -> processAddToCart(selectedBook, quantityStr));
    }

    private void processAddToCart(Book selectedBook, String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);

            if (quantity <= 0 || quantity > selectedBook.getStock()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", ALERT_INVALID_QUANTITY);
                return;
            }

            int customerId = getCustomerId();  // Get customer ID
            cartService.addBookToCart(String.valueOf(customerId), selectedBook.getId(), selectedBook.getTitle(), quantity, selectedBook.getPrice());
            selectedBook.setStock(selectedBook.getStock() - quantity);
            refreshUI();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
        }
    }

    private void refreshUI() {
        loadBooks();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getCustomerId() {
        return 1; // Example customer ID
    }

    private void viewReviewsForBook(int bookId) {
        try {
            List<Review> reviews = reviewService.getReviewsForBook(bookId);  // Fetch reviews for the book
            if (reviews.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Reviews", "No reviews found for this book.");
                return;
            }

            // Create a new window to display reviews
            VBox reviewLayout = new VBox(10);
            reviewLayout.setPadding(new Insets(10));
            reviewLayout.getChildren().add(new Label("Reviews:"));

            // Iterate through reviews and add to the layout
            for (Review review : reviews) {
                VBox reviewBox = new VBox(5);  // To group review info (text, rating)
                reviewBox.setPadding(new Insets(5));

                // Add review content and rating
                Label reviewTextLabel = new Label("Review: " + review.getReviewText());
                Label ratingLabel = new Label("Rating: " + review.getRating() + "/5");

                // Add labels to the reviewBox
                reviewBox.getChildren().addAll(reviewTextLabel, ratingLabel);
                reviewLayout.getChildren().add(reviewBox);
            }

            // Create and configure the review window
            Stage reviewStage = new Stage();
            reviewStage.setTitle("Reviews for Book");
            reviewStage.setScene(new Scene(reviewLayout, 400, 300));  // Adjust the size as needed
            reviewStage.show();

        } catch (Exception e) {
            // Log or print the stack trace for detailed error info
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reviews for this book.");
        }
    }
}
