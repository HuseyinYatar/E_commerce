package org.ecommerce.paymentservice.repository;

import org.ecommerce.paymentservice.model.Payment;
import org.ecommerce.paymentservice.service.PaymentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Optional<Payment> findFirstByCustomerIdOrderByTransactionTimestampDesc(Long customerId);
}
