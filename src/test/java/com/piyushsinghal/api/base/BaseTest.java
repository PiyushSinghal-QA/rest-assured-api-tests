package com.piyushsinghal.api.base;

import com.piyushsinghal.api.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

/**
 * BaseTest - Shared setup for all test classes.
 *
 * Configures:
 *   - Base URL from ConfigManager
 *   - Default content-type (JSON)
 *   - Request/response logging (for debugging failures)
 *   - Allure reporting filter (captures every request/response in the report)
 *   - Timeouts
 *
 * Test classes extend this to inherit the baseline configuration.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static RequestSpecification requestSpec;
    protected final ConfigManager config = ConfigManager.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        logger.info("==================== TEST SUITE STARTING ====================");
        logger.info("Base URL: {}", config.getBaseUrl());
        logger.info("Timeout:  {}ms", config.getTimeout());

        // Global timeout configuration
        RestAssuredConfig restConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.getTimeout())
                        .setParam("http.socket.timeout", config.getTimeout()));

        // Base request spec - reused across all tests
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("x-api-key", config.getApiKey())  // ReqRes requires this header
                .setConfig(restConfig)
                .addFilter(new AllureRestAssured())  // Auto-captures requests in Allure
                .build();

        RestAssured.requestSpecification = requestSpec;
    }
}
