package com.cosmess.finguard.payment.adapter.out.persistence;

import com.cosmess.finguard.payment.application.TransactionRepository;
import com.cosmess.finguard.payment.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class PostgresTransactionRepository implements TransactionRepository {

    private final JpaTransactionRepository repository;

    PostgresTransactionRepository(JpaTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return repository.save(TransactionEntity.fromDomain(transaction)).toDomain();
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(entity -> entity.toDomain());
    }
}
