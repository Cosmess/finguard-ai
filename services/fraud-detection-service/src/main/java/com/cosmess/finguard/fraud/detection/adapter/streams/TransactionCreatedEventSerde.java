package com.cosmess.finguard.fraud.detection.adapter.streams;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

class TransactionCreatedEventSerde implements Serde<TransactionCreatedEvent> {

    private final ObjectMapper objectMapper;

    TransactionCreatedEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<TransactionCreatedEvent> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (IOException exception) {
                throw new IllegalArgumentException("Could not serialize transaction event", exception);
            }
        };
    }

    @Override
    public Deserializer<TransactionCreatedEvent> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, TransactionCreatedEvent.class);
            } catch (IOException exception) {
                throw new IllegalArgumentException("Could not deserialize transaction event", exception);
            }
        };
    }
}
