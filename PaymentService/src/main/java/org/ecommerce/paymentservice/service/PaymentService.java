package org.ecommerce.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.dto.FinishedPaymentEvent;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.ecommerce.paymentservice.exception.InsufficientBalance;
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
        Long customerId = event.getCustomerId().longValue();
        BigDecimal orderAmount = event.getTotalAmount();

        log.info("Payment check started. Customer: {}, Amount: {}", customerId, orderAmount);

        try {
            Payment lastTransaction =
                    paymentRepository.findFirstByCustomerIdOrderByTransactionTimestampDesc(customerId).orElseThrow(() ->
                            new RuntimeException("Customer account not found!"));

            BigDecimal currentBalance = lastTransaction.getBalance();

            if (currentBalance.compareTo(orderAmount) < 0) {
                log.warn("Insufficient balance! Current: {}, Required: {}", currentBalance, orderAmount);
                paymentProducer.sendPaymentFailedEvent(event.getOrderId(),  new InsufficientBalance
                        (String.format("The customer does not have enough balance. customerId:%d",customerId)));
                return;
            }

            Payment newTransaction = new Payment();
            newTransaction.setCustomerId(customerId);
            newTransaction.setBalance(currentBalance.subtract(orderAmount));
            newTransaction.setCurrency(lastTransaction.getCurrency());
            newTransaction.setTransactionTimestamp(LocalDateTime.now());

            paymentRepository.save(newTransaction);

            FinishedPaymentEvent finishedEvent = paymentMapper.toFinishedEvent(event);

            log.info("Payment successful. New Balance: {}", newTransaction.getBalance());

            paymentProducer.sendPaymentSuccessEvent(finishedEvent);

        } catch (Exception e) {
            log.error("Error during payment processing: {}", e.getMessage());
            paymentProducer.sendPaymentFailedEvent(event.getOrderId(),
                    e);
        }
    }
}
