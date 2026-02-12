package com.jgoodies.common.jsdl.check;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/SeverityLevel.class */
public enum SeverityLevel {
    ERROR(30),
    WARNING(20),
    INFO(10),
    IGNORE(Integer.MIN_VALUE);

    private final int value;

    SeverityLevel(int value) {
        this.value = value;
    }

    public final int intValue() {
        return this.value;
    }
}
