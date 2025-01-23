package org.example.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.models.Book;

public class BookUpdateDialog extends Dialog<Book> {

    private final Book book;

    public BookUpdateDialog(Book book) {
        this.book = book;

        setTitle("Update Book");
        setHeaderText("Update details for book: " + book.getTitle());

        // Create form fields (pre-populated)
        TextField titleField = createTextField(book.getTitle(), "Enter book title");
        TextField authorField = createTextField(book.getAuthor(), "Enter author name");
        TextField priceField = createTextField(String.valueOf(book.getPrice()), "Enter book price");
        TextField stockField = createTextField(String.valueOf(book.getStock()), "Enter stock quantity");
        TextField categoryField = createTextField(book.getCategory(), "Enter book category");
        TextField popularityField = createTextField(String.valueOf(book.getPopularity()), "Enter book popularity (integer)");
        TextField editionField = createTextField(book.getEdition(), "Enter edition (e.g., 'First Edition')");
        TextField coverImageField = createTextField(book.getCoverImage(), "Enter cover image URL");

        // Add buttons
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        // Layout
        VBox vbox = new VBox(10, titleField, authorField, priceField, stockField, categoryField, popularityField, editionField, coverImageField);
        vbox.setPrefWidth(400); // Optional: Set a preferred width for better alignment
        getDialogPane().setContent(vbox);

        // Disable the "Update" button until valid input is provided
        final Button update = (Button) getDialogPane().lookupButton(updateButton);
        update.setDisable(true);

        // Add validation listeners
        titleField.textProperty().addListener((obs, oldValue, newValue) -> update.setDisable(!isFormValid(titleField, authorField, priceField, stockField, popularityField)));
        authorField.textProperty().addListener((obs, oldValue, newValue) -> update.setDisable(!isFormValid(titleField, authorField, priceField, stockField, popularityField)));
        priceField.textProperty().addListener((obs, oldValue, newValue) -> update.setDisable(!isFormValid(titleField, authorField, priceField, stockField, popularityField)));
        stockField.textProperty().addListener((obs, oldValue, newValue) -> update.setDisable(!isFormValid(titleField, authorField, priceField, stockField, popularityField)));
        popularityField.textProperty().addListener((obs, oldValue, newValue) -> update.setDisable(!isFormValid(titleField, authorField, priceField, stockField, popularityField)));

        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == updateButton) {
                return createBookFromForm(titleField, authorField, priceField, stockField, categoryField, popularityField, editionField, coverImageField);
            }
            return null;
        });
    }

    private TextField createTextField(String initialValue, String promptText) {
        TextField textField = new TextField(initialValue);
        textField.setPromptText(promptText);
        return textField;
    }

    private Book createBookFromForm(TextField titleField, TextField authorField, TextField priceField, TextField stockField,
                                    TextField categoryField, TextField popularityField, TextField editionField, TextField coverImageField) {
        try {
            double price = parseDouble(priceField.getText(), "Price");
            int stock = parseInt(stockField.getText(), "Stock");
            int popularity = parseInt(popularityField.getText(), "Popularity");

            // Corrected part: Getting the existing ID from the original book object
            int existingId = book.getId();  // 'book' is the instance passed to the dialog constructor

            return new Book(
                    existingId,  // Pass the existing ID
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    price,
                    stock,
                    categoryField.getText().trim(),
                    popularity,
                    editionField.getText().trim(),
                    coverImageField.getText().trim()
            );
        } catch (IllegalArgumentException e) {
            showError("Invalid Input", e.getMessage());
            return null;
        }
    }

    private double parseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid decimal number.");
        }
    }

    private int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer.");
        }
    }

    private boolean isFormValid(TextField titleField, TextField authorField, TextField priceField, TextField stockField, TextField popularityField) {
        try {
            if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty()) {
                return false; // Title and author cannot be empty
            }
            parseDouble(priceField.getText(), "Price");
            parseInt(stockField.getText(), "Stock");
            parseInt(popularityField.getText(), "Popularity");
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Invalid field values
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
