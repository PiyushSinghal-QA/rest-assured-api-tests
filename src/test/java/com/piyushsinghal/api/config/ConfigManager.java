package com.piyushsinghal.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager - Singleton for reading configuration from properties files.
 *
 * Supports environment-specific configs via the TEST_ENV system property:
 *   mvn test -DTEST_ENV=staging    -> loads config-staging.properties
 *   mvn test                       -> defaults to config-dev.properties
 *
 * Values can also be overridden via system properties, making this CI-friendly.
 */
public class ConfigManager {

    private static final String DEFAULT_ENV = "dev";
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        String env = System.getProperty("TEST_ENV", DEFAULT_ENV);
        String configFile = String.format("config-%s.properties", env);

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + configFile);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config file: " + configFile, e);
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Get a config value - system properties take precedence over file properties.
     * This lets CI override values without changing files: -Dbase.url=https://...
     */
    public String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        return properties.getProperty(key);
    }

    public String getBaseUrl() {
        return get("base.url");
    }

    public String getApiKey() {
        return get("api.key");
    }

    public int getTimeout() {
        return Integer.parseInt(get("timeout"));
    }
}
