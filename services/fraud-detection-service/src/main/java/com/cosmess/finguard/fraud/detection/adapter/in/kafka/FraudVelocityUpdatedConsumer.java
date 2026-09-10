package com.cosmess.finguard.fraud.detection.adapter.in.kafka;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.fraud.FraudVelocityUpdatedEvent;
import com.cosmess.finguard.fraud.detection.application.VelocityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "fraud.detection", name = "enabled", havingValue = "true")
class FraudVelocityUpdatedConsumer {

    private final VelocityService velocityService;
    private final ObjectMapper objectMapper;

    FraudVelocityUpdatedConsumer(VelocityService velocityService, ObjectMapper objectMapper) {
        this.velocityService = velocityService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${fraud.detection.velocity-topic}", groupId = "${spring.kafka.consumer.group-id}")
    void onMessage(String message) {
        velocityService.update(readEvent(message).payload());
    }

    private EventEnvelope<FraudVelocityUpdatedEvent> readEvent(String message) {
        try {
            return objectMapper.readValue(message, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid FraudVelocityUpdated event", exception);
        }
    }
}
