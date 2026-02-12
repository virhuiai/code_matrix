package com.jgoodies.binding.value;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/DelayedReadValueModel.class */
public final class DelayedReadValueModel extends AbstractValueModel {
    private final ValueModel subject;
    private final Timer timer;
    private boolean coalesce;
    private Object oldValue;
    private PropertyChangeEvent pendingEvt;

    public DelayedReadValueModel(ValueModel subject, int delay) {
        this(subject, delay, false);
    }

    public DelayedReadValueModel(ValueModel subject, int delay, boolean coalesce) {
        this.subject = subject;
        this.coalesce = coalesce;
        this.timer = new Timer(delay, this::onTimerFired);
        this.timer.setRepeats(false);
        subject.addValueChangeListener(this::onSubjectValueChanged);
        this.oldValue = subject.getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return isPending() ? this.oldValue : this.subject.getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        this.subject.setValue(newValue);
    }

    public int getDelay() {
        return this.timer.getDelay();
    }

    public void setDelay(int delay) {
        this.timer.setInitialDelay(delay);
        this.timer.setDelay(delay);
    }

    public boolean isCoalesce() {
        return this.coalesce;
    }

    public void setCoalesce(boolean b) {
        this.coalesce = b;
    }

    public void stop() {
        this.timer.stop();
    }

    public boolean isPending() {
        return this.timer.isRunning();
    }

    private void fireDelayedValueChange(PropertyChangeEvent evt) {
        this.pendingEvt = evt;
        if (this.coalesce) {
            this.timer.restart();
        } else {
            this.timer.start();
        }
    }

    private void onTimerFired(ActionEvent evt) {
        Object value;
        fireValueChange(this.pendingEvt.getOldValue(), this.pendingEvt.getNewValue(), true);
        stop();
        if (this.pendingEvt.getNewValue() != null) {
            value = this.pendingEvt.getNewValue();
        } else {
            value = this.subject.getValue();
        }
        this.oldValue = value;
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        fireDelayedValueChange(evt);
    }
}
