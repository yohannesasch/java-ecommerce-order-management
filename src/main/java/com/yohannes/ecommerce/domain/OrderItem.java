package com.yohannes.ecommerce.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class OrderItem {

    private final String productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(
            String productId,
            String productName,
            BigDecimal unitPrice,
            int quantity) {

        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");

        if (productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }

        if (productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }

        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}