package org.eccommerce.cordinator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.eccommerce.cordinator.dto.PaymentService.PaymentFailedEvent;
import org.eccommerce.cordinator.handler.PaymentSagaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PaymentConsumer {

    private final PaymentSagaHandler paymentSagaHandler;

    public PaymentConsumer(PaymentSagaHandler paymentSagaHandler) {
        this.paymentSagaHandler = paymentSagaHandler;
    }

    @KafkaListener(topics = "${FINISH_PAYMENT}", groupId = "payment-group-v1")
    public void consumeSuccess(FinishedPaymentEvent event) {
        log.info("Coordinator received Payment SUCCESS for Order: {}", event.getOrderId());
        paymentSagaHandler.handlePaymentSuccess(event);
    }

    @KafkaListener(topics = "${FAIL_PAYMENT}", groupId = "payment-group-v1")
    public void consumeFailure(PaymentFailedEvent event) {
        log.error("Coordinator received Payment FAILURE for Order: {}. Reason: {}",
                event.getOrderId(), event.getErrorMessage());
        paymentSagaHandler.handlePaymentFailure(event);
    }
}
