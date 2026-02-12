package com.jgoodies.binding.extras;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/extras/NonNullValueModel.class */
public final class NonNullValueModel extends AbstractValueModel {
    private final ValueModel subject;
    private final Object defaultValue;

    public NonNullValueModel(ValueModel subject, Object defaultValue) {
        this.subject = subject;
        this.defaultValue = Preconditions.checkNotNull(defaultValue, Messages.MUST_NOT_BE_NULL, "default value");
        subject.addValueChangeListener(this::onSubjectValueChanged);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        Object subjectValue = this.subject.getValue();
        return subjectValue != null ? subjectValue : this.defaultValue;
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        this.subject.setValue(newValue);
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        fireValueChange(evt.getOldValue(), evt.getNewValue(), true);
    }
}
