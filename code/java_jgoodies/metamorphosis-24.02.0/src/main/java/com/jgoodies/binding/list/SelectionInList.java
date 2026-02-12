package com.jgoodies.binding.list;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.collect.ArrayListModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/list/SelectionInList.class */
public final class SelectionInList<E> extends IndirectListModel<E> implements ValueModel {
    public static final String PROPERTY_SELECTION = "selection";
    public static final String PROPERTY_SELECTION_EMPTY = "selectionEmpty";
    public static final String PROPERTY_SELECTION_HOLDER = "selectionHolder";
    public static final String PROPERTY_SELECTION_INDEX = "selectionIndex";
    public static final String PROPERTY_SELECTION_INDEX_HOLDER = "selectionIndexHolder";
    private static final int NO_SELECTION_INDEX = -1;
    private ValueModel selectionHolder;
    private ValueModel selectionIndexHolder;
    private final PropertyChangeListener selectionChangeHandler;
    private final PropertyChangeListener selectionIndexChangeHandler;
    private E oldSelection;
    private int oldSelectionIndex;
    private boolean clearSelectionOnListUpdates;

    public SelectionInList() {
        this((ListModel) new ArrayListModel());
    }

    public SelectionInList(E[] listItems) {
        this(Arrays.asList(listItems));
    }

    public SelectionInList(E[] listItems, ValueModel selectionHolder) {
        this(Arrays.asList(listItems), selectionHolder);
    }

    public SelectionInList(E[] listItems, ValueModel selectionHolder, ValueModel selectionIndexHolder) {
        this(Arrays.asList(listItems), selectionHolder, selectionIndexHolder);
    }

    public SelectionInList(List<E> list) {
        this(new ValueHolder(list, true));
    }

    public SelectionInList(List<E> list, ValueModel selectionHolder) {
        this(new ValueHolder(list, true), selectionHolder);
    }

    public SelectionInList(List<E> list, ValueModel selectionHolder, ValueModel selectionIndexHolder) {
        this(new ValueHolder(list, true), selectionHolder, selectionIndexHolder);
    }

    public SelectionInList(ListModel<E> listModel) {
        this(new ValueHolder(listModel, true));
    }

    public SelectionInList(ListModel<E> listModel, ValueModel selectionHolder) {
        this(new ValueHolder(listModel, true), selectionHolder);
    }

    public SelectionInList(ListModel<E> listModel, ValueModel selectionHolder, ValueModel selectionIndexHolder) {
        this(new ValueHolder(listModel, true), selectionHolder, selectionIndexHolder);
    }

    public SelectionInList(ValueModel listHolder) {
        this(listHolder, new ValueHolder(null, true));
    }

    public SelectionInList(ValueModel listHolder, ValueModel selectionHolder) {
        this(listHolder, selectionHolder, new ValueHolder(Integer.valueOf(NO_SELECTION_INDEX)));
    }

    public SelectionInList(ValueModel listHolder, ValueModel selectionHolder, ValueModel selectionIndexHolder) {
        super(listHolder);
        this.clearSelectionOnListUpdates = true;
        this.selectionHolder = (ValueModel) Preconditions.checkNotNull(selectionHolder, Messages.MUST_NOT_BE_NULL, "selection holder");
        this.selectionIndexHolder = (ValueModel) Preconditions.checkNotNull(selectionIndexHolder, Messages.MUST_NOT_BE_NULL, "selection index holder");
        this.selectionChangeHandler = this::onSelectionChanged;
        this.selectionIndexChangeHandler = this::onSelectionIndexChanged;
        initializeSelectionIndex();
        this.selectionHolder.addValueChangeListener(this.selectionChangeHandler);
        this.selectionIndexHolder.addValueChangeListener(this.selectionIndexChangeHandler);
    }

    public void setListWithoutClearingSelection(List<E> newList) {
        this.clearSelectionOnListUpdates = false;
        try {
            getListHolder().setValue(newList);
        } finally {
            this.clearSelectionOnListUpdates = true;
        }
    }

    public void fireSelectedContentsChanged() {
        if (hasSelection()) {
            int selectionIndex = getSelectionIndex();
            fireContentsChanged(selectionIndex, selectionIndex);
        }
    }

    public E getSelection() {
        return getSafeElementAt(getSelectionIndex());
    }

    public void setSelection(E newSelection) {
        if (!isEmpty()) {
            setSelectionIndex(indexOf(newSelection));
        }
    }

    public boolean hasSelection() {
        return getSelectionIndex() != NO_SELECTION_INDEX;
    }

