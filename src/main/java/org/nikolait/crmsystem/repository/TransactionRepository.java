package org.nikolait.crmsystem.repository;

import org.nikolait.crmsystem.model.Transaction;
import org.nikolait.crmsystem.model.projection.SellerBestPeriodProjection;
import org.nikolait.crmsystem.model.projection.SellerSalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySellerId(Long sellerId, Pageable pageable);

    @Query("""
            SELECT t.seller.id AS sellerId,
                   t.seller.name AS sellerName,
                   SUM(t.amount) AS totalAmount
            FROM Transaction t
            WHERE t.status = 'COMPLETED'
              AND t.transactionDate BETWEEN :from AND :to
            GROUP BY t.seller.id, t.seller.name
            ORDER BY totalAmount DESC
            """)
    List<SellerSalesProjection> findTopSellersByPeriod(
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
            SELECT t.seller.id AS sellerId,
                   t.seller.name AS sellerName,
                   SUM(t.amount) AS totalAmount
            FROM Transaction t
            WHERE t.status = 'COMPLETED'
              AND t.transactionDate BETWEEN :from AND :to
            GROUP BY t.seller.id, t.seller.name
            HAVING SUM(t.amount) < :maxTotal
            ORDER BY totalAmount ASC
            """)
    List<SellerSalesProjection> findSellersWithTotalAmountLessThan(
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal maxTotal
    );

    @Query(value = """
            SELECT
                date_trunc(:unit, t.transaction_date) AS periodStart,
                COUNT(*)                              AS txCount,
                SUM(t.amount)                         AS totalAmount
            FROM transactions t
            WHERE t.seller_id = :sellerId
              AND t.status = 'COMPLETED'
            GROUP BY periodStart
            ORDER BY totalAmount DESC
            LIMIT 1
            """,
            nativeQuery = true)
    SellerBestPeriodProjection findBestPeriodForSeller(
            Long sellerId,
            String unit
    );
}
