package com.jgoodies.common.bean;

import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/bean/ObservableBean.class */
public interface ObservableBean {
    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
