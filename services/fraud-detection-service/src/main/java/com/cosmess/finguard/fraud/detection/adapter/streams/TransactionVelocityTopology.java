package com.cosmess.finguard.fraud.detection.adapter.streams;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.fraud.FraudVelocityUpdatedEvent;
import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Configuration
@EnableKafkaStreams
@ConditionalOnProperty(prefix = "fraud.detection", name = "enabled", havingValue = "true")
class TransactionVelocityTopology {

    private final ObjectMapper objectMapper;
    private final String inputTopic;
    private final String outputTopic;
    private final Duration window;

    TransactionVelocityTopology(
            ObjectMapper objectMapper,
            @Value("${fraud.detection.input-topic}") String inputTopic,
            @Value("${fraud.detection.velocity-topic}") String outputTopic,
            @Value("${fraud.detection.velocity-window}") Duration window
    ) {
        this.objectMapper = objectMapper;
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.window = window;
    }

    @Bean
    KStream<String, String> transactionVelocityStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> transactions = streamsBuilder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));

        transactions
                .mapValues(this::readTransaction)
                .filter((key, event) -> event != null)
                .selectKey((key, event) -> "customer:" + event.customerId())
                .groupByKey(Grouped.with(Serdes.String(), new TransactionCreatedEventSerde(objectMapper)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(window))
                .count()
                .toStream()
                .mapValues(this::toVelocityEvent)
                .to(outputTopic, Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class, window.toMillis()), Serdes.String()));

        return transactions;
    }

    private TransactionCreatedEvent readTransaction(String message) {
        try {
            EventEnvelope<TransactionCreatedEvent> envelope = objectMapper.readValue(message, new TypeReference<>() {
            });
            return envelope.payload();
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private String toVelocityEvent(Windowed<String> key, Long count) {
        String dimensionValue = key.key().replaceFirst("^customer:", "");
        FraudVelocityUpdatedEvent event = new FraudVelocityUpdatedEvent(
                "CUSTOMER",
                dimensionValue,
                count,
                Instant.ofEpochMilli(key.window().start()),
                Instant.ofEpochMilli(key.window().end())
        );

        EventEnvelope<FraudVelocityUpdatedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                "FraudVelocityUpdated",
                dimensionValue,
                Instant.now(),
                event
        );

        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize velocity event", exception);
        }
    }
}
