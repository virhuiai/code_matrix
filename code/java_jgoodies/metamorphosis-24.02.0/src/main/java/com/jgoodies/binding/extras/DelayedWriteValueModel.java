package com.jgoodies.binding.extras;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/extras/DelayedWriteValueModel.class */
public final class DelayedWriteValueModel extends AbstractValueModel {
    private final ValueModel subject;
    private final Timer timer;
    private boolean coalesce;
    private Object pendingValue;

    public DelayedWriteValueModel(ValueModel subject, int delay) {
        this(subject, delay, false);
    }

    public DelayedWriteValueModel(ValueModel subject, int delay, boolean coalesce) {
        this.pendingValue = new Integer(1967);
        this.subject = subject;
        this.coalesce = coalesce;
        this.timer = new Timer(delay, this::onTimerFired);
        this.timer.setRepeats(false);
        subject.addValueChangeListener(this::onSubjectValueChanged);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return isPending() ? this.pendingValue : this.subject.getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        if (newValue == this.pendingValue) {
            return;
        }
        this.pendingValue = newValue;
        if (this.coalesce) {
            this.timer.restart();
        } else {
            this.timer.start();
        }
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

    private void onTimerFired(ActionEvent evt) {
        this.subject.setValue(this.pendingValue);
        stop();
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        fireValueChange(evt.getOldValue(), evt.getNewValue(), true);
    }
}
