package com.yohannes.ecommerce.reporting;

import com.yohannes.ecommerce.domain.Order;
import com.yohannes.ecommerce.domain.OrderItem;
import com.yohannes.ecommerce.domain.OrderStatus;
import com.yohannes.ecommerce.repository.impl.InMemoryCustomerRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryOrderRepository;
import com.yohannes.ecommerce.repository.impl.InMemoryProductRepository;
import com.yohannes.ecommerce.domain.Customer;
import com.yohannes.ecommerce.domain.Product;
import com.yohannes.ecommerce.domain.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;


import static org.junit.jupiter.api.Assertions.*;

class ReportingServiceTest {


    private InMemoryOrderRepository orderRepository;
    private InMemoryProductRepository productRepository;
    private InMemoryCustomerRepository customerRepository;

    private ReportingService reportingService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        productRepository = new InMemoryProductRepository();
        customerRepository = new InMemoryCustomerRepository();

        reportingService = new ReportingService(
                orderRepository,
                customerRepository,
                productRepository

        );
    }

    @Test
    void calculateTotalRevenueShouldExcludeCancelledOrders() {
        Order order1 = new Order(
                "O001",
                "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 2
                ))
        );

        Order order2 = new Order(
                "O002",
                "C002",
                List.of(new OrderItem(
                        "P002", "Mouse", new BigDecimal("50.00"), 1
                ))
        );

        Order cancelledOrder = new Order(
                "O003",
                "C001",
                List.of(new OrderItem(
                        "P003", "Keyboard", new BigDecimal("1000.00"), 1
                ))
        );

        cancelledOrder.cancel();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(cancelledOrder);

        BigDecimal revenue = reportingService.calculateTotalRevenue();

        assertEquals(new BigDecimal("250.00"), revenue);
    }

    @Test
    void calculateTotalRevenueShouldReturnZeroWhenNoOrdersExist() {
        BigDecimal revenue = reportingService.calculateTotalRevenue();

        assertEquals(BigDecimal.ZERO, revenue);
    }

    @Test
    void countTotalOrdersShouldIncludeCancelledOrders() {

        Order order1 = new Order(
                "O001",
                "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 2
                ))
        );

        Order order2 = new Order(
                "O002",
                "C002",
                List.of(new OrderItem(
                        "P002", "Mouse", new BigDecimal("50.00"), 1
                ))
        );

        Order cancelledOrder = new Order(
                "O003",
                "C001",
                List.of(new OrderItem(
                        "P003", "Keyboard", new BigDecimal("1000.00"), 1
                ))
        );

        cancelledOrder.cancel();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(cancelledOrder);

        assertEquals(3, reportingService.countTotalOrders());
    }

    @Test
    void countTotalOrdersShouldReturnZeroWhenNoOrdersExist() {
        assertEquals(0, reportingService.countTotalOrders());
    }

    @Test
    void getOrdersByStatusShouldCountEveryStatus() {
        Order placedOrder = new Order(
                "O001", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 1
                ))
        );

        Order confirmedOrder = new Order(
                "O002", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 1
                ))
        );
        confirmedOrder.confirm();

        Order cancelledOrder = new Order(
                "O003", "C001",
                List.of(new OrderItem(
                        "P002", "Mouse", new BigDecimal("20.00"), 1
                ))
        );
        cancelledOrder.cancel();

        orderRepository.save(placedOrder);
        orderRepository.save(confirmedOrder);
        orderRepository.save(cancelledOrder);

        Map<OrderStatus, Long> result = reportingService.getOrdersByStatus()
                .stream()
                .collect(Collectors.toMap(
                        OrderStatusReport::status,
                        OrderStatusReport::orderCount
                ));

        assertEquals(1L, result.get(OrderStatus.PLACED));
        assertEquals(1L, result.get(OrderStatus.CONFIRMED));
        assertEquals(1L, result.get(OrderStatus.CANCELLED));
        assertEquals(0L, result.get(OrderStatus.SHIPPED));
        assertEquals(0L, result.get(OrderStatus.DELIVERED));
    }

    @Test
    void getBestSellingProductsShouldSortByQuantityDescending() {
        orderRepository.save(new Order(
                "O001", "C001",
                List.of(
                        new OrderItem("P001", "Laptop",
                                new BigDecimal("1000.00"), 2),
                        new OrderItem("P002", "Mouse",
                                new BigDecimal("20.00"), 5)
                )
        ));

        orderRepository.save(new Order(
                "O002", "C001",
                List.of(new OrderItem("P001", "Laptop",
                        new BigDecimal("1000.00"), 3))
        ));

        Order cancelledOrder = new Order(
                "O003", "C001",
                List.of(new OrderItem("P003", "Keyboard",
                        new BigDecimal("50.00"), 100))
        );
        cancelledOrder.cancel();
        orderRepository.save(cancelledOrder);

        List<ProductSalesReport> result =
                reportingService.getBestSellingProducts();

        assertEquals(2, result.size());

        assertEquals("P001", result.get(0).productId());
        assertEquals(5, result.get(0).quantitySold());

        assertEquals("P002", result.get(1).productId());
        assertEquals(5, result.get(1).quantitySold());
    }

    @Test
    void getRevenueByProductShouldAggregateAndSortByRevenueDescending() {
        orderRepository.save(new Order(
                "O001", "C001",
                List.of(
                        new OrderItem("P001", "Laptop",
                                new BigDecimal("1000.00"), 2),
                        new OrderItem("P002", "Mouse",
                                new BigDecimal("20.00"), 5)
                )
        ));

        orderRepository.save(new Order(
                "O002", "C001",
                List.of(new OrderItem("P001", "Laptop",
                        new BigDecimal("1000.00"), 1))
        ));

        List<ProductSalesReport> result =
                reportingService.getRevenueByProduct();

        assertEquals(2, result.size());

        assertEquals("P001", result.get(0).productId());
        assertEquals(3, result.get(0).quantitySold());
        assertEquals(new BigDecimal("3000.00"),
                result.get(0).totalRevenue());

        assertEquals("P002", result.get(1).productId());
        assertEquals(5, result.get(1).quantitySold());
        assertEquals(new BigDecimal("100.00"),
                result.get(1).totalRevenue());
    }

    @Test
    void getTopCustomersBySpendingShouldRankCustomersBySpending() {
        Customer customer1 = new Customer(
                "C001", "Alice", "alice@example.com", true, Instant.now()
        );

        Customer customer2 = new Customer(
                "C002", "Bob", "bob@example.com", true, Instant.now()
        );

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        orderRepository.save(new Order(
                "O001", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 2
                ))
        ));

        orderRepository.save(new Order(
                "O002", "C002",
                List.of(new OrderItem(
                        "P002", "Mouse", new BigDecimal("50.00"), 1
                ))
        ));

        Order cancelledOrder = new Order(
                "O003", "C002",
                List.of(new OrderItem(
                        "P002", "Mouse", new BigDecimal("1000.00"), 1
                ))
        );
        cancelledOrder.cancel();
        orderRepository.save(cancelledOrder);

        List<CustomerSpendingReport> result =
                reportingService.getTopCustomersBySpending();

        assertEquals(2, result.size());

        assertEquals("C001", result.get(0).customerId());
        assertEquals(1L, result.get(0).totalOrders());
        assertEquals(new BigDecimal("200.00"), result.get(0).totalspent());

        assertEquals("C002", result.get(1).customerId());
        assertEquals(1L, result.get(1).totalOrders());
        assertEquals(new BigDecimal("50.00"), result.get(1).totalspent());
    }

    @Test
    void getLowStockProductsShouldReturnProductsAtOrBelowThreshold() {
        Product laptop = new Product(
                "P001", "Laptop", "Business laptop",
                ProductCategory.ELECTRONICS,
                new BigDecimal("50000.00"), 2, true
        );

        Product mouse = new Product(
                "P002", "Mouse", "Wireless mouse",
                ProductCategory.ELECTRONICS,
                new BigDecimal("1000.00"), 5, true
        );

        Product keyboard = new Product(
                "P003", "Keyboard", "Mechanical keyboard",
                ProductCategory.ELECTRONICS,
                new BigDecimal("3000.00"), 15, true
        );

        productRepository.save(laptop);
        productRepository.save(mouse);
        productRepository.save(keyboard);

        List<Product> result = reportingService.getLowStockProducts(5);

        assertEquals(2, result.size());
        assertEquals("P001", result.get(0).getId());
        assertEquals("P002", result.get(1).getId());
    }


    @Test
    void getLowStockProductsShouldRejectNegativeThreshold() {
        assertThrows(
                IllegalArgumentException.class,
                () -> reportingService.getLowStockProducts(-1)
        );
    }

    @Test
    void getCustomerOrderStatisticsShouldIncludeCustomersWithoutOrders() {
        Customer customer1 = new Customer(
                "C001", "Alice", "alice@example.com", true, Instant.now()
        );

        Customer customer2 = new Customer(
                "C002", "Bob", "bob@example.com", true, Instant.now()
        );

        Customer customer3 = new Customer(
                "C003", "Charlie", "charlie@example.com", true, Instant.now()
        );

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        Order placedOrder = new Order(
                "O001", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 1
                ))
        );

        Order deliveredOrder = new Order(
                "O002", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 1
                ))
        );
        deliveredOrder.confirm();
        deliveredOrder.ship();
        deliveredOrder.deliver();

        Order cancelledOrder = new Order(
                "O003", "C001",
                List.of(new OrderItem(
                        "P001", "Laptop", new BigDecimal("100.00"), 1
                ))
        );
        cancelledOrder.cancel();

        orderRepository.save(placedOrder);
        orderRepository.save(deliveredOrder);
        orderRepository.save(cancelledOrder);

        List<CustomerOrderStatistics> result =
                reportingService.getCustomerOrderStatistics();

        assertEquals(3, result.size());

        CustomerOrderStatistics alice = result.get(0);
        assertEquals("C001", alice.customerId());
        assertEquals(3L, alice.totalOrders());
        assertEquals(1L, alice.deliveredOrders());
        assertEquals(1L, alice.cancelledOrders());

        CustomerOrderStatistics bob = result.get(1);
        assertEquals("C002", bob.customerId());
        assertEquals(0L, bob.totalOrders());

        CustomerOrderStatistics charlie = result.get(2);
        assertEquals("C003", charlie.customerId());
        assertEquals(0L, charlie.totalOrders());
    }

}