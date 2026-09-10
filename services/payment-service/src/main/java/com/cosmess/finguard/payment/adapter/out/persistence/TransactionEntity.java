package com.cosmess.finguard.payment.adapter.out.persistence;

import com.cosmess.finguard.payment.domain.PaymentMethod;
import com.cosmess.finguard.payment.domain.Transaction;
import com.cosmess.finguard.payment.domain.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
class TransactionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String merchantId;

    @Column(nullable = false, length = 80)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentMethod paymentMethod;

    @Column(length = 120)
    private String deviceId;

    @Column(length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected TransactionEntity() {
    }

    private TransactionEntity(Transaction transaction) {
        this.id = transaction.id();
        this.merchantId = transaction.merchantId();
        this.customerId = transaction.customerId();
        this.amount = transaction.amount();
        this.currency = transaction.currency();
        this.paymentMethod = transaction.paymentMethod();
        this.deviceId = transaction.deviceId();
        this.ipAddress = transaction.ipAddress();
        this.status = transaction.status();
        this.createdAt = transaction.createdAt();
    }

    static TransactionEntity fromDomain(Transaction transaction) {
        return new TransactionEntity(transaction);
    }

    Transaction toDomain() {
        return Transaction.restore(
                id,
                merchantId,
                customerId,
                amount,
                currency,
                paymentMethod,
                deviceId,
                ipAddress,
                status,
                createdAt
        );
    }
}
