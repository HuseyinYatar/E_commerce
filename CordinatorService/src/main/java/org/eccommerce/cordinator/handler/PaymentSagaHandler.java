package org.eccommerce.cordinator.handler;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.InventoryFailedEvent;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.eccommerce.cordinator.dto.PaymentService.PaymentFailedEvent;
import org.eccommerce.cordinator.service.OutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentSagaHandler {

    private final OutboxService outboxService;

    @Value("${ORDER_COMPLETED}")
    private String orderCompletedTopic;

    @Value("${INVENTORY_REVERSE}")
    private String inventoryReverseTopic;

    public PaymentSagaHandler(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @Transactional
    public void handlePaymentSuccess(FinishedPaymentEvent event) {
        log.info("Saga Step: Completing Order ID: {}", event.getOrderId());
        outboxService.saveToOutbox(orderCompletedTopic, event);
    }

    @Transactional
    public void handlePaymentFailure(PaymentFailedEvent event) {
        log.warn("Saga Step: Rolling back Order ID: {} due to payment failure", event.getOrderId());
        outboxService.saveToOutbox
                (inventoryReverseTopic, new InventoryFailedEvent(event.getOrderId(),event.getErrorMessage()));
    }
}