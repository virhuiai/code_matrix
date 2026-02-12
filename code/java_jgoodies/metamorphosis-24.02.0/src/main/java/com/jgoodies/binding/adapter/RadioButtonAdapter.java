package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/RadioButtonAdapter.class */
public final class RadioButtonAdapter extends JToggleButton.ToggleButtonModel {
    private final ValueModel subject;
    private final Object choice;

    public RadioButtonAdapter(ValueModel subject, Object choice) {
        this.subject = (ValueModel) Preconditions.checkNotNull(subject, Messages.MUST_NOT_BE_NULL, BufferedValueModel.PROPERTY_SUBJECT);
        this.choice = choice;
        subject.addValueChangeListener(this::onSubjectValueChanged);
        updateSelectedState();
    }

    public void setSelected(boolean b) {
        if (!b || isSelected()) {
            return;
        }
        this.subject.setValue(this.choice);
        updateSelectedState();
    }

    public void setGroup(ButtonGroup group) {
        if (group != null) {
            throw new UnsupportedOperationException("You need not and must not use a ButtonGroup with a set of RadioButtonAdapters. These form a group by sharing the same subject ValueModel.");
        }
    }

    private void updateSelectedState() {
        boolean subjectHoldsChoiceValue = Objects.equals(this.choice, this.subject.getValue());
        super.setSelected(subjectHoldsChoiceValue);
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        updateSelectedState();
    }
}
