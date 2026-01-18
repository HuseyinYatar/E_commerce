package org.ecommerce.inventoryservice.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.model.Inventory;
import org.ecommerce.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void checkInventoryStock(InventoryItemDTO startCheckInventoryEvent) {
        Inventory inventory = inventoryRepository.findByProductIdAndCountGreaterThanEqual(
                startCheckInventoryEvent.getProductId(),
                startCheckInventoryEvent.getCount()
        ).orElseThrow(()
         -> new InsufficientStockException(
                String.format("The stock is insufficient. Requested: %d, Product ID: %d",
                        startCheckInventoryEvent.getCount(),
                        startCheckInventoryEvent.getProductId())
        ));

        //If the Stock is enough than reduce it
        inventory.setCount(inventory.getCount()-startCheckInventoryEvent.getCount());
        inventoryRepository.save(inventory);





    }




}
