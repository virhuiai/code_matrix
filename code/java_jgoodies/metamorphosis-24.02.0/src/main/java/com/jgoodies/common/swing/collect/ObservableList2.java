package com.jgoodies.common.swing.collect;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/collect/ObservableList2.class */
public interface ObservableList2<E> extends ObservableList<E> {
    void fireContentsChanged(int i);

    void fireContentsChanged(int i, int i2);
}
