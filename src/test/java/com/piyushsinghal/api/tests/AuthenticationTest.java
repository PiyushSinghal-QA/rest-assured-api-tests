package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.LoginRequest;
import com.piyushsinghal.api.models.LoginResponse;
import com.piyushsinghal.api.utils.ApiEndpoints;
import com.piyushsinghal.api.utils.TestDataProvider;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Tests for /login and /register endpoints.
 *
 * Demonstrates:
 *   - Auth flow testing (positive + negative)
 *   - Token extraction and validation
 *   - Data-driven negative scenarios
 */
@Epic("Authentication API")
@Feature("User Login & Registration")
public class AuthenticationTest extends BaseTest {

    @Test(description = "POST /login - should return token for valid credentials")
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldLoginWithValidCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        LoginResponse response = given()
                .body(loginRequest)
                .when()
                .post(ApiEndpoints.LOGIN)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class);

        assertNotNull(response.getToken(), "Token should be returned on successful login");
        assertFalse(response.getToken().isEmpty(), "Token should not be empty");
        assertNull(response.getError(), "Error should be null on success");
    }

    @Test(description = "POST /register - should register a new user successfully")
    @Story("User registration")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRegisterNewUser() {
        LoginRequest registerRequest = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("pistol")
                .build();

        given()
                .body(registerRequest)
                .when()
                .post(ApiEndpoints.REGISTER)
                .then()
                .statusCode(200)
                .body("token", org.hamcrest.Matchers.notNullValue())
                .body("id", org.hamcrest.Matchers.notNullValue());
    }

    @Test(description = "POST /login - should return 400 when password is missing")
    @Story("Login validation")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WhenPasswordMissing() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("peter@klaven")
                .build();

        given()
                .body(loginRequest)
                .when()
                .post(ApiEndpoints.LOGIN)
                .then()
                .statusCode(400)
                .body("error", org.hamcrest.Matchers.equalTo("Missing password"));
    }

    @Test(description = "POST /login - should return 400 when email is missing")
    @Story("Login validation")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WhenEmailMissing() {
        LoginRequest loginRequest = LoginRequest.builder()
                .password("cityslicka")
                .build();

        given()
                .body(loginRequest)
                .when()
                .post(ApiEndpoints.LOGIN)
                .then()
                .statusCode(400)
                .body("error", org.hamcrest.Matchers.equalTo("Missing email or username"));
    }

    @Test(description = "POST /register - should return 400 when password is missing")
    @Story("Registration validation")
    @Severity(SeverityLevel.NORMAL)
    public void shouldFailRegistrationWithoutPassword() {
        LoginRequest registerRequest = LoginRequest.builder()
                .email("sydney@fife")
                .build();

        given()
                .body(registerRequest)
                .when()
                .post(ApiEndpoints.REGISTER)
                .then()
                .statusCode(400)
                .body("error", org.hamcrest.Matchers.equalTo("Missing password"));
    }
}
