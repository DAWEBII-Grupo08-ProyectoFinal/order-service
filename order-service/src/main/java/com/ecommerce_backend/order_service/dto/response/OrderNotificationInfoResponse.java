package com.ecommerce_backend.order_service.dto.response;

public record OrderNotificationInfoResponse(
        Long orderId,
        Long userId
) {
}
