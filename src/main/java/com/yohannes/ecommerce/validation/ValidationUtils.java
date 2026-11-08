package com.yohannes.ecommerce.validation;

import com.yohannes.ecommerce.exception.ValidationException;

import java.math.BigDecimal;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null.");
        }
    }

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    public static void requireNonNegative(int value, String fieldNeame) {
        if (value < 0) {
            throw new ValidationException(fieldNeame + " cannot be negative.");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) {
        requireNotNull(value, fieldName);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " must be greater than Zero.");
        }
    }

    public static void requireNonNegative(BigDecimal value, String fieldName) {
        requireNotNull(value, fieldName);

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(fieldName + " cannot be negative.");
        }
    }

    public static void requireValidEmail(String email) {
        requireNotBlank(email, "Email");

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(emailPattern)) {
            throw new ValidationException("Email format is invalid.");
        }
    }

    public static void requirePositive(int value, String fieldValue) {

        requireNotNull(value, fieldValue);

        if (value <= 0) {
            throw new ValidationException(fieldValue + " must be positive");
        }
    }
}
