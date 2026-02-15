package org.eccommerce.cordinator.producer;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.InventoryReverseEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderCancelledEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderCompletedEvent;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.eccommerce.cordinator.dto.PaymentService.PaymentFailedEvent;
import org.eccommerce.cordinator.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMapper orderMapper;

    @Value("${INVENTORY_REVERSE}")
    private String inventoryReverseTopic;

    @Value("${ORDER_CANCELLED}")
    private String orderCancelledTopic;

    @Value("${ORDER_COMPLETED}")
    private String orderCompletedTopic;

    public PaymentProducer(KafkaTemplate<String, Object> kafkaTemplate, OrderMapper orderMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderMapper = orderMapper;
    }


    public void orderCompleted(FinishedPaymentEvent finishedPaymentEvent) {
        log.debug("The finishedPayment event orderId:{}", finishedPaymentEvent.getOrderId());
        OrderCompletedEvent orderCompletedEvent = orderMapper.TO_ORDER_COMPLETED_EVENT(finishedPaymentEvent);

        log.info("The order Completed Event started orderId:{}", orderCompletedEvent.getOrderId());

        kafkaTemplate.send(orderCompletedTopic, orderCompletedEvent);

        log.info("The order Completed Event finished orderId:{}", orderCompletedEvent.getOrderId());
    }


    public void triggerPaymentFailureRollback(PaymentFailedEvent paymentFailedEvent) {
        log.warn("Payment failed for Order ID: {}. Reason: {}. Initiating  rollback...",
                paymentFailedEvent.getOrderId(), paymentFailedEvent.getErrorMessage());

        try {
            InventoryReverseEvent inventoryReverseEvent =
                    orderMapper.TO_INVENTORY_REVERSE_EVENT(paymentFailedEvent);

            OrderCancelledEvent orderCancelledEvent =
                    orderMapper.TO_ORDER_CANCELLED_EVENT(paymentFailedEvent);

            kafkaTemplate.send(inventoryReverseTopic, inventoryReverseEvent);
            kafkaTemplate.send(orderCancelledTopic, orderCancelledEvent);

            log.info("Successfully published rollback events to topics: [{}] and [{}] for Order ID: {}",
                    inventoryReverseTopic, orderCancelledTopic, paymentFailedEvent.getOrderId());

        } catch (Exception e) {
            log.error("CRITICAL: Failed to send rollback events for Order ID: {}. Manual intervention may be required.",
                    paymentFailedEvent.getOrderId(), e);
        }
    }
}
