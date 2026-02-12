package com.jgoodies.binding.binder;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ObjectBinder.class */
public interface ObjectBinder {
    <E> ComboBoxBindingBuilder<E> bind(ComboBoxModel<E> comboBoxModel);

    <E> ListBindingBuilder<E> bind(E[] eArr, ListSelectionModel listSelectionModel);

    <E> ListBindingBuilder<E> bind(ListModel<E> listModel, ListSelectionModel listSelectionModel);

    <E> SelectionInListBindingBuilder<E> bind(SelectionInList<E> selectionInList);

    ValueModelBindingBuilder bind(ValueModel valueModel);
}
