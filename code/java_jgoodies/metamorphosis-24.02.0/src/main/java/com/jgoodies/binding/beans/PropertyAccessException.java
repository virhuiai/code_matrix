package com.jgoodies.binding.beans;

import com.jgoodies.common.bean.PropertyException;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAccessException.class */
public final class PropertyAccessException extends PropertyException {
    public PropertyAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PropertyAccessException createReadAccessException(Object bean, PropertyAccessor propertyAccessor, Throwable cause) {
        return new PropertyAccessException(createReadAccessMessage(bean, propertyAccessor, cause), cause);
    }

    public static PropertyAccessException createReadAccessException(Object bean, PropertyAccessor propertyAccessor, String causeMessage) {
        return new PropertyAccessException(createReadAccessMessage(bean, propertyAccessor, causeMessage), null);
    }

    public static PropertyAccessException createWriteAccessException(Object bean, Object value, PropertyAccessor propertyAccessor, Throwable cause) {
        return new PropertyAccessException(createWriteAccessMessage(bean, value, propertyAccessor, cause), cause);
    }

    public static PropertyAccessException createWriteAccessException(Object bean, Object value, PropertyAccessor propertyAccessor, String causeMessage) {
        return new PropertyAccessException(createWriteAccessMessage(bean, value, propertyAccessor, causeMessage), null);
    }

    private static String createReadAccessMessage(Object bean, PropertyAccessor propertyAccessor, Object cause) {
        String beanType = bean == null ? null : bean.getClass().getName();
        return "Failed to read an adapted Java Bean property.\ncause=" + cause + "\nbean=" + bean + "\nbean type=" + beanType + "\nproperty name=" + propertyAccessor.getPropertyName() + "\nproperty type=" + propertyAccessor.getPropertyType().getName() + "\nproperty reader=" + propertyAccessor.getReadMethod();
    }

    private static String createWriteAccessMessage(Object bean, Object value, PropertyAccessor propertyAccessor, Object cause) {
        String beanType = bean == null ? null : bean.getClass().getName();
        String valueType = value == null ? null : value.getClass().getName();
        return "Failed to set an adapted Java Bean property.\ncause=" + cause + "\nbean=" + bean + "\nbean type=" + beanType + "\nvalue=" + value + "\nvalue type=" + valueType + "\nproperty name=" + propertyAccessor.getPropertyName() + "\nproperty type=" + propertyAccessor.getPropertyType().getName() + "\nproperty setter=" + propertyAccessor.getWriteMethod();
    }
}
