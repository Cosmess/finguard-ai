package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import com.cosmess.finguard.fraud.detection.application.ClockProvider;
import com.cosmess.finguard.fraud.detection.application.ProcessedTransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class PostgresProcessedTransactionRepository implements ProcessedTransactionRepository {

    private final JpaProcessedTransactionRepository repository;
    private final ClockProvider clockProvider;

    PostgresProcessedTransactionRepository(JpaProcessedTransactionRepository repository, ClockProvider clockProvider) {
        this.repository = repository;
        this.clockProvider = clockProvider;
    }

    @Override
    public boolean exists(UUID transactionId) {
        return repository.existsById(transactionId);
    }

    @Override
    public void markProcessed(UUID transactionId) {
        repository.save(new ProcessedTransactionEntity(transactionId, clockProvider.now()));
    }
}
