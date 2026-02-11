package org.eccommerce.cordinator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConsumer {

    @KafkaListener(topics = "finished-payment", groupId = "payment-group")
    public void consume(FinishedPaymentEvent event) {
        log.info("Received FinishedPaymentEvent for Order ID: {} and Customer ID: {}",
                event.getOrderId(), event.getCustomerId());
    }
}
