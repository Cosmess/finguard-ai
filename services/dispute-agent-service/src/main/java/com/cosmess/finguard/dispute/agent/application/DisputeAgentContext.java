package com.cosmess.finguard.dispute.agent.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DisputeAgentContext(
        UUID disputeId,
        String status,
        String reason,
        List<String> evidenceReferences
) {

    public DisputeAgentContext {
        Objects.requireNonNull(disputeId, "disputeId is required");
        requireText(status, "status");
        requireText(reason, "reason");
        evidenceReferences = List.copyOf(Objects.requireNonNull(evidenceReferences, "evidenceReferences is required"));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}