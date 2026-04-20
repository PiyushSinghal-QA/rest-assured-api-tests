package com.piyushsinghal.api.utils;

import org.testng.annotations.DataProvider;

/**
 * Centralized test data for data-driven tests.
 *
 * TestNG's @DataProvider lets the same test method run multiple times
 * with different inputs - great for negative test scenarios.
 */
public class TestDataProvider {

    @DataProvider(name = "invalidUserIds")
    public static Object[][] invalidUserIds() {
        return new Object[][] {
                { 999,    "Non-existent user ID" },
                { 0,      "Zero user ID" },
                { 99999,  "Very large user ID" },
        };
    }

    @DataProvider(name = "invalidLoginCredentials")
    public static Object[][] invalidLoginCredentials() {
        return new Object[][] {
                // email, password, expectedErrorContains
                { "peter@klaven",              null,        "Missing password" },
                { null,                        "cityslicka", "Missing email" },
                { "nonexistent@example.com",   "password",   "user not found" },
        };
    }

    @DataProvider(name = "paginationScenarios")
    public static Object[][] paginationScenarios() {
        return new Object[][] {
                // page, expectedStatusCode
                { 1, 200 },
                { 2, 200 },
                { 3, 200 },  // Valid but may return empty data
        };
    }
}
