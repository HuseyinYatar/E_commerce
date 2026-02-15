package org.ecommerce.paymentservice.repository;

import org.ecommerce.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findFirstByCustomerIdOrderByTransactionTimestampDesc(Integer customerId);
}
