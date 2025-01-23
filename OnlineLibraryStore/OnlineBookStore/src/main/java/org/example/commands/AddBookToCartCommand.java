package org.example.commands;

import org.example.models.Cart;
import org.example.models.CartItem;

public class AddBookToCartCommand implements CartCommand {
    private final Cart cart;
    private final CartItem item;

    public AddBookToCartCommand(Cart cart, CartItem item) {
        this.cart = cart;
        this.item = item;
    }

    @Override
    public void execute() {
        cart.addItem(item);
    }
}