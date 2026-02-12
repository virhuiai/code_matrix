package com.jgoodies.binding.binder;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ObjectBinderImpl.class */
public class ObjectBinderImpl implements ObjectBinder {
    @Override // com.jgoodies.binding.binder.ObjectBinder
    public <E> ComboBoxBindingBuilder<E> bind(ComboBoxModel<E> comboBoxModel) {
        return new ComboBoxBindingBuilderImpl(comboBoxModel);
    }

    @Override // com.jgoodies.binding.binder.ObjectBinder
    public <E> ListBindingBuilder<E> bind(E[] data, ListSelectionModel selectionModel) {
        return new ListBindingBuilderImpl(data, selectionModel);
    }

    @Override // com.jgoodies.binding.binder.ObjectBinder
    public <E> ListBindingBuilder<E> bind(ListModel<E> dataModel, ListSelectionModel selectionModel) {
        return new ListBindingBuilderImpl(dataModel, selectionModel);
    }

    @Override // com.jgoodies.binding.binder.ObjectBinder
    public <E> SelectionInListBindingBuilder<E> bind(SelectionInList<E> selectionInList) {
        return new SelectionInListBindingBuilderImpl(selectionInList);
    }

    @Override // com.jgoodies.binding.binder.ObjectBinder
    public ValueModelBindingBuilder bind(ValueModel valueModel) {
        return new ValueModelBindingBuilderImpl(valueModel);
    }
}
