package org.nikolait.crmsystem.controller;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.nikolait.crmsystem.IntegrationTestBase;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class SellerControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldCreateAndGetSellerById() {
        Response sellerCreateResp = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "John Doe",
                        "contactInfo", "john.doe@example.com"
                ))
                .when()
                .post("/sellers")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("name", equalTo("John Doe"))
                .body("contactInfo", equalTo("john.doe@example.com"))
                .extract().response();

        Long sellerId = sellerCreateResp.jsonPath().getLong("id");

        given()
                .pathParam("id", sellerId)
                .when()
                .get("/sellers/{id}")
                .then()
                .statusCode(200)
                .body("id", hasToString(sellerId.toString()))
                .body("name", equalTo("John Doe"))
                .body("contactInfo", equalTo("john.doe@example.com"));
    }

    @Test
    void shouldUpdateSellerAndReflectChanges() {
        Long id = createSeller("Before Update", "before@example.com");

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(Map.of(
                        "contactInfo", "after@example.com"
                ))
                .when()
                .patch("/sellers/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Before Update"))
                .body("contactInfo", equalTo("after@example.com"));

        given()
                .pathParam("id", id)
                .when()
                .get("/sellers/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Before Update"))
                .body("contactInfo", equalTo("after@example.com"));
    }

    @Test
    void shouldSoftDeleteSeller() {
        Long id = createSeller("To Delete", "delete@example.com");

        given()
                .pathParam("id", id)
                .when()
                .delete("/sellers/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", id)
                .when()
                .get("/sellers/{id}")
                .then()
                .statusCode(404);

        given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/sellers")
                .then()
                .statusCode(200)
                .body("content.id", not(hasItem(id.intValue())));
    }

    @Test
    void shouldReturnPagedSellers() {
        createSeller("Paged One", "paged1@example.com");
        createSeller("Paged Two", "paged2@example.com");

        given()
                .queryParam("page", 0)
                .queryParam("size", 1)
                .queryParam("sort", "name,asc")
                .when()
                .get("/sellers")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("size", equalTo(1))
                .body("totalElements", greaterThanOrEqualTo(2))
                .body("totalPages", greaterThanOrEqualTo(2));
    }

    @Test
    void shouldReturn400OnInvalidCreateRequest() {
        Map<String, Object> invalidRequest = Map.of(
                "name", "ab",          // min = 3
                "contactInfo", ""      // min = 5, NotNull
        );

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/sellers")
                .then()
                .statusCode(400)
                .body("type", not(emptyOrNullString()))
                .body("status", equalTo(400))
                .body("errors", notNullValue())
                .body("errors.name", not(emptyOrNullString()))
                .body("errors.contactInfo", not(emptyOrNullString()));
    }

    @Test
    void shouldReturn404WhenSellerNotFound() {
        long notExistingId = 999_999_999L;

        given()
                .pathParam("id", notExistingId)
                .when()
                .get("/sellers/{id}")
                .then()
                .statusCode(404)
                .body("type", not(emptyOrNullString()))
                .body("status", equalTo(404));
    }

    @Test
    void shouldReturn415OnMissingContentType() {
        given()
                .body(Map.of(
                        "name", "No ContentType",
                        "contactInfo", "no.content.type@example.com"
                ))
                .when()
                .post("/sellers")
                .then()
                .statusCode(415);
    }

    private Long createSeller(String name, String contactInfo) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", name,
                        "contactInfo", contactInfo
                ))
                .when()
                .post("/sellers")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

}