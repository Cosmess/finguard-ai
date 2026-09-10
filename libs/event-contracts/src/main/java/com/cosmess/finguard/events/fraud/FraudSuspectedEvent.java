package com.cosmess.finguard.events.fraud;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudSuspectedEvent(
        UUID transactionId,
        String customerId,
        BigDecimal amount,
        String currency,
        int score,
        String riskLevel,
        List<String> triggeredRules,
        Instant evaluatedAt
) {
}
