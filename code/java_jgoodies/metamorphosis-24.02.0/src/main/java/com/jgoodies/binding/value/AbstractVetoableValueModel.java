package com.jgoodies.binding.value;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/AbstractVetoableValueModel.class */
public abstract class AbstractVetoableValueModel extends AbstractValueModel {
    private final ValueModel subject;

    public abstract boolean proposedChange(Object obj, Object obj2);

    protected AbstractVetoableValueModel(ValueModel subject) {
        this.subject = (ValueModel) Preconditions.checkNotNull(subject, Messages.MUST_NOT_BE_NULL, BufferedValueModel.PROPERTY_SUBJECT);
        subject.addValueChangeListener(this::onSubjectValueChanged);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public final Object getValue() {
        return this.subject.getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public final void setValue(Object newValue) {
        Object oldValue = getValue();
        if (oldValue != newValue && proposedChange(oldValue, newValue)) {
            this.subject.setValue(newValue);
        }
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        fireValueChange(evt.getOldValue(), evt.getNewValue(), true);
    }
}
