package org.eccommerce.cordinator.handler;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.CheckedInventoryEvent;
import org.eccommerce.cordinator.dto.OrderService.OrderCancelledEvent;
import org.eccommerce.cordinator.mapper.InventoryMapper;
import org.eccommerce.cordinator.model.InventoryFailedEvent;
import org.eccommerce.cordinator.service.OutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class InventorySagaHandler {

    private final OutboxService outboxService;
    private final InventoryMapper inventoryMapper;
    // Topics for the next steps
    @Value("${START_PAYMENT}")
    private String paymentStartTopic;

    @Value("${ORDER_CANCELLED}")
    private String orderCancelledTopic;

    public InventorySagaHandler(OutboxService outboxService, InventoryMapper inventoryMapper) {
        this.outboxService = outboxService;
        this.inventoryMapper = inventoryMapper;
    }


    @Transactional
    public void handleInventorySuccess(CheckedInventoryEvent event) {
        log.info("Saga Step: Inventory reduction for Order {}. Moving to Payment.", event.getOrderId());

        outboxService.saveToOutbox(paymentStartTopic, inventoryMapper.toStartPaymentEvent(event));
    }

    @Transactional
    public void handleInventoryFailure(InventoryFailedEvent event) {
        log.warn("Saga Step: Inventory Failed for Order {}. Starting Rollback.", event.getOrderId());

        outboxService.saveToOutbox(orderCancelledTopic, new OrderCancelledEvent(event.getOrderId(), event.getErrorMessage()));
    }

}