package com.cosmess.finguard.fraud.ai.adapter.in.kafka;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.fraud.FraudSuspectedEvent;
import com.cosmess.finguard.fraud.ai.application.FraudInvestigationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "fraud.ai", name = "enabled", havingValue = "true")
public class FraudSuspectedConsumer {

    private final FraudInvestigationService investigationService;
    private final ObjectMapper objectMapper;

    public FraudSuspectedConsumer(FraudInvestigationService investigationService, ObjectMapper objectMapper) {
        this.investigationService = investigationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${fraud.ai.input-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        investigationService.investigate(readEvent(message).payload());
    }

    private EventEnvelope<FraudSuspectedEvent> readEvent(String message) {
        try {
            return objectMapper.readValue(message, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid FraudSuspected event", exception);
        }
    }
}