package com.jgoodies.framework.util;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/ThreadUtils.class */
public final class ThreadUtils {
    private ThreadUtils() {
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }
}
