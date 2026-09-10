package com.cosmess.finguard.payment.adapter.out.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 80)
    private String aggregateType;

    @Column(nullable = false, length = 120)
    private String eventType;

    @Column(nullable = false, length = 120)
    private String topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    public Instant publishedAt() {
        return publishedAt;
    }

    protected OutboxEvent() {
    }

    private OutboxEvent(
            UUID id,
            UUID aggregateId,
            String aggregateType,
            String eventType,
            String topic,
            String payload,
            OutboxStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.topic = topic;
        this.payload = payload;
        this.status = status;
        this.attempts = 0;
        this.createdAt = createdAt;
    }

    static OutboxEvent pending(UUID aggregateId, String eventType, String topic, String payload, Instant createdAt) {
        return new OutboxEvent(UUID.randomUUID(), aggregateId, "Transaction", eventType, topic, payload, OutboxStatus.PENDING, createdAt);
    }

    UUID aggregateId() {
        return aggregateId;
    }

    String eventType() {
        return eventType;
    }

    String topic() {
        return topic;
    }

    String payload() {
        return payload;
    }

    int attempts() {
        return attempts;
    }

    void markPublished(Instant publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    void markFailed(String errorMessage, int maxAttempts) {
        this.attempts++;
        this.lastError = errorMessage;
        if (this.attempts >= maxAttempts) {
            this.status = OutboxStatus.FAILED;
        }
    }
}
