package com.cosmess.finguard.dispute.application;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Dispute(
        UUID id,
        UUID transactionId,
        String customerId,
        String reason,
        DisputeStatus status,
        String reviewerId,
        ReviewDecision reviewDecision,
        Instant createdAt,
        Instant updatedAt,
        List<Evidence> evidence
) {

    public Dispute {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(transactionId, "transactionId is required");
        requireText(customerId, "customerId");
        requireText(reason, "reason");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
        evidence = List.copyOf(Objects.requireNonNull(evidence, "evidence is required"));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    public record Evidence(UUID id, String type, String reference, String description, Instant addedAt) {

        public Evidence {
            Objects.requireNonNull(id, "id is required");
            requireText(type, "type");
            requireText(reference, "reference");
            requireText(description, "description");
            Objects.requireNonNull(addedAt, "addedAt is required");
        }
    }
}