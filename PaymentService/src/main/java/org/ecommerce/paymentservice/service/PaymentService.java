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
    private final PaymentRepository paymentRepository;



    @Transactional
    public void processBalanceDeduction(StartPaymentEvent event) {
        Integer customerId = event.getCustomerId();
        BigDecimal orderAmount = event.getTotalAmount();

        // 1. Retrieve current balance
        Payment lastTransaction = paymentRepository
                .findFirstByCustomerIdOrderByTransactionTimestampDesc(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        BigDecimal currentBalance = lastTransaction.getBalance();

        // 2. Validate funds
        if (currentBalance.compareTo(orderAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds.");
        }

        // 3. Save new transaction record
        Payment newTransaction = new Payment();
        newTransaction.setCustomerId(customerId);
        newTransaction.setBalance(currentBalance.subtract(orderAmount));
        newTransaction.setCurrency(lastTransaction.getCurrency());
        newTransaction.setTransactionTimestamp(LocalDateTime.now());

        paymentRepository.save(newTransaction);
    }


}


