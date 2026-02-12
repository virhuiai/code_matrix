package com.jgoodies.common.bean;

import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/bean/ObservableBean2.class */
public interface ObservableBean2 extends ObservableBean {
    void addPropertyChangeListener(String str, PropertyChangeListener propertyChangeListener);

    void removePropertyChangeListener(String str, PropertyChangeListener propertyChangeListener);

    PropertyChangeListener[] getPropertyChangeListeners();

    PropertyChangeListener[] getPropertyChangeListeners(String str);
}
