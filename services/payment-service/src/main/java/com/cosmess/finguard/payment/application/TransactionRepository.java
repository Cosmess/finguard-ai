package com.cosmess.finguard.payment.application;

import com.cosmess.finguard.payment.domain.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
