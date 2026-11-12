package com.qakit.framework.junit;

import com.qakit.framework.config.Settings;
import com.qakit.framework.ui.DriverFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Fresh browser per test (isolation + safe parallelism), injected as a {@code WebDriver}
 * parameter. On failure saves a screenshot + page source to target/failures/ before quitting.
 */
public final class BrowserExtension
        implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BrowserExtension.class);
    private static final String DRIVER = "driver";
    private static final Path FAILURES_DIR = Path.of("target", "failures");

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getStore(NAMESPACE).put(DRIVER, DriverFactory.create(Settings.get()));
    }

    /**
     * Capture lives here, not in afterTestExecution: JUnit skips that callback when a
     * {@code @BeforeEach} fails (e.g. the page never loads), which is exactly when evidence matters.
     */
    @Override
    public void afterEach(ExtensionContext context) {
        WebDriver driver = context.getStore(NAMESPACE).remove(DRIVER, WebDriver.class);
        if (driver == null) {
            return;
        }
        try {
            if (context.getExecutionException().isPresent()) {
                saveFailureArtifacts(context, driver);
            }
        } finally {
            driver.quit();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
        return parameter.getParameter().getType() == WebDriver.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
        return driver(context);
    }

    private static WebDriver driver(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(DRIVER, WebDriver.class);
    }

    private static void saveFailureArtifacts(ExtensionContext context, WebDriver driver) {
        String name = (context.getRequiredTestClass().getSimpleName() + "-" + context.getDisplayName())
                .replaceAll("[^A-Za-z0-9._-]+", "_");
        try {
            Files.createDirectories(FAILURES_DIR);
            Path screenshot = FAILURES_DIR.resolve(name + ".png");
            Files.write(screenshot, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            Files.writeString(FAILURES_DIR.resolve(name + ".html"), driver.getPageSource(), StandardCharsets.UTF_8);
            System.out.println("Failure artifacts: " + screenshot.toAbsolutePath() + " (url: " + driver.getCurrentUrl() + ")");
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not save failure artifacts: " + e);
        }
    }
}
