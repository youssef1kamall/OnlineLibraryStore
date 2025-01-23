package org.example.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.models.Book;

public class BookAddDialog extends Dialog<Book> {

    public BookAddDialog() {
        setTitle("Add New Book");
        setHeaderText("Enter book details:");

        // Create form fields
        TextField titleField = createTextField("Title");
        TextField authorField = createTextField("Author");
        TextField priceField = createTextField("Price");
        TextField stockField = createTextField("Stock");
        TextField categoryField = createTextField("Category");
        TextField popularityField = createTextField("Popularity");
        TextField editionField = createTextField("Edition");
        TextField coverImageField = createTextField("Cover Image URL");

        // Add buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Layout
        VBox vbox = new VBox(10, titleField, authorField, priceField, stockField, categoryField, popularityField, editionField, coverImageField);
        vbox.setPrefWidth(400); // Optional: Set a preferred width for better alignment
        getDialogPane().setContent(vbox);

        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return createBookFromForm(titleField, authorField, priceField, stockField, categoryField, popularityField, editionField, coverImageField);
            }
            return null;
        });
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Book createBookFromForm(TextField titleField, TextField authorField, TextField priceField, TextField stockField,
                                    TextField categoryField, TextField popularityField, TextField editionField, TextField coverImageField) {
        try {
            double price = parseDouble(priceField.getText(), "Price");
            int stock = parseInt(stockField.getText(), "Stock");
            int popularity = parseInt(popularityField.getText(), "Popularity");

            return new Book(
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
