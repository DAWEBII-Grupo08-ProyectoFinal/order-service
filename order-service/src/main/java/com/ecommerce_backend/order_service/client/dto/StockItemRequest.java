package com.ecommerce_backend.order_service.client.dto;

public record StockItemRequest(
        Long productId,
        Integer quantity
) {
}
