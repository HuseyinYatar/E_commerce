package org.ecommerce.paymentservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.PaymentFailedEvent;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.exception.CustomerNotFoundException;
import org.ecommerce.paymentservice.exception.InsufficientBalanceException;
import org.ecommerce.paymentservice.mapper.PaymentMapper;
import org.ecommerce.paymentservice.service.OutboxService;
import org.ecommerce.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentSagaHandler {

    private final PaymentService paymentService;
    private final OutboxService outboxService;
    private final PaymentMapper paymentMapper;

    @Value("${FINISH_PAYMENT}")
    private String finishedPaymentTopic;

    @Value("${FAIL_PAYMENT}")
    private String failedPaymentTopic;

    @Transactional
    public void handlePayment(StartPaymentEvent event) {
        Integer orderId = event.getOrderId();
        try {
            // Core Business Logic: Deduct the balance
            paymentService.processBalanceDeduction(event);

            // Outbox: Stage success event
            outboxService.saveToOutbox(finishedPaymentTopic, paymentMapper.toFinishedEvent(event));
            log.info("Payment success staged for Order: {}", orderId);

        } catch (CustomerNotFoundException | InsufficientBalanceException e) {
            log.warn("Payment Business Failure for Order {}: {}", orderId, e.getMessage());
            // Outbox: Stage business failure (insufficient funds, etc.)
            outboxService.saveToOutbox(failedPaymentTopic, new PaymentFailedEvent(orderId, e.getMessage()));

        } catch (Exception e) {
            log.error("Technical Error in Payment for Order {}: {}", orderId, e.getMessage());
            // Outbox: Stage system failure
            outboxService.saveToOutbox(failedPaymentTopic,
                    new PaymentFailedEvent(orderId, String.format("System technical error Error:%s",e.getMessage())));
        }
    }
}