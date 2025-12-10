package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.analytics.BestPeriodResponse;
import org.nikolait.crmsystem.dto.analytics.SellerStatsResponse;
import org.nikolait.crmsystem.dto.analytics.enums.PeriodType;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.projection.SellerBestPeriodProjection;
import org.nikolait.crmsystem.model.projection.SellerSalesProjection;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.repository.TransactionRepository;
import org.nikolait.crmsystem.service.SellerAnalyticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerAnalyticsServiceImpl implements SellerAnalyticsService {

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public SellerStatsResponse getTopSeller(PeriodType periodType, LocalDate baseDate) {
        LocalDateTime from = getStart(periodType, baseDate);
        LocalDateTime to = getEnd(periodType, baseDate);

        List<SellerSalesProjection> stats =
                transactionRepository.findTopSellersByPeriod(from, to);

        if (stats.isEmpty()) {
            throw new EntityNotFoundException("No sellers found for period");
        }

        SellerSalesProjection top = stats.getFirst();
        return new SellerStatsResponse(
                top.getSellerId(),
                top.getSellerName(),
                top.getTotalAmount()
        );
    }

    @Override
    public List<SellerStatsResponse> getSellersWithTotalLessThan(
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal maxTotal
    ) {
        return transactionRepository.findSellersWithTotalAmountLessThan(from, to, maxTotal)
                .stream()
                .map(sp -> new SellerStatsResponse(
                        sp.getSellerId(),
                        sp.getSellerName(),
                        sp.getTotalAmount()
                ))
                .toList();
    }

    @Override
    public BestPeriodResponse getBestPeriodForSeller(Long sellerId, PeriodType periodType) {
        Seller seller = sellerRepository.findByIdAndDeletedFalse(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found: " + sellerId));

        SellerBestPeriodProjection projection =
                transactionRepository.findBestPeriodForSeller(sellerId, periodType.toPostgresUnit());

        if (projection == null) {
            throw new EntityNotFoundException("No transactions for seller: " + sellerId);
        }

        LocalDateTime start = projection.getPeriodStart();
        LocalDateTime end = addPeriod(start, periodType);

        return new BestPeriodResponse(
                seller.getId(),
                periodType,
                start,
                end,
                projection.getTxCount(),
                projection.getTotalAmount()
        );
    }

    private LocalDateTime getStart(PeriodType type, LocalDate baseDate) {
        return switch (type) {
            case DAY -> baseDate.atStartOfDay();
            case WEEK -> baseDate.with(DayOfWeek.MONDAY).atStartOfDay();
            case MONTH -> baseDate.withDayOfMonth(1).atStartOfDay();
            case QUARTER -> baseDate.with(baseDate.getMonth().firstMonthOfQuarter())
                    .withDayOfMonth(1).atStartOfDay();
            case YEAR -> baseDate.withDayOfYear(1).atStartOfDay();
        };
    }

    private LocalDateTime getEnd(PeriodType type, LocalDate baseDate) {
        return switch (type) {
            case DAY -> baseDate.plusDays(1).atStartOfDay();
            case WEEK -> baseDate.with(DayOfWeek.MONDAY).plusWeeks(1).atStartOfDay();
            case MONTH -> baseDate.withDayOfMonth(1).plusMonths(1).atStartOfDay();
            case QUARTER -> baseDate.with(baseDate.getMonth().firstMonthOfQuarter())
                    .withDayOfMonth(1).plusMonths(3).atStartOfDay();
            case YEAR -> baseDate.withDayOfYear(1).plusYears(1).atStartOfDay();
        };
    }

    private LocalDateTime addPeriod(LocalDateTime start, PeriodType type) {
        return switch (type) {
            case DAY -> start.plusDays(1);
            case WEEK -> start.plusWeeks(1);
            case MONTH -> start.plusMonths(1);
            case QUARTER -> start.plusMonths(3);
            case YEAR -> start.plusYears(1);
        };
    }
}
