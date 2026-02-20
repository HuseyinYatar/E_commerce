package org.eccommerce.orderservice.repository;


import org.eccommerce.orderservice.model.OutboxMessage;
import org.eccommerce.orderservice.model.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Integer> {
    List<OutboxMessage> findAllByStatus(MessageStatus status);
}
