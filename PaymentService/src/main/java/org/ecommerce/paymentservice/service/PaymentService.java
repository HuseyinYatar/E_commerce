package org.ecommerce.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.FinishedPaymentEvent;
import org.ecommerce.paymentservice.dto.PaymentFailedEvent;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.exception.CustomerNotFoundException;
import org.ecommerce.paymentservice.exception.InsufficientBalanceException;
import org.ecommerce.paymentservice.mapper.PaymentMapper;
import org.ecommerce.paymentservice.model.Payment;
import org.ecommerce.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;


    @Value("${FINISHED_PAYMENT}")
    private String finishedPaymentTopic;

    @Value("FAILED_PAYMENT")
    private String failedPaymentTopic;


    @Transactional
    public void deductBalance(StartPaymentEvent event) {
        Integer customerId = event.getCustomerId();
        BigDecimal orderAmount = event.getTotalAmount();
        Integer orderId = event.getOrderId();

        log.info("Starting balance deduction | Customer: {} | Order: {} | Amount: {}",
                customerId, orderId, orderAmount);

        try {
            Payment lastTransaction = paymentRepository
                    .findFirstByCustomerIdOrderByTransactionTimestampDesc(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer account not found: " + customerId));

            BigDecimal currentBalance = lastTransaction.getBalance();

            if (currentBalance.compareTo(orderAmount) < 0) {
                throw new InsufficientBalanceException(
                        String.format("Insufficient funds. Required: %s, Found: %s", orderAmount, currentBalance));
            }

            Payment newTransaction = new Payment();
            newTransaction.setCustomerId(customerId);
            newTransaction.setBalance(currentBalance.subtract(orderAmount));
            newTransaction.setCurrency(lastTransaction.getCurrency());
            newTransaction.setTransactionTimestamp(LocalDateTime.now());
            paymentRepository.save(newTransaction);

            FinishedPaymentEvent successEvent = paymentMapper.toFinishedEvent(event);
            outboxService.saveToOutbox(finishedPaymentTopic, successEvent);

            log.info("Payment successful for Order: {}. Staged to outbox.", orderId);

        } catch (CustomerNotFoundException | InsufficientBalanceException e) {
            log.warn("Business Failure for Order {}: {}", orderId, e.getMessage());

            PaymentFailedEvent failedEvent = new PaymentFailedEvent(orderId, e.getMessage());
            outboxService.saveToOutbox(failedPaymentTopic, failedEvent);

        } catch (Exception e) {
            log.error("Technical Critical Failure for Order {}: {}", orderId, e.getMessage(), e);

            PaymentFailedEvent criticalFailure = new PaymentFailedEvent(orderId, "System technical error");
            outboxService.saveToOutbox(failedPaymentTopic, criticalFailure);
        }
    }


}


