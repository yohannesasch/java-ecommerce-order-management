package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.ShoppingCart;
import com.yohannes.ecommerce.repository.CartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryCartRepository implements CartRepository {

    private final Map<String, ShoppingCart> carts = new HashMap<>();

    @Override
    public void save(ShoppingCart cart) {
        Objects.requireNonNull(cart, "Cart cannot be null");

        carts.put(cart.getCustomerId(), cart);
    }

    @Override
    public Optional<ShoppingCart> findByCustomerId(String customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        return Optional.ofNullable(carts.get(customerId));
    }

    @Override
    public boolean existsByCustomerId(String customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        return carts.containsKey(customerId);
    }

    @Override
    public void deleteByCustomerId(String customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        carts.remove(customerId);
    }
}
