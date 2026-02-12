package com.jgoodies.binding.beans;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAdapter.class */
public final class PropertyAdapter<B> extends AbstractValueModel {
    public static final String PROPERTY_BEFORE_BEAN = "beforeBean";
    public static final String PROPERTY_BEAN = "bean";
    public static final String PROPERTY_AFTER_BEAN = "afterBean";
    public static final String PROPERTY_CHANGED = "changed";
    private final ValueModel beanChannel;
    private final String propertyName;
    private final String getterName;
    private final String setterName;
    private final boolean observeChanges;
    private B storedOldBean;
    private boolean changed;
    private transient PropertyChangeListener propertyChangeHandler;
    private PropertyChangeListener beanChangeHandler;
    private transient PropertyAccessor cachedPropertyAccessor;
    private Class<?> cachedBeanClass;

    public PropertyAdapter(B bean, String propertyName) {
        this((Object) bean, propertyName, false);
    }

    public PropertyAdapter(B bean, String propertyName, boolean observeChanges) {
        this(bean, propertyName, (String) null, (String) null, observeChanges);
    }

    public PropertyAdapter(B bean, String propertyName, String getterName, String setterName) {
        this((Object) bean, propertyName, getterName, setterName, false);
    }

    public PropertyAdapter(B bean, String propertyName, String getterName, String setterName, boolean observeChanges) {
        this((ValueModel) new ValueHolder(bean, true), propertyName, getterName, setterName, observeChanges);
    }

    public PropertyAdapter(ValueModel beanChannel, String propertyName) {
        this(beanChannel, propertyName, false);
    }

    public PropertyAdapter(ValueModel beanChannel, String propertyName, boolean observeChanges) {
        this(beanChannel, propertyName, (String) null, (String) null, observeChanges);
    }

    public PropertyAdapter(ValueModel beanChannel, String propertyName, String getterName, String setterName) {
        this(beanChannel, propertyName, getterName, setterName, false);
    }

    public PropertyAdapter(ValueModel beanChannel, String propertyName, String getterName, String setterName, boolean observeChanges) {
        this.changed = false;
        this.beanChannel = beanChannel != null ? beanChannel : new ValueHolder(null, true);
        this.propertyName = propertyName;
        this.getterName = getterName;
        this.setterName = setterName;
        this.observeChanges = observeChanges;
        Preconditions.checkNotBlank(propertyName, Messages.MUST_NOT_BE_BLANK, "property name");
        checkBeanChannelIdentityCheck(beanChannel);
        this.beanChangeHandler = this::onBeanChanged;
        this.beanChannel.addValueChangeListener(this.beanChangeHandler);
        B initialBean = getBean();
        if (initialBean != null) {
            getPropertyAccessor(initialBean);
            addChangeHandlerTo(initialBean);
        }
        this.storedOldBean = initialBean;
    }

    public B getBean() {
        return (B) this.beanChannel.getValue();
    }

    public void setBean(B newBean) {
        this.beanChannel.setValue(newBean);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public boolean getObserveChanges() {
        return this.observeChanges;
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return getValue0(getBean());
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        B bean = getBean();
        if (bean == null) {
            return;
        }
        try {
            setValue0(bean, newValue);
        } catch (PropertyVetoException e) {
        }
    }

    public void setVetoableValue(Object newValue) throws PropertyVetoException {
        if (getBean() == null) {
            return;
        }
        setValue0(getBean(), newValue);
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

    public synchronized void release() {
        removeChangeHandlerFrom(getBean());
        this.storedOldBean = null;
        this.beanChannel.removeValueChangeListener(this.beanChangeHandler);
    }

    @Override // com.jgoodies.binding.value.AbstractValueModel
    protected String paramString() {
        B bean = getBean();
        String beanType = null;
        Object value = getValue();
        String valueType = null;
        String propertyAccessorName = null;
        String propertyType = null;
        Method propertySetter = null;
        if (bean != null) {
            beanType = bean.getClass().getName();
            valueType = value == null ? null : value.getClass().getName();
            PropertyAccessor propertyAccessor = getPropertyAccessor(bean);
            propertyAccessorName = propertyAccessor.getPropertyName();
            propertyType = propertyAccessor.getPropertyType().getName();
            propertySetter = propertyAccessor.getWriteMethod();
        }
        return "bean=" + bean + "; bean type=" + beanType + "; value=" + value + "; value type=" + valueType + "; property name=" + propertyAccessorName + "; property type=" + propertyType + "; property setter=" + propertySetter;
    }

    private void setBean0(B oldBean, B newBean) {
        firePropertyChange("beforeBean", oldBean, newBean, true);
        removeChangeHandlerFrom(oldBean);
        forwardAdaptedValueChanged(oldBean, newBean);
        resetChanged();
        addChangeHandlerTo(newBean);
        firePropertyChange("bean", oldBean, newBean, true);
        firePropertyChange("afterBean", oldBean, newBean, true);
    }

    private void forwardAdaptedValueChanged(B oldBean, B newBean) {
        Object oldValue = (oldBean == null || isWriteOnlyProperty(oldBean)) ? null : getValue0(oldBean);
        Object newValue = (newBean == null || isWriteOnlyProperty(newBean)) ? null : getValue0(newBean);
        if (oldValue != null || newValue != null) {
            fireValueChange(oldValue, newValue, true);
        }
    }

    private void forwardAdaptedValueChanged(B newBean) {
        Object newValue = (newBean == null || isWriteOnlyProperty(newBean)) ? null : getValue0(newBean);
        fireValueChange((Object) null, newValue);
    }

    private void addChangeHandlerTo(B bean) {
        if (!this.observeChanges || bean == null) {
            return;
        }
        this.propertyChangeHandler = this::onPropertyChanged;
        BeanUtils.addPropertyChangeListener(bean, getBeanClass(bean), this.propertyChangeHandler);
    }

    private void removeChangeHandlerFrom(B bean) {
        if (!this.observeChanges || bean == null || this.propertyChangeHandler == null) {
            return;
        }
        BeanUtils.removePropertyChangeListener(bean, getBeanClass(bean), this.propertyChangeHandler);
        this.propertyChangeHandler = null;
    }

    private Class<?> getBeanClass(B bean) {
        return bean.getClass();
    }

    private Object getValue0(B bean) {
        if (bean == null) {
            return null;
        }
        return getPropertyAccessor(bean).getValue(bean);
    }

    private void setValue0(B bean, Object newValue) throws PropertyVetoException {
        getPropertyAccessor(bean).setValue(bean, newValue);
    }

    private PropertyAccessor getPropertyAccessor(B bean) {
        Class<?> beanClass = getBeanClass(bean);
        if (this.cachedPropertyAccessor == null || beanClass != this.cachedBeanClass) {
            this.cachedPropertyAccessor = PropertyAccessors.getProvider().getAccessor(beanClass, getPropertyName(), this.getterName, this.setterName);
            this.cachedBeanClass = beanClass;
        }
        return this.cachedPropertyAccessor;
    }

    private boolean isWriteOnlyProperty(B bean) {
        return getPropertyAccessor(bean).isWriteOnly();
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
        if (evt.getPropertyName() == null) {
            forwardAdaptedValueChanged(getBean());
        } else if (evt.getPropertyName().equals(getPropertyName())) {
            fireValueChange(evt.getOldValue(), evt.getNewValue(), true);
        }
    }
}
