package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.exception.DuplicateResourceException;
import com.yohannes.ecommerce.exception.ResourceNotFoundException;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.validation.ValidationUtils;

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
        ValidationUtils.requireNotNull(customer, "Customer");

        if (customerRepository.existsById(customer.getId())) {
            throw new DuplicateResourceException(
                    "Customer ID already exists: " + customer.getId()
            );
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                    "Customer email already exists: " + customer.getId()
            );
        }
        ValidationUtils.requireValidEmail(customer.getEmail());

        customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerById(String customerId) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");
        return customerRepository.findById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void deactivateCustomer(String customerId) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + customerId
                ));
        customerRepository.save(customer.withActive(false));
    }

    public void activateCustomer(String customerId) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + customerId
                ));
        customerRepository.save(customer.withActive(true));
    }

    public void updateCustomerName(String customerId, String newName) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");
        ValidationUtils.requireNotBlank(newName, "Customer name");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + customerId
                ));

        customerRepository.save(customer.withName(newName));
    }

    public void updateCustomerEmail(String customerId, String newEmail) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");
        ValidationUtils.requireValidEmail(newEmail);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found:" + customerId
                ));

        customerRepository.findByEmail(newEmail).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(customerId)) {
                throw new DuplicateResourceException(
                        "Customer email already exists: " + newEmail
                );
            }
        });

        customerRepository.save(customer.withEmail(newEmail));
    }
}
