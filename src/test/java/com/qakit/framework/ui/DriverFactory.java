package com.qakit.framework.ui;

import com.qakit.framework.config.Settings;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * One place that knows how to start a browser. Selenium Manager (built into Selenium 4)
 * resolves the matching driver binary automatically - no WebDriverManager needed.
 */
public final class DriverFactory {

    private static final String WINDOW_SIZE = "--window-size=1366,900";

    private DriverFactory() {}

    public static WebDriver create(Settings settings) {
        WebDriver driver = switch (settings.browser()) {
            case "chrome" -> new ChromeDriver(chromeOptions(settings.headless()));
            case "edge" -> new EdgeDriver(edgeOptions(settings.headless()));
            case "firefox" -> new FirefoxDriver(firefoxOptions(settings.headless()));
            default -> throw new IllegalArgumentException("Unsupported BROWSER: " + settings.browser());
        };
        // Deliberately NO implicit wait: mixing implicit + explicit waits gives unpredictable timeouts.
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(WINDOW_SIZE);
        if (headless) {
            options.addArguments("--headless=new");
        }
        // The demo password is in public breach lists; stop Chrome's password pop-ups covering the page.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(WINDOW_SIZE);
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
