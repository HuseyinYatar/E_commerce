package org.eccommerce.cordinator.producer;


import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.InventoryService.CheckedInventoryEvent;
import org.eccommerce.cordinator.dto.PaymentService.StartPaymentEvent;
import org.eccommerce.cordinator.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryProducer {

    @Value("${START_PAYMENT}")
    private String startPayment;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InventoryMapper inventoryMapper;

    public InventoryProducer(KafkaTemplate<String, Object> kafkaTemplate, InventoryMapper inventoryMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryMapper = inventoryMapper;
    }

    public void startPaymentCheck(CheckedInventoryEvent checkedInventoryEvent) {
        StartPaymentEvent startPaymentEvent = inventoryMapper.toStartPaymentEvent(checkedInventoryEvent);

        kafkaTemplate.send(startPayment, startPaymentEvent);
        log.info("The Payment Event Started orderId:{}", startPaymentEvent.getOrderId());
    }
}
