package com.ecommerce_backend.order_service.client.dto;

import java.math.BigDecimal;

public record DeliveryQuoteResponse(
        String method,
        BigDecimal cost
) {
}
