package org.ecommerce.inventoryservice.consumer;


import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.ecommerce.inventoryservice.dto.InventoryReverseEvent;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.producer.InventoryProducer;
import org.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @KafkaListener(topics = "${CHECKED_INVENTORY}", groupId = "inventory-group")
    public void consume(StartCheckInventoryEvent event) {
        log.info("Received Inventory Check request for Order: {}", event.getOrderId());
        inventoryService.processInventoryCheck(event);
    }


    @KafkaListener(topics = "${INVENTORY_REVERSE}", groupId = "inventory-group")
    private void rollback(InventoryReverseEvent inventoryReverseEvent) {
        log.warn("Rollback triggered for Order ID: {}. Reason: {}",
                inventoryReverseEvent.getOrderId(), inventoryReverseEvent.getErrorMessage());

        // Logic to increase stock back to its original state
        inventoryService.releaseInventory(inventoryReverseEvent.getOrderId());

        log.info("Inventory successfully restored for Order ID: {}", inventoryReverseEvent.getOrderId());
    }
}
