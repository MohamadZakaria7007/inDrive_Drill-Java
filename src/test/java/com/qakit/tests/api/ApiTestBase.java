package com.qakit.tests.api;

import com.qakit.framework.api.ApiClient;
import com.qakit.framework.config.Settings;
import com.qakit.framework.junit.ApiTest;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.jupiter.api.AfterEach;

/**
 * Shared clients + guaranteed cleanup. JUnit creates a new instance per test method, so the
 * cleanup stack is per test (parallel-safe). Full example with seeding:
 * C:/Users/moham/Desktop/qa-interview-kit-java/src/test/java/com/qakit/tests/api/ApiTestBase.java
 */
@ApiTest
public abstract class ApiTestBase {

    protected static final Settings SETTINGS = Settings.get();

    /** No credentials. Separate object from {@link #authorized()}. */
    protected final ApiClient anonymous = ApiClient.anonymous(SETTINGS);

    private final Deque<Runnable> cleanups = new ArrayDeque<>();

    /** Replace with a login call if the task provides one (cache the token in a static). */
    protected ApiClient authorized() {
        return anonymous.withBearerToken(SETTINGS.apiToken());
    }

    /** Register teardown for anything a test creates; runs LIFO even when the test fails. */
    protected void cleanupAfterTest(Runnable action) {
        cleanups.push(action);
    }

    @AfterEach
    void runCleanups() {
        while (!cleanups.isEmpty()) {
            try {
                cleanups.pop().run();
            } catch (RuntimeException e) { // best effort: never mask the test result
                System.out.println("cleanup failed: " + e);
            }
        }
    }
}
