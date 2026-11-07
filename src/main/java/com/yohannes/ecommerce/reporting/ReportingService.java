package com.yohannes.ecommerce.reporting;

import com.yohannes.ecommerce.domain.*;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.repository.OrderRepository;
import com.yohannes.ecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportingService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReportingService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    public BigDecimal calculateTotalRevenue() {

        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() !=  OrderStatus.CANCELLED)
                .flatMap(order -> order.getItems().stream())
                .map(item -> item.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countTotalOrders() {

        return orderRepository.findAll()
                .stream()
                .count();
    }

    public List<OrderStatusReport> getOrdersByStatus() {

        Map<OrderStatus, Long> statusCounts = orderRepository.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getStatus,
                                Collectors.counting()
                        )
                );

        return List.of(OrderStatus.values())
                .stream()
                .map(status -> new OrderStatusReport(
                        status,
                        statusCounts.getOrDefault(status, 0L)
                ))
                .toList();
    }

    public List<ProductSalesReport> getBestSellingProducts() {

        return getProductSales()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                ProductSalesReport::quantitySold
                        ).reversed())
                .toList();
    }

    public List<ProductSalesReport> getRevenueByProduct() {

        return getProductSales()
                .stream()
                .sorted(Comparator.comparing(
                        ProductSalesReport::totalRevenue
                ).reversed())
                .toList();
    }

    private List<ProductSalesReport> getProductSales() {

        Map<String, ProductSalesReport> salesByProduct =
                orderRepository.findAll()
                        .stream()
                        .filter(order ->
                                order.getStatus() != OrderStatus.CANCELLED)
                        .flatMap(order -> order.getItems().stream())
                        .collect(Collectors.toMap(
                                OrderItem::getProductId,

                                item -> new ProductSalesReport(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getQuantity(),
                                        item.getSubtotal()
                                ),
                                (existing, incoming) ->
                                        new ProductSalesReport(
                                                existing.productId(),
                                                existing.productName(),
                                                Math.addExact(
                                                        existing.quantitySold(),
                                                        incoming.quantitySold()
                                                ),
                                                existing.totalRevenue()
                                                        .add(incoming.totalRevenue())
                                        )
                        ));
        return List.copyOf(salesByProduct.values());
    }

    public List<CustomerSpendingReport> getTopCustomersBySpending() {

        Map<String, BigDecimal> spendingByCustomer = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.flatMapping(
                                order -> order.getItems().stream(),
                                Collectors.mapping(
                                        OrderItem::getSubtotal,
                                        Collectors.reducing(
                                                BigDecimal.ZERO, BigDecimal::add
                                        )
                                )
                        )
                ));

        Map<String, Long> orderCounts = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.counting()
                ));

        Map<String, Customer> customerById = customerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Customer::getId,
                        Function.identity()
                ));

        return spendingByCustomer.entrySet()
                .stream()
                .filter(entry -> customerById.containsKey(entry.getKey()))
                .map(entry -> {
                    Customer customer = customerById.get(entry.getKey());

                    return new CustomerSpendingReport(
                            customer.getId(),
                            customer.getName(),
                            orderCounts.get(entry.getKey()),
                            entry.getValue()
                    );
                })
                .sorted(Comparator.comparing(
                        CustomerSpendingReport::totalspent
                ).reversed())
                .toList();
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        
        if (threshold < 0) {
            throw new IllegalArgumentException(
                    "Stock threshold cannot be negative."
            );
        }
        
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getStockQuantity() <= threshold)
                .sorted(Comparator.comparingInt(Product::getStockQuantity))
                .toList();
    }

    public List<CustomerOrderStatistics> getCustomerOrderStatistics() {

        Map<String, List<Order>> orderByCustomer = orderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomerId));

        return customerRepository.findAll()
                .stream()
                .map(customer -> {
                    List<Order> orders = orderByCustomer.getOrDefault(
                            customer.getId(),
                            List.of()
                    );

                    long totalOrders = orders.size();

                    long deliveredOrders = orders.stream()
                            .filter(order ->
                                    order.getStatus() == OrderStatus.DELIVERED
                            )
                            .count();

                    long cancelledOrders = orders.stream()
                            .filter(order ->
                                    order.getStatus() == OrderStatus.CANCELLED
                            )
                            .count();

                    return new CustomerOrderStatistics(
                            customer.getId(),
                            customer.getName(),
                            totalOrders,
                            deliveredOrders,
                            cancelledOrders
                    );
                })
                .sorted(Comparator.comparing(
                        CustomerOrderStatistics::customerId
                ))
                .toList();

    }
}
