package org.nikolait.crmsystem.dto;

import java.time.LocalDateTime;

public record SellerResponse(
        Long id,
        String name,
        String contactInfo,
        LocalDateTime registrationDate
) {
}
