package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCustomerRepositoryTest {

    private InMemoryCustomerRepository repository;
    private Customer customer;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCustomerRepository();

        customer = new Customer(
                "C001",
                "Yohannes",
                "yohannes@example.com",
                true
        );
    }

    @Test
    void shouldSaveAndFindCustomer() {
        repository.save(customer);

        Customer found = repository.findById("C001").orElseThrow();

        assertSame(customer, found);
    }

    @Test
    void shouldFindCustomerByEmailIgnoringCase() {
        repository.save(customer);

        Customer found = repository
                .findByEmail("YOHANNES@example.com")
                .orElseThrow();

        assertSame(customer, found);
    }

    @Test
    void shouldReturnEmptyForUnknownEmail() {
        assertTrue(repository.findByEmail("unknown@example.com").isEmpty());
    }

    @Test
    void shouldCheckWhetherCustomerExists() {
        assertFalse(repository.existsById("C001"));

        repository.save(customer);

        assertTrue(repository.existsById("C001"));
    }

    @Test
    void shouldDeleteCustomer() {
        repository.save(customer);

        repository.deleteById("C001");

        assertTrue(repository.findById("C001").isEmpty());
        assertFalse(repository.existsById("C001"));
    }

    @Test
    void shouldReturnUnmodifiableCustomerList() {
        repository.save(customer);

        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.findAll().clear()
        );

        assertEquals(1, repository.findAll().size());
    }
}