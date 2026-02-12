package com.jgoodies.binding.beans;

import com.jgoodies.common.bean.PropertyException;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyNotFoundException.class */
public final class PropertyNotFoundException extends PropertyException {
    public PropertyNotFoundException(String propertyName, Object bean) {
        this(propertyName, bean, (Throwable) null);
    }

    public PropertyNotFoundException(String propertyName, Object bean, Throwable cause) {
        super("Property '" + propertyName + "' not found in bean " + bean, cause);
    }

    public PropertyNotFoundException(String propertyName, Class<?> beanClass, Throwable cause) {
        super("Property '" + propertyName + "' not found in bean class " + beanClass, cause);
    }
}
