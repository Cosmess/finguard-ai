package com.cosmess.finguard.fraud.detection.application;

import java.time.Instant;

public record VelocitySnapshot(
        String dimension,
        String dimensionValue,
        long transactionCount,
        Instant windowStart,
        Instant windowEnd,
        Instant updatedAt
) {
}
