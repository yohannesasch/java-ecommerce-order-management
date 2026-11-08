package com.yohannes.ecommerce.exception;

public class InsufficientStockException extends BusinessRuleException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
