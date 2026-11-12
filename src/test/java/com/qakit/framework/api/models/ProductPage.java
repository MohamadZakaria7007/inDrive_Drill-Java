package com.qakit.framework.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Paginated envelope of GET /products. from/to are null on an empty page, hence Integer. */
public record ProductPage(
        @JsonProperty("current_page") int currentPage,
        List<Product> data,
        Integer from,
        @JsonProperty("last_page") int lastPage,
        @JsonProperty("per_page") int perPage,
        Integer to,
        int total) {}
