package com.qakit.framework.ui.pages;

import com.qakit.framework.config.Settings;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page objects hold locators + user-intent actions and return the next page object.
 * Every interaction goes through an explicit wait. Assertions live in the tests.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Settings settings = Settings.get();

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, settings.uiTimeout());
        this.wait.ignoring(StaleElementReferenceException.class); // React re-renders replace nodes
    }

    protected void navigate(String path) {
        driver.get(settings.uiBaseUrl() + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        if (!text.isEmpty()) {
            field.sendKeys(text);
        }
    }

    protected String text(By locator) {
        return visible(locator).getText();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Texts of all matches, after waiting for at least one to be visible. */
    protected List<String> texts(By locator) {
        visible(locator);
        return driver.findElements(locator).stream().map(WebElement::getText).toList();
    }
}
