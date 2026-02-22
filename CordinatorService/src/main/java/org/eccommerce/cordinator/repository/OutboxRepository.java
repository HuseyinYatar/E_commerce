package org.eccommerce.cordinator.repository;

import org.eccommerce.cordinator.model.OutboxMessage;
import org.eccommerce.cordinator.model.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Integer> {
    List<OutboxMessage> findAllByStatus(MessageStatus messageStatus);
}