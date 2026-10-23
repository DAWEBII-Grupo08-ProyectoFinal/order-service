package com.ecommerce_backend.order_service.service;

import com.ecommerce_backend.order_service.client.CartClient;
import com.ecommerce_backend.order_service.client.CatalogClient;
import com.ecommerce_backend.order_service.client.DeliveryClient;
import com.ecommerce_backend.order_service.client.InventoryClient;
import com.ecommerce_backend.order_service.client.dto.*;
import com.ecommerce_backend.order_service.dto.request.CreateOrderRequest;
import com.ecommerce_backend.order_service.dto.response.OrderNotificationInfoResponse;
import com.ecommerce_backend.order_service.dto.response.OrderPaymentInfoResponse;
import com.ecommerce_backend.order_service.dto.response.OrderResponse;
import com.ecommerce_backend.order_service.entity.Order;
import com.ecommerce_backend.order_service.entity.OrderItem;
import com.ecommerce_backend.order_service.entity.OrderStatus;
import com.ecommerce_backend.order_service.exception.EmptyCartException;
import com.ecommerce_backend.order_service.mapper.OrderMapper;
import com.ecommerce_backend.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final CartClient cartClient;
    private final CatalogClient catalogClient;
    private final InventoryClient inventoryClient;
    private final DeliveryClient deliveryClient;

    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findMyOrderById(Long userId, Long orderId) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(
            Long userId,
            String authorization,
            CreateOrderRequest request
    ) {
        // 1. Obtener carrito persistente
        CartResponse cart = cartClient.getCart(authorization);

        if (cart.items() == null || cart.items().isEmpty()) {
            throw new EmptyCartException();
        }

        // 2. Cotizar método de entrega
        DeliveryQuoteResponse deliveryQuote = deliveryClient.quote(
                new DeliveryQuoteRequest(
                        request.deliveryMethod(),
                        request.shippingOptionId(),
                        request.pickupLocationId()
                )
        );

        // 3. Preparar pedido
        Order order = new Order();

        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setRecipientName(request.recipientName());
        order.setPhone(request.phone());
        order.setAddress(request.address());
        order.setDistrict(request.district());
        order.setCity(request.city());
        order.setReference(request.reference());
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        BigDecimal subtotal = BigDecimal.ZERO;

        // 4. Construir snapshot usando Catalog
        for (var cartItem : cart.items()) {

            ProductResponse product =
                    catalogClient.getProductById(cartItem.productId());

            var inventory = inventoryClient.getInventoryByProductId(cartItem.productId());

            if (inventory.currentQuantity() < cartItem.quantity()) {
                throw new IllegalStateException(
                        "Stock insuficiente para " + product.name()
                );
            }

            BigDecimal itemSubtotal = product.price()
                    .multiply(BigDecimal.valueOf(cartItem.quantity()));

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(product.id());
            orderItem.setProductName(product.name());
            orderItem.setUnitPrice(product.price());
            orderItem.setQuantity(cartItem.quantity());
            orderItem.setSubtotal(itemSubtotal);

            order.getItems().add(orderItem);

            subtotal = subtotal.add(itemSubtotal);

        }

        // 5. Calcular totales
        BigDecimal deliveryCost = deliveryQuote.cost();
        BigDecimal total = subtotal.add(deliveryCost);

        order.setSubtotal(subtotal);
        order.setDeliveryCost(deliveryCost);
        order.setTotal(total);

        // 6. Guardar pedido + items
        Order savedOrder = orderRepository.save(order);

        // 7. Crear entrega asociada al pedido
        deliveryClient.createDelivery(
                new CreateDeliveryRequest(
                        savedOrder.getId(),
                        request.deliveryMethod(),
                        request.recipientName(),
                        request.phone(),
                        request.address(),
                        request.district(),
                        request.reference(),
                        request.shippingOptionId(),
                        request.pickupLocationId()
                )
        );

        // 8. Vaciar carrito
        cartClient.clearCart(authorization);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        order.setStatus(status);
    }

    @Transactional
    public void confirmPaidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return;
        }

        List<StockItemRequest> stockItems = order.getItems()
                .stream()
                .map(item -> new StockItemRequest(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        inventoryClient.consumeStock(
                new ConsumeStockRequest(stockItems)
        );

        order.setStatus(OrderStatus.CONFIRMED);
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public OrderPaymentInfoResponse findPaymentInfo(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        return new OrderPaymentInfoResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal()
        );
    }

    @Transactional(readOnly = true)
    public OrderNotificationInfoResponse findNotificationInfo(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido no encontrado")
                );

        return new OrderNotificationInfoResponse(
                order.getId(),
                order.getUserId()
        );
    }

}
