package com.yohannes.ecommerce.domain;

import java.util.Objects;

public final class CartItem {

    private final Product product;
    private final int quantity;

    public CartItem(
            Product product,
            int quantity
    ) {
        this.product = Objects.requireNonNull(
                product, "Procuct cannot be null"
        );

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
