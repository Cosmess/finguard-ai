package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.cosmess.finguard.fraud.detection.domain.FraudRulesEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FraudDetectionServiceTest {

    @Test
    void createsAndPublishesFraudCaseForMediumOrHigherRisk() {
        InMemoryFraudCaseRepository fraudCases = new InMemoryFraudCaseRepository();
        InMemoryProcessedTransactionRepository processed = new InMemoryProcessedTransactionRepository();
        InMemoryFraudEventPublisher publisher = new InMemoryFraudEventPublisher();
        FraudDetectionService service = service(fraudCases, processed, publisher);

        service.evaluate(transaction(UUID.randomUUID(), new BigDecimal("7500.00"), "new-device-1"));

        assertThat(fraudCases.saved).hasSize(1);
        assertThat(publisher.published).hasSize(1);
        assertThat(fraudCases.saved.getFirst().riskLevel().name()).isEqualTo("HIGH");
    }

    @Test
    void ignoresDuplicateTransaction() {
        InMemoryFraudCaseRepository fraudCases = new InMemoryFraudCaseRepository();
        InMemoryProcessedTransactionRepository processed = new InMemoryProcessedTransactionRepository();
        InMemoryFraudEventPublisher publisher = new InMemoryFraudEventPublisher();
        FraudDetectionService service = service(fraudCases, processed, publisher);
        UUID transactionId = UUID.randomUUID();

        service.evaluate(transaction(transactionId, new BigDecimal("7500.00"), "new-device-1"));
        service.evaluate(transaction(transactionId, new BigDecimal("7500.00"), "new-device-1"));

        assertThat(fraudCases.saved).hasSize(1);
        assertThat(publisher.published).hasSize(1);
    }

    @Test
    void marksLowRiskTransactionAsProcessedWithoutPublishingFraudCase() {
        InMemoryFraudCaseRepository fraudCases = new InMemoryFraudCaseRepository();
        InMemoryProcessedTransactionRepository processed = new InMemoryProcessedTransactionRepository();
        InMemoryFraudEventPublisher publisher = new InMemoryFraudEventPublisher();
        FraudDetectionService service = service(fraudCases, processed, publisher);
        UUID transactionId = UUID.randomUUID();

        service.evaluate(transaction(transactionId, new BigDecimal("100.00"), "known-device-1"));

        assertThat(processed.exists(transactionId)).isTrue();
        assertThat(fraudCases.saved).isEmpty();
        assertThat(publisher.published).isEmpty();
    }

    private FraudDetectionService service(
            InMemoryFraudCaseRepository fraudCases,
            InMemoryProcessedTransactionRepository processed,
            InMemoryFraudEventPublisher publisher
    ) {
        return new FraudDetectionService(
                new FraudRulesEngine(customerId -> Optional.empty()),
                fraudCases,
                processed,
                publisher,
                () -> Instant.parse("2026-09-10T12:00:00Z")
        );
    }

    private TransactionCreatedEvent transaction(UUID transactionId, BigDecimal amount, String deviceId) {
        return new TransactionCreatedEvent(
                transactionId,
                "merchant-1",
                "customer-1",
                amount,
                "BRL",
                "CREDIT_CARD",
                deviceId,
                "127.0.0.1",
                "CREATED",
                Instant.parse("2026-09-10T02:30:00Z")
        );
    }

    private static class InMemoryFraudCaseRepository implements FraudCaseRepository {

        private final List<FraudCase> saved = new ArrayList<>();

        @Override
        public FraudCase save(FraudCase fraudCase) {
            saved.add(fraudCase);
            return fraudCase;
        }
    }

    private static class InMemoryProcessedTransactionRepository implements ProcessedTransactionRepository {

        private final Set<UUID> processed = new HashSet<>();

        @Override
        public boolean exists(UUID transactionId) {
            return processed.contains(transactionId);
        }

        @Override
        public void markProcessed(UUID transactionId) {
            processed.add(transactionId);
        }
    }

    private static class InMemoryFraudEventPublisher implements FraudEventPublisher {

        private final List<FraudCase> published = new ArrayList<>();

        @Override
        public void publishFraudSuspected(FraudCase fraudCase) {
            published.add(fraudCase);
        }
    }
}
