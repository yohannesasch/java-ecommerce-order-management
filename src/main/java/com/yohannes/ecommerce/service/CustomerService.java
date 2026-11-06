package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.repository.CustomerRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(
                customerRepository,
                "Customer repository cannot be null"
        );
    }

    public void registerCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");

        if (customerRepository.existsById(customer.getId())) {
            throw new IllegalArgumentException(
                    "Customer ID already exists: " + customer.getId()
            );
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Customer email already exists: " + customer.getId()
            );
        }

        customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void deactivateCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + customerId
                ));
        customerRepository.save(customer.withActive(false));
    }

    public void activateCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + customerId
                ));
        customerRepository.save(customer.withActive(true));
    }

    public void updateCustomerName(String cutomerId, String newName) {
        Customer customer = customerRepository.findById(cutomerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + cutomerId
                ));

        customerRepository.save(customer.withName(newName));
    }

    public void updateCustomerEmail(String customerId, String newEmail) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found:" + customerId
                ));

        customerRepository.findByEmail(newEmail).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(customerId)) {
                throw new IllegalArgumentException(
                        "Customer email already exists: " + newEmail
                );
            }
        });

        customerRepository.save(customer.withEmail(newEmail));
    }
}
