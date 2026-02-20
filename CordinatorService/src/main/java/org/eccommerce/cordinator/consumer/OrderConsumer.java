package org.eccommerce.cordinator.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.OrderService.StartOrderPlacedEvent;
import org.eccommerce.cordinator.handler.OrderSagaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderSagaHandler orderSagaHandler;


    @KafkaListener(topics = "${ORDER_CREATED}", groupId = "order-group")
    public void consumeOrderCreated(StartOrderPlacedEvent event) {
        log.info("Coordinator received Order Created Event: {}", event.getOrderId());
        orderSagaHandler.handleOrderPlacement(event);
    }
}

