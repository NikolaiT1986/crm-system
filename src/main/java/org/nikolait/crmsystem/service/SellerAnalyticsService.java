package org.nikolait.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.nikolait.crmsystem.dto.analytics.BestPeriodResponse;
import org.nikolait.crmsystem.dto.analytics.SellerStatsResponse;
import org.nikolait.crmsystem.dto.analytics.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for analytics operations related to sellers and their transactions.
 */
public interface SellerAnalyticsService {

    /**
     * Returns the most productive seller for a period derived from {@code baseDate}.
     *
     * <p>The start and end of the period are calculated according to the specified
     * {@link PeriodType}. A seller is considered the most productive if the sum of all
     * their completed transactions within that period is greater than that of any other seller.</p>
     *
     * @param periodType the period definition used to calculate boundaries; must not be {@code null}
     * @param baseDate   the reference date for determining the period range; must not be {@code null}
     * @return statistics for the most productive seller for this period
     * @throws EntityNotFoundException if no sellers have completed transactions within this period
     */
    SellerStatsResponse getTopSeller(PeriodType periodType, LocalDate baseDate);

    /**
     * Returns sellers whose total completed transaction amount for the specified time range
     * is strictly less than {@code maxTotal}.
     *
     * <p>The method does not infer any period semantics on its own; it works directly with
     * the provided datetime interval.</p>
     *
     * @param from     start of the interval; must not be {@code null}
     * @param to       end of the interval; must not be {@code null} and must be after {@code from}
     * @param maxTotal exclusive upper bound for total transaction amount; must not be {@code null}
     * @return list of matching sellers; an empty list if none qualify
     */
    List<SellerStatsResponse> getSellersWithTotalLessThan(
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal maxTotal
    );

    /**
     * Returns the seller's most profitable period of the specified {@link PeriodType}.
     *
     * <p>All transactions of the seller are grouped into periods determined by {@link PeriodType}.
     * Among all such periods, the method selects the one with the highest total transaction amount.
     * The response also includes the number of transactions in that period and the calculated
     * start and end boundaries of the period.</p>
     *
     * @param sellerId   identifier of the seller; must not be {@code null}
     * @param periodType the type of period used to group transactions (e.g., DAY, MONTH); must not be {@code null}
     * @return information about the seller's most profitable period
     * @throws EntityNotFoundException if the seller does not exist, is deleted, or has no completed transactions
     */
    BestPeriodResponse getBestPeriodForSeller(Long sellerId, PeriodType periodType);
}
