package com.jgoodies.binding.binder;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ComboBoxBindingBuilderImpl.class */
public class ComboBoxBindingBuilderImpl<E> implements ComboBoxBindingBuilder<E> {
    private final ComboBoxModel<E> comboBoxModel;

    public ComboBoxBindingBuilderImpl(ComboBoxModel<E> comboBoxModel) {
        this.comboBoxModel = (ComboBoxModel) Preconditions.checkNotNull(comboBoxModel, Messages.MUST_NOT_BE_NULL, "ComboBoxModel");
    }

    @Override // com.jgoodies.binding.binder.ComboBoxBindingBuilder
    public void to(JComboBox<E> comboBox) {
        Bindings.bind(comboBox, this.comboBoxModel);
    }

    @Override // com.jgoodies.binding.binder.ComboBoxBindingBuilder
    public void to(JComboBox<E> comboBox, String nullText) {
        Bindings.bind(comboBox, this.comboBoxModel, nullText);
    }

    protected final ComboBoxModel<E> getComboBoxModel() {
        return this.comboBoxModel;
    }
}
