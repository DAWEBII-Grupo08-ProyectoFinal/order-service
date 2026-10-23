package com.ecommerce_backend.order_service.dto.response;

import com.ecommerce_backend.order_service.entity.OrderStatus;

import java.math.BigDecimal;

public record OrderPaymentInfoResponse(
        Long id,
        OrderStatus status,
        BigDecimal total
) {
}
