package org.eccommerce.cordinator.consumer;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.CheckedInventoryEvent;
import org.eccommerce.cordinator.producer.InventoryProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryConsumer {

    private static final String CHECKED_INVENTORY_TOPIC = "checked-inventoryItem";
    private final InventoryProducer inventoryProducer;

    public InventoryConsumer(InventoryProducer inventoryProducer) {
        this.inventoryProducer = inventoryProducer;
    }

    @KafkaListener(topics = {CHECKED_INVENTORY_TOPIC}, groupId = "inventory-group")
    private void checkedInventory(CheckedInventoryEvent checkedInventoryEvent) {
        log.info("The checked inventory event  recieved orderId:{}", checkedInventoryEvent.getOrderId());
        inventoryProducer.startPaymentCheck(checkedInventoryEvent);

    }
}
