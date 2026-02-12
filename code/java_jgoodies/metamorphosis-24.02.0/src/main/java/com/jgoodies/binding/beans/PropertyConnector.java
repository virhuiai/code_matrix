package com.jgoodies.binding.beans;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyConnector.class */
public final class PropertyConnector {
    private final Object bean1;
    private final Object bean2;
    private final Class<?> bean1Class;
    private final Class<?> bean2Class;
    private final String property1Name;
    private final String property2Name;
    private final PropertyChangeListener property1ChangeHandler;
    private final PropertyChangeListener property2ChangeHandler;
    private final PropertyAccessor property1Accessor;
    private final PropertyAccessor property2Accessor;

    private PropertyConnector(Object bean1, String property1Name, Object bean2, String property2Name) {
        this.bean1 = Preconditions.checkNotNull(bean1, Messages.MUST_NOT_BE_NULL, "bean 1");
        this.bean2 = Preconditions.checkNotNull(bean2, Messages.MUST_NOT_BE_NULL, "bean 2");
        this.bean1Class = bean1.getClass();
        this.bean2Class = bean2.getClass();
        this.property1Name = (String) Preconditions.checkNotBlank(property1Name, Messages.MUST_NOT_BE_BLANK, "propertyName1");
        this.property2Name = (String) Preconditions.checkNotBlank(property2Name, Messages.MUST_NOT_BE_BLANK, "propertyName2");
        Preconditions.checkArgument((bean1 == bean2 && property1Name.equals(property2Name)) ? false : true, "Cannot connect a bean property to itself on the same bean.");
        this.property1Accessor = getPropertyAccessor(this.bean1Class, property1Name);
        this.property2Accessor = getPropertyAccessor(this.bean2Class, property2Name);
        boolean property1Writable = this.property1Accessor.getWriteMethod() != null;
        boolean property1Readable = this.property1Accessor.getReadMethod() != null;
        Preconditions.checkArgument(!property1Writable || property1Readable, "Property1 must be readable.");
        boolean property2Writable = this.property2Accessor.getWriteMethod() != null;
        boolean property2Readable = this.property2Accessor.getReadMethod() != null;
        Preconditions.checkArgument(!property2Writable || property2Readable, "Property2 must be readable.");
        Preconditions.checkArgument(property1Writable || property2Writable, "Cannot connect two read-only properties.");
        boolean property1Observable = BeanUtils.supportsBoundProperties(this.bean1Class);
        boolean property2Observable = BeanUtils.supportsBoundProperties(this.bean2Class);
        if (property1Observable && property2Writable) {
            this.property1ChangeHandler = new PropertyChangeHandler(bean1, this.property1Accessor, bean2, this.property2Accessor);
            addPropertyChangeHandler(bean1, this.bean1Class, this.property1ChangeHandler);
        } else {
            this.property1ChangeHandler = null;
        }
        if (property2Observable && property1Writable) {
            this.property2ChangeHandler = new PropertyChangeHandler(bean2, this.property2Accessor, bean1, this.property1Accessor);
            addPropertyChangeHandler(bean2, this.bean2Class, this.property2ChangeHandler);
        } else {
            this.property2ChangeHandler = null;
        }
    }

    public static PropertyConnector connect(Object bean1, String property1Name, Object bean2, String property2Name) {
        return new PropertyConnector(bean1, property1Name, bean2, property2Name);
    }

    public static void connectAndUpdate(ValueModel valueModel, Object bean2, String property2Name) {
        PropertyConnector connector = new PropertyConnector(valueModel, ValueModel.PROPERTY_VALUE, bean2, property2Name);
        connector.updateProperty2();
    }

    public Object getBean1() {
        return this.bean1;
    }

    public Object getBean2() {
        return this.bean2;
    }

    public String getProperty1Name() {
        return this.property1Name;
    }

    public String getProperty2Name() {
        return this.property2Name;
    }

