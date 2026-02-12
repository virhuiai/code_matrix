package com.jgoodies.binding.adapter;

import javax.swing.ListModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/ListModelBindable.class */
public interface ListModelBindable<E> {
    ListModel<E> getListModel();

    void setListModel(ListModel<E> listModel);
}
