package com.ecommerce_backend.order_service.dto.response;

import com.ecommerce_backend.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,

        BigDecimal subtotal,
        BigDecimal deliveryCost,
        BigDecimal total,

        String recipientName,
        String phone,
        String address,
        String district,
        String city,
        String reference,

        LocalDateTime createdAt,

        List<OrderItemResponse> items
) {
}
