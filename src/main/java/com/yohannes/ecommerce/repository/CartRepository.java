package com.yohannes.ecommerce.repository;

import com.yohannes.ecommerce.domain.ShoppingCart;

import java.util.Optional;

public interface CartRepository {

    void save(ShoppingCart cart);

    Optional<ShoppingCart> findByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);

    void deleteByCustomerId(String customerId);
}
