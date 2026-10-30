package com.yohannes.ecommerce.domain;

import java.time.Instant;
import java.util.Objects;

public final class Customer implements Identifiable<String> {

    private final String id;
    private final String name;
    private final String email;
    private final boolean active;
    private final Instant registeredAt;

    public Customer(
            String id,
            String name,
            String email,
            boolean active
    ) {
        this.id = Objects.requireNonNull(id, "Customer Id cannot null");
        this.name = Objects.requireNonNull(name, "customer name cannot null");
        this.email = Objects.requireNonNull(email, "Customer email cannot null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot blank");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot blank");
        }

        if (email.isBlank() || !email.matches("^[A-Za-z0-9-_.]+@[A-Za-z0-9-.]+$")) {
            throw  new IllegalArgumentException("Invalid customer email");
        }

        this.active = active;
        this.registeredAt = Instant.now();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
