package com.jgoodies.common.bean;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/bean/PropertyException.class */
public abstract class PropertyException extends RuntimeException {
    public PropertyException(String message) {
        this(message, null);
    }

    public PropertyException(String message, Throwable cause) {
        super(message, cause);
    }
}
