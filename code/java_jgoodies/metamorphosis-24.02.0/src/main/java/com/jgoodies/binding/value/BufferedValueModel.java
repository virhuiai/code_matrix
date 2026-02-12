package com.jgoodies.binding.value;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/BufferedValueModel.class */
public final class BufferedValueModel extends AbstractValueModel {
    public static final String PROPERTY_BUFFERING = "buffering";
    public static final String PROPERTY_SUBJECT = "subject";
    public static final String PROPERTY_TRIGGER_CHANNEL = "triggerChannel";
    private ValueModel subject;
    private ValueModel triggerChannel;
    private Object bufferedValue;
    private Object oldValue;
    private boolean valueAssigned;
    private final PropertyChangeListener valueChangeHandler = this::onValueChanged;
    private final PropertyChangeListener triggerChangeHandler = this::onTriggerChanged;

    public BufferedValueModel(ValueModel subject, ValueModel triggerChannel) {
        setSubject(subject);
        setTriggerChannel(triggerChannel);
        setBuffering(false);
    }

    public ValueModel getSubject() {
        return this.subject;
    }

    public void setSubject(ValueModel newSubject) {
        ValueModel oldSubject = getSubject();
        Object oldValue = null;
        if (oldSubject != null) {
            ReadAccessResult oldReadValue = readBufferedOrSubjectValue();
            oldValue = oldReadValue.value;
            oldSubject.removeValueChangeListener(this.valueChangeHandler);
        }
        this.subject = newSubject;
        if (newSubject != null) {
            newSubject.addValueChangeListener(this.valueChangeHandler);
        }
        firePropertyChange(PROPERTY_SUBJECT, oldSubject, newSubject);
        if (isBuffering()) {
            return;
        }
        ReadAccessResult newReadValue = readBufferedOrSubjectValue();
        Object newValue = newReadValue.value;
        if (oldValue != null || newValue != null) {
            fireValueChange(oldValue, newValue, true);
        }
    }

    public ValueModel getTriggerChannel() {
        return this.triggerChannel;
    }

    public void setTriggerChannel(ValueModel newTriggerChannel) {
        Preconditions.checkNotNull(newTriggerChannel, Messages.MUST_NOT_BE_NULL, "trigger channel");
        ValueModel oldTriggerChannel = getTriggerChannel();
        if (oldTriggerChannel != null) {
            oldTriggerChannel.removeValueChangeListener(this.triggerChangeHandler);
        }
        this.triggerChannel = newTriggerChannel;
        newTriggerChannel.addValueChangeListener(this.triggerChangeHandler);
        firePropertyChange("triggerChannel", oldTriggerChannel, newTriggerChannel);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        Preconditions.checkNotNull(this.subject, "The subject must not be null when reading a value from a BufferedValueModel.");
        return isBuffering() ? this.bufferedValue : this.subject.getValue();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newBufferedValue) {
        Preconditions.checkNotNull(this.subject, "The subject must not be null when setting a value to a BufferedValueModel.");
        ReadAccessResult oldReadValue = readBufferedOrSubjectValue();
        Object oldValue = oldReadValue.value;
        this.bufferedValue = newBufferedValue;
        setBuffering(true);
        if (oldReadValue.readable && oldValue == newBufferedValue) {
            return;
        }
        fireValueChange(oldValue, newBufferedValue, true);
    }

    private ReadAccessResult readBufferedOrSubjectValue() {
        try {
            Object value = getValue();
            return new ReadAccessResult(value, true);
        } catch (Exception e) {
            return new ReadAccessResult(null, false);
        }
    }

    public void release() {
        ValueModel aSubject = getSubject();
        if (aSubject != null) {
            aSubject.removeValueChangeListener(this.valueChangeHandler);
        }
        ValueModel aTriggerChannel = getTriggerChannel();
        if (aTriggerChannel != null) {
            aTriggerChannel.removeValueChangeListener(this.triggerChangeHandler);
        }
    }

    public boolean isBuffering() {
        return this.valueAssigned;
    }

    private void setBuffering(boolean newValue) {
        boolean oldValue = isBuffering();
        this.valueAssigned = newValue;
        firePropertyChange("buffering", oldValue, newValue);
    }

    private void commit() {
        if (isBuffering()) {
            setBuffering(false);
            this.oldValue = this.bufferedValue;
            this.subject.setValue(this.bufferedValue);
            this.oldValue = null;
            return;
        }
        Preconditions.checkNotNull(this.subject, "The subject must not be null while committing a value in a BufferedValueModel.");
    }

    private void flush() {
        Object oldValue = getValue();
        setBuffering(false);
        Object newValue = getValue();
        fireValueChange(oldValue, newValue, true);
    }

    @Override // com.jgoodies.binding.value.AbstractValueModel
    protected String paramString() {
        return "value=" + valueString() + "; buffering" + isBuffering();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/BufferedValueModel$ReadAccessResult.class */
    public static final class ReadAccessResult {
        final Object value;
        final boolean readable;

        private ReadAccessResult(Object value, boolean readable) {
            this.value = value;
            this.readable = readable;
        }
    }

    private void onValueChanged(PropertyChangeEvent evt) {
        if (!isBuffering()) {
            fireValueChange(this.oldValue != null ? this.oldValue : evt.getOldValue(), evt.getNewValue(), true);
        }
    }

    private void onTriggerChanged(PropertyChangeEvent evt) {
        if (Boolean.TRUE.equals(evt.getNewValue())) {
            commit();
        } else if (Boolean.FALSE.equals(evt.getNewValue())) {
            flush();
        }
    }
}
