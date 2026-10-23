package com.ecommerce_backend.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank
        String recipientName,

        @NotBlank
        String phone,

        String address,

        String district,

        String city,

        String reference,

        @NotBlank
        String deliveryMethod,

        Long shippingOptionId,

        Long pickupLocationId
) {
}
