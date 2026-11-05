package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.repository.CustomerRepository;

import java.util.*;

public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<String, Customer> customers = new HashMap<>();

    @Override
    public void save(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");

        customers.put(customer.getId(), customer);
    }

    @Override
    public Optional<Customer> findById(String id) {
        Objects.requireNonNull(id, "Customer ID cannot be null");

        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        Objects.requireNonNull(email, "Customer email cannot null");

        return customers.values()
                .stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Customer> findAll() {
        return List.copyOf(customers.values());
    }

    @Override
    public boolean existsById(String id) {
        Objects.requireNonNull(id, "Customer ID cannot be null");

        return customers.containsKey(id);
    }

    @Override
    public void deleteById(String id) {
        Objects.requireNonNull(id, "Customer ID cannot be null");

        customers.remove(id);
    }
}
