package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryProductRepositoryTest {

    private InMemoryProductRepository repository;
    private Product product;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();

        product = new Product(
                "P001",
                "Laptop",
                "Dell laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );
    }

    @Test
    void shouldSaveAndFindProduct() {
        repository.save(product);

        Product found = repository.findById("P001").orElseThrow();

        assertEquals(product, found);
    }

    @Test
    void shouldReturnEmptyWhenProductDoesNotExist() {
        assertTrue(repository.findById("P999").isEmpty());
    }

    @Test
    void shouldUpdateExistingProduct() {
        repository.save(product);

        Product updated = product.withPrice(new BigDecimal("55000.00"));
        repository.save(updated);

        assertEquals(
                new BigDecimal("55000.00"),
                repository.findById("P001").orElseThrow().getPrice()
        );

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldCheckWhetherProductExists() {
        assertFalse(repository.existsById("P001"));

        repository.save(product);

        assertTrue(repository.existsById("P001"));
    }

    @Test
    void shouldDeleteProduct() {
        repository.save(product);

        repository.deleteById("P001");

        assertFalse(repository.existsById("P001"));
        assertTrue(repository.findById("P001").isEmpty());
    }

    @Test
    void shouldReturnUnmodifiableListFromFindAll() {
        repository.save(product);

        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.findAll().clear()
        );

        assertEquals(1, repository.findAll().size());
    }

}
