package com.jgoodies.binding.util;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/util/ChangeTracker.class */
public final class ChangeTracker extends Bean {
    public static final String PROPERTY_CHANGED = "changed";
    private boolean changed = false;
    private final PropertyChangeListener updateHandler = this::onModelChanged;

    public boolean isChanged() {
        return this.changed;
    }

    public void reset() {
        setChanged(false);
    }

    private void setChanged(boolean newValue) {
        boolean oldValue = isChanged();
        this.changed = newValue;
        firePropertyChange("changed", oldValue, newValue);
    }

    public void observe(Object bean, String propertyName) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        BeanUtils.addPropertyChangeListener(bean, propertyName, this.updateHandler);
    }

    public void observe(ValueModel valueModel) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "ValueModel");
        valueModel.addValueChangeListener(this.updateHandler);
    }

    public void retractInterestFor(Object bean, String propertyName) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        BeanUtils.removePropertyChangeListener(bean, propertyName, this.updateHandler);
    }

    public void retractInterestFor(ValueModel valueModel) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "ValueModel");
        valueModel.removeValueChangeListener(this.updateHandler);
    }

    private void onModelChanged(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (!"changed".equals(propertyName) || ((Boolean) evt.getNewValue()).booleanValue()) {
            setChanged(true);
        }
    }
}
