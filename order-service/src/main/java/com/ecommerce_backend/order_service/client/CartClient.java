package com.ecommerce_backend.order_service.client;

import com.ecommerce_backend.order_service.client.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/api/cart")
    CartResponse getCart(@RequestHeader("Authorization") String authorization);

    @DeleteMapping("/api/cart")
    void clearCart(@RequestHeader("Authorization") String authorization);

}
