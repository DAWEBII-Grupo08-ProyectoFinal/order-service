package com.ecommerce_backend.order_service.controller;

import com.ecommerce_backend.order_service.client.CartClient;
import com.ecommerce_backend.order_service.client.CatalogClient;
import com.ecommerce_backend.order_service.client.InventoryClient;
import com.ecommerce_backend.order_service.client.dto.CartResponse;
import com.ecommerce_backend.order_service.client.dto.InventoryResponse;
import com.ecommerce_backend.order_service.client.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/test")
@RequiredArgsConstructor
public class OrderTestController {

    private final CartClient cartClient;
    private final CatalogClient catalogClient;
    private final InventoryClient inventoryClient;

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> testCart(
            @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(
                cartClient.getCart(authorization)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> testProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                catalogClient.getProductById(productId)
        );
    }

    @GetMapping("/inventory/{productId}")
    public ResponseEntity<InventoryResponse> testInventory(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                inventoryClient.getInventoryByProductId(productId)
        );
    }

}
