package com.qakit.framework.data;

import java.util.UUID;

/**
 * Test-data builders: valid by default, the test overrides only what it cares about.
 * Add one builder per entity, returning a mutable Map (negative tests remove/put fields)
 * or a record.
 */
public final class TestData {

    private TestData() {}

    /** Unique per call: keeps parallel runs and re-runs from colliding on data. */
    public static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String uniqueEmail() {
        return "qa+" + uniqueSuffix() + "@example.com";
    }
}
