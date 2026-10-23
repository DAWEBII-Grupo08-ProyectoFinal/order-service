package com.ecommerce_backend.order_service.client;

import com.ecommerce_backend.order_service.client.dto.ConsumeStockRequest;
import com.ecommerce_backend.order_service.client.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/products/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable("productId") Long productId);

    @PostMapping("/api/inventory/consume")
    void consumeStock(@RequestBody ConsumeStockRequest request);
}
