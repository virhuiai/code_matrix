package com.jgoodies.common.jsdl.check;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/DefaultStyleViolationReporter.class */
public class DefaultStyleViolationReporter implements StyleViolationReporter {
    private static final String SYSTEM_PROPERTY_KEY = "StyleViolation.logUpToLevel";
    private static final String SYSTEM_PROPERTY_DEFAULT_VALUE = "ERROR";
    private static final Logger LOGGER = Logger.getLogger(DefaultStyleViolationReporter.class.getName());
    private final SeverityLevel severityForLogging;

    public DefaultStyleViolationReporter() {
        this(severityForLoggingFromSystemProperties());
    }

    public DefaultStyleViolationReporter(SeverityLevel severityForLogging) {
        this.severityForLogging = severityForLogging;
    }

    @Override // com.jgoodies.common.jsdl.check.StyleViolationReporter
    public void report(SeverityLevel severityLevel, StyleCheck check, String message) {
        if (severityLevel.equals(SeverityLevel.IGNORE)) {
            return;
        }
        RuntimeException exception = new StyleViolationException(String.format("%s\n%s", severityLevel.name(), message));
        if (severityLevel.intValue() <= this.severityForLogging.intValue()) {
            LOGGER.log(logLevel(severityLevel), "User experience style guide violation.", (Throwable) exception);
            return;
        }
        throw exception;
    }

    protected Level logLevel(SeverityLevel severityLevel) {
        return severityLevel.intValue() >= SeverityLevel.ERROR.intValue() ? Level.WARNING : Level.INFO;
    }

    protected static SeverityLevel severityForLoggingFromSystemProperties() {
        try {
            String name = System.getProperty(SYSTEM_PROPERTY_KEY, SYSTEM_PROPERTY_DEFAULT_VALUE);
            return SeverityLevel.valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            return SeverityLevel.ERROR;
        }
    }
}
