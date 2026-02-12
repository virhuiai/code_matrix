package com.jgoodies.binding.value;

import com.jgoodies.binding.beans.Model;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/AbstractValueModel.class */
public abstract class AbstractValueModel extends Model implements ValueModel {
    @Override // com.jgoodies.binding.value.ValueModel
    public final void addValueChangeListener(PropertyChangeListener l) {
        addPropertyChangeListener(ValueModel.PROPERTY_VALUE, l);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public final void removeValueChangeListener(PropertyChangeListener l) {
        removePropertyChangeListener(ValueModel.PROPERTY_VALUE, l);
    }

    public final void fireValueChange(Object oldValue, Object newValue) {
        firePropertyChange(ValueModel.PROPERTY_VALUE, oldValue, newValue);
    }

    public final void fireValueChange(Object oldValue, Object newValue, boolean checkIdentity) {
        firePropertyChange(ValueModel.PROPERTY_VALUE, oldValue, newValue, checkIdentity);
    }

    public final void fireValueChange(boolean oldValue, boolean newValue) {
        fireValueChange(Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    public final void fireValueChange(int oldValue, int newValue) {
        fireValueChange(Integer.valueOf(oldValue), Integer.valueOf(newValue));
    }

    public final void fireValueChange(long oldValue, long newValue) {
        fireValueChange(Long.valueOf(oldValue), Long.valueOf(newValue));
    }

    public final void fireValueChange(double oldValue, double newValue) {
        fireValueChange(Double.valueOf(oldValue), Double.valueOf(newValue));
    }

    public final void fireValueChange(float oldValue, float newValue) {
        fireValueChange(Float.valueOf(oldValue), Float.valueOf(newValue));
    }

    public String toString() {
        return getClass().getName() + "[" + paramString() + "]";
    }

    protected String paramString() {
        return "value=" + valueString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String valueString() {
        try {
            Object value = getValue();
            return value == null ? "null" : value.toString();
        } catch (Exception e) {
            return "Can't read";
        }
    }
}
