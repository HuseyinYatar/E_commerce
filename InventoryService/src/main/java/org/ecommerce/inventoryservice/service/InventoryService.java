package org.ecommerce.inventoryservice.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.mapper.InventoryMapper;
import org.ecommerce.inventoryservice.model.InventoryItem;
import org.ecommerce.inventoryservice.model.InventoryReservation;
import org.ecommerce.inventoryservice.model.MessageStatus;
import org.ecommerce.inventoryservice.model.OutboxMessage;
import org.ecommerce.inventoryservice.repository.InventoryRepository;
import org.ecommerce.inventoryservice.repository.InventoryReservationRepository;
import org.ecommerce.inventoryservice.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final InventoryMapper inventoryMapper;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryReservationRepository inventoryReservationRepository, InventoryMapper inventoryMapper, ObjectMapper objectMapper, OutboxRepository outboxRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.inventoryMapper = inventoryMapper;
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
    }


    @Transactional
    public void processInventoryCheck(StartCheckInventoryEvent event) throws InsufficientStockException {
        for (InventoryItemDTO item : event.getOrderItemDTOS()) {
            InventoryItem inventoryItem = inventoryRepository.findByProductIdAndCountGreaterThanEqual(
                    item.getProductId(), item.getCount()
            ).orElseThrow(() -> new InsufficientStockException("Out of stock: " + item.getProductId()));

            inventoryItem.setCount(inventoryItem.getCount() - item.getCount());

            InventoryReservation reservation = InventoryReservation.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getCount())
                    .build();
            inventoryReservationRepository.save(reservation);
        }

        saveOutBoxMessage(event);
    }

    public void saveOutBoxMessage(StartCheckInventoryEvent event) {
        try {
            // 2. Transform the Event to JSON string for the Outbox
            CheckedInventoryEvent successEvent = inventoryMapper.startEvenToCheckedEvent(event);
            String jsonPayload = objectMapper.writeValueAsString(successEvent);

            // 3. Save to Outbox table
            OutboxMessage outbox = OutboxMessage.builder()
                    .topic("inventory-checked-topic")
                    .payload(jsonPayload)
                    .status(MessageStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);
            log.info("Transaction completed and message staged in Outbox for Order ID: {}", event.getOrderId());

        } catch (JacksonException e) {
            log.error("Failed to serialize event for Order ID: {}", event.getOrderId());
            throw new RuntimeException("Serialization error", e);
        }
    }

    /**
     * Reverses the inventory deduction for a specific order.
     * This is the Compensating Transaction for the Saga flow.
     */
    @Transactional
    public void releaseInventory(Integer orderId) {
        log.info(" Rollback: Starting stock restoration for Order ID: {}", orderId);

        // 1. Fetch local reservation records for this order
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderId(orderId);

        if (reservations.isEmpty()) {
            log.warn("Rollback Warning: No reserved items found for Order ID: {}. It might be already rolled back.", orderId);
            return;
        }

        // 2. Restore stock for each product
        for (InventoryReservation reservation : reservations) {
            log.info("Restoring Product ID: {}, Quantity: {}",
                    reservation.getProductId(), reservation.getQuantity());

            // Atomically increase stock in the main inventory table
            inventoryRepository.increaseStock(reservation.getProductId(), reservation.getQuantity());
        }

        inventoryReservationRepository.deleteByOrderId(orderId);

        log.info(" Rollback Completed: Inventory restored for Order ID: {}", orderId);
    }


}
