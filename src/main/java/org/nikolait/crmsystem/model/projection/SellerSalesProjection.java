package org.nikolait.crmsystem.model.projection;

import java.math.BigDecimal;

public interface SellerSalesProjection {
    Long getSellerId();

    String getSellerName();

    BigDecimal getTotalAmount();
}
