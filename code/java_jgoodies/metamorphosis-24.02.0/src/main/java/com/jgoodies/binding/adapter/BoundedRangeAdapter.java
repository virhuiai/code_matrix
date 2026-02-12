package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.ValueModel;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/BoundedRangeAdapter.class */
public final class BoundedRangeAdapter implements BoundedRangeModel, Serializable {
    private final ValueModel subject;
    private transient ChangeEvent changeEvent = null;
    private final EventListenerList listenerList = new EventListenerList();
    private int theExtent = 0;
    private int min = 0;
    private int max = 100;
    private boolean isAdjusting = false;

    public BoundedRangeAdapter(ValueModel subject, int extent, int min, int max) {
        this.subject = subject;
        Object subjectValue = subject.getValue();
        int initialValue = subjectValue == null ? min : ((Integer) subjectValue).intValue();
        initialize(initialValue, extent, min, max);
        subject.addValueChangeListener(this::onSubjectValueChanged);
    }

    public int getExtent() {
        return this.theExtent;
    }

    public int getMaximum() {
        return this.max;
    }

    public int getMinimum() {
        return this.min;
    }

    public int getValue() {
        Object subjectValue = this.subject.getValue();
        if (subjectValue == null) {
            return getMinimum();
        }
        return ((Integer) subjectValue).intValue();
    }

    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }

    public void setExtent(int n) {
        int newExtent = Math.max(0, n);
        int value = getValue();
        if (value + newExtent > this.max) {
            newExtent = this.max - value;
        }
        setRangeProperties(value, newExtent, this.min, this.max, this.isAdjusting);
    }

    public void setMaximum(int n) {
        int newMin = Math.min(n, this.min);
        int newValue = Math.min(n, getValue());
        int newExtent = Math.min(n - newValue, this.theExtent);
        setRangeProperties(newValue, newExtent, newMin, n, this.isAdjusting);
    }

    public void setMinimum(int n) {
        int newMax = Math.max(n, this.max);
        int newValue = Math.max(n, getValue());
        int newExtent = Math.min(newMax - newValue, this.theExtent);
        setRangeProperties(newValue, newExtent, n, newMax, this.isAdjusting);
    }

    public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
        if (newMin > newMax) {
            newMin = newMax;
        }
        if (newValue > newMax) {
            newMax = newValue;
        }
        if (newValue < newMin) {
            newMin = newValue;
        }
        if (newExtent + newValue > newMax) {
            newExtent = newMax - newValue;
        }
        if (newExtent < 0) {
            newExtent = 0;
        }
        boolean isChange = (newValue == getValue() && newExtent == this.theExtent && newMin == this.min && newMax == this.max && adjusting == this.isAdjusting) ? false : true;
        if (isChange) {
            setValue0(newValue);
            this.theExtent = newExtent;
            this.min = newMin;
            this.max = newMax;
            this.isAdjusting = adjusting;
            fireStateChanged();
        }
    }

    public void setValue(int n) {
        int newValue = Math.max(n, this.min);
        if (newValue + this.theExtent > this.max) {
            newValue = this.max - this.theExtent;
        }
        setRangeProperties(newValue, this.theExtent, this.min, this.max, this.isAdjusting);
    }

    public void setValueIsAdjusting(boolean b) {
        setRangeProperties(getValue(), this.theExtent, this.min, this.max, b);
    }

    public void addChangeListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }

    private void initialize(int initialValue, int extent, int minimum, int maximum) {
        if (maximum >= minimum && initialValue >= minimum && initialValue + extent >= initialValue && initialValue + extent <= maximum) {
            this.theExtent = extent;
            this.min = minimum;
            this.max = maximum;
            return;
        }
        throw new IllegalArgumentException("invalid range properties");
    }

    private void setValue0(int newValue) {
        this.subject.setValue(Integer.valueOf(newValue));
    }

    public String toString() {
        String modelString = "value=" + getValue() + ", extent=" + getExtent() + ", min=" + getMinimum() + ", max=" + getMaximum() + ", adj=" + getValueIsAdjusting();
        return getClass().getName() + "[" + modelString + "]";
    }

    private void onSubjectValueChanged(PropertyChangeEvent evt) {
        fireStateChanged();
    }
}
