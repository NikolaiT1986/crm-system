package org.nikolait.crmsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SellerCreateRequest(
        @NotNull
        @Size(min = 3, max = 255)
        String name,

        @NotNull
        @Size(min = 5, max = 500)
        String contactInfo
) {
}
