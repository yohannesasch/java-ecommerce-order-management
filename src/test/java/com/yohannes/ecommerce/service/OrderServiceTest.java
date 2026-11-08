package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.*;
import com.yohannes.ecommerce.exception.BusinessRuleException;
import com.yohannes.ecommerce.exception.InsufficientStockException;
import com.yohannes.ecommerce.repository.CartRepository;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.repository.OrderRepository;
import com.yohannes.ecommerce.repository.ProductRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryCartRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryCustomerRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryOrderRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;

    private CartService cartService;
    private OrderService orderService;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        customerRepository = new InMemoryCustomerRepository();
        cartRepository = new InMemoryCartRepository();
        orderRepository = new InMemoryOrderRepository();

        cartService = new CartService(
                cartRepository,
                customerRepository,
                productRepository
        );

        orderService = new OrderService(
                orderRepository,
                productRepository,
                customerRepository,
                cartRepository
        );

        customer = new Customer(
                "C001", "Yohannes", "yohannes@example.com", true
        );

        product = new Product(
                "P001",
                "Laptop",
                "Development laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"),
                10,
                true
        );

        customerRepository.save(customer);
        productRepository.save(product);
    }

    @Test
    void shouldPlaceOrderSuccessfully() {

        cartService.addProduct(customer.getId(), product.getId(),2);

        Order order = orderService.placeOrder(customer.getId());

        assertNotNull(order);
        assertEquals(customer.getId(), order.getCustomerId());
        assertEquals(OrderStatus.PLACED, order.getStatus());

        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());

        assertEquals(
                new BigDecimal("100000.00"),
                order.getTotalAmount()
        );

        Product updatedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(8, updatedProduct.getStockQuantity());

        ShoppingCart cart = cartRepository
                .findByCustomerId(customer.getId())
                .orElseThrow();

        assertTrue(cart.isEmpty());

        assertTrue(orderRepository.existsById(order.getId()));
    }

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() {

        cartService.addProduct(customer.getId(), product.getId(), 11);

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(customer.getId())
        );

        Product unchangedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(10, unchangedProduct.getStockQuantity());

        ShoppingCart cart = cartRepository
                .findByCustomerId(customer.getId())
                .orElseThrow();

        assertFalse(cart.isEmpty());
        assertEquals(11, cart.getItems().get(0).getQuantity());

        assertEquals(0, orderRepository.findAll().size());
    }

    @Test
    void shouldRejectOrderWhenCustomerIsInactive() {

        cartService.addProduct(customer.getId(), product.getId(), 2);

        customerRepository.save(customer.withActive(false));

        assertThrows(
                BusinessRuleException.class,
                () -> orderService.placeOrder(customer.getId())
        );

        Product unchangedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(10, unchangedProduct.getStockQuantity());

        ShoppingCart cart = cartRepository
                .findByCustomerId(customer.getId())
                .orElseThrow();

        assertFalse(cart.isEmpty());

        assertEquals(0, orderRepository.findAll().size());
    }

    @Test
    void shouldRestoreInventoryWhenOrderIsCancelled() {

        cartService.addProduct(customer.getId(), product.getId(), 3);

        Order order = orderService.placeOrder(customer.getId());

        assertEquals(
                7,
                productRepository.findById(product.getId())
                        .orElseThrow()
                        .getStockQuantity()
        );

        Order cancelledOrder = orderService.cancelOrder(order.getId());

        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());

        Product restoredProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(10, restoredProduct.getStockQuantity());
    }

    @Test
    void shouldNotRestoreInvertoryTwiceWhenOrderIsCancelledAgain() {

        cartService.addProduct(customer.getId(), product.getId(), 3);

        Order order = orderService.placeOrder(customer.getId());

        orderService.cancelOrder(order.getId());

        assertThrows(
                BusinessRuleException.class,
                () -> orderService.cancelOrder(order.getId())
        );

        Product finalProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(10, finalProduct.getStockQuantity());
    }

    @Test
    void shouldRejectCancellationOfShippedOrder() {
        cartService.addProduct(customer.getId(), product.getId(), 2);

        Order order = orderService.placeOrder(customer.getId());

        orderService.confirmOrder(order.getId());
        orderService.shipOrder(order.getId());

        assertThrows(
                BusinessRuleException.class,
                () -> orderService.cancelOrder(order.getId())
        );

        Product unchangedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(8, unchangedProduct.getStockQuantity());

        assertEquals(
                OrderStatus.SHIPPED,
                orderRepository.findById(order.getId())
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void shouldCompleteOrderLifecycleSuccessfully() {
        cartService.addProduct(customer.getId(), product.getId(), 2);

        Order order = orderService.placeOrder(customer.getId());
        orderService.confirmOrder(order.getId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        orderService.shipOrder(order.getId());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        orderService.deliverOrder(order.getId());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        assertThrows(
                BusinessRuleException.class,
                () -> orderService.cancelOrder(order.getId())
        );

        Product updatedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(8, updatedProduct.getStockQuantity());
    }

    @Test
    void shouldFindOrderById() {
        cartService.addProduct(customer.getId(), product.getId(), 2);

        Order createdOrder = orderService.placeOrder(customer.getId());

        Order foundOrder = orderService.findOrderById(createdOrder.getId());

        assertEquals(createdOrder.getId(), foundOrder.getId());
        assertEquals(customer.getId(), foundOrder.getCustomerId());
    }

    @Test
    void shouldGetAllOrdersForCustomer() {
        cartService.addProduct(customer.getId(), product.getId(), 2);
        orderService.placeOrder(customer.getId());

        cartService.addProduct(customer.getId(), product.getId(), 1);
        orderService.placeOrder(customer.getId());

        var customerOrders = orderService.getCustomerOrders(customer.getId());

        assertEquals(2, customerOrders.size());
    }


}
