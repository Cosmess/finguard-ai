package com.cosmess.finguard.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final String merchantId;
    private final String customerId;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentMethod paymentMethod;
    private final String deviceId;
    private final String ipAddress;
    private final TransactionStatus status;
    private final Instant createdAt;

    private Transaction(
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
        this.id = Objects.requireNonNull(id);
        this.merchantId = requireText(merchantId, "merchantId");
        this.customerId = requireText(customerId, "customerId");
        this.amount = requirePositiveAmount(amount);
        this.currency = requireCurrency(currency);
        this.paymentMethod = Objects.requireNonNull(paymentMethod);
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Transaction create(
            String merchantId,
            String customerId,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String deviceId,
            String ipAddress,
            Instant createdAt
    ) {
        return new Transaction(
                UUID.randomUUID(),
                merchantId,
                customerId,
                amount,
                currency,
                paymentMethod,
                deviceId,
                ipAddress,
                TransactionStatus.CREATED,
                createdAt
        );
    }

    public static Transaction restore(
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
        return new Transaction(id, merchantId, customerId, amount, currency, paymentMethod, deviceId, ipAddress, status, createdAt);
    }

    public UUID id() {
        return id;
    }

    public String merchantId() {
        return merchantId;
    }

    public String customerId() {
        return customerId;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public String deviceId() {
        return deviceId;
    }

    public String ipAddress() {
        return ipAddress;
    }

    public TransactionStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    private static BigDecimal requirePositiveAmount(BigDecimal value) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return value;
    }

    private static String requireCurrency(String value) {
        String currency = requireText(value, "currency").toUpperCase();
        if (!currency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("currency must use ISO-4217 alpha-3 format");
        }
        return currency;
    }
}
