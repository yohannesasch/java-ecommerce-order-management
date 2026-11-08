package com.yohannes.ecommerce.domain;

import com.yohannes.ecommerce.exception.BusinessRuleException;
import com.yohannes.ecommerce.validation.ValidationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {

    private String customerId;
    private final List<CartItem> items = new ArrayList<>();

    public ShoppingCart(String  customerId) {

        ValidationUtils.requireNotBlank(customerId, "Customer ID");

        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public void addItem(Product product, int quantity) {
        ValidationUtils.requireNotNull(product, "Product");
        ValidationUtils.requirePositive(quantity, "Quantity");

        if (!product.isActive()) {
            throw new BusinessRuleException(
                    "Cannot add an inactive product"
            );
        }
        CartItem newItem = new CartItem(product,quantity);

        for (int i = 0; i < items.size(); i++) {
            CartItem existing = items.get(i);

            if (existing.getProduct().getId().equalsIgnoreCase(product.getId())) {
                int updatedQuantity = Math.addExact(
                        existing.getQuantity(),
                        quantity
                );

                items.set(i, new CartItem(product, updatedQuantity));
                return;
            }
        }
        items.add(newItem);
    }

    public boolean removeItem(String prductId) {

        ValidationUtils.requireNotBlank(prductId, "Product ID");

        return items.removeIf(
                item -> item.getProduct().getId().equals(prductId)
        );
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal()  {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void updateQuantity(String productId, int newQuantity) {
        ValidationUtils.requireNotBlank(productId, "Product ID");
        ValidationUtils.requirePositive(newQuantity, "Quantity");

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);

            if (item.getProduct().getId().equals(productId)) {
                items.set(i, new CartItem(item.getProduct(), newQuantity));
                return;
            }
        }

        throw new BusinessRuleException(
                "Product not found in shopping cart: " + productId
        );
    }
}
