package com.yohannes.ecommerce.service;

import com.yohannes.ecommerce.domain.*;
import com.yohannes.ecommerce.repository.CartRepository;
import com.yohannes.ecommerce.repository.CustomerRepository;
import com.yohannes.ecommerce.repository.OrderRepository;
import com.yohannes.ecommerce.repository.ProductRepository;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            CartRepository cartRepository
    ) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.cartRepository = Objects.requireNonNull(cartRepository);
    }

    public Order placeOrder(String customerId) {

        // validate customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + customerId
                ));

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is inactive");
        }

        // retrieve cart
        ShoppingCart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException(
                        "Shopping cart not found"
                ));

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot place and order with an empty cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        Map<String, Product> productsToUpdate = new HashMap<>();

        for (CartItem cartItem : cart.getItems()) {

            Product currentProduct = productRepository
                    .findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: " + cartItem.getProduct().getId()
                    ));

            if (!currentProduct.isActive()) {
                throw new IllegalStateException(
                        "Product is inactive: " + currentProduct.getName()
                );
            }

            if (currentProduct.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product: " + currentProduct.getName()
                );
            }

            orderItems.add(new OrderItem(
                    currentProduct.getId(),
                    currentProduct.getName(),
                    currentProduct.getPrice(),
                    cartItem.getQuantity()
            ));

            productsToUpdate.put(
                    currentProduct.getId(),
                    currentProduct.withStockQuantity(
                            currentProduct.getStockQuantity() - cartItem.getQuantity()
                    )
            );
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                customerId,
                orderItems
        );

        Map<String, Product> originalProducts = new HashMap<>();

        try {
            for (String  productId : productsToUpdate.keySet()) {
                Product originalProduct = productRepository.findById(productId)
                        .orElseThrow();

                originalProducts.put(productId, originalProduct);

                productRepository.save(productsToUpdate.get(productId));
            }

            orderRepository.save(order);

        } catch (RuntimeException exception) {

            originalProducts.values().forEach(productRepository::save);

            throw new IllegalStateException(
                    "Order placement failed. Inventory changes were tolled back.",
                    exception
            );
        }

        cart.clear();

        cartRepository.save(cart);

        return order;
    }

    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found " + orderId)
                );
    }

    public List<Order> getCustomerOrders(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException(
                    "Customer not found: " + customerId
            );
        }

        return orderRepository.findByCustomerId(customerId);
    }

    public Order confirmOrder(String orderId) {
        Order order = findOrderById(orderId);

        order.confirm();

        orderRepository.save(order);

        return order;
    }

    public Order shipOrder(String orderId) {
        Order order = findOrderById(orderId);

        order.ship();

        orderRepository.save(order);

        return order;
    }

    public Order deliverOrder(String orderId) {
        Order order = findOrderById(orderId);

        order.deliver();

        orderRepository.save(order);

        return order;
    }

    public Order cancelOrder(String orderId) {

        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only placed or confirmed orders can be cancelled."
            );
        }

        Map<String, Product> originalProducts = new HashMap<>();
        Map<String, Product> restoredProducts = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Product not found: " + item.getProductId()
                    ));

            originalProducts.put(product.getId(), product);

            int restoredStock = Math.addExact(
                    product.getStockQuantity(),
                    item.getQuantity()
            );

            restoredProducts.put(
                    product.getId(),
                    product.withStockQuantity(restoredStock)
            );
        }

        try {
            for (Product product : restoredProducts.values()) {
                productRepository.save(product);
            }

            order.cancel();
            orderRepository.save(order);
        } catch (RuntimeException exception) {

            originalProducts.values().forEach(productRepository::save);

            throw new IllegalStateException(
                    "Order cancellation failed.", exception
            );
        }

        return order;
    }
}
