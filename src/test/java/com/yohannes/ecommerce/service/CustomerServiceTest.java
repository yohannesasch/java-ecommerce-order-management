package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.repository.impl.InMemoryCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(
                new InMemoryCustomerRepository()
        );
    }

    @Test
    void shouldRegisterCustomer() {
        Customer customer = new Customer(
                "C001", "Yohannes", "yohannes@gmail.com", true
        );

        customerService.registerCustomer(customer);

        assertTrue(customerService.findCustomerById("C001").isPresent());
    }

    @Test
    void shouldRejectDuplicateCustomer() {
        Customer first = new Customer(
                "C001", "Yohannes", "yohannes@gmail.com", true
        );

        Customer second = new Customer(
                "C001", "Abreham", "abreham@gmail.com", true
        );

        customerService.registerCustomer(first);

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(second)
        );
    }

    @Test
    void shouldRejectDuplicateEmailIgnoringCase() {
        Customer first = new Customer(
                "C001", "Yohannes", "yohannes@gmail.com", true
        );

        Customer second = new Customer(
                "C002", "Birhan", "YOHANNES@gmail.com", true
        );

        customerService.registerCustomer(first);

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.registerCustomer(second)
        );
    }

    @Test
    void shouldReturnAllCustomers() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        customerService.registerCustomer(
                new Customer("C002", "Abebe", "abebe@example.com", true)
        );

        assertEquals(2, customerService.getAllCustomers().size());
    }

    @Test
    void shouldDeactivateCustomer() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

       customerService.deactivateCustomer("C001");

       Customer updated = customerService.findCustomerById("C001")
               .orElseThrow();

       assertFalse(updated.isActive());
    }

    @Test
    void shouldActivateCustomer() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        customerService.activateCustomer("C001");

        Customer updated = customerService.findCustomerById("C001")
                .orElseThrow();

        assertTrue(updated.isActive());
    }

    @Test
    void shouldPreserveRegistrationTimestampWhenUpdatingCustomer() {
        Customer customer = new Customer(
                "C001", "Dagim", "dagim@gmail.com", true
        );

        Instant originalRegistrationTime = customer.getRegisteredAt();

        customerService.registerCustomer(customer);
        customerService.deactivateCustomer("C001");

        Customer updated = customerService.findCustomerById("C001")
                .orElseThrow();

        assertEquals(originalRegistrationTime, updated.getRegisteredAt());
    }

    @Test
    void shouldRejectDeactivatingUnknownCustomer() {
        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.deactivateCustomer("UKNOWN")
        );
    }

    @Test
    void shouldUpdateCustomerName() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        customerService.updateCustomerName("C001", "Yohannes Aschalew");

        Customer updated = customerService.findCustomerById("C001")
                .orElseThrow();

        assertEquals("Yohannes Aschalew", updated.getName());
    }

    @Test
    void shouldUpdateCustomerEmail() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        customerService.updateCustomerEmail("C001", "newemail@example.com");

        Customer updated = customerService.findCustomerById("C001")
                .orElseThrow();

        assertEquals("newemail@example.com", updated.getEmail());
    }

    @Test
    void shouldRejectEmailAlreadyUsedByAnotherCustomer() {
        customerService.registerCustomer(
                new Customer("C001", "Yohannes", "yohannes@example.com", true)
        );

        customerService.registerCustomer(
                new Customer("C002", "Abebe", "abebe@example.com", true)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.updateCustomerEmail(
                        "C002", "yohannes@example.com"
                )
        );
    }

}
