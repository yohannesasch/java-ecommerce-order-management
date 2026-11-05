package com.yohannes.ecommerce.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class Order implements Identifiable<String> {

    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private final BigDecimal totalAmount;
    private OrderStatus status;

    public Order(
            String id,
            String customerId,
            List<OrderItem> items) {

        Objects.requireNonNull(id, "Order ID cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(items, "Order items cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be blank");
        }

        if (customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = id;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.createdAt = Instant.now();
        this.totalAmount = calculateTotal(items);
        this.status = OrderStatus.PLACED;
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }






    public void confirm() {
        requiredStatus(OrderStatus.PLACED);
        status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        requiredStatus(OrderStatus.CONFIRMED);
        status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        requiredStatus(OrderStatus.SHIPPED);
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED
                || status == OrderStatus.DELIVERED
                || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order cannot ve cancelled in status: " + status
            );
        }

        status = OrderStatus.CANCELLED;
    }

    private void requiredStatus(OrderStatus expectedStatus) {
        if (status != expectedStatus) {
            throw new IllegalStateException(
                    "Expected status " + expectedStatus
                    + " but current status is " + status
            );
        }
    }
}