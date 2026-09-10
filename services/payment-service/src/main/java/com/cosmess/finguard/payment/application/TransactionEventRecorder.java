package com.cosmess.finguard.payment.application;

import com.cosmess.finguard.payment.domain.Transaction;

public interface TransactionEventRecorder {

    void recordTransactionCreated(Transaction transaction);
}
