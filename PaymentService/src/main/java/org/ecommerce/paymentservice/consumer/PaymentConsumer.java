package org.ecommerce.paymentservice.consumer;


import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConsumer {

    private final PaymentService paymentService;
    private static final String START_PAYMENT="start-payment";

    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = {START_PAYMENT},groupId = "payment-group")
    private void consume(StartPaymentEvent startPaymentEvent)
    {
        log.info("The start payment event recieved orderId:{}",startPaymentEvent.getOrderId());
        paymentService.deductBalance(startPaymentEvent);
    }
}
