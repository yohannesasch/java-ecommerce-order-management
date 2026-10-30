package com.yohannes.ecommerce.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {

    private String customerId;
    private final List<CartItem> items = new ArrayList<>();

    public ShoppingCart(String  customerId) {
        this.customerId = Objects.requireNonNull(
                customerId, "Customer ID cannot be null"
        );

        if (customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be blank"
            );
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public void addItem(Product product, int quantity) {
        Objects.requireNonNull(product, "Products cannot be null");

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot add an inactive product"
            );
        }

        CartItem newItem = new CartItem(product,quantity);

        for (int i = 0; i < items.size(); i++) {
            CartItem existing = items.get(i);

            if (existing.getProduct().getId().equalsIgnoreCase(product.getId())) {
                int updatedQuantity =
                        existing.getQuantity() + quantity;

                items.set(i, new CartItem(product, updatedQuantity));
                return;
            }
        }
        items.add(newItem);
    }

    public boolean removeItem(String prductId) {
        Objects.requireNonNull(prductId, "Product ID cannot be null");

        return items.removeIf(
                item -> item.getProduct().getId().equals(prductId)
        );
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal()  {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void updateQuantity(String productId, int newQuantity) {
        Objects.requireNonNull(productId, "Prduct ID cannot be null");

        if (newQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);

            if (item.getProduct().getId().equals(productId)) {
                items.set(i, new CartItem(item.getProduct(), newQuantity));
                return;
            }
        }

        throw new IllegalArgumentException(
                "Product not found in shopping cart: " + productId
        );
    }
}
