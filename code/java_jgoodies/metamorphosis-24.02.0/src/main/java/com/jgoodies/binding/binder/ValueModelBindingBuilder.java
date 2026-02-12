package com.jgoodies.binding.binder;

import com.jgoodies.binding.value.BindingConverter;
import java.text.Format;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ValueModelBindingBuilder.class */
public interface ValueModelBindingBuilder {

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ValueModelBindingBuilder$Commit.class */
    public enum Commit {
        ON_FOCUS_LOST,
        ON_KEY_TYPED
    }

    <S, T> ValueModelBindingBuilder converted(BindingConverter<S, T> bindingConverter);

    ValueModelBindingBuilder formatted(Format format);

    <E> SelectionInListBindingBuilder<E> asSelectionIn(E[] eArr);

    <E> SelectionInListBindingBuilder<E> asSelectionIn(List<E> list);

    <E> SelectionInListBindingBuilder<E> asSelectionIn(ListModel<E> listModel);

    void to(AbstractButton abstractButton);

    void to(AbstractButton abstractButton, Object obj, Object obj2);

    void to(AbstractButton abstractButton, Object obj);

    void to(JFormattedTextField jFormattedTextField);

    void to(JComboBox<?> jComboBox);

    void to(JComboBox<?> jComboBox, String str);

    void to(JLabel jLabel);

    void to(JTextArea jTextArea);

    void to(JTextArea jTextArea, Commit commit);

    void to(JTextField jTextField);

    void to(JTextField jTextField, Commit commit);

    void to(String str, Object obj);

    void to(ValueModelBindable valueModelBindable);
}
