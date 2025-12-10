package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nikolait.crmsystem.dto.analytics.BestPeriodResponse;
import org.nikolait.crmsystem.dto.analytics.SellerStatsResponse;
import org.nikolait.crmsystem.dto.analytics.enums.PeriodType;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.projection.SellerBestPeriodProjection;
import org.nikolait.crmsystem.model.projection.SellerSalesProjection;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerAnalyticsServiceImplUnitTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SellerAnalyticsServiceImpl service;

    @Test
    void getTopSeller_returnsTopSellerAndUsesCorrectPeriod_forDay() {
        LocalDate baseDate = LocalDate.of(2025, Month.JANUARY, 15);

        SellerSalesProjection projection = mock(SellerSalesProjection.class);
        when(projection.getSellerId()).thenReturn(1L);
        when(projection.getSellerName()).thenReturn("Alice");
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("150.00"));

        when(transactionRepository.findTopSellersByPeriod(any(), any()))
                .thenReturn(List.of(projection));

        SellerStatsResponse response = service.getTopSeller(PeriodType.DAY, baseDate);

        assertThat(response.sellerId()).isEqualTo(1L);
        assertThat(response.sellerName()).isEqualTo("Alice");
        assertThat(response.totalAmount()).isEqualByComparingTo("150.00");

        ArgumentCaptor<LocalDateTime> fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(transactionRepository).findTopSellersByPeriod(fromCaptor.capture(), toCaptor.capture());

        LocalDateTime expectedFrom = baseDate.atStartOfDay();
        LocalDateTime expectedTo = baseDate.plusDays(1).atStartOfDay();

        assertThat(fromCaptor.getValue()).isEqualTo(expectedFrom);
        assertThat(toCaptor.getValue()).isEqualTo(expectedTo);
    }

    @Test
    void getTopSeller_throwsIfNoStats() {
        when(transactionRepository.findTopSellersByPeriod(any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.getTopSeller(PeriodType.DAY, LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No sellers found for period");
    }

    @Test
    void getSellersWithTotalLessThan_mapsProjectionsToResponses() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 2, 1, 0, 0);
        BigDecimal maxTotal = new BigDecimal("1000.00");

        SellerSalesProjection p1 = mock(SellerSalesProjection.class);
        when(p1.getSellerId()).thenReturn(1L);
        when(p1.getSellerName()).thenReturn("Alice");
        when(p1.getTotalAmount()).thenReturn(new BigDecimal("500.00"));

        SellerSalesProjection p2 = mock(SellerSalesProjection.class);
        when(p2.getSellerId()).thenReturn(2L);
        when(p2.getSellerName()).thenReturn("Bob");
        when(p2.getTotalAmount()).thenReturn(new BigDecimal("750.00"));

        when(transactionRepository.findSellersWithTotalAmountLessThan(from, to, maxTotal))
                .thenReturn(List.of(p1, p2));

        List<SellerStatsResponse> result =
                service.getSellersWithTotalLessThan(from, to, maxTotal);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).sellerId()).isEqualTo(1L);
        assertThat(result.get(0).sellerName()).isEqualTo("Alice");
        assertThat(result.get(0).totalAmount()).isEqualByComparingTo("500.00");

        assertThat(result.get(1).sellerId()).isEqualTo(2L);
        assertThat(result.get(1).sellerName()).isEqualTo("Bob");
        assertThat(result.get(1).totalAmount()).isEqualByComparingTo("750.00");
    }

    @Test
    void getSellersWithTotalLessThan_returnsEmptyListWhenNoData() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 2, 1, 0, 0);
        BigDecimal maxTotal = new BigDecimal("1000.00");

        when(transactionRepository.findSellersWithTotalAmountLessThan(from, to, maxTotal))
                .thenReturn(List.of());

        List<SellerStatsResponse> result =
                service.getSellersWithTotalLessThan(from, to, maxTotal);

        assertThat(result).isEmpty();
    }

    @Test
    void getBestPeriodForSeller_returnsResponseAndComputesEndCorrectly_forMonth() {
        Long sellerId = 10L;
        Seller seller = new Seller();
        seller.setId(sellerId);

        SellerBestPeriodProjection projection = mock(SellerBestPeriodProjection.class);
        LocalDateTime periodStart = LocalDateTime.of(2025, 1, 1, 0, 0);

        when(projection.getPeriodStart()).thenReturn(periodStart);
        when(projection.getTxCount()).thenReturn(5L);
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("123.45"));

        when(sellerRepository.findByIdAndDeletedFalse(sellerId))
                .thenReturn(Optional.of(seller));

        when(transactionRepository.findBestPeriodForSeller(sellerId, "month"))
                .thenReturn(projection);

        BestPeriodResponse response =
                service.getBestPeriodForSeller(sellerId, PeriodType.MONTH);

        assertThat(response.sellerId()).isEqualTo(sellerId);
        assertThat(response.periodType()).isEqualTo(PeriodType.MONTH);
        assertThat(response.periodStart()).isEqualTo(periodStart);
        assertThat(response.periodEnd()).isEqualTo(periodStart.plusMonths(1));
        assertThat(response.transactionCount()).isEqualTo(5L);
        assertThat(response.totalAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void getBestPeriodForSeller_throwsIfSellerNotFound() {
        when(sellerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBestPeriodForSeller(1L, PeriodType.DAY))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Seller not found: 1");
    }

    @Test
    void getBestPeriodForSeller_throwsIfNoTransactions() {
        Long sellerId = 10L;
        Seller seller = new Seller();
        seller.setId(sellerId);

        when(sellerRepository.findByIdAndDeletedFalse(sellerId))
                .thenReturn(Optional.of(seller));

        when(transactionRepository.findBestPeriodForSeller(eq(sellerId), anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> service.getBestPeriodForSeller(sellerId, PeriodType.DAY))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No transactions for seller: " + sellerId);
    }
}