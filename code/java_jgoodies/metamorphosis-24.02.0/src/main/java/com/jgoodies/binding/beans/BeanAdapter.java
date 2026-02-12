package com.jgoodies.binding.beans;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.bean.PropertyUnboundException;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/BeanAdapter.class */
public class BeanAdapter<B> extends Model {
    public static final String PROPERTY_BEFORE_BEAN = "beforeBean";
    public static final String PROPERTY_BEAN = "bean";
    public static final String PROPERTY_AFTER_BEAN = "afterBean";
    public static final String PROPERTY_CHANGED = "changed";
    private final ValueModel beanChannel;
    private final boolean observeChanges;
    private final Map<String, SimplePropertyAdapter> propertyAdapters;
    private IndirectPropertyChangeSupport indirectChangeSupport;
    B storedOldBean;
    private boolean changed;
    private PropertyChangeListener propertyChangeHandler;
    private PropertyChangeListener beanChangeHandler;

    public BeanAdapter(B bean) {
        this((Object) bean, false);
    }

    public BeanAdapter(B bean, boolean observeChanges) {
        this((ValueModel) new ValueHolder(bean, true), observeChanges);
    }

    public BeanAdapter(ValueModel beanChannel) {
        this(beanChannel, false);
    }

    public BeanAdapter(ValueModel beanChannel, boolean observeChanges) {
        this.changed = false;
        this.beanChannel = beanChannel != null ? beanChannel : new ValueHolder(null, true);
        checkBeanChannelIdentityCheck(beanChannel);
        this.observeChanges = observeChanges;
        this.propertyAdapters = new HashMap();
        this.beanChangeHandler = this::onBeanChanged;
        this.beanChannel.addValueChangeListener(this.beanChangeHandler);
        B initialBean = getBean();
        if (initialBean != null) {
            if (observeChanges && !BeanUtils.supportsBoundProperties(getBeanClass(initialBean))) {
                throw new PropertyUnboundException("The bean must provide support for listening on property changes as described in section 7.4.5 of the Java Bean Specification.");
            }
            addChangeHandlerTo(initialBean);
        }
        this.storedOldBean = initialBean;
    }

    public ValueModel getBeanChannel() {
        return this.beanChannel;
    }

    public B getBean() {
        return (B) this.beanChannel.getValue();
    }

    public void setBean(B newBean) {
        this.beanChannel.setValue(newBean);
        resetChanged();
    }

    public boolean getObserveChanges() {
        return this.observeChanges;
    }

    public Object getValue(String propertyName) {
        return getValueModel(propertyName).getValue();
    }

    public void setValue(String propertyName, Object newValue) {
        getValueModel(propertyName).setValue(newValue);
    }

    public void setVetoableValue(String propertyName, Object newValue) throws PropertyVetoException {
        getValueModel(propertyName).setVetoableValue(newValue);
    }

    public SimplePropertyAdapter getValueModel(String propertyName) {
        return getValueModel(propertyName, null, null);
    }

    public SimplePropertyAdapter getValueModel(String propertyName, String getterName, String setterName) {
        Preconditions.checkNotBlank(propertyName, Messages.MUST_NOT_BE_BLANK, "property name");
        SimplePropertyAdapter adaptingModel = getPropertyAdapter(propertyName);
        if (adaptingModel == null) {
            adaptingModel = createPropertyAdapter(propertyName, getterName, setterName);
            this.propertyAdapters.put(propertyName, adaptingModel);
        } else {
            Preconditions.checkArgument(Objects.equals(getterName, adaptingModel.getterName) && Objects.equals(setterName, adaptingModel.setterName), "You must not invoke this method twice with different getter and/or setter names.");
        }
        return adaptingModel;
    }

    SimplePropertyAdapter getPropertyAdapter(String propertyName) {
        return this.propertyAdapters.get(propertyName);
    }

