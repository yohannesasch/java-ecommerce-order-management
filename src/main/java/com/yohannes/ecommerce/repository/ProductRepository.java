package com.yohannes.ecommerce.repository;

import com.yohannes.ecommerce.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}
