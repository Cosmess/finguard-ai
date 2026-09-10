package com.cosmess.finguard.fraud.detection.adapter.out.kafka;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.fraud.FraudSuspectedEvent;
import com.cosmess.finguard.fraud.detection.application.FraudCase;
import com.cosmess.finguard.fraud.detection.application.FraudEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
class KafkaFraudEventPublisher implements FraudEventPublisher {

    private static final String FRAUD_SUSPECTED = "FraudSuspected";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String outputTopic;

    KafkaFraudEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${fraud.detection.output-topic}") String outputTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outputTopic = outputTopic;
    }

    @Override
    public void publishFraudSuspected(FraudCase fraudCase) {
        FraudSuspectedEvent event = new FraudSuspectedEvent(
                fraudCase.transactionId(),
                fraudCase.customerId(),
                fraudCase.amount(),
                fraudCase.currency(),
                fraudCase.score(),
                fraudCase.riskLevel().name(),
                fraudCase.triggeredRules(),
                fraudCase.createdAt()
        );

        EventEnvelope<FraudSuspectedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                FRAUD_SUSPECTED,
                fraudCase.transactionId().toString(),
                fraudCase.createdAt(),
                event
        );

        try {
            String payload = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(outputTopic, fraudCase.transactionId().toString(), payload).get(10, TimeUnit.SECONDS);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize fraud event", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not publish fraud event", exception);
        }
    }
}
