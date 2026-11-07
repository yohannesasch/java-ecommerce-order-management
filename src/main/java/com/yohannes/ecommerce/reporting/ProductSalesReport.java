package com.yohannes.ecommerce.reporting;

import java.math.BigDecimal;

public record ProductSalesReport(
        String productId,
        String productName,
        int quantitySold,
        BigDecimal totalRevenue
) {
}
