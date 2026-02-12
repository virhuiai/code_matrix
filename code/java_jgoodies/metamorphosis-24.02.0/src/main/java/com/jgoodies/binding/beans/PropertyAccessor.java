package com.jgoodies.binding.beans;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/PropertyAccessor.class */
public final class PropertyAccessor {
    private final String propertyName;
    private final Method readMethod;
    private final Method writeMethod;

    public PropertyAccessor(String propertyName, Method readMethod, Method writeMethod) {
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        Preconditions.checkArgument((readMethod == null && writeMethod == null) ? false : true, "Either the reader or writer must not be null.");
        this.propertyName = propertyName;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Class<?> getPropertyType() {
        if (this.readMethod != null) {
            return this.readMethod.getReturnType();
        }
        return this.writeMethod.getParameterTypes()[0];
    }

    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public boolean isReadOnly() {
        return this.writeMethod == null;
    }

    public boolean isWriteOnly() {
        return this.readMethod == null;
    }

    public Object getValue(Object bean) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        if (isWriteOnly()) {
            throw PropertyAccessException.createReadAccessException(bean, this, createMissingGetterCause(bean));
        }
        try {
            return this.readMethod.invoke(bean, (Object[]) null);
        } catch (IllegalAccessException e) {
            throw PropertyAccessException.createReadAccessException(bean, this, e);
        } catch (InvocationTargetException e2) {
            throw PropertyAccessException.createReadAccessException(bean, this, e2.getCause());
        }
    }

    public void setValue(Object bean, Object newValue) throws PropertyVetoException {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        if (isReadOnly()) {
            throw PropertyAccessException.createWriteAccessException(bean, newValue, this, createMissingSetterCause(bean));
        }
        try {
            this.writeMethod.invoke(bean, newValue);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw PropertyAccessException.createWriteAccessException(bean, newValue, this, e);
        } catch (InvocationTargetException e2) {
            PropertyVetoException cause = e2.getCause();
            if (cause instanceof PropertyVetoException) {
                throw cause;
            }
            throw PropertyAccessException.createWriteAccessException(bean, newValue, this, (Throwable) cause);
        }
    }

    private String createMissingGetterCause(Object bean) {
        boolean isBooleanProperty = getPropertyType() == Boolean.class || getPropertyType() == Boolean.TYPE;
        String methodName1 = formatAccessorMethodSignature(bean, "is", null);
        String methodName2 = formatAccessorMethodSignature(bean, "get", null);
        String typicalMethodOrMethods = isBooleanProperty ? methodName1 + " or " + methodName2 : methodName2;
        return String.format("The bean property is write-only; the bean class has no public getter method, typically %s.", typicalMethodOrMethods);
    }

    private String createMissingSetterCause(Object bean) {
        String methodName = formatAccessorMethodSignature(bean, "set", getPropertyType());
        return String.format("The bean property is read-only; the bean class has no public setter method, typically %s.", methodName);
    }

    private String formatAccessorMethodSignature(Object bean, String methodPrefix, Class<?> methodParamType) {
        Object[] objArr = new Object[4];
        objArr[0] = bean.getClass().getSimpleName();
        objArr[1] = methodPrefix;
        objArr[2] = capitalizedPropertyName();
        objArr[3] = methodParamType == null ? "" : methodParamType.getSimpleName();
        return String.format("%1$s#%2$s%3$s(%4$s)", objArr);
    }

    private String capitalizedPropertyName() {
        return getPropertyName().substring(0, 1).toUpperCase() + getPropertyName().substring(1);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PropertyAccessor other = (PropertyAccessor) obj;
        if (!Objects.equals(this.propertyName, other.propertyName) || !Objects.equals(this.readMethod, other.readMethod) || !Objects.equals(this.writeMethod, other.writeMethod)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.propertyName, this.readMethod, this.writeMethod);
    }
}
