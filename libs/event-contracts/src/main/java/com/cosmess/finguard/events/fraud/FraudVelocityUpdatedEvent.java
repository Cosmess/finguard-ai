package com.cosmess.finguard.events.fraud;

import java.time.Instant;

public record FraudVelocityUpdatedEvent(
        String dimension,
        String dimensionValue,
        long transactionCount,
        Instant windowStart,
        Instant windowEnd
) {
}
