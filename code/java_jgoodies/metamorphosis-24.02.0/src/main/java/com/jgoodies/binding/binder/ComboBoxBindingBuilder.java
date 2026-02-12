package com.jgoodies.binding.binder;

import javax.swing.JComboBox;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ComboBoxBindingBuilder.class */
public interface ComboBoxBindingBuilder<E> {
    void to(JComboBox<E> jComboBox);

    void to(JComboBox<E> jComboBox, String str);
}
