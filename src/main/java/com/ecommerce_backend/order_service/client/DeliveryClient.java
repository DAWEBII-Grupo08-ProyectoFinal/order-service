package com.ecommerce_backend.order_service.client;

import com.ecommerce_backend.order_service.client.dto.CreateDeliveryRequest;
import com.ecommerce_backend.order_service.client.dto.DeliveryQuoteRequest;
import com.ecommerce_backend.order_service.client.dto.DeliveryQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PostMapping("/api/deliveries/quote")
    DeliveryQuoteResponse quote(@RequestBody DeliveryQuoteRequest request);

    @PostMapping("/api/deliveries")
    void createDelivery(@RequestBody CreateDeliveryRequest request);
}
