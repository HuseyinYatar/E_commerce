package org.ecommerce.inventoryservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryFailedEvent;
import org.ecommerce.inventoryservice.dto.OrderCancelledEvent;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.mapper.InventoryMapper;
import org.ecommerce.inventoryservice.service.InventoryService;
import org.ecommerce.inventoryservice.service.OutboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventorySagaHandler {

    private final InventoryService inventoryService;
    private final OutboxService outboxService;
    private final InventoryMapper inventoryMapper;

    @Value("${CHECKED_INVENTORY}")
    private String inventoryCheckedTopic;

    @Value("${FAILED_CHECK_INVENTORY}")
    private String inventoryFailedTopic;



    @Transactional
    public void handleCheckInventory(StartCheckInventoryEvent event) {
        Integer orderId = event.getOrderId();
        try {
            // Business Logic: Actual stock deduction
            inventoryService.deductStockForOrder(event);

            // Outbox: Stage success
            outboxService.saveToOutbox(inventoryCheckedTopic, inventoryMapper.startEvenToCheckedEvent(event));

        } catch (InsufficientStockException e) {
            log.warn("Inventory check failed for Order {}: {}", orderId, e.getMessage());
            outboxService.saveToOutbox(inventoryFailedTopic, new InventoryFailedEvent(orderId, e.getMessage()));
        } catch (Exception e) {
            log.error("Technical error for Order {}: {}", orderId, e.getMessage());
            outboxService.saveToOutbox(inventoryFailedTopic, new InventoryFailedEvent(orderId, "System Error"));
        }
    }

    @Transactional
    public void handleRollback(InventoryFailedEvent event) {
        // Business Logic: Restore stock
        inventoryService.releaseInventory(event.getOrderId());

        // Outbox: Stage rollback completion
        outboxService.saveToOutbox(inventoryFailedTopic,
                new OrderCancelledEvent(event.getOrderId(),event.getErrorMessage()));
    }
}