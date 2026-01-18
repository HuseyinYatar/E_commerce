package org.eccommerce.cordinator.consumer;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.Producer.OrderProducer;
import org.eccommerce.cordinator.dto.StartOrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderConsumer {

    private final OrderProducer orderProducer;
    //Topics
    private static final String PLACE_ORDER_TOPIC = "order-created";

    public OrderConsumer(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }


    @KafkaListener(topics = {PLACE_ORDER_TOPIC}, groupId = "order-group")
    private void checkInventory(StartOrderPlacedEvent startOrderPlacedEvent) {
        log.info("The created order recieved orderId:{}", startOrderPlacedEvent.getOrderId());

        orderProducer.startInventoryCheck(startOrderPlacedEvent);
    }


}
