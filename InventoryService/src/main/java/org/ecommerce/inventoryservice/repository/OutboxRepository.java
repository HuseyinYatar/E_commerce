package org.ecommerce.inventoryservice.repository;

import org.ecommerce.inventoryservice.model.MessageStatus;
import org.ecommerce.inventoryservice.model.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage,Integer> {
    List<OutboxMessage> findAllByStatus(MessageStatus status);
}
