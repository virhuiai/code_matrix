package com.jgoodies.binding.value;

import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ValueModel.class */
public interface ValueModel {
    public static final String PROPERTY_VALUE = "value";

    Object getValue();

    void setValue(Object obj);

    void addValueChangeListener(PropertyChangeListener propertyChangeListener);

    void removeValueChangeListener(PropertyChangeListener propertyChangeListener);

    default boolean booleanValue() {
        return ((Boolean) getValue()).booleanValue();
    }

    default double doubleValue() {
        return ((Double) getValue()).doubleValue();
    }

    default float floatValue() {
        return ((Float) getValue()).floatValue();
    }

    default int intValue() {
        return ((Integer) getValue()).intValue();
    }

    default long longValue() {
        return ((Long) getValue()).longValue();
    }

    default String getString() {
        return (String) getValue();
    }

    default void setValue(boolean b) {
        setValue(Boolean.valueOf(b));
    }

    default void setValue(double d) {
        setValue(Double.valueOf(d));
    }

    default void setValue(float f) {
        setValue(Float.valueOf(f));
    }

    default void setValue(int i) {
        setValue(Integer.valueOf(i));
    }

    default void setValue(long l) {
        setValue(Long.valueOf(l));
    }
}
