package com.piyushsinghal.api.utils;

/**
 * ApiEndpoints - Centralizes all API paths.
 *
 * If a URL changes, it's updated in one place instead of hunting through test files.
 */
public final class ApiEndpoints {

    private ApiEndpoints() {
        // Prevent instantiation
    }

    // User endpoints
    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/users/{id}";

    // Post endpoints (used for POST/PUT/PATCH/DELETE demos)
    public static final String POSTS = "/posts";
    public static final String POST_BY_ID = "/posts/{id}";

    // Comments
    public static final String COMMENTS_BY_POST = "/posts/{id}/comments";
}
