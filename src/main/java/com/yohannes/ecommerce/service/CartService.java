package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.exception.BusinessRuleException;
import com.yohannes.ecommerce.exception.ResourceNotFoundException;
import com.yohannes.ecommerce.repository.CartRepository;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.repository.ProductRepository;
import com.yohannes.ecommerce.validation.ValidationUtils;

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

        ValidationUtils.requireNotBlank(productId, "Product ID");
        ValidationUtils.requirePositive(quantity, "Quantity");

        Customer customer = validateCustomer(customerId);

        if (!customer.isActive()) {
            throw new BusinessRuleException("Customer account is inactive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId
                ));

        if (!product.isActive()) {
            throw new BusinessRuleException("Product is inactive");
        }

        ShoppingCart cart = getCart(customerId);
        cart.addItem(product, quantity);

        cartRepository.save(cart);
    }

    public void removeProduct(String customerId, String productId) {

        ValidationUtils.requireNotBlank(productId, "Product ID");

        ShoppingCart cart = getCart(customerId);

        boolean removed = cart.removeItem(productId);

        if (!removed) {
            throw new BusinessRuleException(
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
        ValidationUtils.requirePositive(newQuantity, "Quantity");
        ValidationUtils.requireNotBlank(productId, "Product ID");

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

        ValidationUtils.requireNotBlank(customerId, "Customer ID");

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + customerId
                ));
    }
}