    protected SimplePropertyAdapter createPropertyAdapter(String propertyName, String getterName, String setterName) {
        return new SimplePropertyAdapter(propertyName, getterName, setterName);
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void resetChanged() {
        setChanged(false);
    }

    private void setChanged(boolean newValue) {
        boolean oldValue = isChanged();
        this.changed = newValue;
        firePropertyChange("changed", oldValue, newValue);
    }

    public synchronized void addBeanPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (this.indirectChangeSupport == null) {
            this.indirectChangeSupport = new IndirectPropertyChangeSupport(this.beanChannel);
        }
        this.indirectChangeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removeBeanPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || this.indirectChangeSupport == null) {
            return;
        }
        this.indirectChangeSupport.removePropertyChangeListener(listener);
    }

    public synchronized void addBeanPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (this.indirectChangeSupport == null) {
            this.indirectChangeSupport = new IndirectPropertyChangeSupport(this.beanChannel);
        }
        this.indirectChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removeBeanPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || this.indirectChangeSupport == null) {
            return;
        }
        this.indirectChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners() {
        if (this.indirectChangeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.indirectChangeSupport.getPropertyChangeListeners();
    }

    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners(String propertyName) {
        if (this.indirectChangeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.indirectChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public synchronized void release() {
        removeChangeHandlerFrom(getBean());
        this.storedOldBean = null;
        if (this.indirectChangeSupport != null) {
            this.indirectChangeSupport.removeAll();
        }
        this.beanChannel.removeValueChangeListener(this.beanChangeHandler);
    }

    private void setBean0(B oldBean, B newBean) {
        firePropertyChange("beforeBean", oldBean, newBean, true);
        removeChangeHandlerFrom(oldBean);
        forwardAllAdaptedValuesChanged(oldBean, newBean);
        resetChanged();
        addChangeHandlerTo(newBean);
        firePropertyChange("bean", oldBean, newBean, true);
        firePropertyChange("afterBean", oldBean, newBean, true);
    }

    private void forwardAllAdaptedValuesChanged(B oldBean, B newBean) {
        Object[] adapters = this.propertyAdapters.values().toArray();
        for (Object adapter : adapters) {
            ((SimplePropertyAdapter) adapter).setBean0(oldBean, newBean);
        }
    }

    private void forwardAllAdaptedValuesChanged() {
        B currentBean = getBean();
        Object[] adapters = this.propertyAdapters.values().toArray();
        for (Object adapter : adapters) {
            ((SimplePropertyAdapter) adapter).fireChange(currentBean);
        }
    }

    private void addChangeHandlerTo(B bean) {
        if (!this.observeChanges || bean == null) {
            return;
        }
        this.propertyChangeHandler = this::onPropertyChanged;
        BeanUtils.addPropertyChangeListener(bean, (Class<?>) null, this.propertyChangeHandler);
    }

    private void removeChangeHandlerFrom(B bean) {
        if (!this.observeChanges || bean == null || this.propertyChangeHandler == null) {
            return;
        }
        BeanUtils.removePropertyChangeListener(bean, (Class<?>) null, this.propertyChangeHandler);
        this.propertyChangeHandler = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Class<?> getBeanClass(B bean) {
        return bean.getClass();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getValue0(B bean, PropertyAccessor propertyDescriptor) {
        if (bean == null) {
            return null;
        }
        return propertyDescriptor.getValue(bean);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValue0(B bean, PropertyAccessor propertyDescriptor, Object newValue) throws PropertyVetoException {
        propertyDescriptor.setValue(bean, newValue);
    }

    private static void checkBeanChannelIdentityCheck(ValueModel valueModel) {
        if (!(valueModel instanceof ValueHolder)) {
            return;
        }
        ValueHolder valueHolder = (ValueHolder) valueModel;
        if (!valueHolder.isIdentityCheckEnabled()) {
            throw new IllegalArgumentException("The bean channel must have the identity check enabled.");
        }
    }

    private void onBeanChanged(PropertyChangeEvent propertyChangeEvent) {
        B bean;
        if (propertyChangeEvent.getNewValue() != null) {
            bean = (B) propertyChangeEvent.getNewValue();
        } else {
            bean = getBean();
        }
        B b = bean;
        setBean0(this.storedOldBean, b);
        this.storedOldBean = b;
    }

    private void onPropertyChanged(PropertyChangeEvent evt) {
        setChanged(true);
        String propertyName = evt.getPropertyName();
        if (propertyName == null) {
            forwardAllAdaptedValuesChanged();
            return;
        }
        SimplePropertyAdapter adapter = getPropertyAdapter(propertyName);
        if (adapter != null) {
            adapter.fireChange(evt.getOldValue(), evt.getNewValue());
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/BeanAdapter$SimplePropertyAdapter.class */
    public class SimplePropertyAdapter extends AbstractValueModel {
        private final String propertyName;
        final String getterName;
        final String setterName;
        private transient PropertyAccessor cachedPropertyDescriptor;
        private Class<?> cachedBeanClass;

        /* JADX WARN: Multi-variable type inference failed */
        protected SimplePropertyAdapter(String propertyName, String getterName, String setterName) {
            this.propertyName = propertyName;
            this.getterName = getterName;
            this.setterName = setterName;
            Object bean = BeanAdapter.this.getBean();
            if (bean != null) {
                getPropertyAccessor(bean);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.jgoodies.binding.value.ValueModel
        public Object getValue() {
            Object bean = BeanAdapter.this.getBean();
            if (bean != null) {
                return BeanAdapter.this.getValue0(bean, getPropertyAccessor(bean));
            }
            return null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.jgoodies.binding.value.ValueModel
        public void setValue(Object newValue) {
            Object bean = BeanAdapter.this.getBean();
            if (bean != null) {
                try {
                    BeanAdapter.this.setValue0(bean, getPropertyAccessor(bean), newValue);
                } catch (PropertyVetoException e) {
                    // 将 Throwable 转换为 PropertyVetoException
                    PropertyAccessException.createWriteAccessException(bean, newValue, getPropertyAccessor(bean), e);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void setVetoableValue(Object newValue) throws PropertyVetoException {
            Object bean = BeanAdapter.this.getBean();
            if (bean != null) {
                BeanAdapter.this.setValue0(bean, getPropertyAccessor(bean), newValue);
            }
        }

        private PropertyAccessor getPropertyAccessor(B bean) {
            Class<?> beanClass = BeanAdapter.this.getBeanClass(bean);
            if (this.cachedPropertyDescriptor == null || beanClass != this.cachedBeanClass) {
                this.cachedPropertyDescriptor = PropertyAccessors.getProvider().getAccessor(beanClass, this.propertyName, this.getterName, this.setterName);
                this.cachedBeanClass = beanClass;
            }
            return this.cachedPropertyDescriptor;
        }

        protected void fireChange(B currentBean) {
            Object newValue;
            if (currentBean == null) {
                newValue = null;
            } else {
                PropertyAccessor propertyDescriptor = getPropertyAccessor(currentBean);
                boolean isWriteOnly = propertyDescriptor.isWriteOnly();
                newValue = isWriteOnly ? null : BeanAdapter.this.getValue0(currentBean, propertyDescriptor);
            }
            fireValueChange((Object) null, newValue);
        }

        protected void fireChange(Object oldValue, Object newValue) {
            fireValueChange(oldValue, newValue, true);
        }

        protected void setBean0(B oldBean, B newBean) {
            Object oldValue;
            Object newValue;
            if (oldBean == null) {
                oldValue = null;
            } else {
                PropertyAccessor propertyAccessor = getPropertyAccessor(oldBean);
                boolean isWriteOnly = propertyAccessor.isWriteOnly();
                oldValue = isWriteOnly ? null : BeanAdapter.this.getValue0(oldBean, propertyAccessor);
            }
            if (newBean == null) {
                newValue = null;
            } else {
                PropertyAccessor propertyAccessor2 = getPropertyAccessor(newBean);
                boolean isWriteOnly2 = propertyAccessor2.isWriteOnly();
                newValue = isWriteOnly2 ? null : BeanAdapter.this.getValue0(newBean, propertyAccessor2);
            }
            if (oldValue != null || newValue != null) {
                fireValueChange(oldValue, newValue, true);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.jgoodies.binding.value.AbstractValueModel
        protected String paramString() {
            Object bean = BeanAdapter.this.getBean();
            String beanType = null;
            Object value = getValue();
            String valueType = null;
            String propertyDescriptorName = null;
            String propertyType = null;
            Method propertyGetter = null;
            Method propertySetter = null;
            if (bean != null) {
                beanType = bean.getClass().getName();
                valueType = value == null ? null : value.getClass().getName();
                PropertyAccessor propertyDescriptor = getPropertyAccessor(bean);
                propertyDescriptorName = propertyDescriptor.getPropertyName();
                propertyType = propertyDescriptor.getPropertyType().getName();
                propertyGetter = propertyDescriptor.getReadMethod();
                propertySetter = propertyDescriptor.getWriteMethod();
            }
            return "bean=" + bean + "; bean type=" + beanType + "; value=" + value + "; value type=" + valueType + "; property name=" + propertyDescriptorName + "; property type=" + propertyType + "; property getter=" + propertyGetter + "; property setter=" + propertySetter;
        }
    }
}