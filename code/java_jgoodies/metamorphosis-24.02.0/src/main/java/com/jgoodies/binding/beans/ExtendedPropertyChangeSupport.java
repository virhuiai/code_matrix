package com.jgoodies.binding.beans;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/ExtendedPropertyChangeSupport.class */
public final class ExtendedPropertyChangeSupport extends PropertyChangeSupport {
    private final Object source;
    private final boolean checkIdentityDefault;
    private final boolean ensureNotificationInEDT;

    public ExtendedPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, false);
    }

    public ExtendedPropertyChangeSupport(Object sourceBean, boolean checkIdentityDefault) {
        this(sourceBean, checkIdentityDefault, false);
    }

    public ExtendedPropertyChangeSupport(Object sourceBean, boolean checkIdentityDefault, boolean ensureNotificationInEDT) {
        super(sourceBean);
        this.source = sourceBean;
        this.checkIdentityDefault = checkIdentityDefault;
        this.ensureNotificationInEDT = ensureNotificationInEDT;
    }

    @Override // java.beans.PropertyChangeSupport
    public void firePropertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt, this.checkIdentityDefault);
    }

    @Override // java.beans.PropertyChangeSupport
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        firePropertyChange(propertyName, oldValue, newValue, this.checkIdentityDefault);
    }

    public void firePropertyChange(PropertyChangeEvent evt, boolean checkIdentity) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        if (oldValue != null && oldValue == newValue) {
            return;
        }
        if (!this.ensureNotificationInEDT || EventQueue.isDispatchThread()) {
            firePropertyChange0(evt, checkIdentity);
        } else {
            EventQueue.invokeLater(() -> {
                firePropertyChange0(evt, checkIdentity);
            });
        }
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue, boolean checkIdentity) {
        if (oldValue != null && oldValue == newValue) {
            return;
        }
        if (!this.ensureNotificationInEDT || EventQueue.isDispatchThread()) {
            firePropertyChange0(propertyName, oldValue, newValue, checkIdentity);
        } else {
            EventQueue.invokeLater(() -> {
                firePropertyChange0(propertyName, oldValue, newValue, checkIdentity);
            });
        }
    }

    private void firePropertyChange0(PropertyChangeEvent evt, boolean checkIdentity) {
        if (checkIdentity) {
            fireUnchecked(evt);
        } else {
            super.firePropertyChange(evt);
        }
    }

    private void firePropertyChange0(String propertyName, Object oldValue, Object newValue, boolean checkIdentity) {
        if (checkIdentity) {
            fireUnchecked(new PropertyChangeEvent(this.source, propertyName, oldValue, newValue));
        } else {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    private void fireUnchecked(PropertyChangeEvent evt) {
        PropertyChangeListener[] listeners;
        synchronized (this) {
            listeners = getPropertyChangeListeners();
        }
        String propertyName = evt.getPropertyName();
        for (PropertyChangeListener listener : listeners) {
            if (listener instanceof PropertyChangeListenerProxy) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
                if (proxy.getPropertyName().equals(propertyName)) {
                    proxy.propertyChange(evt);
                }
            } else {
                listener.propertyChange(evt);
            }
        }
    }
}
