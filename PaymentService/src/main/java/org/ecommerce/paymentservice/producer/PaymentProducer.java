package org.ecommerce.paymentservice.producer;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.FinishedPaymentEvent;
import org.ecommerce.paymentservice.dto.PaymentFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentProducer {
    private static final String FINISHED_PAYMENT_TOPIC = "finished-payment";
    private static final String FAILED_PAYMENT_TOPIC = "failed-payment";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentSuccessEvent(FinishedPaymentEvent finishedPaymentEvent) {
        log.info("Sending success payment event for Order ID: {}", finishedPaymentEvent.getOrderId());
        kafkaTemplate.send(FINISHED_PAYMENT_TOPIC, finishedPaymentEvent);
    }

    public void sendPaymentFailedEvent(Integer orderId, Exception ex) {
        log.error("Sending failed payment event for Order ID: {}. Reason: {}", orderId, ex.getMessage());

        PaymentFailedEvent failedEvent = new PaymentFailedEvent();
        failedEvent.setOrderId(orderId);
        failedEvent.setErrorMessage(ex.getMessage());
        kafkaTemplate.send(FAILED_PAYMENT_TOPIC, failedEvent);
    }
}
