package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.User;
import com.piyushsinghal.api.models.UserListResponse;
import com.piyushsinghal.api.utils.ApiEndpoints;
import com.piyushsinghal.api.utils.TestDataProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

/**
 * Tests for GET /users endpoints (list and single user retrieval).
 *
 * Demonstrates:
 *   - Status code assertions
 *   - Response body deserialization to POJOs
 *   - Response time validation (performance)
 *   - JSON schema validation (contract testing)
 *   - Data-driven negative tests
 *   - Allure annotations for rich reporting
 */
@Epic("User Management API")
@Feature("Retrieve Users")
public class GetUsersTest extends BaseTest {

    @Test(description = "GET /users - should return paginated list of users")
    @Story("List users with pagination")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldReturnPaginatedUsersList() {
        UserListResponse response = given()
                .queryParam("page", 1)
                .when()
                .get(ApiEndpoints.USERS)
                .then()
                .statusCode(200)
                .time(lessThanMillis(2000))
                .extract()
                .as(UserListResponse.class);

        assertEquals(response.getPage(), 1, "Page number mismatch");
        assertTrue(response.getPer_page() > 0, "per_page should be positive");
        assertTrue(response.getTotal() > 0, "total should be positive");
        assertNotNull(response.getData(), "Users list should not be null");
        assertFalse(response.getData().isEmpty(), "Users list should not be empty");
    }

    @Test(description = "GET /users/{id} - should return single user by ID")
    @Story("Retrieve single user")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturnSingleUserById() {
        int userId = 2;

        Response response = given()
                .pathParam("id", userId)
                .when()
                .get(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        User user = response.jsonPath().getObject("data", User.class);
        assertEquals(user.getId(), Integer.valueOf(userId), "User ID mismatch");
        assertNotNull(user.getEmail(), "Email should not be null");
        assertTrue(user.getEmail().contains("@"), "Email should be valid format");
        assertNotNull(user.getFirst_name(), "First name should not be null");
        assertNotNull(user.getLast_name(), "Last name should not be null");
    }

    @Test(description = "GET /users/{id} - should validate response against JSON schema")
    @Story("Contract testing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures the API response structure matches the agreed contract. If backend changes the response shape, this test catches it.")
    public void shouldMatchUserJsonSchema() {
        given()
                .pathParam("id", 1)
                .when()
                .get(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"));
    }

    @Test(dataProvider = "invalidUserIds", dataProviderClass = TestDataProvider.class,
          description = "GET /users/{id} - should return 404 for invalid user IDs")
    @Story("Negative testing")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn404ForInvalidUserId(int invalidId, String scenarioName) {
        given()
                .pathParam("id", invalidId)
                .when()
                .get(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(404);
    }

    @Test(dataProvider = "paginationScenarios", dataProviderClass = TestDataProvider.class,
          description = "GET /users - should handle different page numbers")
    @Story("Pagination")
    @Severity(SeverityLevel.NORMAL)
    public void shouldHandlePagination(int pageNumber, int expectedStatus) {
        given()
                .queryParam("page", pageNumber)
                .when()
                .get(ApiEndpoints.USERS)
                .then()
                .statusCode(expectedStatus)
                .body("page", org.hamcrest.Matchers.equalTo(pageNumber));
    }

    private static org.hamcrest.Matcher<Long> lessThanMillis(long millis) {
        return org.hamcrest.Matchers.lessThan(millis);
    }
}
