package com.jgoodies.binding.binder;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/SelectionInListBindingBuilderImpl.class */
public class SelectionInListBindingBuilderImpl<E> implements SelectionInListBindingBuilder<E> {
    private final SelectionInList<E> selectionInList;
    private final String propertyName;

    public SelectionInListBindingBuilderImpl(SelectionInList<E> selectionInList) {
        this(selectionInList, null);
    }

    public SelectionInListBindingBuilderImpl(SelectionInList<E> selectionInList, String propertyName) {
        this.selectionInList = (SelectionInList) Preconditions.checkNotNull(selectionInList, Messages.MUST_NOT_BE_NULL, "SelectionInList");
        this.propertyName = propertyName;
        if (propertyName != null) {
            Preconditions.checkNotBlank(propertyName, Messages.MUST_NOT_BE_BLANK, "bean property name");
        }
    }

    @Override // com.jgoodies.binding.binder.SelectionInListBindingBuilder
    public void to(JComboBox<E> comboBox) {
        Bindings.bind((JComboBox) comboBox, (SelectionInList) this.selectionInList);
        setValidationMessageKey(comboBox);
    }

    @Override // com.jgoodies.binding.binder.SelectionInListBindingBuilder
    public void to(JComboBox<E> comboBox, String nullText) {
        Bindings.bind((JComboBox) comboBox, (SelectionInList) this.selectionInList, nullText);
        setValidationMessageKey(comboBox);
    }

    @Override // com.jgoodies.binding.binder.SelectionInListBindingBuilder
    public void to(JList<E> list) {
        Bindings.bind(list, this.selectionInList);
    }

    @Override // com.jgoodies.binding.binder.SelectionInListBindingBuilder
    public void to(JTable table) {
        Bindings.bind(table, this.selectionInList);
    }

    protected final SelectionInList<E> getSelectionInList() {
        return this.selectionInList;
    }

    protected final String getPropertyName() {
        return this.propertyName;
    }

    protected final void setValidationMessageKey(JComponent comp) {
        if (getPropertyName() != null) {
            BinderUtils.setValidationMessageKey(comp, getPropertyName());
        }
    }
}
