package com.ecommerce_backend.order_service.mapper;

import com.ecommerce_backend.order_service.dto.response.OrderItemResponse;
import com.ecommerce_backend.order_service.dto.response.OrderResponse;
import com.ecommerce_backend.order_service.entity.Order;
import com.ecommerce_backend.order_service.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),

                order.getSubtotal(),
                order.getDeliveryCost(),
                order.getTotal(),

                order.getRecipientName(),
                order.getPhone(),
                order.getAddress(),
                order.getDistrict(),
                order.getCity(),
                order.getReference(),

                order.getCreatedAt(),

                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private OrderItemResponse toItemResponse(
            OrderItem item
    ) {

        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
