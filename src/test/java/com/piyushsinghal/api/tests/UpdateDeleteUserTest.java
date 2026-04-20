package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.User;
import com.piyushsinghal.api.utils.ApiEndpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Tests for PUT, PATCH, and DELETE /users/{id} endpoints.
 */
@Epic("User Management API")
@Feature("Update and Delete Users")
public class UpdateDeleteUserTest extends BaseTest {

    @Test(description = "PUT /users/{id} - should fully update an existing user")
    @Story("Full update with PUT")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldUpdateUserWithPut() {
        User updated = User.builder()
                .name("Piyush Updated")
                .job("Senior SDET")
                .build();

        User response = given()
                .pathParam("id", 2)
                .body(updated)
                .when()
                .put(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        assertEquals(response.getName(), updated.getName());
        assertEquals(response.getJob(), updated.getJob());
        assertNotNull(response.getUpdatedAt(), "updatedAt should be set after PUT");
    }

    @Test(description = "PATCH /users/{id} - should partially update a user")
    @Story("Partial update with PATCH")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldPartiallyUpdateUserWithPatch() {
        User partialUpdate = User.builder()
                .job("Lead SDET")
                .build();

        User response = given()
                .pathParam("id", 2)
                .body(partialUpdate)
                .when()
                .patch(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        assertEquals(response.getJob(), "Lead SDET");
        assertNotNull(response.getUpdatedAt());
    }

    @Test(description = "DELETE /users/{id} - should delete a user and return 204")
    @Story("Delete user")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDeleteUser() {
        given()
                .pathParam("id", 2)
                .when()
                .delete(ApiEndpoints.USER_BY_ID)
                .then()
                .statusCode(204);
    }
}
