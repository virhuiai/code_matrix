package com.jgoodies.binding.beans;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/IndirectPropertyChangeSupport.class */
public final class IndirectPropertyChangeSupport {
    private final ValueModel beanChannel;
    private final List<PropertyChangeListener> listenerList;
    private final Map<String, List<PropertyChangeListener>> namedListeners;

    public IndirectPropertyChangeSupport() {
        this((ValueModel) new ValueHolder(null, true));
    }

    public IndirectPropertyChangeSupport(Object bean) {
        this((ValueModel) new ValueHolder(bean, true));
    }

    public IndirectPropertyChangeSupport(ValueModel beanChannel) {
        this.beanChannel = (ValueModel) Preconditions.checkNotNull(beanChannel, Messages.MUST_NOT_BE_NULL, "bean channel");
        this.listenerList = new ArrayList();
        this.namedListeners = new HashMap();
        beanChannel.addValueChangeListener(this::onBeanChanged);
    }

    public Object getBean() {
        return this.beanChannel.getValue();
    }

    public void setBean(Object newBean) {
        this.beanChannel.setValue(newBean);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        this.listenerList.add(listener);
        Object bean = getBean();
        if (bean != null) {
            BeanUtils.addPropertyChangeListener(bean, listener);
        }
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        this.listenerList.remove(listener);
        Object bean = getBean();
        if (bean != null) {
            BeanUtils.removePropertyChangeListener(bean, listener);
        }
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        List<PropertyChangeListener> namedListenerList = this.namedListeners.get(propertyName);
        if (namedListenerList == null) {
            namedListenerList = new ArrayList();
            this.namedListeners.put(propertyName, namedListenerList);
        }
        namedListenerList.add(listener);
        Object bean = getBean();
        if (bean != null) {
            BeanUtils.addPropertyChangeListener(bean, propertyName, listener);
        }
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        List<PropertyChangeListener> namedListenerList;
        if (listener == null || (namedListenerList = this.namedListeners.get(propertyName)) == null) {
            return;
        }
        namedListenerList.remove(listener);
        Object bean = getBean();
        if (bean != null) {
            BeanUtils.removePropertyChangeListener(bean, propertyName, listener);
        }
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.listenerList.isEmpty()) {
            return new PropertyChangeListener[0];
        }
        return (PropertyChangeListener[]) this.listenerList.toArray(new PropertyChangeListener[this.listenerList.size()]);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        List<PropertyChangeListener> namedListenerList = this.namedListeners.get(propertyName);
        if (namedListenerList == null || namedListenerList.isEmpty()) {
            return new PropertyChangeListener[0];
        }
        return (PropertyChangeListener[]) namedListenerList.toArray(new PropertyChangeListener[namedListenerList.size()]);
    }

    public void removeAll() {
        removeAllListenersFrom(getBean());
    }

    private void setBean0(Object oldBean, Object newBean) {
        removeAllListenersFrom(oldBean);
        addAllListenersTo(newBean);
    }

    private void addAllListenersTo(Object bean) {
        if (bean == null) {
            return;
        }
        for (PropertyChangeListener listener : this.listenerList) {
            BeanUtils.addPropertyChangeListener(bean, listener);
        }
        for (Map.Entry<String, List<PropertyChangeListener>> entry : this.namedListeners.entrySet()) {
            String propertyName = entry.getKey();
            for (PropertyChangeListener listener2 : entry.getValue()) {
                BeanUtils.addPropertyChangeListener(bean, propertyName, listener2);
            }
        }
    }

    private void removeAllListenersFrom(Object bean) {
        if (bean == null) {
            return;
        }
        for (PropertyChangeListener listener : this.listenerList) {
            BeanUtils.removePropertyChangeListener(bean, listener);
        }
        for (Map.Entry<String, List<PropertyChangeListener>> entry : this.namedListeners.entrySet()) {
            String propertyName = entry.getKey();
            for (PropertyChangeListener listener2 : entry.getValue()) {
                BeanUtils.removePropertyChangeListener(bean, propertyName, listener2);
            }
        }
    }

    private void onBeanChanged(PropertyChangeEvent evt) {
        setBean0(evt.getOldValue(), evt.getNewValue());
    }
}
