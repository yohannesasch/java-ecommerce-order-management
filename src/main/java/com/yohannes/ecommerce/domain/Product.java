package com.yohannes.ecommerce.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product implements Identifiable<String> {

    private final String id;
    private final String name;
    private final String description;
    private final ProductCategory category;
    private final BigDecimal price;
    private final int stockQuantity;
    private final boolean active;

    public Product(
            String id,
            String  name,
            String description,
            ProductCategory category,
            BigDecimal price,
            int stockQuantity,
            boolean active
    ) {
        this.id = Objects.requireNonNull(id, "Product ID cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }

        this.name = Objects.requireNonNull(name, "Product name cannot be null");

        if (name.isBlank()) {
            throw  new IllegalArgumentException("Product name cannot be blank");
        }

        this.description = description == null ? "" : description;

        this.category = Objects.requireNonNull(
                category, "Product category cannot be null"
        );

        this.price = Objects.requireNonNull(price, "Product price cannot be null");

        if (price.signum() <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        this.stockQuantity = stockQuantity;

        this.active = active;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public Product withPrice(BigDecimal newPrice) {
        return new Product(
                id,
                name,
                description,
                category,
                newPrice,
                stockQuantity,
                active
        );
    }

    public Product withStockQuantity(int newStockQuantity) {
        return new Product(
                id,
                name,
                description,
                category,
                price,
                newStockQuantity,
                active
        );
    }

    public Product withActive(boolean newActiveStatus) {
        return new Product(
                id,
                name,
                description,
                category,
                price,
                stockQuantity,
                newActiveStatus
        );
    }
}
