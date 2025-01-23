package org.example.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.models.Book;
import org.example.services.BookService;
import org.example.services.BookServiceProxy;
import org.example.services.OrderService;

import java.util.List;

public class AdminBookUI {

    private static final String ERROR_LOADING_BOOKS = "Failed to load books.";
    private static final String ERROR_SEARCH_BOOKS = "Failed to search books.";
    private static final String ALERT_INVALID_QUANTITY = "Please enter a valid quantity.";

    private final BookServiceProxy bookServiceProxy = new BookServiceProxy();
    private final BookService bookService = new BookService();

    private final TableView<Book> bookTable = new TableView<>();
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    public AdminBookUI() {}

    // Main Layout Creation
    public VBox createMainLayout() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        Label titleLabel = createTitleLabel("Admin - Book Management System");
        HBox controls = createAdminControls();
        configureTable(bookTable, 400);

        loadBookTableColumns();
        refreshUI();

        mainLayout.getChildren().addAll(titleLabel, controls, bookTable);
        return mainLayout;
    }

    // Control Setup
    private HBox createAdminControls() {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10, 0, 10, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks(searchField.getText().trim()));

        Button addButton = new Button("Add Book");
        addButton.setOnAction(e -> addBook());

        Button deleteButton = new Button("Delete Book");
        deleteButton.setOnAction(e -> deleteBook());

        Button updateButton = new Button("Update Book");
        updateButton.setOnAction(e -> updateBook());

        Button viewOrdersButton = new Button("View Orders");
        viewOrdersButton.setOnAction(e -> viewOrders());

        controls.getChildren().addAll(searchField, searchButton, addButton, deleteButton, updateButton, viewOrdersButton);
        return controls;
    }

    // Title Label Creation
    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        return label;
    }

    // Table Configuration
    private void configureTable(TableView<?> table, double height) {
        table.setPrefHeight(height);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Column Setup for the TableView
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

        bookTable.getColumns().addAll(idColumn, titleColumn, authorColumn, priceColumn, stockColumn);
    }

    private void updateBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningMessage("No Book Selected", "Please select a book to update.");
            return;
        }

        BookUpdateDialog dialog = new BookUpdateDialog(selectedBook);
        dialog.showAndWait().ifPresent(updatedBook -> {
            try {
                validateBookDetails(updatedBook);
                boolean updated = bookServiceProxy.updateBook(updatedBook);

                if (updated) {
                    showSuccessMessage("Book updated successfully.");
                    refreshUI(); // Reload the UI with updated data
                } else {
                    // Added debug information
                    System.err.println("Update failed. Updated Book ID: " + updatedBook.getId());
                    showErrorMessage("Failed to update the book. Make sure the book exists.");
                }
            } catch (IllegalArgumentException e) {
                showErrorMessage("Failed to update book: " + e.getMessage());
            } catch (Exception e) {
                showErrorMessage("An error occurred: " + e.getMessage());
            }
        });
    }


    // Validate Book Details
    private void validateBookDetails(Book book) throws IllegalArgumentException {
        if (book.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (book.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be a positive integer.");
        }
    }

    // Book Search
    private void searchBooks(String keyword) {
        if (keyword.isEmpty()) {
            refreshUI();
            return;
        }

        try {
            List<Book> result = bookServiceProxy.searchBooks(keyword);
            if (result.isEmpty()) {
                showInfoMessage("No Results", "No books found for the given search.");
            } else {
                bookData.setAll(result);
            }
        } catch (Exception e) {
            showErrorMessage(ERROR_SEARCH_BOOKS);
        }
    }

    private void refreshUIWithFeedback() {
        // Set a loading placeholder (consider adding a spinner instead of text for a better UX)
        bookTable.setPlaceholder(new Label("Loading books, please wait..."));

        // Fetch updated book data asynchronously to prevent UI blocking
        new Thread(() -> {
            try {
                // Fetch updated book data
                List<Book> books = bookServiceProxy.viewAllBooks();

                // Update the observable list on the JavaFX Application Thread (UI thread)
                Platform.runLater(() -> {
                    // Update the observable list
                    bookData.setAll(books);

                    // Set the updated list to the table
                    bookTable.setItems(bookData);

                    // Update the placeholder to indicate the results
                    if (books.isEmpty()) {
                        bookTable.setPlaceholder(new Label("No books available."));
                    } else {
                        bookTable.setPlaceholder(new Label("")); // Clear the placeholder if books are loaded
                    }
                });
            } catch (Exception e) {
                // Handle error and update UI with an error message
                Platform.runLater(() -> {
                    bookTable.setPlaceholder(new Label("Failed to load books."));
                    showErrorMessage(ERROR_LOADING_BOOKS + "\n" + e.getMessage());
                });
            }
        }).start(); // Start the background thread for fetching data
    }


    // Refresh UI initially
    private void refreshUI() {
        try {
            // Clear existing data
            bookData.clear();

            // Fetch updated book data
            List<Book> books = bookServiceProxy.viewAllBooks();
            if (books.isEmpty()) {
                showInfoMessage("No Books Available", "No books found in the system.");
            }

            // Add new data to the observable list
            bookData.addAll(books);

            // Update the table items
            bookTable.setItems(bookData);

        } catch (Exception e) {
            showErrorMessage(ERROR_LOADING_BOOKS + "\n" + e.getMessage());
        }
    }

    // Add Book
    private void addBook() {
        BookAddDialog dialog = new BookAddDialog();
        dialog.showAndWait().ifPresent(book -> {
            try {
                validateBookDetails(book);
                bookService.addBook(book);
                showSuccessMessage("Book added successfully.");
                refreshUIWithFeedback(); // Use enhanced refresh
            } catch (Exception e) {
                showErrorMessage("Failed to add book: " + e.getMessage());
            }
        });
    }

    // Delete Book
    private void deleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningMessage("No Book Selected", "Please select a book to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText("Are you sure you want to delete this book?");
        confirmation.setContentText("Book: " + selectedBook.getTitle());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = bookService.removeBookById(selectedBook.getId());
                    if (deleted) {
                        showSuccessMessage("Book deleted successfully.");
                        refreshUIWithFeedback();
                    } else {
                        showErrorMessage("Failed to delete the book. Ensure it exists.");
                    }
                } catch (Exception e) {
                    showErrorMessage("Error deleting book: " + e.getMessage());
                }
            }
        });
    }

    // View Orders
    private void viewOrders() {
        OrderService orderService = new OrderService();
        AdminOrderUI adminOrderUI = new AdminOrderUI(orderService);
        Stage orderStage = new Stage();
        adminOrderUI.start(orderStage);
    }

    // Alert Dialogs
    private void showSuccessMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }

    private void showErrorMessage(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showWarningMessage(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    private void showInfoMessage(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
