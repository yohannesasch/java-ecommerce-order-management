package com.yohannes.ecommerce.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private OrderItem laptop;
    private OrderItem phone;

    @BeforeEach
    void setUp() {
        laptop = new OrderItem(
                "P001",
                "Laptop",
                new BigDecimal("50000.00"),
                2
        );

        phone = new OrderItem(
                "P002",
                "Phone",
                new BigDecimal("20000.00"),
                1
        );

        order = new Order(
                "O001",
                "C001",
                List.of(laptop, phone)
        );
    }

    @Test
    void shouldCreateOrderWithPlacedStatus() {
        assertEquals("O001", order.getId());
        assertEquals("C001", order.getCustomerId());
        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        assertEquals(
                new BigDecimal("120000.00"),
                order.getTotalAmount()
        );
    }

    @Test
    void shouldReturnUnmodifiableItemsList() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> order.getItems().clear()
        );
    }

    @Test
    void shouldConfirmPlacedOrder() {
        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldShipConfirmedOrder() {
        order.confirm();
        order.ship();

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void shouldDeliverShippedOrder() {
        order.confirm();
        order.ship();
        order.deliver();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldCancelPlacedOrder() {
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldCancelConfirmedOrder() {
        order.confirm();
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldRejectShippingPlacedOrder() {
        assertThrows(
                IllegalStateException.class,
                () -> order.ship()
        );
    }

    @Test
    void shouldRejectCancellationAfterShipment() {
        order.confirm();
        order.ship();

        assertThrows(
                IllegalStateException.class,
                () -> order.cancel()
        );
    }

    @Test
    void shouldRejectCancellationAfterDelivery() {
        order.confirm();
        order.ship();
        order.deliver();

        assertThrows(
                IllegalStateException.class,
                () -> order.cancel()
        );
    }

    @Test
    void shouldRejectEmptyOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order("O002", "C001", List.of())
        );
    }
}