package com.cosmess.finguard.payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    @Test
    void createsTransactionWithCreatedStatusAndUppercaseCurrency() {
        Transaction transaction = Transaction.create(
                "merchant-1",
                "customer-1",
                new BigDecimal("125.50"),
                "brl",
                PaymentMethod.CREDIT_CARD,
                "device-1",
                "127.0.0.1",
                Instant.parse("2026-09-10T12:00:00Z")
        );

        assertThat(transaction.id()).isNotNull();
        assertThat(transaction.currency()).isEqualTo("BRL");
        assertThat(transaction.status()).isEqualTo(TransactionStatus.CREATED);
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> Transaction.create(
                "merchant-1",
                "customer-1",
                BigDecimal.ZERO,
                "BRL",
                PaymentMethod.CREDIT_CARD,
                null,
                null,
                Instant.parse("2026-09-10T12:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must be positive");
    }
}
