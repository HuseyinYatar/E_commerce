package org.ecommerce.paymentservice.repository;


import org.ecommerce.paymentservice.model.OutboxMessage;
import org.ecommerce.paymentservice.model.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Integer> {
    List<OutboxMessage> findAllByStatus(MessageStatus status);
}
