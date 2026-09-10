package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_transactions")
class ProcessedTransactionEntity {

    @Id
    private UUID transactionId;

    @Column(nullable = false)
    private Instant processedAt;

    protected ProcessedTransactionEntity() {
    }

    ProcessedTransactionEntity(UUID transactionId, Instant processedAt) {
        this.transactionId = transactionId;
        this.processedAt = processedAt;
    }
}
