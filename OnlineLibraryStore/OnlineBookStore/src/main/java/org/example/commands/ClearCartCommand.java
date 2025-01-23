package org.example.commands;

import org.example.models.Cart;

public class ClearCartCommand implements CartCommand {
    private final Cart cart;

    public ClearCartCommand(Cart cart) {
        this.cart = cart;
    }

    @Override
    public void execute() {
        cart.clear();
    }
}