package com.jgoodies.binding.binder;

import javax.swing.JList;
import javax.swing.JTable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/ListBindingBuilder.class */
public interface ListBindingBuilder<E> {
    void to(JList<E> jList);

    void to(JTable jTable);
}
