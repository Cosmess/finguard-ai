package com.cosmess.finguard.fraud.detection.application;

import java.util.UUID;

public interface ProcessedTransactionRepository {

    boolean exists(UUID transactionId);

    void markProcessed(UUID transactionId);
}
