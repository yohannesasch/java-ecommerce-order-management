package com.yohannes.ecommerce.repository.impl;

import com.yohannes.ecommerce.domain.Order;
import com.yohannes.ecommerce.repository.OrderRepository;


import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        Objects.requireNonNull(id, "Order ID cannot be null");

        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        return orders.values()
                .stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .toList();
    }

   @Override
   public boolean existsById(String  id) {
        Objects.requireNonNull(id, "Order ID cannot be null");
        return orders.containsKey(id);
   }

}
