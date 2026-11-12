package com.qakit.framework.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

/** Browser test class: tagged "ui" and gets a fresh WebDriver per test (inject it as a parameter). */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Tag("ui")
@ExtendWith(BrowserExtension.class)
public @interface UiTest {}
