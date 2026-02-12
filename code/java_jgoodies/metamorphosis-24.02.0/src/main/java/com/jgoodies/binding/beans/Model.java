package com.jgoodies.binding.beans;

import com.jgoodies.common.bean.Bean;
import java.beans.PropertyChangeSupport;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/Model.class */
public abstract class Model extends Bean {
    @Override // com.jgoodies.common.bean.Bean
    protected PropertyChangeSupport createPropertyChangeSupport(Object bean) {
        return new ExtendedPropertyChangeSupport(bean);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void firePropertyChange(String propertyName, Object oldValue, Object newValue, boolean checkIdentity) {
        if (this.changeSupport == null) {
            return;
        }
        if (this.changeSupport instanceof ExtendedPropertyChangeSupport) {
            ((ExtendedPropertyChangeSupport) this.changeSupport).firePropertyChange(propertyName, oldValue, newValue, checkIdentity);
        } else {
            this.changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
