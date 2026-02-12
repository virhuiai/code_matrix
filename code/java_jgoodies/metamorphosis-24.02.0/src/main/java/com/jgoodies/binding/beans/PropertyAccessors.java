package com.jgoodies.binding.beans;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAccessors.class */
public final class PropertyAccessors {
    private static PropertyAccessorProvider instance = new IntrospectionPropertyAccessorProvider();

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAccessors$PropertyAccessorProvider.class */
    public interface PropertyAccessorProvider {
        PropertyAccessor getAccessor(Class<?> cls, String str, String str2, String str3);
    }

    private PropertyAccessors() {
    }

    public static PropertyAccessorProvider getProvider() {
        return instance;
    }

    public static void setProvider(PropertyAccessorProvider provider) {
        Preconditions.checkNotNull(provider, Messages.MUST_NOT_BE_NULL, "provider");
        instance = provider;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAccessors$IntrospectionPropertyAccessorProvider.class */
    public static class IntrospectionPropertyAccessorProvider implements PropertyAccessorProvider {
        @Override // com.jgoodies.binding.beans.PropertyAccessors.PropertyAccessorProvider
        public PropertyAccessor getAccessor(Class<?> beanClass, String propertyName, String getterName, String setterName) {
            PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName, getterName, setterName);
            return new PropertyAccessor(propertyName, pd.getReadMethod(), pd.getWriteMethod());
        }

        private static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) throws IntrospectionException {
            BeanInfo info = Introspector.getBeanInfo(beanClass);
            for (PropertyDescriptor element : info.getPropertyDescriptors()) {
                if (propertyName.equals(element.getName())) {
                    return element;
                }
            }
            throw new IntrospectionException("Property '" + propertyName + "' not found in bean " + beanClass);
        }

        private static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName, String getterName, String setterName) {
            try {
                return (getterName == null && setterName == null) ? getPropertyDescriptor(beanClass, propertyName) : new PropertyDescriptor(propertyName, beanClass, getterName, setterName);
            } catch (IntrospectionException e) {
                throw new PropertyNotFoundException(propertyName, beanClass, (Throwable) e);
            }
        }
    }
}
