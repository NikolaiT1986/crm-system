package org.nikolait.crmsystem.dto.analytics;

import java.math.BigDecimal;

public record SellerStatsResponse(
        Long sellerId,
        String sellerName,
        BigDecimal totalAmount
) {
}
