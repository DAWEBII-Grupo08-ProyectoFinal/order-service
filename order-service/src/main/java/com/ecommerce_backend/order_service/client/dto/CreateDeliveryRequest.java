package com.ecommerce_backend.order_service.client.dto;

public record CreateDeliveryRequest(
        Long orderId,
        String method,
        String recipientName,
        String phone,
        String address,
        String district,
        String reference,
        Long shippingOptionId,
        Long pickupLocationId
) {
}
