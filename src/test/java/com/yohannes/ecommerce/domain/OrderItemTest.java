package com.yohannes.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWithValidData() {
        OrderItem item = new OrderItem(
                "P001",
                "Laptop",
                new BigDecimal("50000.00"),
                2
        );

        assertEquals("P001", item.getProductId());
        assertEquals("Laptop", item.getProductName());
        assertEquals(new BigDecimal("50000.00"), item.getUnitPrice());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        OrderItem item = new OrderItem(
                "P001",
                "Laptop",
                new BigDecimal("50000.00"),
                3
        );

        assertEquals(
                new BigDecimal("150000.00"),
                item.getSubtotal()
        );
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        "P001",
                        "Laptop",
                        new BigDecimal("50000.00"),
                        0
                )
        );
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        "P001",
                        "Laptop",
                        new BigDecimal("-500.00"),
                        1
                )
        );
    }

    @Test
    void shouldPreserveHistoricalPrice() {
        BigDecimal originalPrice = new BigDecimal("50000.00");

        OrderItem item = new OrderItem(
                "P001",
                "Laptop",
                originalPrice,
                2
        );


        BigDecimal newCatalogPrice = new BigDecimal("60000.00");

        assertEquals(originalPrice, item.getUnitPrice());
        assertNotEquals(newCatalogPrice, item.getUnitPrice());
    }
}