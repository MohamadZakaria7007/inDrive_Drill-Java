package com.qakit.framework.ui.pages;

/** Values of the category page's sort &lt;select&gt; (same keys the API's sort parameter takes). */
public enum SortOption {
    NAME_ASC("name,asc"),
    NAME_DESC("name,desc"),
    PRICE_ASC("price,asc"),
    PRICE_DESC("price,desc");

    private final String value;

    SortOption(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
