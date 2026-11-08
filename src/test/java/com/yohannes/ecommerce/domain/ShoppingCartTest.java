package com.yohannes.ecommerce.domain;

import com.yohannes.ecommerce.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    private ShoppingCart cart;
    private Product laptop;
    private Product phone;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart("C001");

        laptop = new Product(
                "P001",
                "Laptop",
                "Dell laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );

        phone = new Product(
                "P002",
                "Phone",
                "Samsung phone",
                ProductCategory.ELECTRONICS,
                new BigDecimal("20000.00"),
                15,
                true
        );
    }

    @Test
    void shouldAddProductToCart() {
        cart.addItem(laptop, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldIncreaseQuantityWhenSameProductIsAddedAgain() {
        cart.addItem(laptop, 2);
        cart.addItem(laptop, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldUpdateExistingItemQuantity() {
        cart.addItem(laptop, 2);

        cart.updateQuantity("P001", 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRemoveProductFromCart() {
        cart.addItem(laptop, 2);

        boolean removed = cart.removeItem("P001");

        assertTrue(removed);
        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldCalculateCartTotalCorrectly() {
        cart.addItem(laptop, 2);
        cart.addItem(phone, 1);

        assertEquals(
                new BigDecimal("120000.00"),
                cart.getTotal()
        );
    }

    @Test
    void shouldRejectInactiveProduct() {
        Product inactiveProduct = new Product(
                "P003",
                "Old Laptop",
                "Inactive product",
                ProductCategory.ELECTRONICS,
                new BigDecimal("30000.00"),
                5,
                false
        );

        assertThrows(BusinessRuleException.class, () ->
                cart.addItem(inactiveProduct, 1)
        );
    }

    @Test
    void shouldClearAllItems() {
        cart.addItem(laptop, 2);
        cart.addItem(phone, 1);

        cart.clear();

        assertTrue(cart.isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }
}