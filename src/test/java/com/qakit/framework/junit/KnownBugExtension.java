package com.qakit.framework.junit;

import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.opentest4j.TestAbortedException;

/**
 * JUnit's equivalent of pytest's xfail(strict=True), driven by {@link KnownBug}:
 * an assertion failure matching {@link KnownBug#failureContains()} is reported as "aborted
 * (known bug)"; a PASS fails the build, because then the bug is fixed and the marker must go.
 * A different failure, or an unexpected exception, is reported as a real failure.
 */
public final class KnownBugExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(
            Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext context) throws Throwable {
        expectFailure(invocation, context);
    }

    @Override
    public void interceptTestTemplateMethod(
            Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext context) throws Throwable {
        expectFailure(invocation, context); // @ParameterizedTest invocations
    }

    private static void expectFailure(Invocation<Void> invocation, ExtensionContext context) throws Throwable {
        KnownBug bug = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), KnownBug.class)
                .orElseThrow();
        try {
            invocation.proceed();
        } catch (AssertionError failure) {
            String message = String.valueOf(failure.getMessage());
            if (!message.contains(bug.failureContains())) {
                throw failure; // failing, but not the way the known bug fails: a real problem
            }
            throw new TestAbortedException("Known bug, failing as expected: " + bug.value(), failure);
        }
        throw new AssertionError("Test passed, so the known bug looks FIXED. Remove @KnownBug: " + bug.value());
    }
}
