package com.piyushsinghal.api.tests;

import com.piyushsinghal.api.base.BaseTest;
import com.piyushsinghal.api.models.Post;
import com.piyushsinghal.api.utils.ApiEndpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Tests for PUT, PATCH, and DELETE /posts/{id} endpoints.
 */
@Epic("Post Management API")
@Feature("Update and Delete Posts")
public class UpdateDeletePostTest extends BaseTest {

    @Test(description = "PUT /posts/{id} - should fully update an existing post")
    @Story("Full update with PUT")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldUpdatePostWithPut() {
        Post updated = Post.builder()
                .id(1)
                .userId(1)
                .title("Updated Title via PUT")
                .body("Fully replaced content.")
                .build();

        Post response = given()
                .pathParam("id", 1)
                .body(updated)
                .when()
                .put(ApiEndpoints.POST_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        assertEquals(response.getTitle(), updated.getTitle());
        assertEquals(response.getBody(), updated.getBody());
        assertEquals(response.getId(), Integer.valueOf(1));
    }

    @Test(description = "PATCH /posts/{id} - should partially update a post")
    @Story("Partial update with PATCH")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldPartiallyUpdatePostWithPatch() {
        Post partialUpdate = Post.builder()
                .title("Only the title changes")
                .build();

        Post response = given()
                .pathParam("id", 1)
                .body(partialUpdate)
                .when()
                .patch(ApiEndpoints.POST_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

        assertEquals(response.getTitle(), "Only the title changes");
        // Body should still exist from the original post (not overwritten)
        assertNotNull(response.getBody(), "Body should remain after partial update");
    }

    @Test(description = "DELETE /posts/{id} - should delete a post and return 200")
    @Story("Delete post")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDeletePost() {
        given()
                .pathParam("id", 1)
                .when()
                .delete(ApiEndpoints.POST_BY_ID)
                .then()
                .statusCode(200);
    }
}
