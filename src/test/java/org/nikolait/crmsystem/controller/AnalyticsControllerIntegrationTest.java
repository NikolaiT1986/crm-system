package org.nikolait.crmsystem.controller;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.nikolait.crmsystem.IntegrationTestBase;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.Transaction;
import org.nikolait.crmsystem.model.enums.PaymentType;
import org.nikolait.crmsystem.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalyticsControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void getTopSeller_returnsSellerWithMaxTotalForDay() {
        // arrange
        Seller seller1 = sellerRepository.save(
                Seller.builder()
                        .name("Alice")
                        .contactInfo("alice@example.com")
                        .deleted(false)
                        .build()
        );

        Seller seller2 = sellerRepository.save(
                Seller.builder()
                        .name("Bob")
                        .contactInfo("bob@example.com")
                        .deleted(false)
                        .build()
        );

        LocalDate baseDate = LocalDate.of(2024, 1, 10);

        // seller1: 100 + 200 = 300
        transactionRepository.save(
                Transaction.builder()
                        .seller(seller1)
                        .amount(new BigDecimal("100.00"))
                        .paymentType(PaymentType.CARD)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(baseDate.atTime(10, 0))
                        .build()
        );
        transactionRepository.save(
                Transaction.builder()
                        .seller(seller1)
                        .amount(new BigDecimal("200.00"))
                        .paymentType(PaymentType.CASH)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(baseDate.atTime(15, 30))
                        .build()
        );

        // seller2: 50
        transactionRepository.save(
                Transaction.builder()
                        .seller(seller2)
                        .amount(new BigDecimal("50.00"))
                        .paymentType(PaymentType.TRANSFER)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(baseDate.atTime(11, 0))
                        .build()
        );

        // act + assert
        given()
                .queryParam("periodType", "DAY")
                .queryParam("date", baseDate.toString())
                .when()
                .get("/analytics/top-seller")
                .then()
                .statusCode(200)
                .body("sellerId", hasToString(seller1.getId().toString()))
                .body("sellerName", equalTo("Alice"))
                .body("totalAmount", equalTo(300.0F));
    }

    @Test
    void getSellersBelow_returnsOnlySellersWithTotalLessThanMaxTotal() {
        // arrange
        Seller lowSeller = sellerRepository.save(
                Seller.builder()
                        .name("Low")
                        .contactInfo("low@example.com")
                        .deleted(false)
                        .build()
        );

        Seller highSeller = sellerRepository.save(
                Seller.builder()
                        .name("High")
                        .contactInfo("high@example.com")
                        .deleted(false)
                        .build()
        );

        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);

        // lowSeller: 100
        transactionRepository.save(
                Transaction.builder()
                        .seller(lowSeller)
                        .amount(new BigDecimal("100.00"))
                        .paymentType(PaymentType.CARD)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(from.plusDays(1))
                        .build()
        );

        // highSeller: 500
        transactionRepository.save(
                Transaction.builder()
                        .seller(highSeller)
                        .amount(new BigDecimal("200.00"))
                        .paymentType(PaymentType.CASH)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(from.plusDays(2))
                        .build()
        );
        transactionRepository.save(
                Transaction.builder()
                        .seller(highSeller)
                        .amount(new BigDecimal("300.00"))
                        .paymentType(PaymentType.CARD)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(from.plusDays(3))
                        .build()
        );

        BigDecimal maxTotal = new BigDecimal("200.00");

        // act + assert
        given()
                .queryParam("from", from.toString())
                .queryParam("to", to.toString())
                .queryParam("maxTotal", maxTotal)
                .when()
                .get("/analytics/sellers-below")
                .then()
                .statusCode(200)
                .body("$", iterableWithSize(1))
                .body("[0].sellerId", equalTo(lowSeller.getId().intValue()))
                .body("[0].sellerName", equalTo("Low"))
                .body("[0].totalAmount", equalTo(100.0F));
    }

    @Test
    void getBestPeriod_returnsBestMonthForSeller() {
        // arrange
        Seller seller = sellerRepository.save(
                Seller.builder()
                        .name("Charlie")
                        .contactInfo("charlie@example.com")
                        .deleted(false)
                        .build()
        );

        Long sellerId = seller.getId();

        // January: 2 transactions, amount 300
        LocalDateTime janTx1 = LocalDateTime.of(2024, 1, 10, 10, 0);
        LocalDateTime janTx2 = LocalDateTime.of(2024, 1, 12, 15, 0);

        transactionRepository.save(
                Transaction.builder()
                        .seller(seller)
                        .amount(new BigDecimal("100.00"))
                        .paymentType(PaymentType.CARD)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(janTx1)
                        .build()
        );

        transactionRepository.save(
                Transaction.builder()
                        .seller(seller)
                        .amount(new BigDecimal("200.00"))
                        .paymentType(PaymentType.CASH)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(janTx2)
                        .build()
        );

        // February: 1 transaction, amount 50
        LocalDateTime febTx = LocalDateTime.of(2024, 2, 5, 11, 0);

        transactionRepository.save(
                Transaction.builder()
                        .seller(seller)
                        .amount(new BigDecimal("50.00"))
                        .paymentType(PaymentType.TRANSFER)
                        .status(TransactionStatus.COMPLETED)
                        .transactionDate(febTx)
                        .build()
        );

        // expect the best period for MONTH to be January 2024.
        LocalDateTime expectedStart = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2024, 2, 1, 0, 0);

        // act + assert
        Response bestPeriodResp = given()
                .pathParam("sellerId", sellerId)
                .queryParam("periodType", "MONTH")
                .when()
                .get("/analytics/sellers/{sellerId}/best-period")
                .then()
                .statusCode(200)
                .body("sellerId", equalTo(sellerId.intValue()))
                .body("periodType", equalTo("MONTH"))
                .body("periodStart", not(emptyOrNullString()))
                .body("periodEnd", not(emptyOrNullString()))
                .body("transactionCount", equalTo(2))
                .body("totalAmount", equalTo(300.0F))
                .extract().response();

        LocalDateTime periodStart = LocalDateTime.parse(bestPeriodResp.jsonPath().getString("periodStart"));
        LocalDateTime periodEnd = LocalDateTime.parse(bestPeriodResp.jsonPath().getString("periodEnd"));

        assertEquals(expectedStart, periodStart);
        assertEquals(expectedEnd, periodEnd);
    }
}