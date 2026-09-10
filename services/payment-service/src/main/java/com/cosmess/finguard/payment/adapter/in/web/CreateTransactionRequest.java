package com.cosmess.finguard.payment.adapter.in.web;

import com.cosmess.finguard.payment.application.TransactionCommand;
import com.cosmess.finguard.payment.domain.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank
        @Size(max = 80)
        String merchantId,

        @NotBlank
        @Size(max = 80)
        String customerId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "[A-Za-z]{3}")
        String currency,

        @NotNull
        PaymentMethod paymentMethod,

        @Size(max = 120)
        String deviceId,

        @Size(max = 45)
        String ipAddress
) {

    TransactionCommand toCommand() {
        return new TransactionCommand(merchantId, customerId, amount, currency, paymentMethod, deviceId, ipAddress);
    }
}
