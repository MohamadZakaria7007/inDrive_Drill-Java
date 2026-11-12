package com.qakit.tests.api;

import static com.qakit.framework.api.ApiAssertions.assertStatus;
import static org.assertj.core.api.Assertions.assertThat;

import com.qakit.framework.api.Json;
import com.qakit.framework.api.ProductsApi;
import com.qakit.framework.api.models.ProductPage;
import com.qakit.framework.junit.KnownBug;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Invalid filter input on GET /products (A6). */
class PowerToolsProductsNegativeApiTest extends ApiTestBase {

    private final ProductsApi products = new ProductsApi(anonymous);

    @Test
    void unknownCategoryIdReturnsEmptyPage() {
        Response response = products.search(Map.of("by_category", "01ZZZZZZZZZZZZZZZZZZZZZZZZ"));

        assertStatus(response, 200); // a filter with no matches is an empty result, not an error
        ProductPage page = Json.read(response, ProductPage.class);
        assertThat(page.total()).isZero();
        assertThat(page.data()).isEmpty();
    }

    @Test
    @KnownBug(value = "BUG: unsupported sort value returns 500 'Something went wrong' instead of 400",
            failureContains = "but was: 500")
    void unsupportedSortValueIsRejectedAsBadRequest() {
        Response response = products.search(Map.of("by_category_slug", "power-tools", "sort", "bogus,sideways"));

        assertStatus(response, 400);
    }
}
