package com.yohannes.ecommerce.repository;

import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.repository.impl.InMemoryCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartRepositoryTest {

    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository = new InMemoryCartRepository();
    }

    @Test
    void shouldSaveAndFindCartByCustomerId() {
        ShoppingCart cart = new ShoppingCart("C001");

        cartRepository.save(cart);

        ShoppingCart found = cartRepository.findByCustomerId("C001")
                .orElseThrow();

        assertEquals("C001", found.getCustomerId());
    }

    @Test
    void shouldReturnEmptyWhenCartDoesNotExist() {
        assertTrue(
                cartRepository.findByCustomerId("UNKNOWN").isEmpty()
        );
    }

    @Test
    void shouldCheckWhetherCartExists() {
        ShoppingCart cart = new ShoppingCart("C001");

        cartRepository.save(cart);

        assertTrue(cartRepository.existsByCustomerId("C001"));
        assertFalse(cartRepository.existsByCustomerId("C002"));
    }

    @Test
    void shouldDeleteCart() {
        ShoppingCart cart = new ShoppingCart("C001");

        cartRepository.save(cart);
        cartRepository.deleteByCustomerId("C001");

        assertFalse(cartRepository.existsByCustomerId("C001"));
    }

    @Test
    void shouldReplaceExistingCartForSameCustomer() {
        ShoppingCart firstCart = new ShoppingCart("C001");
        ShoppingCart secondCart = new ShoppingCart("C001");

        cartRepository.save(firstCart);
        cartRepository.save(secondCart);

        ShoppingCart found = cartRepository.findByCustomerId("C001")
                .orElseThrow();

        assertSame(secondCart, found);
    }

    @Test
    void shouldRejectNullCart() {
        assertThrows(
                NullPointerException.class,
                () -> cartRepository.save(null)
        );
    }
}