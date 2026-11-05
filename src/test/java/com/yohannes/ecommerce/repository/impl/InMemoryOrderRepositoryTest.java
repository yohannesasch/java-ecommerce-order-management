package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Order;
import com.yohannes.ecommerce.domain.OrderItem;
import com.yohannes.ecommerce.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();

        OrderItem laptop = new OrderItem(
                "P001",
                "Laptop",
                new BigDecimal("50000.00"),
                1
        );

        OrderItem phone = new OrderItem(
                "P002",
                "Phone",
                new BigDecimal("20000.00"),
                2
        );

        order1 = new Order("O001", "C001", List.of(laptop));
        order2 = new Order("O002", "C002", List.of(phone));
    }

    @Test
    void shouldSaveAndFindOrder() {
        repository.save(order1);

        Order found = repository.findById("O001").orElseThrow();

        assertSame(order1, found);
    }

    @Test
    void shouldReturnEmptyForNonexistentOrder() {
        assertTrue(repository.findById("O999").isEmpty());
    }

    @Test
    void shouldFindOrdersByCustomerId() {
        repository.save(order1);
        repository.save(order2);

        List<Order> customerOrders =
                repository.findByCustomerId("C001");

        assertEquals(1, customerOrders.size());
        assertEquals("O001", customerOrders.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListForCustomerWithoutOrders() {
        repository.save(order1);

        assertTrue(repository.findByCustomerId("C999").isEmpty());
    }

    @Test
    void shouldCheckWhetherOrderExists() {
        assertFalse(repository.existsById("O001"));

        repository.save(order1);

        assertTrue(repository.existsById("O001"));
    }

    @Test
    void shouldReturnUnmodifiableList() {
        repository.save(order1);

        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.findAll().clear()
        );

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldPreserveOrderStatusWhenSaved() {
        order1.confirm();
        repository.save(order1);

        assertEquals(
                OrderStatus.CONFIRMED,
                repository.findById("O001").orElseThrow().getStatus()
        );
    }
}