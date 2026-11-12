package com.qakit.framework.api;

import com.qakit.framework.config.Settings;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.Duration;

/**
 * Immutable RestAssured client: base URI, JSON, timeouts, one-line logging in one place.
 * {@link #withHeader}/{@link #withBearerToken} return a NEW client, so anonymous and authorized
 * clients never share state. Build one service class per resource on top of {@link #request()}:
 *
 * <pre>
 * public Response create(Object payload) { return client.request().body(Json.write(payload)).post("/users"); }
 * </pre>
 */
public final class ApiClient {

    private final RequestSpecification spec;

    private ApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public static ApiClient anonymous(Settings settings) {
        return new ApiClient(new RequestSpecBuilder()
                .setBaseUri(settings.apiBaseUrl())
                .setContentType(ContentType.JSON)
                // Exact value on purpose: ContentType.JSON expands to 4 MIME types, which some
                // APIs reject (restful-booker answers 418).
                .setAccept("application/json")
                .setConfig(withTimeout(settings.apiTimeout()))
                .addFilter(new OneLineLogFilter())
                .build());
    }

    public ApiClient withHeader(String name, String value) {
        return new ApiClient(new RequestSpecBuilder().addRequestSpecification(spec).addHeader(name, value).build());
    }

    public ApiClient withBearerToken(String token) {
        return withHeader("Authorization", "Bearer " + token);
    }

    public RequestSpecification request() {
        return RestAssured.given().spec(spec);
    }

    private static RestAssuredConfig withTimeout(Duration timeout) {
        int millis = Math.toIntExact(timeout.toMillis());
        return RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", millis)
                .setParam("http.socket.timeout", millis));
    }
}
