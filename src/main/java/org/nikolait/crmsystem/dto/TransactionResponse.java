package org.nikolait.crmsystem.dto;

import org.nikolait.crmsystem.model.enums.PaymentType;
import org.nikolait.crmsystem.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long sellerId,
        BigDecimal amount,
        PaymentType paymentType,
        LocalDateTime transactionDate,
        TransactionStatus status,
        LocalDateTime createdAt
) {
}
