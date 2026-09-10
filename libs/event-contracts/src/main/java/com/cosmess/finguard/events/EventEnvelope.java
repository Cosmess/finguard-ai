package com.cosmess.finguard.events;

import java.time.Instant;

public record EventEnvelope<T>(
        String eventId,
        String eventType,
        String correlationId,
        Instant occurredAt,
        T payload
) {
}
