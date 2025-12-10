package org.nikolait.crmsystem.controller;

import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.analytics.BestPeriodResponse;
import org.nikolait.crmsystem.dto.analytics.SellerStatsResponse;
import org.nikolait.crmsystem.dto.analytics.enums.PeriodType;
import org.nikolait.crmsystem.service.SellerAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final SellerAnalyticsService analyticsService;

    @GetMapping("/top-seller")
    public SellerStatsResponse getTopSeller(
            @RequestParam PeriodType periodType,
            @RequestParam LocalDate date
    ) {
        return analyticsService.getTopSeller(periodType, date);
    }

    @GetMapping("/sellers-below")
    public List<SellerStatsResponse> getSellersBelow(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam BigDecimal maxTotal
    ) {
        return analyticsService.getSellersWithTotalLessThan(from, to, maxTotal);
    }

    @GetMapping("/sellers/{sellerId}/best-period")
    public BestPeriodResponse getBestPeriod(
            @PathVariable Long sellerId,
            @RequestParam PeriodType periodType
    ) {
        return analyticsService.getBestPeriodForSeller(sellerId, periodType);
    }

}
