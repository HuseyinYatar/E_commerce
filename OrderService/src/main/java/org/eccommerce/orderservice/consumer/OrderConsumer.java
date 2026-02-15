package org.eccommerce.orderservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.orderservice.dto.OrderCancelledEvent;
import org.eccommerce.orderservice.dto.OrderCompletedEvent;
import org.eccommerce.orderservice.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderConsumer {

    private final OrderService orderService;

    // Topic Constants
    private final static String ORDER_COMPLETED = "order-completed";
    private final static String ORDER_CANCELLED = "order-cancelled";

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Final Success Path: Updates order status to COMPLETED.
     */
    @KafkaListener(topics = {ORDER_COMPLETED}, groupId = "order-group")
    public void consumeCompleted(OrderCompletedEvent event) {
        log.info(" Received completion for Order ID: {}", event.getOrderId());
        orderService.updateOrderStatus(event.getOrderId(), true);
    }

    /**
     * Final Rollback Path: Updates order status to CANCELLED.
     */
    @KafkaListener(topics = {ORDER_CANCELLED}, groupId = "order-group")
    public void consumeCancelled(OrderCancelledEvent event) {
        log.warn(" Rollback: Received cancellation for Order ID: {}. Reason: {}",
                event.getOrderId(), event.getErrorMessage());
        orderService.updateOrderStatus(event.getOrderId(), false);
    }
}