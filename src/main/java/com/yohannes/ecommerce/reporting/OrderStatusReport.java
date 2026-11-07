package com.yohannes.ecommerce.reporting;

import com.yohannes.ecommerce.domain.OrderStatus;

public record OrderStatusReport(
        OrderStatus status,
        long orderCount
) {
}
