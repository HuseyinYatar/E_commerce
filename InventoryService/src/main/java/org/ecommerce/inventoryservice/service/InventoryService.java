package org.ecommerce.inventoryservice.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.exception.InsufficientStockException;
import org.ecommerce.inventoryservice.mapper.InventoryMapper;
import org.ecommerce.inventoryservice.model.Inventory;
import org.ecommerce.inventoryservice.producer.InventoryProducer;
import org.ecommerce.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryProducer inventoryProducer;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, InventoryProducer inventoryProducer, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryProducer = inventoryProducer;
        this.inventoryMapper = inventoryMapper;
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
        inventory.setCount(inventory.getCount() - startCheckInventoryEvent.getCount());
        inventoryRepository.save(inventory);



    }
    public void checkedInventory(StartCheckInventoryEvent startCheckInventoryEvent)
    {
        CheckedInventoryEvent inventoryEvent=inventoryMapper.startEvenToCheckedEvent(startCheckInventoryEvent);
        inventoryProducer.checkedMessageSuccessfully(inventoryEvent);

    }
}
