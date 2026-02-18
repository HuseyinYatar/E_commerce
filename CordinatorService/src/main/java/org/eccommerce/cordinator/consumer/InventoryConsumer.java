package org.eccommerce.cordinator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.CheckedInventoryEvent;
import org.eccommerce.cordinator.handler.InventorySagaHandler;
import org.eccommerce.cordinator.model.InventoryFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryConsumer {

    private final InventorySagaHandler inventorySagaHandler;

    public InventoryConsumer(InventorySagaHandler inventorySagaHandler) {
        this.inventorySagaHandler = inventorySagaHandler;
    }

    /**
     * Listens for successful inventory reservations.
     */
    @KafkaListener(topics = "${CHECKED_INVENTORY}", groupId = "inventory-group")
    public void consumeSuccess(CheckedInventoryEvent event) {
        log.info("Coordinator received SUCCESS: Inventory reserved for Order ID: {}", event.getOrderId());

        inventorySagaHandler.handleInventorySuccess(event);
    }

    /**
     * Listens for inventory failures (e.g., out of stock).
     */
    @KafkaListener(topics = "${FAILED_CHECK_INVENTORY}", groupId = "inventory-group")
    public void consumeFailure(InventoryFailedEvent event) {
        log.warn("Coordinator received FAILURE: Inventory failed for Order ID: {}. Reason: {}",
                event.getOrderId(), event.getErrorMessage());

        inventorySagaHandler.handleInventoryFailure(event);
    }
}