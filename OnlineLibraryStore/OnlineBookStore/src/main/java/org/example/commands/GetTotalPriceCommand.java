package org.example.commands;

import org.example.models.Cart;

public class GetTotalPriceCommand implements CartCommand {
    private final Cart cart;

    public GetTotalPriceCommand(Cart cart) {
        this.cart = cart;
    }

    @Override
    public void execute() {
        double totalPrice = cart.getTotalPrice();
        System.out.println("Total Price: " + totalPrice);
    }
}
