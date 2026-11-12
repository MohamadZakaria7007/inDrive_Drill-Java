package com.qakit.framework.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;

/** Assertions whose failure message is enough to debug without re-running. */
public final class ApiAssertions {

    private ApiAssertions() {}

    public static void assertStatus(Response response, int expected) {
        assertThat(response.statusCode())
                .as("HTTP status (body: %s)", Json.abbreviate(response.asString()))
                .isEqualTo(expected);
    }
}
