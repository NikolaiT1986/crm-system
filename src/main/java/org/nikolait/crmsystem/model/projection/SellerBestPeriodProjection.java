package org.nikolait.crmsystem.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SellerBestPeriodProjection {
    LocalDateTime getPeriodStart();

    Long getTxCount();

    BigDecimal getTotalAmount();
}
