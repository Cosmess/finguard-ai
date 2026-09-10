package com.cosmess.finguard.payment.adapter.out.outbox;

import com.cosmess.finguard.payment.application.ClockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ClockProvider clockProvider
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.clockProvider = clockProvider;
    }

    @Scheduled(fixedDelayString = "${payment.outbox.fixed-delay}")
    @Transactional
    void publishPendingEvents() {
        for (OutboxEvent event : outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            publish(event);
            event.markPublished(clockProvider.now());
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
