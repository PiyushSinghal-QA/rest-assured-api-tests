package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.User;
import com.piyushsinghal.api.utils.ApiEndpoints;
import com.piyushsinghal.api.utils.TestDataProvider;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

/**
 * Tests for GET /users endpoints.
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

    @Test(description = "GET /users - should return list of all users")
    @Story("List all users")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldReturnAllUsers() {
        User[] users = given()
                .when()
                .get(ApiEndpoints.USERS)
                .then()
                .statusCode(200)
                .time(lessThanMillis(2000))
                .extract()
                .as(User[].class);

        assertTrue(users.length > 0, "Users list should not be empty");
        assertEquals(users.length, 10, "JSONPlaceholder should return 10 users");

        // Validate first user has required fields
        User firstUser = users[0];
        assertNotNull(firstUser.getId(), "User ID should not be null");
        assertNotNull(firstUser.getEmail(), "Email should not be null");
        assertTrue(firstUser.getEmail().contains("@"), "Email should be valid format");
    }

    @Test(description = "GET /users/{id} - should return single user by ID")
    @Story("Retrieve single user")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturnSingleUserById() {
        int userId = 2;

        User user = given()
                .pathParam("id", userId)
                .when()
                .get(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        assertEquals(user.getId(), Integer.valueOf(userId), "User ID mismatch");
        assertNotNull(user.getEmail(), "Email should not be null");
        assertTrue(user.getEmail().contains("@"), "Email should be valid format");
        assertNotNull(user.getName(), "Name should not be null");
        assertNotNull(user.getUsername(), "Username should not be null");
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

    @Test(description = "GET /users - should support filtering by query parameter")
    @Story("Query parameter filtering")
    @Severity(SeverityLevel.NORMAL)
    public void shouldFilterUsersByUsername() {
        User[] users = given()
                .queryParam("username", "Bret")
                .when()
                .get(ApiEndpoints.USERS)
                .then()
                .statusCode(200)
                .extract()
                .as(User[].class);

        assertEquals(users.length, 1, "Should find exactly one user with username 'Bret'");
        assertEquals(users[0].getUsername(), "Bret");
    }

    private static org.hamcrest.Matcher<Long> lessThanMillis(long millis) {
        return org.hamcrest.Matchers.lessThan(millis);
    }
}
