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
        this(id, name, email, active, Instant.now());
    }

    public Customer(
            String id,
            String name,
            String email,
            boolean active,
            Instant registeredAt
    ) {
        Objects.requireNonNull(id, "Customer ID cannot be null");
        Objects.requireNonNull(name, "Customer name cannot be null");
        Objects.requireNonNull(email, "Customer email cannot be null");
        Objects.requireNonNull(registeredAt, "Registration timestamp cannot be null");


        if (id.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot blank");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot blank");
        }

        if (email.isBlank() || !email.matches("^[A-Za-z0-9-_.]+@[A-Za-z0-9-.]+$")) {
            throw  new IllegalArgumentException("Invalid customer email");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.registeredAt = registeredAt;
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

    public Customer withName(String newName) {
        return new Customer(
                id,
                newName,
                email,
                active,
                registeredAt
        );
    }

    public Customer withEmail(String  newEmail) {
        return new Customer(
                id,
                name,
                newEmail,
                active,
                registeredAt
        );
    }

    public Customer withActive(boolean newActiveStatus) {
        return new Customer(
                id,
                name,
                email,
                newActiveStatus,
                registeredAt
        );
    }

}
