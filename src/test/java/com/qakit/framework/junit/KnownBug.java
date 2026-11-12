package com.qakit.framework.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/** Test asserts the EXPECTED behaviour of a feature that is currently broken. See KnownBugExtension. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(KnownBugExtension.class)
public @interface KnownBug {
    /** Bug id / one-line description, e.g. "BUG-123: returns 500 instead of 400". */
    String value();

    /**
     * Text the failure message must contain for it to count as THIS bug, e.g. "but was: 500".
     * Any other failure (a 418, a timeout...) is reported as a real failure, not hidden.
     */
    String failureContains() default "";
}
