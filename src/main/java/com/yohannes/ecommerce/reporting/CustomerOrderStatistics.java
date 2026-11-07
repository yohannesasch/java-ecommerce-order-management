package com.yohannes.ecommerce.reporting;

public record CustomerOrderStatistics(
        String customerId,
        String customerName,
        long totalOrders,
        long deliveredOrders,
        long cancelledOrders
) {
}
