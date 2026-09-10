package com.cosmess.finguard.fraud.detection.adapter.streams;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionCreatedEventSerdeTest {

    @Test
    void serializesAndDeserializesTransactionCreatedEvent() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        TransactionCreatedEventSerde serde = new TransactionCreatedEventSerde(objectMapper);
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(),
                "merchant-1",
                "customer-1",
                new BigDecimal("100.00"),
                "BRL",
                "CREDIT_CARD",
                "device-1",
                "127.0.0.1",
                "CREATED",
                Instant.parse("2026-09-10T12:00:00Z")
        );

        byte[] bytes = serde.serializer().serialize("transaction.created", event);
        TransactionCreatedEvent restored = serde.deserializer().deserialize("transaction.created", bytes);

        assertThat(restored).isEqualTo(event);
    }
}
