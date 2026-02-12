package com.jgoodies.binding.extras;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.beans.PropertyAccessor;
import com.jgoodies.binding.beans.PropertyAccessors;
import com.jgoodies.binding.beans.PropertyNotFoundException;
import com.jgoodies.common.bean.Bean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/extras/FastPropertyAccessorProvider.class */
public final class FastPropertyAccessorProvider implements PropertyAccessors.PropertyAccessorProvider {
    private final Map<Class<?>, CacheEntry> cache = new IdentityHashMap(100);

    @Override // com.jgoodies.binding.beans.PropertyAccessors.PropertyAccessorProvider
    public PropertyAccessor getAccessor(Class<?> beanClass, String propertyName, String getterName, String setterName) {
        PropertyAccessor accessor;
        synchronized (this.cache) {
            CacheEntry entry = this.cache.get(beanClass);
            if (entry == null) {
                entry = new CacheEntry(beanClass);
                this.cache.put(beanClass, entry);
            }
            accessor = entry.getAccessor(propertyName, getterName, setterName);
        }
        return accessor;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/extras/FastPropertyAccessorProvider$CacheEntry.class */
    private static final class CacheEntry {
        private final Map<String, Object> methods;
        private final Map<String, PropertyAccessor> accessors;

        CacheEntry(Class<?> beanClass) {
            Method[] publicMethods = beanClass.getMethods();
            int size = publicMethods.length;
            this.methods = new IdentityHashMap(size);
            this.accessors = new IdentityHashMap(size / 2);
            for (Method m : publicMethods) {
                Class<?> declaringClass = m.getDeclaringClass();
                if (!Modifier.isStatic(m.getModifiers()) && declaringClass != Model.class && declaringClass != Bean.class && declaringClass != Object.class) {
                    Class<?>[] ptypes = m.getParameterTypes();
                    Class<?> rtype = m.getReturnType();
                    if (ptypes.length <= 0 || rtype == Void.TYPE || rtype == Void.class) {
                        String name = m.getName();
                        if (name.startsWith("set")) {
                            if (ptypes.length != 0 && ptypes.length <= 1) {
                                Object o = this.methods.get(name);
                                if (o == null) {
                                    this.methods.put(name, m);
                                } else if (o instanceof Method) {
                                    Map<Class<?>, Method> map = new IdentityHashMap<>(10);
                                    map.put(ptypes[0], m);
                                    Method other = (Method) o;
                                    Class<?>[] otherTypes = other.getParameterTypes();
                                    map.put(otherTypes[0], other);
                                    this.methods.put(name, map);
                                } else if (o instanceof Map) {
                                    ((Map) o).put(ptypes[0], m);
                                }
                            }
                        } else {
                            this.methods.put(name, m);
                        }
                    }
                }
            }
        }

        PropertyAccessor getAccessor(String propertyName, String getterName, String setterName) {
            PropertyAccessor d = this.accessors.get(propertyName);
            if (d != null) {
                return d;
            }
            if (setterName != null || getterName != null) {
                Method readMethod = (Method) this.methods.get(getterName);
                Method writeMethod = (Method) this.methods.get(setterName);
                PropertyAccessor pa = new PropertyAccessor(propertyName, readMethod, writeMethod);
                this.accessors.put(propertyName, pa);
                return pa;
            }
            String methodSuffix = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            String getter0 = ("get" + methodSuffix).intern();
            Method readMethod2 = (Method) this.methods.get(getter0);
            if (readMethod2 == null) {
                String getter1 = ("is" + methodSuffix).intern();
                readMethod2 = (Method) this.methods.get(getter1);
            }
            Class<?> rtype = null;
            if (readMethod2 != null) {
                rtype = readMethod2.getReturnType();
            }
            String setter = ("set" + methodSuffix).intern();
            Object o = this.methods.get(setter);
            Method writeMethod2 = null;
            if (o instanceof Method) {
                writeMethod2 = (Method) o;
            } else if (o instanceof Map) {
                Map<Class<?>, Method> map = (Map) o;
                writeMethod2 = map.get(rtype);
            }
            if (readMethod2 != null && writeMethod2 != null) {
                Class<?>[] ptypes = writeMethod2.getParameterTypes();
                if (ptypes.length > 0 && ptypes[0] != rtype && rtype != null && !rtype.isAssignableFrom(ptypes[0])) {
                    throw new IllegalStateException("Type mismatch between read and write methods.\npropertyName=" + propertyName + "\nread method =" + readMethod2 + "\nwrite method=" + writeMethod2);
                }
            }
            if (readMethod2 == null && writeMethod2 == null) {
                throw new PropertyNotFoundException(propertyName, null);
            }
            PropertyAccessor d2 = new PropertyAccessor(propertyName, readMethod2, writeMethod2);
            this.accessors.put(propertyName, d2);
            return d2;
        }
    }
}
