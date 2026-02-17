package org.eccommerce.cordinator.service;

import lombok.extern.slf4j.Slf4j;
import org.eccommerce.cordinator.model.OutboxMessage;
import org.eccommerce.cordinator.model.enums.MessageStatus;
import org.eccommerce.cordinator.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Common method to stage any event into the outbox.
     */
    @Transactional(propagation = Propagation.MANDATORY) // Ensures it joins an existing transaction
    public void saveToOutbox(String topic, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            OutboxMessage outbox = OutboxMessage.builder()
                    .topic(topic)
                    .payload(jsonPayload)
                    .status(MessageStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);
            log.debug("Event staged to outbox for topic: {}", topic);

        } catch (JacksonException e) {
            log.error("Failed to serialize outbox payload for topic: {}", topic, e);
            throw new RuntimeException("Serialization error during outbox staging", e);
        }
    }
}