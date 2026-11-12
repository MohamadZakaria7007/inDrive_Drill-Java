package com.qakit.framework.ui.pages;

import static com.qakit.framework.ui.Locators.testId;

import com.qakit.framework.ui.Money;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/** Category listing, e.g. /category/power-tools: sort, filters and the product grid. */
public final class CategoryPage extends BasePage {

    private static final By TITLE = testId("page-title");
    private static final By SORT = testId("sort");
    private static final By PRODUCT_NAME = testId("product-name");
    private static final By PRODUCT_PRICE = testId("product-price");
    private static final By EMPTY_STATE = testId("category-empty");
    private static final By ECO_FRIENDLY = testId("eco-friendly-filter");

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the category and waits until the grid has rendered its first product. */
    public CategoryPage open(String categorySlug) {
        navigate("/category/" + categorySlug);
        visible(TITLE);
        visible(PRODUCT_NAME);
        return this;
    }

    public String title() {
        return text(TITLE);
    }

    public CategoryPage sortBy(SortOption option) {
        new Select(visible(SORT)).selectByValue(option.value());
        return this;
    }

    public CategoryPage toggleCategory(String label) {
        click(filterCheckbox("category-", label));
        return this;
    }

    public CategoryPage toggleBrand(String label) {
        click(filterCheckbox("brand-", label));
        return this;
    }

    public CategoryPage toggleEcoFriendlyOnly() {
        click(ECO_FRIENDLY);
        return this;
    }

    public boolean isCategorySelected(String label) {
        return visible(filterCheckbox("category-", label)).isSelected();
    }

    /** Names as rendered right now (may be empty mid-update); poll with await() in tests. */
    public List<String> productNames() {
        return currentTexts(PRODUCT_NAME);
    }

    public List<BigDecimal> productPrices() {
        return currentTexts(PRODUCT_PRICE).stream().map(Money::parse).toList();
    }

    /** Text of the "no products" message, or empty when it is not rendered. */
    public String emptyStateMessage() {
        return currentTexts(EMPTY_STATE).stream().findFirst().map(String::trim).orElse("");
    }

    /** Category/brand checkbox ids are generated ULIDs, so anchor on the visible label instead. */
    private By filterCheckbox(String testIdPrefix, String label) {
        return By.xpath("//label[normalize-space()='%s']//input[starts-with(@%s,'%s')]"
                .formatted(label, settings.testIdAttribute(), testIdPrefix));
    }

    /** Snapshot of all matches; the wait retries if Angular replaces nodes while we read them. */
    private List<String> currentTexts(By locator) {
        return wait.until(d -> d.findElements(locator).stream().map(WebElement::getText).toList());
    }
}
