package com.yohannes.ecommerce.repository;

import com.yohannes.ecommerce.domain.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    void save(Customer customer);

    Optional<Customer> findById(String id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}
