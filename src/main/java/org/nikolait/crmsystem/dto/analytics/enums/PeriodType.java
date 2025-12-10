package org.nikolait.crmsystem.dto.analytics.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PeriodType {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year");

    private final String postgresUnit;

    public String toPostgresUnit() {
        return postgresUnit;
    }
}
