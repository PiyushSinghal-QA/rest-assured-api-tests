package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.User;
import com.piyushsinghal.api.utils.ApiEndpoints;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Tests for POST /users endpoint (user creation).
 *
 * Demonstrates:
 *   - POJO-based request serialization (no raw JSON strings)
 *   - Response body assertions
 *   - Timestamp validation
 */
@Epic("User Management API")
@Feature("Create Users")
public class CreateUserTest extends BaseTest {

    @Test(description = "POST /users - should create a new user with valid data")
    @Story("Create user with complete data")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldCreateUserWithValidData() {
        User newUser = User.builder()
                .name("Piyush Singhal")
                .job("SDET")
                .build();

        Response response = given()
                .body(newUser)
                .when()
                .post(ApiEndpoints.USERS)
                .then()
                .statusCode(201)
                .time(lessThanMillis(2000))
                .extract()
                .response();

        User created = response.as(User.class);
        assertEquals(created.getName(), newUser.getName(), "Name mismatch");
        assertEquals(created.getJob(), newUser.getJob(), "Job mismatch");
        assertNotNull(created.getId(), "ID should be generated");
        assertNotNull(created.getCreatedAt(), "createdAt timestamp should be set");
    }

    @Test(description = "POST /users - should create user with only name")
    @Story("Create user with minimal data")
    @Severity(SeverityLevel.NORMAL)
    public void shouldCreateUserWithMinimalData() {
        User newUser = User.builder()
                .name("Test User")
                .build();

        User created = given()
                .body(newUser)
                .when()
                .post(ApiEndpoints.USERS)
                .then()
                .statusCode(201)
                .extract()
                .as(User.class);

        assertEquals(created.getName(), "Test User");
        assertNotNull(created.getId());
    }

    @Test(description = "POST /users - should accept empty body and return 201")
    @Story("Edge cases")
    @Severity(SeverityLevel.MINOR)
    @Description("ReqRes is lenient - real APIs would return 400 here. This test documents the observed behavior.")
    public void shouldHandleEmptyBodyGracefully() {
        given()
                .body("{}")
                .when()
                .post(ApiEndpoints.USERS)
                .then()
                .statusCode(201);
    }

    private static org.hamcrest.Matcher<Long> lessThanMillis(long millis) {
        return org.hamcrest.Matchers.lessThan(millis);
    }
}
