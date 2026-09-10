package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.fraud.detection.domain.RiskLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudCase(
        UUID id,
        UUID transactionId,
        String customerId,
        BigDecimal amount,
        String currency,
        int score,
        RiskLevel riskLevel,
        List<String> triggeredRules,
        Instant createdAt
) {
}
