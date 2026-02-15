package org.ecommerce.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String payload;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private LocalDateTime createdAt;
}