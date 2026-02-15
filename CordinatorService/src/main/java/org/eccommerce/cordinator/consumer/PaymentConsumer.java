package org.eccommerce.cordinator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.dto.PaymentService.FinishedPaymentEvent;
import org.eccommerce.cordinator.dto.PaymentService.PaymentFailedEvent;
import org.eccommerce.cordinator.producer.PaymentProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConsumer {

    private final static String FINISHED_TOPIC = "finished-payment";

    private static final String PAYMENT_FAILED = "failed-payment";

    private final PaymentProducer paymentProducer;

    public PaymentConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topics = FINISHED_TOPIC, groupId = "payment-group-v1")
    public void consume(FinishedPaymentEvent event) {
        log.info("Received FinishedPaymentEvent for Order ID: {} and Customer ID: {}",
                event.getOrderId(), event.getCustomerId());

        paymentProducer.orderCompleted(event);
    }

    @KafkaListener(topics = PAYMENT_FAILED, groupId = "payment-group-v1")
    public void handlePaymentFailure(PaymentFailedEvent event) {
        log.error("Payment failed for Order ID: {}. Reason: {}", event.getOrderId(), event.getErrorMessage());
        paymentProducer.triggerPaymentFailureRollback(event);

    }
}
