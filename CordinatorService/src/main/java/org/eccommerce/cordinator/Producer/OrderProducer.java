package org.eccommerce.cordinator.Producer;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.StartCheckInventoryEvent;
import org.eccommerce.cordinator.dto.StartOrderPlacedEvent;
import org.eccommerce.cordinator.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMapper orderMapper;

    public OrderProducer(KafkaTemplate<String, Object> kafkaTemplate, OrderMapper orderMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderMapper = orderMapper;
    }

     @Value("${CHECK_INVENTORY}")
     private String checkInventory;

    public void startInventoryCheck(StartOrderPlacedEvent startOrderPlacedEvents) {
        StartCheckInventoryEvent startCheckInventoryEvent =
                orderMapper.START_CHECK_INVENTORY_EVENT(startOrderPlacedEvents);

        kafkaTemplate.send(checkInventory,startCheckInventoryEvent);
        log.info("The check Inventory Event started");
    }
}
