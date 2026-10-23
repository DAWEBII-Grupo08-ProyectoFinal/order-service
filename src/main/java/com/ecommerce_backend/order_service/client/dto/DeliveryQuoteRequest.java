package com.ecommerce_backend.order_service.client.dto;

public record DeliveryQuoteRequest(
        String method,
        Long shippingOptionId,
        Long pickupLocationId
) {
}
