package org.example.services;

import org.example.commands.*;
import org.example.models.Cart;
import org.example.models.CartItem;

import java.util.List;

public class CartService {
    private final CommandInvoker commandInvoker;

    public CartService() {
        this.commandInvoker = new CommandInvoker();
    }

    public void addBookToCart(String userId, int bookId, String bookName, int quantity, double price) {
        Cart cart = Cart.getInstance(userId);
        CartItem newItem = new CartItem(bookId, bookName, quantity, price);
        AddBookToCartCommand command = new AddBookToCartCommand(cart, newItem);
        commandInvoker.executeCommand(command);
    }

    public void removeBookFromCart(String userId, int bookId) {
        Cart cart = Cart.getInstance(userId);
        RemoveBookFromCartCommand command = new RemoveBookFromCartCommand(cart, bookId);
        commandInvoker.executeCommand(command);
    }

    public void updateQuantity(String userId, int bookId, int quantity) {
        Cart cart = Cart.getInstance(userId);
        UpdateQuantityCommand command = new UpdateQuantityCommand(cart, bookId, quantity);
        commandInvoker.executeCommand(command);
    }

    public List<CartItem> getCartItems(String userId) {
        Cart cart = Cart.getInstance(userId);
        return cart.getItems();
    }

    public void clearCart(String userId) {
        Cart cart = Cart.getInstance(userId);
        ClearCartCommand command = new ClearCartCommand(cart);
        commandInvoker.executeCommand(command);
    }

    public double calculateTotal(String userId) {
        Cart cart = Cart.getInstance(userId);
        return cart.getTotalPrice();
    }
}