package com.qakit.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

import com.qakit.framework.junit.Smoke;
import com.qakit.framework.junit.UiTest;
import com.qakit.framework.ui.pages.CategoryPage;
import com.qakit.framework.ui.pages.SortOption;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;

/** User journeys on /category/power-tools (U1, U2, U4). Filter rules are proven in the API tests. */
@UiTest
class PowerToolsListingTest {

    /** Seed data of the public demo shop. */
    private static final int POWER_TOOLS_COUNT = 8;

    private CategoryPage page;

    @BeforeEach
    void openPowerTools(WebDriver driver) {
        page = new CategoryPage(driver).open("power-tools");
    }

    @Test
    @Smoke
    void listingShowsTitleAndEveryPowerToolWithNameAndPrice() {
        assertThat(page.title()).isEqualTo("Category: Power Tools");
        await().untilAsserted(() -> {
            assertThat(page.productNames()).hasSize(POWER_TOOLS_COUNT).doesNotContain("");
            assertThat(page.productPrices()).hasSize(POWER_TOOLS_COUNT).allMatch(price -> price.signum() > 0);
        });
    }

    @ParameterizedTest
    @MethodSource("priceSorts")
    void sortByPriceReordersAllProducts(SortOption option, Comparator<BigDecimal> expectedOrder) {
        await().untilAsserted(() -> assertThat(page.productPrices()).hasSize(POWER_TOOLS_COUNT));
        List<BigDecimal> before = page.productPrices();
        List<BigDecimal> expected = before.stream().sorted(expectedOrder).toList();
        assertThat(before).as("precondition: default order must differ, or this test proves nothing")
                .isNotEqualTo(expected);

        page.sortBy(option);

        await().untilAsserted(() -> assertThat(page.productPrices()).isEqualTo(expected));
    }

    static Stream<Arguments> priceSorts() {
        return Stream.of(
                argumentSet("price low to high", SortOption.PRICE_ASC, Comparator.<BigDecimal>naturalOrder()),
                argumentSet("price high to low", SortOption.PRICE_DESC, Comparator.<BigDecimal>reverseOrder()));
    }

    @Test
    void subcategoryFilterShowsOnlyThatSubcategory() {
        page.toggleCategory("Sander");

        assertThat(page.isCategorySelected("Sander")).isTrue();
        await().untilAsserted(() -> assertThat(page.productNames())
                .containsExactlyInAnyOrder("Sheet Sander", "Belt Sander", "Random Orbit Sander"));
    }

    @Test
    void categoryAndBrandFiltersCombineToNarrowTheList() {
        page.toggleCategory("Drill");
        await().untilAsserted(() -> assertThat(page.productNames()).hasSize(4).allMatch(name -> name.startsWith("Cordless Drill")));

        page.toggleBrand("MightyCraft Hardware");

        await().untilAsserted(() -> assertThat(page.productNames()).containsExactly("Cordless Drill 12V"));
    }

    @ParameterizedTest
    @MethodSource("filtersWithNoMatches")
    void filterWithNoMatchesShowsEmptyState(Consumer<CategoryPage> applyFilter) {
        applyFilter.accept(page);

        await().untilAsserted(() -> {
            assertThat(page.emptyStateMessage()).isEqualTo("There are no products found.");
            assertThat(page.productNames()).isEmpty();
        });
    }

    static Stream<Arguments> filtersWithNoMatches() {
        return Stream.of(
                argumentSet("eco-friendly only (no power tool is eco-friendly)",
                        (Consumer<CategoryPage>) CategoryPage::toggleEcoFriendlyOnly),
                argumentSet("empty subcategory Grinder",
                        (Consumer<CategoryPage>) p -> p.toggleCategory("Grinder")));
    }
}
