package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/SpinnerToValueModelConnector.class */
public final class SpinnerToValueModelConnector {
    private final SpinnerModel spinnerModel;
    private final ValueModel valueModel;
    private final Object defaultValue;
    private final PropertyChangeListener valueChangeHandler = this::onValueChanged;
    private final ChangeListener spinnerChangeHandler = this::onSpinnerChanged;

    public SpinnerToValueModelConnector(SpinnerModel spinnerModel, ValueModel valueModel, Object defaultValue) {
        this.spinnerModel = (SpinnerModel) Preconditions.checkNotNull(spinnerModel, Messages.MUST_NOT_BE_NULL, "spinner model");
        this.valueModel = (ValueModel) Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "value model");
        this.defaultValue = Preconditions.checkNotNull(defaultValue, Messages.MUST_NOT_BE_NULL, "default value");
        spinnerModel.addChangeListener(this.spinnerChangeHandler);
        valueModel.addValueChangeListener(this.valueChangeHandler);
    }

    public static void connect(SpinnerModel spinnerModel, ValueModel valueModel, Object defaultValue) {
        new SpinnerToValueModelConnector(spinnerModel, valueModel, defaultValue);
    }

    public void updateSpinnerModel() {
        Object value = this.valueModel.getValue();
        Object valueWithDefault = value != null ? value : this.defaultValue;
        setSpinnerModelValueSilently(valueWithDefault);
    }

    public void updateValueModel() {
        setValueModelValueSilently(this.spinnerModel.getValue());
    }

    private void setSpinnerModelValueSilently(Object newValue) {
        this.spinnerModel.removeChangeListener(this.spinnerChangeHandler);
        this.spinnerModel.setValue(newValue);
        this.spinnerModel.addChangeListener(this.spinnerChangeHandler);
    }

    private void setValueModelValueSilently(Object newValue) {
        this.valueModel.removeValueChangeListener(this.valueChangeHandler);
        this.valueModel.setValue(newValue);
        this.valueModel.addValueChangeListener(this.valueChangeHandler);
    }

    public void release() {
        this.spinnerModel.removeChangeListener(this.spinnerChangeHandler);
        this.valueModel.removeValueChangeListener(this.valueChangeHandler);
    }

    private void onValueChanged(PropertyChangeEvent evt) {
        updateSpinnerModel();
    }

    private void onSpinnerChanged(ChangeEvent evt) {
        updateValueModel();
    }
}
