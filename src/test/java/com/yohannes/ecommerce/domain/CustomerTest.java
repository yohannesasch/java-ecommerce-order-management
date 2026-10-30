package com.yohannes.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithValidData() {
        Customer customer = new Customer(
                "C001",
                "Yohannes",
                "yohannes@gmail.com",
                true
        );

        assertEquals("C001", customer.getId());
        assertEquals("Yohannes", customer.getName());
        assertEquals("yohannes@gmail.com", customer.getEmail());
        assertTrue(customer.isActive());
    }

    @Test
    void shouldSetRegistrationTimestampWhenCustomerIsCreated() {
        Instant beforeCreation = Instant.now();

        Customer customer = new Customer(
                "C001",
                "Yohannes",
                "yohannes@gmail.com",
                true
        );

        Instant afterCreation = Instant.now();

        assertNotNull(customer.getRegisteredAt());
        assertFalse(customer.getRegisteredAt().isBefore(beforeCreation));
        assertFalse(customer.getRegisteredAt().isAfter(afterCreation));
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThrows(NullPointerException.class,
                () -> new Customer(
                        null,
                        "Yohannes",
                        "yohannes@example.com",
                        true
                )
        );
    }

    @Test
    void shouldRejectBlankCustomerName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(
                        "C001",
                        " ",
                        "yohannes@gmail.com",
                        true
                )
        );
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(
                        "C001",
                        "Yohannes",
                        "invalid-email",
                        true
                )
        );
    }
}
