package com.cosmess.finguard.payment.application;

import com.cosmess.finguard.payment.domain.PaymentMethod;

import java.math.BigDecimal;

public record TransactionCommand(
        String merchantId,
        String customerId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String deviceId,
        String ipAddress
) {
}
