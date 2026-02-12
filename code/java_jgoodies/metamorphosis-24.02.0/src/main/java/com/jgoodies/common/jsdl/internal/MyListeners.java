package com.jgoodies.common.jsdl.internal;

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/MyListeners.class */
public final class MyListeners {
    private MyListeners() {
    }

    public static DelayedPropertyChangeHandler delayed(PropertyChangeListener delegate) {
        delegate.getClass();
        return new DelayedPropertyChangeHandler(delegate::propertyChange);
    }
}