    public boolean isSelectionEmpty() {
        return !hasSelection();
    }

    public void clearSelection() {
        setSelectionIndex(NO_SELECTION_INDEX);
    }

    public int getSelectionIndex() {
        return ((Integer) getSelectionIndexHolder().getValue()).intValue();
    }

    public void setSelectionIndex(int newSelectionIndex) {
        int upperBound = getSize() - 1;
        if (newSelectionIndex < NO_SELECTION_INDEX || newSelectionIndex > upperBound) {
            throw new IndexOutOfBoundsException("The selection index " + newSelectionIndex + " must be in [-1, " + upperBound + "]");
        }
        this.oldSelectionIndex = getSelectionIndex();
        if (this.oldSelectionIndex == newSelectionIndex) {
            return;
        }
        getSelectionIndexHolder().setValue(Integer.valueOf(newSelectionIndex));
    }

    public ValueModel getSelectionHolder() {
        return this.selectionHolder;
    }

    public void setSelectionHolder(ValueModel valueModel) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "new selection holder");
        ValueModel selectionHolder = getSelectionHolder();
        selectionHolder.removeValueChangeListener(this.selectionChangeHandler);
        this.selectionHolder = valueModel;
        this.oldSelection = (E) valueModel.getValue();
        valueModel.addValueChangeListener(this.selectionChangeHandler);
        firePropertyChange(PROPERTY_SELECTION_HOLDER, selectionHolder, valueModel);
    }

    public ValueModel getSelectionIndexHolder() {
        return this.selectionIndexHolder;
    }

    public void setSelectionIndexHolder(ValueModel newSelectionIndexHolder) {
        Preconditions.checkNotNull(newSelectionIndexHolder, Messages.MUST_NOT_BE_NULL, "new selection index holder");
        Preconditions.checkNotNull(newSelectionIndexHolder.getValue(), Messages.MUST_NOT_BE_NULL, "value of the new selection index holder");
        ValueModel oldSelectionIndexHolder = getSelectionIndexHolder();
        if (Objects.equals(oldSelectionIndexHolder, newSelectionIndexHolder)) {
            return;
        }
        oldSelectionIndexHolder.removeValueChangeListener(this.selectionIndexChangeHandler);
        this.selectionIndexHolder = newSelectionIndexHolder;
        newSelectionIndexHolder.addValueChangeListener(this.selectionIndexChangeHandler);
        this.oldSelectionIndex = getSelectionIndex();
        this.oldSelection = getSafeElementAt(this.oldSelectionIndex);
        firePropertyChange(PROPERTY_SELECTION_INDEX_HOLDER, oldSelectionIndexHolder, newSelectionIndexHolder);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public E getValue() {
        return getSelection();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object obj) {
        setSelection(obj);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void addValueChangeListener(PropertyChangeListener l) {
        addPropertyChangeListener(ValueModel.PROPERTY_VALUE, l);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void removeValueChangeListener(PropertyChangeListener l) {
        removePropertyChangeListener(ValueModel.PROPERTY_VALUE, l);
    }

    void fireValueChange(Object oldValue, Object newValue) {
        firePropertyChange(ValueModel.PROPERTY_VALUE, oldValue, newValue);
    }

    @Override // com.jgoodies.binding.list.IndirectListModel
    public void release() {
        super.release();
        this.selectionHolder.removeValueChangeListener(this.selectionChangeHandler);
        this.selectionIndexHolder.removeValueChangeListener(this.selectionIndexChangeHandler);
        this.selectionHolder = null;
        this.selectionIndexHolder = null;
        this.oldSelection = null;
    }

    private E getSafeElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return getElementAt(index);
    }

    private int indexOf(Object element) {
        return indexOf(getListHolder().getValue(), element);
    }

    private static int indexOf(Object aList, Object element) {
        if (element == null || getSize(aList) == 0) {
            return NO_SELECTION_INDEX;
        }
        if (aList instanceof List) {
            return ((List) aList).indexOf(element);
        }
        ListModel<?> listModel = (ListModel) aList;
        int size = listModel.getSize();
        for (int index = 0; index < size; index++) {
            if (element.equals(listModel.getElementAt(index))) {
                return index;
            }
        }
        return NO_SELECTION_INDEX;
    }

    private void initializeSelectionIndex() {
        E e = (E) this.selectionHolder.getValue();
        if (e != null) {
            setSelectionIndex(indexOf(e));
        }
        this.oldSelection = e;
        this.oldSelectionIndex = getSelectionIndex();
    }

    @Override // com.jgoodies.binding.list.IndirectListModel
    protected ListDataListener createListDataChangeHandler() {
        return new ListDataChangeHandler();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.binding.list.IndirectListModel
    public void updateList(Object oldList, int oldSize, Object newList) {
        boolean hadSelection = hasSelection();
        Object oldSelectionHolderValue = hadSelection ? getSelectionHolder().getValue() : null;
        if (hadSelection && this.clearSelectionOnListUpdates) {
            clearSelection();
        }
        super.updateList(oldList, oldSize, newList);
        if (hadSelection) {
            setSelectionIndex(indexOf(newList, oldSelectionHolderValue));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/list/SelectionInList$ListDataChangeHandler.class */
    private final class ListDataChangeHandler implements ListDataListener {
        private ListDataChangeHandler() {
        }

        public void intervalAdded(ListDataEvent evt) {
            int index0 = evt.getIndex0();
            int index1 = evt.getIndex1();
            int index = SelectionInList.this.getSelectionIndex();
            SelectionInList.this.fireIntervalAdded(index0, index1);
            if (index >= index0) {
                SelectionInList.this.setSelectionIndex(((index + index1) - index0) + 1);
            }
        }

        public void intervalRemoved(ListDataEvent evt) {
            int index0 = evt.getIndex0();
            int index1 = evt.getIndex1();
            int index = SelectionInList.this.getSelectionIndex();
            SelectionInList.this.fireIntervalRemoved(index0, index1);
            if (index >= index0) {
                if (index <= index1) {
                    SelectionInList.this.setSelectionIndex(SelectionInList.NO_SELECTION_INDEX);
                } else {
                    SelectionInList.this.setSelectionIndex(index - ((index1 - index0) + 1));
                }
            }
        }

        public void contentsChanged(ListDataEvent evt) {
            SelectionInList.this.fireContentsChanged(evt.getIndex0(), evt.getIndex1());
            updateSelectionContentsChanged(evt.getIndex0(), evt.getIndex1());
        }

        private void updateSelectionContentsChanged(int first, int last) {
            int selectionIndex;
            if (first >= 0 && first <= (selectionIndex = SelectionInList.this.getSelectionIndex()) && selectionIndex <= last) {
                SelectionInList.this.getSelectionHolder().setValue(SelectionInList.this.getElementAt(selectionIndex));
            }
        }
    }

    private void onSelectionChanged(PropertyChangeEvent propertyChangeEvent) {
        Object oldValue = propertyChangeEvent.getOldValue();
        E e = (E) propertyChangeEvent.getNewValue();
        int indexOf = indexOf(e);
        if (indexOf != this.oldSelectionIndex) {
            this.selectionIndexHolder.removeValueChangeListener(this.selectionIndexChangeHandler);
            this.selectionIndexHolder.setValue(Integer.valueOf(indexOf));
            this.selectionIndexHolder.addValueChangeListener(this.selectionIndexChangeHandler);
        }
        int i = this.oldSelectionIndex;
        this.oldSelectionIndex = indexOf;
        this.oldSelection = e;
        firePropertyChange(PROPERTY_SELECTION_INDEX, i, indexOf);
        firePropertyChange(PROPERTY_SELECTION_EMPTY, i == NO_SELECTION_INDEX, indexOf == NO_SELECTION_INDEX);
        firePropertyChange("selection", oldValue, e);
        fireValueChange(oldValue, e);
    }

    private void onSelectionIndexChanged(PropertyChangeEvent evt) {
        int newSelectionIndex = getSelectionIndex();
        E theOldSelection = this.oldSelection;
        E newSelection = getSafeElementAt(newSelectionIndex);
        if (!Objects.equals(theOldSelection, newSelection)) {
            this.selectionHolder.removeValueChangeListener(this.selectionChangeHandler);
            this.selectionHolder.setValue(newSelection);
            this.selectionHolder.addValueChangeListener(this.selectionChangeHandler);
        }
        int theOldSelectionIndex = this.oldSelectionIndex;
        this.oldSelectionIndex = newSelectionIndex;
        this.oldSelection = newSelection;
        firePropertyChange(PROPERTY_SELECTION_INDEX, theOldSelectionIndex, newSelectionIndex);
        firePropertyChange(PROPERTY_SELECTION_EMPTY, theOldSelectionIndex == NO_SELECTION_INDEX, newSelectionIndex == NO_SELECTION_INDEX);
        firePropertyChange("selection", theOldSelection, newSelection);
        fireValueChange(theOldSelection, newSelection);
    }
}
