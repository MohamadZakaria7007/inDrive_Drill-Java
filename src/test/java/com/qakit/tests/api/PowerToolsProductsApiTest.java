package com.qakit.tests.api;

import static com.qakit.framework.api.ApiAssertions.assertStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

import com.qakit.framework.api.Json;
import com.qakit.framework.api.ProductsApi;
import com.qakit.framework.api.models.Product;
import com.qakit.framework.api.models.ProductPage;
import com.qakit.framework.junit.Smoke;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Catalogue rules behind the Power Tools listing page, proven at API level (A1-A4). */
class PowerToolsProductsApiTest extends ApiTestBase {

    private static final String POWER_TOOLS = "power-tools";
    private static final Set<String> POWER_TOOL_SUBCATEGORIES = Set.of("grinder", "sander", "saw", "drill");
    /** Seed data of the public demo shop; a count catches a filter that silently drops items. */
    private static final int POWER_TOOLS_COUNT = 8;

    private final ProductsApi products = new ProductsApi(anonymous);

    @Test
    @Smoke
    void powerToolsCategoryReturnsOnlyPowerToolProducts() {
        Response response = products.search(Map.of("by_category_slug", POWER_TOOLS));

        ProductPage page = okPage(response);
        assertThat(page.total()).isEqualTo(POWER_TOOLS_COUNT);
        assertThat(page.data())
                .hasSize(POWER_TOOLS_COUNT)
                .extracting(product -> product.category().slug())
                .isSubsetOf(POWER_TOOL_SUBCATEGORIES);
    }

    @Test
    void subcategoryFilterReturnsOnlyThatSubcategory() {
        String sanderId = products.subcategoryId(POWER_TOOLS, "sander");

        Response response = products.search(Map.of("by_category", sanderId));

        assertThat(okPage(response).data())
                .isNotEmpty()
                .extracting(product -> product.category().slug())
                .containsOnly("sander");
    }

    @Test
    void brandFilterReturnsOnlyThatBrand() {
        String mightyCraftId = products.brandId("MightyCraft Hardware");

        Response response = products.search(Map.of("by_category_slug", POWER_TOOLS, "by_brand", mightyCraftId));

        assertThat(okPage(response).data())
                .isNotEmpty()
                .extracting(product -> product.brand().name())
                .containsOnly("MightyCraft Hardware");
    }

    @ParameterizedTest
    @MethodSource("priceSorts")
    void sortByPriceOrdersAllPowerTools(String sort, Comparator<BigDecimal> expectedOrder) {
        Response response = products.search(Map.of("by_category_slug", POWER_TOOLS, "sort", sort));

        assertThat(okPage(response).data())
                .hasSize(POWER_TOOLS_COUNT)
                .extracting(Product::price)
                .isSortedAccordingTo(expectedOrder);
    }

    static Stream<Arguments> priceSorts() {
        return Stream.of(
                argumentSet("price low to high", "price,asc", Comparator.<BigDecimal>naturalOrder()),
                argumentSet("price high to low", "price,desc", Comparator.<BigDecimal>reverseOrder()));
    }

    private static ProductPage okPage(Response response) {
        assertStatus(response, 200);
        return Json.read(response, ProductPage.class); // strict contract check
    }
}
