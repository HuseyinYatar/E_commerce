package org.ecommerce.inventoryservice.producer;


import lombok.extern.slf4j.Slf4j;
import org.ecommerce.inventoryservice.dto.CheckedInventoryEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;



    @Value("${CHECKED_INVENTORY}")
    private String checked_inventory;

    public InventoryProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void checkedMessageSuccessfully(CheckedInventoryEvent checkedInventoryEvent)
    {
    kafkaTemplate.send(checked_inventory,checkedInventoryEvent);
    log.info("The checked inventory event sent orderId:{}",checkedInventoryEvent.getOrderId());
    }

    public void sendInventoryFailedEvent(Integer orderId, String message) {


    }
}
