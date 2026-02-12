package com.jgoodies.common.jsdl.check;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.util.List;
import java.util.function.BooleanSupplier;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/StyleChecker.class */
public class StyleChecker {
    private static StyleChecker current;
    private StyleCheckConfiguration defaultConfiguration;
    private StyleViolationReporter reporter;
    private StyleCheckConfiguration configuration;

    protected StyleChecker() {
        this(null);
    }

    protected StyleChecker(StyleCheckConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void setupStrictDevelopmentMode() {
        getCurrent().setReporter(new DefaultStyleViolationReporter(SeverityLevel.IGNORE));
    }

    public static void setupDevelopmentMode() {
        getCurrent().setReporter(new DefaultStyleViolationReporter(SeverityLevel.INFO));
    }

    public static void setupDefaultRuntimeMode() {
        getCurrent().setReporter(new DefaultStyleViolationReporter(SeverityLevel.ERROR));
    }

    public static StyleCheckConfiguration ignore(StyleCheck... checks) {
        Preconditions.checkNotNull(checks, Messages.MUST_NOT_BE_NULL, "checks");
        StyleCheckConfiguration original = getCurrent().getConfiguration();
        StyleCheckConfiguration child = new StyleCheckConfiguration(original);
        getCurrent().setConfiguration(child);
        for (StyleCheck check : checks) {
            child.put(check, SeverityLevel.IGNORE);
        }
        return original;
    }

    public static StyleCheckConfiguration ignoreAll() {
        List<StyleCheck> checks = StyleChecks.getCurrent().allChecks();
        return ignore((StyleCheck[]) checks.toArray(new StyleCheck[checks.size()]));
    }

    public static boolean isIgnored(StyleCheck check) {
        return getCurrent().getConfiguration().get(check).equals(SeverityLevel.IGNORE);
    }

    public static void restore(StyleCheckConfiguration configuration) {
        getCurrent().setConfiguration(configuration);
    }

    public static void restoreDefault() {
        getCurrent().setConfiguration(null);
    }

    public static StyleChecker getCurrent() {
        if (current == null) {
            current = new StyleChecker();
        }
        return current;
    }

    public static void setCurrent(StyleChecker newInstance) {
        current = newInstance;
    }

    public static void check(StyleCheck check, boolean expression) {
        check(check, () -> {
            return expression;
        });
    }

    public static void check(StyleCheck check, BooleanSupplier deferredExpression) {
        if (isIgnored(check) || deferredExpression.getAsBoolean()) {
            return;
        }
        getCurrent();
        report(check, check.getDescription(), new Object[0]);
    }

    public static void check(StyleCheck check, boolean expression, String messageFormat, Object... messageArgs) {
        check(check, () -> {
            return expression;
        }, messageFormat, messageArgs);
    }

    public static void check(StyleCheck check, BooleanSupplier deferredExpression, String messageFormat, Object... messageArgs) {
        if (isIgnored(check) || deferredExpression.getAsBoolean()) {
            return;
        }
        getCurrent();
        report(check, check.getDescription() + '\n' + messageFormat, messageArgs);
    }

    public static void report(StyleCheck check, String message, Object... args) {
        SeverityLevel level = getCurrent().getConfiguration().get(check);
        if (!level.equals(SeverityLevel.IGNORE)) {
            getCurrent().getReporter().report(level, check, Strings.get(message, args));
        }
    }

    public final StyleCheckConfiguration getConfiguration() {
        if (this.configuration == null) {
            this.configuration = getDefaultConfiguration();
        }
        return this.configuration;
    }

    public final void setConfiguration(StyleCheckConfiguration configuration) {
        this.configuration = configuration;
    }

    public final StyleCheckConfiguration getDefaultConfiguration() {
        if (this.defaultConfiguration == null) {
            this.defaultConfiguration = createDefaultConfiguration();
        }
        return this.defaultConfiguration;
    }

    protected StyleCheckConfiguration createDefaultConfiguration() {
        StyleCheckConfiguration root = new StyleCheckConfiguration(null);
        for (StyleCheck check : StyleChecks.getCurrent().allChecks()) {
            root.put(check, check.getDefaultSeverityLevel());
        }
        return root;
    }

    public final StyleViolationReporter getReporter() {
        if (this.reporter == null) {
            this.reporter = new DefaultStyleViolationReporter();
        }
        return this.reporter;
    }

    public final void setReporter(StyleViolationReporter reporter) {
        this.reporter = reporter;
    }
}
