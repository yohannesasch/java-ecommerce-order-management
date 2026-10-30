package com.yohannes.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithValidData() {
        Product product = new Product(
                "P001",
                "Laptop",
                "Dell laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );

        assertEquals("P001", product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(new BigDecimal("50000.00"), product.getPrice());
        assertEquals(10, product.getStockQuantity());
        assertTrue(product.isActive());

    }

    @Test
    void shouldRejectNullProductId() {
        assertThrows(NullPointerException.class,
                () -> new Product(
                        null,
                        "Laptop",
                        "Dell laptop",
                        ProductCategory.ELECTRONICS,
                        new BigDecimal("50000.00"),
                        10,
                        true
                )
        );
    }

    @Test
    void shouldRejectNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(
                        "P001",
                        "Laptop",
                        "Dell laptop",
                        ProductCategory.ELECTRONICS,
                        new BigDecimal("-100.00"),
                        10,
                        true
                )
        );
    }

    @Test
    void shouldRejectNegativeStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product(
                        "P001",
                        "Laptop",
                        "Dell laptop",
                        ProductCategory.ELECTRONICS,
                        new BigDecimal("50000.00"),
                        -1,
                        true
                )
        );
    }

    @Test
    void shouldReturnNewProductWhenPriceIsUpdated() {
        Product original = new Product(
                "P001",
                "Laptop",
                "Dell laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );

        Product updated = original.withPrice(new BigDecimal("55000.00"));

        assertEquals(new BigDecimal("50000.00"), original.getPrice());
        assertEquals(new BigDecimal("55000.00"), updated.getPrice());
        assertNotSame(original, updated);
    }
}
