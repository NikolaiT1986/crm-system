package org.nikolait.crmsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.nikolait.crmsystem.model.enums.PaymentType;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotNull
        @Positive
        Long sellerId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        PaymentType paymentType
) {
}