    public void updateProperty1() {
        Object property2Value = this.property2Accessor.getValue(this.bean2);
        setValueSilently(this.bean2, this.property2Accessor, this.bean1, this.property1Accessor, property2Value);
    }

    public void updateProperty2() {
        Object property1Value = this.property1Accessor.getValue(this.bean1);
        setValueSilently(this.bean1, this.property1Accessor, this.bean2, this.property2Accessor, property1Value);
    }

    public void release() {
        removePropertyChangeHandler(this.bean1, this.bean1Class, this.property1ChangeHandler);
        removePropertyChangeHandler(this.bean2, this.bean2Class, this.property2ChangeHandler);
    }

    private static void addPropertyChangeHandler(Object bean, Class<?> beanClass, PropertyChangeListener listener) {
        if (bean != null) {
            BeanUtils.addPropertyChangeListener(bean, beanClass, listener);
        }
    }

    private static void removePropertyChangeHandler(Object bean, Class<?> beanClass, PropertyChangeListener listener) {
        if (bean != null) {
            BeanUtils.removePropertyChangeListener(bean, beanClass, listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValueSilently(Object sourceBean, PropertyAccessor sourcePropertyAccessor, Object targetBean, PropertyAccessor targetPropertyAccessor, Object newValue) {
        if (targetPropertyAccessor.getValue(targetBean) == newValue) {
            return;
        }
        if (this.property1ChangeHandler != null) {
            removePropertyChangeHandler(this.bean1, this.bean1Class, this.property1ChangeHandler);
        }
        if (this.property2ChangeHandler != null) {
            removePropertyChangeHandler(this.bean2, this.bean2Class, this.property2ChangeHandler);
        }
        try {
            targetPropertyAccessor.setValue(targetBean, newValue);
        } catch (PropertyVetoException e) {
        }
        Object targetValue = targetPropertyAccessor.getValue(targetBean);
        if (!Objects.equals(targetValue, newValue)) {
            boolean sourcePropertyWritable = sourcePropertyAccessor.getWriteMethod() != null;
            if (sourcePropertyWritable) {
                try {
                    sourcePropertyAccessor.setValue(sourceBean, targetValue);
                } catch (PropertyVetoException e2) {
                }
            }
        }
        if (this.property1ChangeHandler != null) {
            addPropertyChangeHandler(this.bean1, this.bean1Class, this.property1ChangeHandler);
        }
        if (this.property2ChangeHandler != null) {
            addPropertyChangeHandler(this.bean2, this.bean2Class, this.property2ChangeHandler);
        }
    }

    private static PropertyAccessor getPropertyAccessor(Class<?> beanClass, String propertyName) {
        return PropertyAccessors.getProvider().getAccessor(beanClass, propertyName, null, null);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyConnector$PropertyChangeHandler.class */
    private final class PropertyChangeHandler implements PropertyChangeListener {
        private final Object sourceBean;
        private final PropertyAccessor sourcePropertyDescriptor;
        private final Object targetBean;
        private final PropertyAccessor targetPropertyDescriptor;

        private PropertyChangeHandler(Object sourceBean, PropertyAccessor sourcePropertyDescriptor, Object targetBean, PropertyAccessor targetPropertyDescriptor) {
            this.sourceBean = sourceBean;
            this.sourcePropertyDescriptor = sourcePropertyDescriptor;
            this.targetBean = targetBean;
            this.targetPropertyDescriptor = targetPropertyDescriptor;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            String sourcePropertyName = this.sourcePropertyDescriptor.getPropertyName();
            String propertyName = evt.getPropertyName();
            if (propertyName == null || propertyName.equals(sourcePropertyName)) {
                Object newValue = evt.getNewValue();
                if (newValue == null || propertyName == null) {
                    newValue = this.sourcePropertyDescriptor.getValue(this.sourceBean);
                }
                PropertyConnector.this.setValueSilently(this.sourceBean, this.sourcePropertyDescriptor, this.targetBean, this.targetPropertyDescriptor, newValue);
            }
        }
    }
}
