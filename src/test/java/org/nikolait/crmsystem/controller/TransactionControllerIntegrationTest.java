package org.nikolait.crmsystem.controller;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nikolait.crmsystem.IntegrationTestBase;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.enums.PaymentType;
import org.nikolait.crmsystem.model.enums.TransactionStatus;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class TransactionControllerIntegrationTest extends IntegrationTestBase {

    private Long sellerId;

    @BeforeEach
    void setup() {
        super.setUpRestAssured();
        Seller seller = new Seller();
        seller.setName("Test Seller");
        seller.setContactInfo("test@domain.com");
        seller.setDeleted(false);
        sellerRepository.save(seller);
        sellerId = seller.getId();
    }

    @Test
    void testCreateTransaction() {
        Response transactionResp = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "sellerId", sellerId,
                        "amount", 100.50,
                        "paymentType", PaymentType.CASH
                ))
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", not(emptyOrNullString()))
                .body("sellerId", hasToString(sellerId.toString()))
                .body("amount", equalTo(100.50f))
                .body("paymentType", equalTo(PaymentType.CASH.name()))
                .extract().response();

        Long transactionId = transactionResp.jsonPath().getLong("id");

        given()
                .when()
                .get("/transactions/{id}", transactionId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", hasToString(transactionId.toString()))
                .body("sellerId", hasToString(sellerId.toString()))
                .body("amount", equalTo(100.50f))
                .body("paymentType", equalTo(PaymentType.CASH.name()));
    }

    @Test
    void testCompleteTransaction() {
        Response transactionResp = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "sellerId", sellerId,
                        "amount", 100.50,
                        "paymentType", PaymentType.CASH
                ))
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Long transactionId = transactionResp.jsonPath().getLong("id");

        given()
                .when()
                .post("/transactions/{id}/complete", transactionId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo(TransactionStatus.COMPLETED.name()));
    }

    @Test
    void testGetAllTransactions() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "sellerId", sellerId,
                        "amount", 100.50,
                        "paymentType", PaymentType.CASH
                ))
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .get("/transactions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", iterableWithSize(1));
    }

    @Test
    public void testGetTransactionsBySeller() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "sellerId", sellerId,
                        "amount", 100.50,
                        "paymentType", PaymentType.CASH.name()
                ))
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .param("sellerId", sellerId)
                .param("page", 0)
                .param("size", 10)
                .when()
                .get("/transactions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", iterableWithSize(1))
                .body("content[0].sellerId", hasToString(sellerId.toString()));
    }
}