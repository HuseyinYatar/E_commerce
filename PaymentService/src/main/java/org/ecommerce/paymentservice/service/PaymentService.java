package org.ecommerce.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.FinishedPaymentEvent;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.exception.CustomerNotFoundException;
import org.ecommerce.paymentservice.exception.InsufficientBalanceException;
import org.ecommerce.paymentservice.mapper.PaymentMapper;
import org.ecommerce.paymentservice.model.Payment;
import org.ecommerce.paymentservice.producer.PaymentProducer;
import org.ecommerce.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    public PaymentService(PaymentMapper paymentMapper, PaymentRepository paymentRepository,
                          PaymentProducer paymentProducer) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
    }

    @Transactional
    public void deductBalance(StartPaymentEvent event) {
        Integer customerId = event.getCustomerId();
        BigDecimal orderAmount = event.getTotalAmount();

        log.info("Starting balance deduction for Customer ID: {} | Order ID: {} | Amount: {}",
                customerId, event.getOrderId(), orderAmount);

        try {
            Payment lastTransaction = paymentRepository
                    .findFirstByCustomerIdOrderByTransactionTimestampDesc(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Account not found for customer: " + customerId));

            BigDecimal currentBalance = lastTransaction.getBalance();

            if (currentBalance.compareTo(orderAmount) < 0) {
                log.warn("Payment Denied: Insufficient funds for Customer ID: {}. Required: {}, Available: {}",
                        customerId, orderAmount, currentBalance);

                throw new InsufficientBalanceException(
                        String.format("Insufficient balance. Customer: %d, Required: %s", customerId, orderAmount));
            }

            Payment newTransaction = new Payment();
            newTransaction.setCustomerId(customerId);
            newTransaction.setBalance(currentBalance.subtract(orderAmount));
            newTransaction.setCurrency(lastTransaction.getCurrency());
            newTransaction.setTransactionTimestamp(LocalDateTime.now());

            paymentRepository.save(newTransaction);

            FinishedPaymentEvent finishedEvent = paymentMapper.toFinishedEvent(event);
            paymentProducer.sendPaymentSuccessEvent(finishedEvent);

            log.info("Payment Successful for Order ID: {}. New Balance for Customer {}: {}",
                    event.getOrderId(), customerId, newTransaction.getBalance());

        } catch (CustomerNotFoundException | InsufficientBalanceException e) {
            log.warn("Business Rule Violation for OrderId {}: {}", event.getOrderId(), e.getMessage());
            paymentProducer.sendPaymentFailedEvent(event.getOrderId(), e);

        } catch (Exception e) {
            log.error("Technical Failure during payment for OrderId {}: {}", event.getOrderId(), e.getMessage(), e);
            paymentProducer.sendPaymentFailedEvent(event.getOrderId(), e);
        }
    }
}


