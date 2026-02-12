package com.jgoodies.binding.value;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ValueHolder.class */
public final class ValueHolder extends AbstractValueModel {
    private Object value;
    private boolean checkIdentity;

    public ValueHolder() {
        this((Object) null);
    }

    public ValueHolder(Object initialValue) {
        this(initialValue, false);
    }

    public ValueHolder(Object initialValue, boolean checkIdentity) {
        this.value = initialValue;
        this.checkIdentity = checkIdentity;
    }

    public ValueHolder(boolean initialValue) {
        this(Boolean.valueOf(initialValue));
    }

    public ValueHolder(double initialValue) {
        this(Double.valueOf(initialValue));
    }

    public ValueHolder(float initialValue) {
        this(Float.valueOf(initialValue));
    }

    public ValueHolder(int initialValue) {
        this(Integer.valueOf(initialValue));
    }

    public ValueHolder(long initialValue) {
        this(Long.valueOf(initialValue));
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return this.value;
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        setValue(newValue, isIdentityCheckEnabled());
    }

    public boolean isIdentityCheckEnabled() {
        return this.checkIdentity;
    }

    public void setIdentityCheckEnabled(boolean checkIdentity) {
        this.checkIdentity = checkIdentity;
    }

    public void setValue(Object newValue, boolean checkIdentity) {
        Object oldValue = getValue();
        if (oldValue == newValue) {
            return;
        }
        this.value = newValue;
        fireValueChange(oldValue, newValue, checkIdentity);
    }
}
