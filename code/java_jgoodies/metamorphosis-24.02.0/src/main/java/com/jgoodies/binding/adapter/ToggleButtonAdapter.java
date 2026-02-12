package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.swing.JToggleButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/ToggleButtonAdapter.class */
public final class ToggleButtonAdapter extends JToggleButton.ToggleButtonModel {
    private final ValueModel subject;
    private final Object selectedValue;
    private final Object deselectedValue;

    public ToggleButtonAdapter(ValueModel subject) {
        this(subject, Boolean.TRUE, Boolean.FALSE);
    }

    public ToggleButtonAdapter(ValueModel subject, Object selectedValue, Object deselectedValue) {
        this.subject = (ValueModel) Preconditions.checkNotNull(subject, Messages.MUST_NOT_BE_NULL, BufferedValueModel.PROPERTY_SUBJECT);
        Preconditions.checkArgument(!Objects.equals(selectedValue, deselectedValue), "The selected value must not equal the deselected value.");
        this.selectedValue = selectedValue;
        this.deselectedValue = deselectedValue;
        subject.addValueChangeListener(this::onSubjectChanged);
        updateSelectedState();
    }

    public void setSelected(boolean b) {
        this.subject.setValue(b ? this.selectedValue : this.deselectedValue);
        updateSelectedState();
    }

    private void updateSelectedState() {
        boolean subjectHoldsChoiceValue = Objects.equals(this.selectedValue, this.subject.getValue());
        super.setSelected(subjectHoldsChoiceValue);
    }

    private void onSubjectChanged(PropertyChangeEvent evt) {
        updateSelectedState();
    }
}
