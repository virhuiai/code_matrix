package com.jgoodies.binding.binder;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ListBindingBuilderImpl.class */
public class ListBindingBuilderImpl<E> implements ListBindingBuilder<E> {
    private final ListModel<E> dataModel;
    private final ListSelectionModel selectionModel;

    public ListBindingBuilderImpl(E[] data, ListSelectionModel selectionModel) {
        this((ListModel) new ArrayToListModel(data), selectionModel);
    }

    public ListBindingBuilderImpl(List<E> data, ListSelectionModel selectionModel) {
        this((ListModel) new ListToListModel(data), selectionModel);
    }

    public ListBindingBuilderImpl(ListModel<E> dataModel, ListSelectionModel selectionModel) {
        this.dataModel = (ListModel) Preconditions.checkNotNull(dataModel, Messages.MUST_NOT_BE_NULL, "ListModel");
        this.selectionModel = (ListSelectionModel) Preconditions.checkNotNull(selectionModel, Messages.MUST_NOT_BE_NULL, "ListSelectionModel");
    }

    @Override // com.jgoodies.binding.binder.ListBindingBuilder
    public void to(JTable table) {
        Bindings.bind(table, this.dataModel, this.selectionModel);
    }

    @Override // com.jgoodies.binding.binder.ListBindingBuilder
    public void to(JList<E> list) {
        list.setModel(this.dataModel);
        list.setSelectionModel(this.selectionModel);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ListBindingBuilderImpl$ArrayToListModel.class */
    private static final class ArrayToListModel<T> extends AbstractListModel<T> {
        private final T[] array;

        ArrayToListModel(T[] array) {
            this.array = array;
        }

        public T getElementAt(int index) {
            return this.array[index];
        }

        public int getSize() {
            return this.array.length;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ListBindingBuilderImpl$ListToListModel.class */
    private static final class ListToListModel<T> extends AbstractListModel<T> {
        private final List<T> list;

        ListToListModel(List<T> list) {
            this.list = list;
        }

        public T getElementAt(int index) {
            return this.list.get(index);
        }

        public int getSize() {
            return this.list.size();
        }
    }
}
