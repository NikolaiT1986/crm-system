package org.nikolait.crmsystem;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ExtendWith(SpringExtension.class)
public abstract class IntegrationTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected SellerRepository sellerRepository;

    @BeforeEach
    protected void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    protected void tearDown() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
        RestAssured.reset();
    }
}
