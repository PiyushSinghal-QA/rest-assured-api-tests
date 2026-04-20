package com.piyushsinghal.api.utils;

/**
 * ApiEndpoints - Centralizes all API paths.
 *
 * If a URL changes, it's updated in one place instead of hunting through test files.
 * This is the Page Object pattern applied to REST endpoints.
 */
public final class ApiEndpoints {

    private ApiEndpoints() {
        // Prevent instantiation
    }

    // User endpoints
    public static final String USERS = "/api/users";
    public static final String USER_BY_ID = "/api/users/{id}";

    // Auth endpoints
    public static final String LOGIN = "/api/login";
    public static final String REGISTER = "/api/register";

    // Resource endpoints
    public static final String RESOURCES = "/api/unknown";
    public static final String RESOURCE_BY_ID = "/api/unknown/{id}";
}
