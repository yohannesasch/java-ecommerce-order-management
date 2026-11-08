package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ProductCategory;
import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.exception.BusinessRuleException;
import com.yohannes.ecommerce.exception.ResourceNotFoundException;
import com.yohannes.ecommerce.exception.ValidationException;
import com.yohannes.ecommerce.repository.impl.InMemoryCartRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryCustomerRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryProductRepository;
import com.yohannes.ecommerce.validation.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CartServiceTest {

    private CartService cartService;
    private InMemoryCustomerRepository customerRepository;
    private InMemoryProductRepository productRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        productRepository = new InMemoryProductRepository();

        cartService = new CartService(
                new InMemoryCartRepository(),
                customerRepository,
                productRepository
        );

        customerRepository.save(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        productRepository.save(
                new Product(
                        "P001",
                        "Laptop",
                        "Gaming laptop",
                        ProductCategory.ELECTRONICS,
                        new BigDecimal("50000.00"),
                        10,
                        true
                )
        );
    }

    @Test
    void shouldCreateCartForExistingCustomer() {
        ShoppingCart cart = cartService.getCart("C001");

        assertEquals("C001", cart.getCustomerId());
        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldAddProductToCart() {
        cartService.addProduct("C001", "P001", 2);

        ShoppingCart cart = cartService.getCart("C001");

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRejectUnknownCustomer() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getCart("UNKNOWN")
        );
    }

    @Test
    void shouldRejectUnknownProduct() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.addProduct("C001", "UNKNOWN", 1)
        );
    }

    @Test
    void shouldRejectInactiveCustomer() {
        customerRepository.save(
                new Customer("C002", "Abebe", "abebe@example.com", false)
        );

        assertThrows(
                BusinessRuleException.class,
                () -> cartService.addProduct("C002", "P001", 1)
        );
    }

    @Test
    void shouldRejectInactiveProduct() {
        productRepository.save(
                new Product(
                        "P002",
                        "Old Laptop",
                        "Discontinued laptop",
                        ProductCategory.ELECTRONICS,
                        new BigDecimal("30000.00"),
                        5,
                        false
                )
        );

        assertThrows(
                BusinessRuleException.class,
                () -> cartService.addProduct("C001", "P002", 1)
        );
    }

    @Test
    void shouldCalculateCartTotal() {
        cartService.addProduct("C001", "P001", 2);

        assertEquals(
                0,
                new BigDecimal("100000.00")
                        .compareTo(cartService.calculateTotal("C001"))
        );
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThrows(
                ValidationException.class,
                () -> cartService.addProduct("C001", "P001", 0)
        );
    }

    @Test
    void shouldRemoveProductFromCart() {
        cartService.addProduct("C001", "P001", 2);

        cartService.removeProduct("C001", "P001");

        ShoppingCart cart = cartService.getCart("C001");

        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldUpdateProductQuantity() {
        cartService.addProduct("C001", "P001", 2);

        cartService.updateProductQuantity("C001", "P001", 5);

        ShoppingCart cart = cartService.getCart("C001");

        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRejectUpdatingProductNotInCart() {
        assertThrows(
                BusinessRuleException.class,
                () -> cartService.updateProductQuantity("C001", "P001", 3)
        );
    }

    @Test
    void shouldClearCart() {
        cartService.addProduct("C001", "P001", 2);

        cartService.clearCart("C001");

        ShoppingCart cart = cartService.getCart("C001");

        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotal().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldMergeSameProductIntoOneCartItem() {
        cartService.addProduct("C001", "P001", 2);
        cartService.addProduct("C001", "P001", 3);

        ShoppingCart cart = cartService.getCart("C001");

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRejectRemovingProductNotInCart() {
        assertThrows(
                BusinessRuleException.class,
                () -> cartService.removeProduct("C001", "P001")
        );
    }

    @Test
    void shouldRejectNonPositiveUpdatedQuantity() {
        cartService.addProduct("C001", "P001", 2);

        assertThrows(
                ValidationException.class,
                () -> cartService.updateProductQuantity("C001", "P001", 0)
        );
    }

}
