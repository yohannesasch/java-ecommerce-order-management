package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.repository.CartRepository;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Objects;

public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = Objects.requireNonNull(cartRepository);
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    public ShoppingCart getCart(String customerId) {
        validateCustomer(customerId);

        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart(customerId);
                    cartRepository.save(cart);
                    return cart;
                });
    }

    public void addProduct(String customerId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Customer customer = validateCustomer(customerId);

        if (!customer.isActive()) {
            throw new IllegalArgumentException("Customer account is inactive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId
                ));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is inactive");
        }

        ShoppingCart cart = getCart(customerId);
        cart.addItem(product, quantity);

        cartRepository.save(cart);
    }

    public void removeProduct(String customerId, String productId) {
        ShoppingCart cart = getCart(customerId);

        boolean removed = cart.removeItem(productId);

        if (!removed) {
            throw new IllegalArgumentException(
                    "Product is not in the cart: " + productId
            );
        }

        cartRepository.save(cart);
    }

    public void updateProductQuantity(
            String customerId,
            String productId,
            int newQuantity
    ) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = getCart(customerId);

        cart.updateQuantity(productId, newQuantity);

        cartRepository.save(cart);
    }

    public BigDecimal calculateTotal(String customerId) {
        return getCart(customerId).getTotal();
    }

    public void clearCart(String customerId) {
        ShoppingCart cart = getCart(customerId);
        cart.clear();

        cartRepository.save(cart);
    }


    private Customer validateCustomer(String customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + customerId
                ));
    }
}
