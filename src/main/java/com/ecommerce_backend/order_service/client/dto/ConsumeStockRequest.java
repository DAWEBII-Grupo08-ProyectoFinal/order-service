package com.ecommerce_backend.order_service.client.dto;

import java.util.List;

public record ConsumeStockRequest(
        List<StockItemRequest> items
) {
}
