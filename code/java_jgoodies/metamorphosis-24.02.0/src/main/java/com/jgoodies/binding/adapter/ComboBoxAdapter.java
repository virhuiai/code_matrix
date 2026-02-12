package com.jgoodies.binding.adapter;

import com.jgoodies.binding.list.IndirectListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/ComboBoxAdapter.class */
public final class ComboBoxAdapter<E> extends AbstractListModel<E> implements ComboBoxModel<E> {
    private final ListModel<E> listModel;
    private ValueModel selectionHolder;
    private final PropertyChangeListener selectionChangeHandler;

    public ComboBoxAdapter(List<E> items, ValueModel selectionHolder) {
        this((ListModel) new ListModelAdapter(items), selectionHolder);
        Preconditions.checkNotNull(items, Messages.MUST_NOT_BE_NULL, IndirectListModel.PROPERTY_LIST);
    }

    public ComboBoxAdapter(ListModel<E> listModel, ValueModel selectionHolder) {
        this.listModel = (ListModel) Preconditions.checkNotNull(listModel, Messages.MUST_NOT_BE_NULL, "ListModel");
        this.selectionHolder = (ValueModel) Preconditions.checkNotNull(selectionHolder, Messages.MUST_NOT_BE_NULL, "selection holder");
        listModel.addListDataListener(new ListDataChangeHandler());
        this.selectionChangeHandler = this::onSelectionChanged;
        setSelectionHolder(selectionHolder);
    }

    public ComboBoxAdapter(E[] items, ValueModel selectionHolder) {
        this((ListModel) new ListModelAdapter(items), selectionHolder);
    }

    public ComboBoxAdapter(SelectionInList<E> selectionInList) {
        this(selectionInList, selectionInList);
        selectionInList.addPropertyChangeListener(SelectionInList.PROPERTY_SELECTION_HOLDER, this::onSelectionHolderChanged);
    }

    public E getSelectedItem() {
        return (E) this.selectionHolder.getValue();
    }

    public void setSelectedItem(Object object) {
        this.selectionHolder.setValue(object);
    }

    public int getSize() {
        return this.listModel.getSize();
    }

    public E getElementAt(int i) {
        return (E) this.listModel.getElementAt(i);
    }

    private void setSelectionHolder(ValueModel newSelectionHolder) {
        ValueModel oldSelectionHolder = this.selectionHolder;
        if (oldSelectionHolder != null) {
            oldSelectionHolder.removeValueChangeListener(this.selectionChangeHandler);
        }
        this.selectionHolder = (ValueModel) Preconditions.checkNotNull(newSelectionHolder, Messages.MUST_NOT_BE_NULL, "selection holder");
        newSelectionHolder.addValueChangeListener(this.selectionChangeHandler);
    }

    private void fireContentsChanged() {
        fireContentsChanged(this, -1, -1);
    }

    private void onSelectionChanged(PropertyChangeEvent evt) {
        fireContentsChanged();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/ComboBoxAdapter$ListDataChangeHandler.class */
    private final class ListDataChangeHandler implements ListDataListener {
        private ListDataChangeHandler() {
        }

        public void intervalAdded(ListDataEvent evt) {
            ComboBoxAdapter.this.fireIntervalAdded(ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            ComboBoxAdapter.this.fireIntervalRemoved(ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            ComboBoxAdapter.this.fireContentsChanged(ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
        }
    }

    private void onSelectionHolderChanged(PropertyChangeEvent evt) {
        setSelectionHolder((ValueModel) evt.getNewValue());
        fireContentsChanged();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/ComboBoxAdapter$ListModelAdapter.class */
    private static final class ListModelAdapter<E> extends AbstractListModel<E> {
        private final List<E> aList;

        ListModelAdapter(List<E> list) {
            this.aList = list;
        }

        ListModelAdapter(E[] elements) {
            this(Arrays.asList(elements));
        }

        public int getSize() {
            return this.aList.size();
        }

        public E getElementAt(int index) {
            return this.aList.get(index);
        }
    }
}
