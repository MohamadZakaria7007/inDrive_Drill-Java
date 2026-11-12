package com.qakit.framework.ui;

import com.qakit.framework.config.Settings;
import org.openqa.selenium.By;

/** Test-id locators; the attribute name comes from TEST_ID_ATTRIBUTE (saucedemo: data-test). */
public final class Locators {

    private static final String ATTRIBUTE = Settings.get().testIdAttribute();

    private Locators() {}

    public static By testId(String id) {
        return By.cssSelector("[%s='%s']".formatted(ATTRIBUTE, id));
    }

    public static By testIdStartingWith(String prefix) {
        return By.cssSelector("[%s^='%s']".formatted(ATTRIBUTE, prefix));
    }
}
