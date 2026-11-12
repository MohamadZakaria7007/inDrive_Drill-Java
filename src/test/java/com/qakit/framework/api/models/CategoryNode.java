package com.qakit.framework.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Contract of one node in GET /categories/tree. */
public record CategoryNode(
        String id,
        String name,
        String slug,
        @JsonProperty("parent_id") String parentId,
        @JsonProperty("sub_categories") List<CategoryNode> subCategories) {}
