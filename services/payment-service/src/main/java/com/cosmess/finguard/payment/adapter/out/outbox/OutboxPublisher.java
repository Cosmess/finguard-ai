package com.cosmess.finguard.payment.adapter.out.outbox;

import com.cosmess.finguard.payment.application.ClockProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "payment.outbox", name = "publishing-enabled", havingValue = "true")
class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ClockProvider clockProvider;
    private final int maxAttempts;
    private final Counter publishedCounter;
    private final Counter failedCounter;

    OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ClockProvider clockProvider,
            @Value("${payment.outbox.max-attempts}") int maxAttempts,
            MeterRegistry meterRegistry
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.clockProvider = clockProvider;
        this.maxAttempts = maxAttempts;
        this.publishedCounter = Counter.builder("finguard.outbox.events.published")
                .tag("service", "payment-service")
                .register(meterRegistry);
        this.failedCounter = Counter.builder("finguard.outbox.events.failed")
                .tag("service", "payment-service")
                .register(meterRegistry);
    }

    @Scheduled(fixedDelayString = "${payment.outbox.fixed-delay}")
    @Transactional
    void publishPendingEvents() {
        for (OutboxEvent event : outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            try {
                publish(event);
                event.markPublished(clockProvider.now());
                publishedCounter.increment();
            } catch (Exception exception) {
                event.markFailed(exception.getMessage(), maxAttempts);
                failedCounter.increment();
            }
        }
    }

    private void publish(OutboxEvent event) {
        try {
            kafkaTemplate.send(event.topic(), event.aggregateId().toString(), event.payload()).get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not publish outbox event " + event.eventType(), exception);
        }
    }
}
