package com.jgoodies.common.internal;

import com.jgoodies.common.base.Strings;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/BuilderSupport.class */
public final class BuilderSupport {
    private static ReportStyle reportStyle = ReportStyle.THROW_EXCEPTION;
    private final Set<String> alreadyCalled = new HashSet();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/BuilderSupport$ReportStyle.class */
    public enum ReportStyle {
        LOG_VIOLATION,
        THROW_EXCEPTION
    }

    public static void logViolations() {
        reportStyle = ReportStyle.LOG_VIOLATION;
    }

    public static void throwExceptions() {
        reportStyle = ReportStyle.THROW_EXCEPTION;
    }

    public void checkNotCalledTwice(String methodName) {
        if (this.alreadyCalled.contains(methodName)) {
            report(Strings.get("You must call #%s only once.", methodName));
        }
        this.alreadyCalled.add(methodName);
    }

    private static void report(String message) {
        if (reportStyle == ReportStyle.THROW_EXCEPTION) {
            throw new IllegalStateException(message);
        }
        Logger.getLogger(BuilderSupport.class.getName()).warning(message);
    }
}
