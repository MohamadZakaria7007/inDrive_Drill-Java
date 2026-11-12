package com.qakit.framework.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.time.Duration;

/**
 * Environment-driven settings. Lookup order: -DKEY=value > environment variable > .env file.
 * Values are read lazily: a missing value only fails the tests that actually need it.
 */
public final class Settings {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    private static final Settings INSTANCE = new Settings();

    private Settings() {}

    public static Settings get() {
        return INSTANCE;
    }

    public String uiBaseUrl() {
        return stripTrailingSlash(require("UI_BASE_URL"));
    }

    public String uiUsername() {
        return require("UI_USERNAME");
    }

    public String uiPassword() {
        return require("UI_PASSWORD");
    }

    public String testIdAttribute() {
        return optional("TEST_ID_ATTRIBUTE", "data-testid");
    }

    public String browser() {
        return optional("BROWSER", "chrome").toLowerCase();
    }

    public boolean headless() {
        return Boolean.parseBoolean(optional("HEADLESS", "true"));
    }

    public Duration uiTimeout() {
        return Duration.ofSeconds(Long.parseLong(optional("UI_TIMEOUT_SECONDS", "10")));
    }

    public String apiBaseUrl() {
        return stripTrailingSlash(require("API_BASE_URL"));
    }

    public String apiToken() {
        return require("API_TOKEN");
    }

    public Duration apiTimeout() {
        return Duration.ofSeconds(Long.parseLong(optional("API_TIMEOUT_SECONDS", "15")));
    }

    private static String lookup(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(key);
        }
        if (isBlank(value)) {
            value = DOTENV.get(key, null);
        }
        return isBlank(value) ? null : value.trim();
    }

    private static String require(String key) {
        String value = lookup(key);
        if (value == null) {
            throw new IllegalStateException("Setting " + key
                    + " is missing. Add it to .env (see .env.example), export it, or pass -D" + key + "=...");
        }
        return value;
    }

    private static String optional(String key, String fallback) {
        String value = lookup(key);
        return value == null ? fallback : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @Override
    public String toString() { // never print secrets into logs/reports
        return "Settings[browser=" + browser() + ", headless=" + headless() + "]";
    }
}
