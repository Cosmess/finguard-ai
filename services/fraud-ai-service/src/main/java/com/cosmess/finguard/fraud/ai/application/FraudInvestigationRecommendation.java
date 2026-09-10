package com.cosmess.finguard.fraud.ai.application;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record FraudInvestigationRecommendation(
        UUID transactionId,
        String riskLevel,
        String recommendedAction,
        String rationale,
        List<String> evidence,
        Instant generatedAt
) {

    public FraudInvestigationRecommendation {
        Objects.requireNonNull(transactionId, "transactionId is required");
        Objects.requireNonNull(riskLevel, "riskLevel is required");
        Objects.requireNonNull(recommendedAction, "recommendedAction is required");
        Objects.requireNonNull(rationale, "rationale is required");
        Objects.requireNonNull(evidence, "evidence is required");
        Objects.requireNonNull(generatedAt, "generatedAt is required");
        evidence = List.copyOf(evidence);
        if (evidence.isEmpty()) {
            throw new IllegalArgumentException("evidence must not be empty");
        }
    }
}