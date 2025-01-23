package org.example.commands;

import org.example.models.Cart;

public class UpdateQuantityCommand implements CartCommand {
    private final Cart cart;
    private final int bookId;
    private final int quantity;

    public UpdateQuantityCommand(Cart cart, int bookId, int quantity) {
        this.cart = cart;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        cart.updateQuantity(bookId, quantity);
    }
}