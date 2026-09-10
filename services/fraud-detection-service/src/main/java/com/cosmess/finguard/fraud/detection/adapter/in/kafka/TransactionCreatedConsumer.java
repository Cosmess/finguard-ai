package com.cosmess.finguard.fraud.detection.adapter.in.kafka;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.cosmess.finguard.fraud.detection.application.FraudDetectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "fraud.detection", name = "enabled", havingValue = "true")
class TransactionCreatedConsumer {

    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;
    private final Counter consumedCounter;

    TransactionCreatedConsumer(FraudDetectionService fraudDetectionService, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.fraudDetectionService = fraudDetectionService;
        this.objectMapper = objectMapper;
        this.consumedCounter = Counter.builder("finguard.kafka.messages.consumed")
                .tag("service", "fraud-detection-service")
                .tag("topic", "transaction.created")
                .register(meterRegistry);
    }

    @KafkaListener(topics = "${fraud.detection.input-topic}", groupId = "${spring.kafka.consumer.group-id}")
    void onMessage(String message) {
        fraudDetectionService.evaluate(readEvent(message).payload());
        consumedCounter.increment();
    }

    private EventEnvelope<TransactionCreatedEvent> readEvent(String message) {
        try {
            return objectMapper.readValue(message, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid TransactionCreated event", exception);
        }
    }
}
