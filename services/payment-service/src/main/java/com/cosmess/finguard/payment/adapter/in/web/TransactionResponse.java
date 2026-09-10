package com.cosmess.finguard.payment.adapter.in.web;

import com.cosmess.finguard.payment.domain.PaymentMethod;
import com.cosmess.finguard.payment.domain.Transaction;
import com.cosmess.finguard.payment.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String merchantId,
        String customerId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String deviceId,
        String ipAddress,
        TransactionStatus status,
        Instant createdAt
) {

    static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.merchantId(),
                transaction.customerId(),
                transaction.amount(),
                transaction.currency(),
                transaction.paymentMethod(),
                transaction.deviceId(),
                transaction.ipAddress(),
                transaction.status(),
                transaction.createdAt()
        );
    }
}
