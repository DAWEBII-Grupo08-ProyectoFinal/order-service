package com.ecommerce_backend.order_service.event;

import java.math.BigDecimal;

public record PaymentStatusChangedEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String status
) {
}
