package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.repository.ProductRepository;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {


    private final Map<String, Product> products = new HashMap<>();

    @Override
    public void save(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(String id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public boolean existsById(String id) {
        Objects.requireNonNull(id, "Product ID cannot be null");

        return products.containsKey(id);
    }

    @Override
    public void deleteById(String id) {
        Objects.requireNonNull(id, "Product ID cannot be null");

        products.remove(id);
    }

}
