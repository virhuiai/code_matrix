package com.jgoodies.common.bean;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/bean/BeanUtils.class */
public final class BeanUtils {
    private static final Class<?>[] PCL_PARAMS = {PropertyChangeListener.class};
    private static final Class<?>[] NAMED_PCL_PARAMS = {String.class, PropertyChangeListener.class};

    private BeanUtils() {
    }

    public static boolean supportsBoundProperties(Class<?> clazz) {
        return (getPCLAdder(clazz) == null || getPCLRemover(clazz) == null) ? false : true;
    }

    public static Method getPCLAdder(Class<?> clazz) {
        try {
            return clazz.getMethod("addPropertyChangeListener", PCL_PARAMS);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getPCLRemover(Class<?> clazz) {
        try {
            return clazz.getMethod("removePropertyChangeListener", PCL_PARAMS);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getNamedPCLAdder(Class<?> clazz) {
        try {
            return clazz.getMethod("addPropertyChangeListener", NAMED_PCL_PARAMS);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getNamedPCLRemover(Class<?> clazz) {
        try {
            return clazz.getMethod("removePropertyChangeListener", NAMED_PCL_PARAMS);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static void addPropertyChangeListener(Object bean, Class<?> beanClass, PropertyChangeListener listener) {
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "listener");
        if (beanClass == null) {
            beanClass = bean.getClass();
        } else {
            Preconditions.checkArgument(beanClass.isInstance(bean), "The bean %s must be an instance of %s", bean, beanClass);
        }
        if (bean instanceof ObservableBean) {
            ((ObservableBean) bean).addPropertyChangeListener(listener);
            return;
        }
        if (bean instanceof Component) {
            ((Component) bean).addPropertyChangeListener(listener);
            return;
        }
        if (!supportsBoundProperties(beanClass)) {
            throw new PropertyUnboundException("Bound properties unsupported by bean class=" + beanClass + "\nThe Bean class must provide a pair of methods:\npublic void addPropertyChangeListener(PropertyChangeListener x);\npublic void removePropertyChangeListener(PropertyChangeListener x);");
        }
        Method multicastPCLAdder = getPCLAdder(beanClass);
        try {
            multicastPCLAdder.invoke(bean, listener);
        } catch (IllegalAccessException e) {
            throw new PropertyNotBindableException("Due to an IllegalAccessException we failed to add a multicast PropertyChangeListener to bean: " + bean, e);
        } catch (InvocationTargetException e2) {
            throw new PropertyNotBindableException("Due to an InvocationTargetException we failed to add a multicast PropertyChangeListener to bean: " + bean, e2.getCause());
        }
    }

    public static void addPropertyChangeListener(Object bean, Class<?> beanClass, String propertyName, PropertyChangeListener listener) {
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "listener");
        if (beanClass == null) {
            beanClass = bean.getClass();
        } else {
            Preconditions.checkArgument(beanClass.isInstance(bean), "The bean %s must be an instance of %s", bean, beanClass);
        }
        if (bean instanceof ObservableBean2) {
            ((ObservableBean2) bean).addPropertyChangeListener(propertyName, listener);
            return;
        }
        if (bean instanceof Component) {
            ((Component) bean).addPropertyChangeListener(propertyName, listener);
            return;
        }
        Method namedPCLAdder = getNamedPCLAdder(beanClass);
        if (namedPCLAdder == null) {
            throw new PropertyNotBindableException("Could not find the bean method\npublic void addPropertyChangeListener(String, PropertyChangeListener);\nin bean: " + bean);
        }
        try {
            namedPCLAdder.invoke(bean, propertyName, listener);
        } catch (IllegalAccessException e) {
            throw new PropertyNotBindableException("Due to an IllegalAccessException we failed to add a named PropertyChangeListener to bean: " + bean, e);
        } catch (InvocationTargetException e2) {
            throw new PropertyNotBindableException("Due to an InvocationTargetException we failed to add a named PropertyChangeListener to bean: " + bean, e2.getCause());
        }
    }

    public static void addPropertyChangeListener(Object bean, PropertyChangeListener listener) {
        addPropertyChangeListener(bean, (Class<?>) null, listener);
    }

    public static void addPropertyChangeListener(Object bean, String propertyName, PropertyChangeListener listener) {
        addPropertyChangeListener(bean, null, propertyName, listener);
    }

    public static void removePropertyChangeListener(Object bean, Class<?> beanClass, PropertyChangeListener listener) {
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "listener");
        if (beanClass == null) {
            beanClass = bean.getClass();
        } else {
            Preconditions.checkArgument(beanClass.isInstance(bean), "The bean %s must be an instance of %s", bean, beanClass);
        }
        if (bean instanceof ObservableBean) {
            ((ObservableBean) bean).removePropertyChangeListener(listener);
            return;
        }
        if (bean instanceof Component) {
            ((Component) bean).removePropertyChangeListener(listener);
            return;
        }
        Method multicastPCLRemover = getPCLRemover(beanClass);
        if (multicastPCLRemover == null) {
            throw new PropertyUnboundException("Could not find the method:\npublic void removePropertyChangeListener(String, PropertyChangeListener x);\nfor bean:" + bean);
        }
        try {
            multicastPCLRemover.invoke(bean, listener);
        } catch (IllegalAccessException e) {
            throw new PropertyNotBindableException("Due to an IllegalAccessException we failed to remove a multicast PropertyChangeListener from bean: " + bean, e);
        } catch (InvocationTargetException e2) {
            throw new PropertyNotBindableException("Due to an InvocationTargetException we failed to remove a multicast PropertyChangeListener from bean: " + bean, e2.getCause());
        }
    }

    public static void removePropertyChangeListener(Object bean, Class<?> beanClass, String propertyName, PropertyChangeListener listener) {
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "listener");
        if (beanClass == null) {
            beanClass = bean.getClass();
        } else {
            Preconditions.checkArgument(beanClass.isInstance(bean), "The bean %s must be an instance of %s", bean, beanClass);
        }
        if (bean instanceof ObservableBean2) {
            ((ObservableBean2) bean).removePropertyChangeListener(propertyName, listener);
            return;
        }
        if (bean instanceof Component) {
            ((Component) bean).removePropertyChangeListener(propertyName, listener);
            return;
        }
        Method namedPCLRemover = getNamedPCLRemover(beanClass);
        if (namedPCLRemover == null) {
            throw new PropertyNotBindableException("Could not find the bean method\npublic void removePropertyChangeListener(String, PropertyChangeListener);\nin bean: " + bean);
        }
        try {
            namedPCLRemover.invoke(bean, propertyName, listener);
        } catch (IllegalAccessException e) {
            throw new PropertyNotBindableException("Due to an IllegalAccessException we failed to remove a named PropertyChangeListener from bean: " + bean, e);
        } catch (InvocationTargetException e2) {
            throw new PropertyNotBindableException("Due to an InvocationTargetException we failed to remove a named PropertyChangeListener from bean: " + bean, e2.getCause());
        }
    }

    public static void removePropertyChangeListener(Object bean, PropertyChangeListener listener) {
        removePropertyChangeListener(bean, (Class<?>) null, listener);
    }

    public static void removePropertyChangeListener(Object bean, String propertyName, PropertyChangeListener listener) {
        removePropertyChangeListener(bean, null, propertyName, listener);
    }
}
