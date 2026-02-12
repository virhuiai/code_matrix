package com.jgoodies.binding.binder;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.binder.ValueModelBindingBuilder;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.BindingConverter;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ConverterValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.text.Format;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ValueModelBindingBuilderImpl.class */
public class ValueModelBindingBuilderImpl implements ValueModelBindingBuilder {
    private final ValueModel valueModel;
    private final String propertyName;

    public ValueModelBindingBuilderImpl(ValueModel valueModel) {
        this(valueModel, null);
    }

    public ValueModelBindingBuilderImpl(ValueModel valueModel, String propertyName) {
        this.valueModel = (ValueModel) Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "ValueModel");
        this.propertyName = propertyName;
        if (propertyName != null) {
            Preconditions.checkNotBlank(propertyName, "The bean property name must not be empty, or whitespace.");
        }
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public <S, T> ValueModelBindingBuilder converted(BindingConverter<S, T> converter) {
        return new ValueModelBindingBuilderImpl(new ConverterValueModel(getValueModel(), converter), getPropertyName());
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public ValueModelBindingBuilder formatted(Format format) {
        return new ValueModelBindingBuilderImpl(ConverterFactory.createStringConverter(getValueModel(), format), getPropertyName());
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public <E> SelectionInListBindingBuilder<E> asSelectionIn(E[] array) {
        Preconditions.checkNotNull(array, Messages.MUST_NOT_BE_NULL, "array");
        SelectionInList<E> selectionInList = new SelectionInList<>(array, getValueModel());
        return new SelectionInListBindingBuilderImpl(selectionInList, getPropertyName());
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public <E> SelectionInListBindingBuilder<E> asSelectionIn(List<E> list) {
        Preconditions.checkNotNull(list, Messages.MUST_NOT_BE_NULL, "List");
        SelectionInList<E> selectionInList = new SelectionInList<>(list, getValueModel());
        return new SelectionInListBindingBuilderImpl(selectionInList, getPropertyName());
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public <E> SelectionInListBindingBuilder<E> asSelectionIn(ListModel<E> listModel) {
        Preconditions.checkNotNull(listModel, Messages.MUST_NOT_BE_NULL, "ListModel");
        SelectionInList<E> selectionInList = new SelectionInList<>(listModel, getValueModel());
        return new SelectionInListBindingBuilderImpl(selectionInList, getPropertyName());
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(AbstractButton toggleButton) {
        Preconditions.checkNotNull(toggleButton, Messages.MUST_NOT_BE_NULL, "toggle button");
        Bindings.bind(toggleButton, this.valueModel);
        setValidationMessageKey(toggleButton);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(AbstractButton toggleButton, Object selectedValue, Object deselectedValue) {
        Preconditions.checkNotNull(toggleButton, Messages.MUST_NOT_BE_NULL, "toggle button");
        Bindings.bind(toggleButton, this.valueModel, selectedValue, deselectedValue);
        setValidationMessageKey(toggleButton);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(AbstractButton toggleButton, Object choice) {
        Preconditions.checkNotNull(toggleButton, Messages.MUST_NOT_BE_NULL, "toggle button");
        Bindings.bind(toggleButton, this.valueModel, choice);
        setValidationMessageKey(toggleButton);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JComboBox<?> comboBox) {
        Preconditions.checkNotNull(comboBox, Messages.MUST_NOT_BE_NULL, "combo box");
        Bindings.bind(comboBox, this.valueModel);
        setValidationMessageKey(comboBox);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JComboBox<?> comboBox, String nullText) {
        Preconditions.checkNotNull(comboBox, Messages.MUST_NOT_BE_NULL, "combo box");
        Bindings.bind(comboBox, this.valueModel, nullText);
        setValidationMessageKey(comboBox);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JFormattedTextField formattedTextField) {
        Preconditions.checkNotNull(formattedTextField, Messages.MUST_NOT_BE_NULL, "formatted text field");
        Bindings.bind(formattedTextField, this.valueModel);
        setValidationMessageKey(formattedTextField);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JLabel label) {
        Preconditions.checkNotNull(label, Messages.MUST_NOT_BE_NULL, "label");
        Bindings.bind(label, this.valueModel);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JTextArea textArea) {
        to(textArea, Commit.ON_FOCUS_LOST);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JTextArea textArea, Commit commitType) {
        Preconditions.checkNotNull(textArea, Messages.MUST_NOT_BE_NULL, "text area");
        Preconditions.checkNotNull(commitType, Messages.MUST_NOT_BE_NULL, "commit type");
        Bindings.bind(textArea, this.valueModel, commitType == Commit.ON_FOCUS_LOST);
        setValidationMessageKey(textArea);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JTextField textField) {
        if (textField instanceof JFormattedTextField) {
            to((JFormattedTextField) textField);
        } else {
            to(textField, Commit.ON_FOCUS_LOST);
        }
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(JTextField textField, Commit commitType) {
        Preconditions.checkNotNull(textField, Messages.MUST_NOT_BE_NULL, "text field");
        Preconditions.checkNotNull(commitType, Messages.MUST_NOT_BE_NULL, "commit type");
        Preconditions.checkArgument(!(textField instanceof JFormattedTextField), "For JFormattedTextField use method #to(JFormattedTextField)");
        Bindings.bind(textField, this.valueModel, commitType == Commit.ON_FOCUS_LOST);
        setValidationMessageKey(textField);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(String propertyName, Object bean) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        Preconditions.checkNotBlank(propertyName, Messages.MUST_NOT_BE_BLANK, "property name");
        PropertyConnector.connectAndUpdate(getValueModel(), bean, propertyName);
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindingBuilder
    public void to(ValueModelBindable bindable) {
        Preconditions.checkNotNull(bindable, Messages.MUST_NOT_BE_NULL, "bindable");
        bindable.bind(this.valueModel, this.propertyName);
    }

    protected final ValueModel getValueModel() {
        return this.valueModel;
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
