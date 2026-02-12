package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.ValueModel;
import java.beans.PropertyChangeEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/SingleListSelectionAdapter.class */
public final class SingleListSelectionAdapter implements ListSelectionModel {
    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private final ValueModel selectionIndexHolder;
    private boolean valueIsAdjusting;
    private int firstAdjustedIndex = MAX;
    private int lastAdjustedIndex = MIN;
    private int firstChangedIndex = MAX;
    private int lastChangedIndex = MIN;
    private final EventListenerList listenerList = new EventListenerList();

    public SingleListSelectionAdapter(ValueModel selectionIndexHolder) {
        this.selectionIndexHolder = selectionIndexHolder;
        this.selectionIndexHolder.addValueChangeListener(this::onSelectionIndexChanged);
    }

    private int getSelectionIndex() {
        Object value = this.selectionIndexHolder.getValue();
        return value == null ? MIN : ((Integer) value).intValue();
    }

    private void setSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(getSelectionIndex(), newSelectionIndex);
    }

    private void setSelectionIndex(int oldSelectionIndex, int newSelectionIndex) {
        if (oldSelectionIndex == newSelectionIndex) {
            return;
        }
        markAsDirty(oldSelectionIndex);
        markAsDirty(newSelectionIndex);
        this.selectionIndexHolder.setValue(Integer.valueOf(newSelectionIndex));
        fireValueChanged();
    }

    public void setSelectionInterval(int index0, int index1) {
        if (index0 == MIN || index1 == MIN) {
            return;
        }
        setSelectionIndex(index1);
    }

    public void addSelectionInterval(int index0, int index1) {
        setSelectionInterval(index0, index1);
    }

    public void removeSelectionInterval(int index0, int index1) {
        if (index0 == MIN || index1 == MIN) {
            return;
        }
        int max = Math.max(index0, index1);
        int min = Math.min(index0, index1);
        if (min <= getSelectionIndex() && getSelectionIndex() <= max) {
            clearSelection();
        }
    }

    public int getMinSelectionIndex() {
        return getSelectionIndex();
    }

    public int getMaxSelectionIndex() {
        return getSelectionIndex();
    }

    public boolean isSelectedIndex(int index) {
        return index >= 0 && index == getSelectionIndex();
    }

    public int getAnchorSelectionIndex() {
        return getSelectionIndex();
    }

    public void setAnchorSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(newSelectionIndex);
    }

    public int getLeadSelectionIndex() {
        return getSelectionIndex();
    }

    public void setLeadSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(newSelectionIndex);
    }

    public void clearSelection() {
        setSelectionIndex(MIN);
    }

    public boolean isSelectionEmpty() {
        return getSelectionIndex() == MIN;
    }

    public void insertIndexInterval(int index, int length, boolean before) {
        if (isSelectionEmpty()) {
            return;
        }
        int insMinIndex = before ? index : index + 1;
        int selectionIndex = getSelectionIndex();
        if (selectionIndex >= insMinIndex) {
            setSelectionIndex(selectionIndex + length);
        }
    }

    public void removeIndexInterval(int index0, int index1) {
        if (index0 < MIN || index1 < MIN) {
            throw new IndexOutOfBoundsException("Both indices must be greater or equals to -1.");
        }
        if (isSelectionEmpty()) {
            return;
        }
        int lower = Math.min(index0, index1);
        int upper = Math.max(index0, index1);
        int selectionIndex = getSelectionIndex();
        if (lower <= selectionIndex && selectionIndex <= upper) {
            clearSelection();
        } else if (upper < selectionIndex) {
            int translated = selectionIndex - ((upper - lower) + 1);
            setSelectionInterval(translated, translated);
        }
    }

    public void setValueIsAdjusting(boolean newValueIsAdjusting) {
        boolean oldValueIsAdjusting = this.valueIsAdjusting;
        if (oldValueIsAdjusting == newValueIsAdjusting) {
            return;
        }
        this.valueIsAdjusting = newValueIsAdjusting;
        fireValueChanged(newValueIsAdjusting);
    }

    public boolean getValueIsAdjusting() {
        return this.valueIsAdjusting;
    }

    public void setSelectionMode(int selectionMode) {
        if (selectionMode != 0) {
            throw new UnsupportedOperationException("The SingleListSelectionAdapter must be used in single selection mode.");
        }
    }

    public int getSelectionMode() {
        return 0;
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        this.listenerList.add(ListSelectionListener.class, listener);
    }

    public void removeListSelectionListener(ListSelectionListener listener) {
        this.listenerList.remove(ListSelectionListener.class, listener);
    }

    public ListSelectionListener[] getListSelectionListeners() {
        return this.listenerList.getListeners(ListSelectionListener.class);
    }

    private void markAsDirty(int index) {
        if (index < 0) {
            return;
        }
        this.firstAdjustedIndex = Math.min(this.firstAdjustedIndex, index);
        this.lastAdjustedIndex = Math.max(this.lastAdjustedIndex, index);
    }

    private void fireValueChanged(boolean isAdjusting) {
        if (this.lastChangedIndex == MIN) {
            return;
        }
        int oldFirstChangedIndex = this.firstChangedIndex;
        int oldLastChangedIndex = this.lastChangedIndex;
        this.firstChangedIndex = MAX;
        this.lastChangedIndex = MIN;
        fireValueChanged(oldFirstChangedIndex, oldLastChangedIndex, isAdjusting);
    }

    private void fireValueChanged(int firstIndex, int lastIndex) {
        fireValueChanged(firstIndex, lastIndex, getValueIsAdjusting());
    }

    private void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
        Object[] listeners = this.listenerList.getListenerList();
        ListSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
                }
                ((ListSelectionListener) listeners[i + 1]).valueChanged(e);
            }
        }
    }

    private void fireValueChanged() {
        if (this.lastAdjustedIndex == MIN) {
            return;
        }
        if (getValueIsAdjusting()) {
            this.firstChangedIndex = Math.min(this.firstChangedIndex, this.firstAdjustedIndex);
            this.lastChangedIndex = Math.max(this.lastChangedIndex, this.lastAdjustedIndex);
        }
        int oldFirstAdjustedIndex = this.firstAdjustedIndex;
        int oldLastAdjustedIndex = this.lastAdjustedIndex;
        this.firstAdjustedIndex = MAX;
        this.lastAdjustedIndex = MIN;
        fireValueChanged(oldFirstAdjustedIndex, oldLastAdjustedIndex);
    }

    private void onSelectionIndexChanged(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        int oldIndex = oldValue == null ? MIN : ((Integer) oldValue).intValue();
        int newIndex = newValue == null ? MIN : ((Integer) newValue).intValue();
        setSelectionIndex(oldIndex, newIndex);
    }
}
