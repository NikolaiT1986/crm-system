package org.nikolait.crmsystem.dto;

import jakarta.validation.constraints.Size;

public record SellerUpdateRequest(
        @Size(min = 3, max = 255)
        String name,

        @Size(min = 5, max = 500)
        String contactInfo
) {
}
