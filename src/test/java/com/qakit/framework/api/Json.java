package com.qakit.framework.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.Response;
import java.util.Map;

/**
 * Strict JSON (de)serialization = contract checks. Unknown field, missing field, null for a
 * primitive, or "150" where an int is expected -> the test fails with the offending body.
 */
public final class Json {

    private static final ObjectMapper STRICT = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();

    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {};

    private Json() {}

    /** Parse the body into a contract record (fails the test on any mismatch). */
    public static <T> T read(Response response, Class<T> contract) {
        return read(response.asString(), STRICT.getTypeFactory().constructType(contract));
    }

    public static <T> T read(Response response, TypeReference<T> contract) {
        return read(response.asString(), STRICT.getTypeFactory().constructType(contract));
    }

    /** Body as a plain map, for comparing against a payload map. */
    public static Map<String, Object> toMap(Response response) {
        return read(response, MAP);
    }

    public static String write(Object value) {
        try {
            return STRICT.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize " + value, e);
        }
    }

    private static <T> T read(String body, com.fasterxml.jackson.databind.JavaType type) {
        try {
            return STRICT.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Response does not match contract " + type.getTypeName()
                    + ": " + e.getOriginalMessage() + "\nbody: " + abbreviate(body), e);
        }
    }

    static String abbreviate(String body) {
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
