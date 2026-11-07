package com.yohannes.ecommerce.reporting;

import java.math.BigDecimal;

public record CustomerSpendingReport(
        String customerId,
        String customerName,
        long totalOrders,
        BigDecimal totalspent
) {
}
