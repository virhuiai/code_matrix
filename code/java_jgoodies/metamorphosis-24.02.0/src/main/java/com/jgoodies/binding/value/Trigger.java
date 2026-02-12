package com.jgoodies.binding.value;

import com.jgoodies.common.base.Preconditions;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/Trigger.class */
public final class Trigger extends AbstractValueModel {
    private static final Boolean COMMIT = Boolean.TRUE;
    private static final Boolean FLUSH = Boolean.FALSE;
    private static final Boolean NEUTRAL = null;
    private Boolean value = NEUTRAL;

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return this.value;
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        Preconditions.checkArgument(newValue == null || (newValue instanceof Boolean), "Trigger values must be of type Boolean.");
        Object oldValue = this.value;
        this.value = (Boolean) newValue;
        fireValueChange(oldValue, newValue);
    }

    public void triggerCommit() {
        if (COMMIT.equals(getValue())) {
            setValue(NEUTRAL);
        }
        setValue(COMMIT);
    }

    public void triggerFlush() {
        if (FLUSH.equals(getValue())) {
            setValue(NEUTRAL);
        }
        setValue(FLUSH);
    }
}
