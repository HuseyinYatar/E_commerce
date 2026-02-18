package org.ecommerce.inventoryservice.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.mapper.InventoryMapper;
import org.ecommerce.inventoryservice.model.InventoryItem;
import org.ecommerce.inventoryservice.model.InventoryReservation;
import org.ecommerce.inventoryservice.repository.InventoryRepository;
import org.ecommerce.inventoryservice.repository.InventoryReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final InventoryMapper inventoryMapper;
    private final OutboxService outboxService;


    @Value("${CHECKED_INVENTORY}")
    private String inventoryCheckedTopic;

    @Value("${FAILED_CHECK_INVENTORY}")
    private String inventoryFailedTopic;

    public InventoryService(InventoryRepository inventoryRepository, InventoryReservationRepository inventoryReservationRepository, InventoryMapper inventoryMapper, OutboxService outboxService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.inventoryMapper = inventoryMapper;
        this.outboxService = outboxService;
    }


    @Transactional
    public void processInventoryCheck(StartCheckInventoryEvent event) {
        Integer orderId = event.getOrderId();

        try {
            for (InventoryItemDTO item : event.getOrderItemDTOS()) {
                deductStockAndReserve(item, orderId);
            }

            outboxService.saveToOutbox(inventoryCheckedTopic, inventoryMapper.startEvenToCheckedEvent(event));

        } catch (InsufficientStockException e) {
            log.warn("Inventory check failed for Order {}: {}", orderId, e.getMessage());

            InventoryFailedEvent failure = new InventoryFailedEvent(orderId, e.getMessage());
            outboxService.saveToOutbox(inventoryFailedTopic, failure);

        } catch (Exception e) {
            log.error("Technical error for Order {}: {}", orderId, e.getMessage(), e);

            // 4. Stage FAILURE to Outbox (System failure)
            outboxService.saveToOutbox(inventoryFailedTopic, new InventoryFailedEvent(orderId, "System Error"));
        }
    }

    private void deductStockAndReserve(InventoryItemDTO item, Integer orderId) {
        InventoryItem inventoryItem = inventoryRepository.findByProductIdAndCountGreaterThanEqual
                        (item.getProductId(), item.getCount())
                .orElseThrow(() -> new InsufficientStockException("Out of stock: " + item.getProductId()));

        inventoryItem.setCount(inventoryItem.getCount() - item.getCount());

        InventoryReservation reservation = InventoryReservation.builder()
                .orderId(orderId)
                .productId(item.getProductId())
                .quantity(item.getCount())
                .build();
        inventoryReservationRepository.save(reservation);
    }

    /**
     * Reverses the inventory deduction for a specific order.
     * This is the Compensating Transaction for the Saga flow.
     */
    @Transactional
    public void releaseInventory(Integer orderId) {
        log.info(" Rollback: Starting stock restoration for Order ID: {}", orderId);

        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderId(orderId);

        if (reservations.isEmpty()) {
            log.warn("Rollback Warning: No reserved items found for Order ID: {}. It might be already rolled back.", orderId);
            return;
        }

        for (InventoryReservation reservation : reservations) {
            log.info("Restoring Product ID: {}, Quantity: {}",
                    reservation.getProductId(), reservation.getQuantity());

            inventoryRepository.increaseStock(reservation.getProductId(), reservation.getQuantity());
        }
        inventoryReservationRepository.deleteByOrderId(orderId);

        log.info(" Rollback Completed: Inventory restored for Order ID: {}", orderId);
    }


}
