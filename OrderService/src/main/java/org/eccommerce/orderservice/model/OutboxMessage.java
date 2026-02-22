package org.eccommerce.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eccommerce.orderservice.model.enums.MessageStatus;

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

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private LocalDateTime createdAt;
}