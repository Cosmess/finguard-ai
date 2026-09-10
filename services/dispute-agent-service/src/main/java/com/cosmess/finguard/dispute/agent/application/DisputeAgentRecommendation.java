package com.cosmess.finguard.dispute.agent.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DisputeAgentRecommendation(
        UUID disputeId,
        String action,
        String rationale,
        List<String> evidence
) {

    public DisputeAgentRecommendation {
        Objects.requireNonNull(disputeId, "disputeId is required");
        requireText(action, "action");
        requireText(rationale, "rationale");
        evidence = List.copyOf(Objects.requireNonNull(evidence, "evidence is required"));
        if (evidence.isEmpty()) {
            throw new IllegalArgumentException("evidence must not be empty");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}