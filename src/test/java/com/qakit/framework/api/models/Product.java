package com.qakit.framework.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/** Contract of one item in GET /products. Parsed strictly by Json.read (see Json for the rules). */
public record Product(
        String id,
        String name,
        String description,
        BigDecimal price,
        @JsonProperty("is_location_offer") boolean isLocationOffer,
        @JsonProperty("is_rental") boolean isRental,
        @JsonProperty("co2_rating") String co2Rating,
        @JsonProperty("in_stock") boolean inStock,
        @JsonProperty("is_eco_friendly") boolean isEcoFriendly,
        @JsonProperty("product_image") Image productImage,
        CategoryRef category,
        BrandRef brand) {

    public record CategoryRef(String id, String name, String slug) {}

    public record BrandRef(String id, String name) {}

    public record Image(
            String id,
            @JsonProperty("by_name") String byName,
            @JsonProperty("by_url") String byUrl,
            @JsonProperty("source_name") String sourceName,
            @JsonProperty("source_url") String sourceUrl,
            @JsonProperty("file_name") String fileName,
            String title) {}
}
