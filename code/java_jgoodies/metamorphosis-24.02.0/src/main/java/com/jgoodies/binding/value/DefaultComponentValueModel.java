package com.jgoodies.binding.value;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/DefaultComponentValueModel.class */
public final class DefaultComponentValueModel extends AbstractWrappedValueModel {
    public DefaultComponentValueModel(ValueModel wrappee) {
        super(wrappee);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return getWrappee().getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        getWrappee().setValue(newValue);
    }

    @Override // com.jgoodies.binding.value.AbstractWrappedValueModel
    protected PropertyChangeListener createValueChangeHandler() {
        return this::onValueChanged;
    }

    private void onValueChanged(PropertyChangeEvent evt) {
        fireValueChange(evt.getOldValue(), evt.getNewValue(), true);
    }
}
