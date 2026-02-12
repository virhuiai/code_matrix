package com.jgoodies.common.swing.internal;

import com.jgoodies.common.bean.Bean;
import java.beans.PropertyChangeSupport;
import javax.swing.event.SwingPropertyChangeSupport;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/internal/EDTBean.class */
public abstract class EDTBean extends Bean {
    @Override // com.jgoodies.common.bean.Bean
    protected PropertyChangeSupport createPropertyChangeSupport(Object bean) {
        return new SwingPropertyChangeSupport(bean, true);
    }
}
