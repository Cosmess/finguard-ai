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
    private Instant createdAt;

    private Instant publishedAt;

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

    void markPublished(Instant publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
    }
}
