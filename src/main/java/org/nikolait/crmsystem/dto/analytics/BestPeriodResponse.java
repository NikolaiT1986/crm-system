package org.nikolait.crmsystem.dto.analytics;

import org.nikolait.crmsystem.dto.analytics.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BestPeriodResponse(
        Long sellerId,
        PeriodType periodType,
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        long transactionCount,
        BigDecimal totalAmount
) {
}
