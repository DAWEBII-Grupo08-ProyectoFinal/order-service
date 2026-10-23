package com.ecommerce_backend.order_service.messaging;

import com.ecommerce_backend.order_service.event.PaymentStatusChangedEvent;
import com.ecommerce_backend.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;

    @RabbitListener(queues = "order.payment.queue")
    public void handlePaymentEvent(PaymentStatusChangedEvent event) {
        if ("APPROVED".equals(event.status())) {
            orderService.confirmPaidOrder(event.orderId());
        }

        if ("FAILED".equals(event.status())) {
            orderService.cancelOrder(event.orderId());
        }
    }

}
