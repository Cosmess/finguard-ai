package com.cosmess.finguard.decision.application;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DecisionResult(
        UUID decisionId,
        UUID transactionId,
        DecisionOutcome outcome,
        String rationale,
        List<String> signals,
        Instant decidedAt
) {

    public DecisionResult {
        Objects.requireNonNull(decisionId, "decisionId is required");
        Objects.requireNonNull(transactionId, "transactionId is required");
        Objects.requireNonNull(outcome, "outcome is required");
        Objects.requireNonNull(rationale, "rationale is required");
        signals = List.copyOf(Objects.requireNonNull(signals, "signals is required"));
        Objects.requireNonNull(decidedAt, "decidedAt is required");
    }
}