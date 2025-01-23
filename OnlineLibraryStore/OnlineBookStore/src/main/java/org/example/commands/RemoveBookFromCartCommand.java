package org.example.commands;

import org.example.models.Cart;

public class RemoveBookFromCartCommand implements CartCommand {
    private final Cart cart;
    private final int bookId;

    public RemoveBookFromCartCommand(Cart cart, int bookId) {
        this.cart = cart;
        this.bookId = bookId;
    }

    @Override
    public void execute() {
        cart.removeItem(bookId);
    }
}