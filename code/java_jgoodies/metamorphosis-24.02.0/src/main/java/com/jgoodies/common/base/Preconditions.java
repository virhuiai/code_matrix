package com.jgoodies.common.base;

import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/base/Preconditions.class */
public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgument(boolean expression, String messageFormat, Object... messageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(messageFormat, messageArgs));
        }
    }

    public static <T> T checkNotNull(T t, String str) {
        return (T) java.util.Objects.requireNonNull(t, str);
    }

    public static <T> T checkNotNull(T reference, String messageFormat, Object... messageArgs) {
        if (reference == null) {
            throw new NullPointerException(String.format(messageFormat, messageArgs));
        }
        return reference;
    }

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkState(boolean expression, String messageFormat, Object... messageArgs) {
        if (!expression) {
            throw new IllegalStateException(String.format(messageFormat, messageArgs));
        }
    }

    public static <S extends CharSequence> S checkNotBlank(S str, String message) {
        checkNotNull(str, message);
        checkArgument(Strings.isNotBlank(str), message);
        return str;
    }

    public static <S extends CharSequence> S checkNotBlank(S str, String messageFormat, Object... messageArgs) {
        checkNotNull(str, messageFormat, messageArgs);
        checkArgument(Strings.isNotBlank(str), messageFormat, messageArgs);
        return str;
    }

    public static <T> T[] checkNotNullOrEmpty(T[] reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        if (reference.length == 0) {
            throw new IllegalArgumentException(message);
        }
        return reference;
    }

    public static <T> T[] checkNotNullOrEmpty(T[] reference, String messageFormat, Object... messageArgs) {
        if (reference == null) {
            throw new NullPointerException(String.format(messageFormat, messageArgs));
        }
        if (reference.length == 0) {
            throw new IllegalArgumentException(String.format(messageFormat, messageArgs));
        }
        return reference;
    }

    public static <T> List<T> checkNotNullOrEmpty(List<T> reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        if (reference.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return reference;
    }

    public static <T> List<T> checkNotNullOrEmpty(List<T> reference, String messageFormat, Object... messageArgs) {
        if (reference == null) {
            throw new NullPointerException(String.format(messageFormat, messageArgs));
        }
        if (reference.isEmpty()) {
            throw new IllegalArgumentException(String.format(messageFormat, messageArgs));
        }
        return reference;
    }
}
