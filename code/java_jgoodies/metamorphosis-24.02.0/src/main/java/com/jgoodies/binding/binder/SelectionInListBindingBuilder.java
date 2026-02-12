package com.jgoodies.binding.binder;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/SelectionInListBindingBuilder.class */
public interface SelectionInListBindingBuilder<E> {
    void to(JComboBox<E> jComboBox);

    void to(JComboBox<E> jComboBox, String str);

    void to(JList<E> jList);

    void to(JTable jTable);
}
