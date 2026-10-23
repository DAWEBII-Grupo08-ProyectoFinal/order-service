package com.ecommerce_backend.order_service.client.dto;

public record InventoryResponse(
        Long id,
        Long productId,
        Integer currentQuantity
) {
}
