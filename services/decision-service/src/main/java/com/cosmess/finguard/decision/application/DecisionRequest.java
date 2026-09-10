package com.cosmess.finguard.decision.application;

import java.util.Objects;
import java.util.UUID;

public record DecisionRequest(
        UUID transactionId,
        int fraudScore,
        String fraudRiskLevel,
        String aiRecommendation,
        String disputeStatus
) {

    public DecisionRequest {
        Objects.requireNonNull(transactionId, "transactionId is required");
        requireText(fraudRiskLevel, "fraudRiskLevel");
        requireText(aiRecommendation, "aiRecommendation");
        if (fraudScore < 0 || fraudScore > 100) {
            throw new IllegalArgumentException("fraudScore must be between 0 and 100");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}