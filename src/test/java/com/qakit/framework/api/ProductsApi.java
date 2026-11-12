package com.qakit.framework.api;

import static com.qakit.framework.api.ApiAssertions.assertStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qakit.framework.api.models.Brand;
import com.qakit.framework.api.models.CategoryNode;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;

/**
 * Service object for the Toolshop catalogue (https://api.practicesoftwaretesting.com).
 * Methods return the raw Response so tests can assert status codes, incl. negative cases.
 */
public final class ProductsApi {

    private final ApiClient client;

    public ProductsApi(ApiClient client) {
        this.client = client;
    }

    /** GET /products with filters such as by_category_slug, by_category, by_brand, sort, page. */
    public Response search(Map<String, ?> filters) {
        return client.request().queryParams(filters).get("/products");
    }

    public Response categoryTree(String categorySlug) {
        return client.request().queryParam("by_category_slug", categorySlug).get("/categories/tree");
    }

    public Response brands() {
        return client.request().get("/brands");
    }

    public String brandId(String brandName) {
        Response response = brands();
        assertStatus(response, 200);
        return Json.read(response, new TypeReference<List<Brand>>() {}).stream()
                .filter(brand -> brand.name().equals(brandName))
                .map(Brand::id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No brand named '" + brandName + "'"));
    }

    /** Resolves a subcategory id by slug, so tests never hard-code generated ids. */
    public String subcategoryId(String categorySlug, String subcategorySlug) {
        Response response = categoryTree(categorySlug);
        assertStatus(response, 200);
        return Json.read(response, new TypeReference<List<CategoryNode>>() {}).stream()
                .flatMap(root -> root.subCategories().stream())
                .filter(sub -> sub.slug().equals(subcategorySlug))
                .map(CategoryNode::id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No subcategory '" + subcategorySlug + "' under " + categorySlug));
    }
}
