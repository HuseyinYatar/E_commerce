package org.ecommerce.paymentservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.handler.PaymentSagaHandler;
import org.ecommerce.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentSagaHandler paymentSagaHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${START_PAYMENT}", groupId = "payment-group")
    public void consume(StartPaymentEvent event ) {
        log.info("Received StartPaymentEvent for Order ID: {}", event.getOrderId());
        paymentSagaHandler.handlePayment(event);
    }
}