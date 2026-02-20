package org.ecommerce.paymentservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.handler.PaymentSagaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentSagaHandler paymentSagaHandler;

    @KafkaListener(topics = "${START_PAYMENT}", groupId = "payment-group")
    public void consume(StartPaymentEvent event) {
        log.info("Received StartPaymentEvent for Order ID: {}", event.getOrderId());
        paymentSagaHandler.handlePayment(event);
    }
}