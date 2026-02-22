package org.ecommerce.inventoryservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryFailedEvent;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.handler.InventorySagaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventorySagaHandler sagaHandler;

    @KafkaListener(topics = "${CHECK_INVENTORY}", groupId = "inventory-group")
    public void onCheckInventory(StartCheckInventoryEvent event) {  // receive as String
        try {
            log.info("Received inventory check for Order: {}", event.getOrderId());
            sagaHandler.handleCheckInventory(event);
        } catch (Exception e) {
            log.error("Failed to deserialize inventory event: {}", e.getMessage(), e);
            throw new RuntimeException("Deserialization failed", e); // triggers retry/DLT if configured
        }
    }


    @KafkaListener(topics = "${INVENTORY_REVERSE}", groupId = "inventory-group")
    public void onRollbackInventory(InventoryFailedEvent event) {
        try {
            log.info("Received rollback request for Order: {}", event.getOrderId());
            sagaHandler.handleRollback(event);
        } catch (Exception e) {
            log.error("Failed to process inventory rollback for Order {}: {}",
                    event.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Rollback processing failed", e);
        }
    }


}