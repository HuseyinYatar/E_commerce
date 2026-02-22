package org.ecommerce.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.paymentservice.model.OutboxMessage;
import org.ecommerce.paymentservice.model.enums.MessageStatus;
import org.ecommerce.paymentservice.repository.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxRelayer {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Scheduled(fixedDelay = 2000) // Runs every 2 seconds
    @Transactional
    public void relayMessages() {
        List<OutboxMessage> pendingMessages = outboxRepository.findAllByStatus(MessageStatus.PENDING);

        for (OutboxMessage message : pendingMessages) {
            try {
                kafkaTemplate.send(message.getTopic(), message.getPayload());
                message.setStatus(MessageStatus.PROCESSED);
                outboxRepository.save(message);
                log.info("Relayed message ID: {} to Kafka", message.getId());
            } catch (Exception e) {
                log.error("Failed to relay message: {}", message.getId());
                message.setStatus(MessageStatus.FAILED); // Or retry logic
            }
        }
    }
}