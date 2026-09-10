package com.cosmess.finguard.payment.adapter.out.outbox;

import com.cosmess.finguard.events.EventEnvelope;
import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.cosmess.finguard.payment.application.TransactionEventRecorder;
import com.cosmess.finguard.payment.domain.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class PostgresTransactionEventRecorder implements TransactionEventRecorder {

    private static final String TRANSACTION_CREATED = "TransactionCreated";

    private final OutboxEventRepository outboxEventRepository;
    private final JsonPayloadSerializer jsonPayloadSerializer;
    private final String topic;

    PostgresTransactionEventRecorder(
            OutboxEventRepository outboxEventRepository,
            JsonPayloadSerializer jsonPayloadSerializer,
            @Value("${payment.outbox.topic}") String topic
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.jsonPayloadSerializer = jsonPayloadSerializer;
        this.topic = topic;
    }

    @Override
    public void recordTransactionCreated(Transaction transaction) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                transaction.id(),
                transaction.merchantId(),
                transaction.customerId(),
                transaction.amount(),
                transaction.currency(),
                transaction.paymentMethod().name(),
                transaction.deviceId(),
                transaction.ipAddress(),
                transaction.status().name(),
                transaction.createdAt()
        );

        EventEnvelope<TransactionCreatedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                TRANSACTION_CREATED,
                transaction.id().toString(),
                transaction.createdAt(),
                event
        );

        outboxEventRepository.save(OutboxEvent.pending(
                transaction.id(),
                TRANSACTION_CREATED,
                topic,
                jsonPayloadSerializer.serialize(envelope),
                transaction.createdAt()
        ));
    }
}
