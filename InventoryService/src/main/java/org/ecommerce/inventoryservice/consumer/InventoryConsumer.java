package org.ecommerce.inventoryservice.consumer;


import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.InventoryItemDTO;
import org.ecommerce.inventoryservice.dto.StartCheckInventoryEvent;
import org.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    //TOPICS
    private final static String CHECK_INVENTORY = "check-inventory";

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }



    @KafkaListener(topics = {CHECK_INVENTORY}, groupId = "inventory-group")
    private void consume(StartCheckInventoryEvent startCheckInventoryEvent) {
        startCheckInventoryEvent.getOrderItemDTOS().forEach((i)->
                log.info("The check Inventory Event recieved productId:{}",i.getProductId()));
        startCheckInventoryEvent.getOrderItemDTOS().forEach(inventoryService::checkInventoryStock);
        inventoryService.checkedInventory(startCheckInventoryEvent);
    }

}
