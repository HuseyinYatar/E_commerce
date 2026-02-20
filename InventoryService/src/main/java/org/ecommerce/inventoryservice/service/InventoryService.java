package org.ecommerce.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.model.InventoryItem;
import org.ecommerce.inventoryservice.model.InventoryReservation;
import org.ecommerce.inventoryservice.repository.InventoryRepository;
import org.ecommerce.inventoryservice.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository inventoryReservationRepository;

    @Transactional
    public void deductStockForOrder(StartCheckInventoryEvent event) {
        for (InventoryItemDTO item : event.getOrderItemDTOS()) {

            InventoryItem inventoryItem = inventoryRepository
                    .findByProductIdAndCountGreaterThanEqual(item.getProductId(), item.getCount())
                    .orElseThrow(() -> new InsufficientStockException("Out of stock: " + item.getProductId()));

            inventoryItem.setCount(inventoryItem.getCount() - item.getCount());
            InventoryReservation reservation = InventoryReservation.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getCount())
                    .build();
            inventoryReservationRepository.save(reservation);
            log.info("The inventory item stock reduction completed ProductId:{}",item.getProductId());

        }
    }

    @Transactional
    public void releaseInventory(Integer orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderId(orderId);

        if (reservations.isEmpty()) return;

        for (InventoryReservation reservation : reservations) {
            inventoryRepository.increaseStock(reservation.getProductId(), reservation.getQuantity());
        }
        inventoryReservationRepository.deleteByOrderId(orderId);
    }
}