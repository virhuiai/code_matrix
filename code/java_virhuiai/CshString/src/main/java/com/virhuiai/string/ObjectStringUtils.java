package com.virhuiai.string;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * 包含对象转字符串相关的方法
 *    - toString
 *    - isClassOrInterface
 *    - isSubClassOf
 *    - processIterator
 *    - processEnumeration
 *    - processMap
 */
public interface ObjectStringUtils {


    /**
     * 判断一个类是否是指定的类或接口
     * @param objClass 要判断的类
     * @param className 类名或接口名
     * @return 如果是,返回true,否则返回false
     */
    default boolean isClassOrInterface(Class objClass, String className) {
        if (objClass.getClass().getName().equals(className)) {
            return true;
        }
        Class[] classes = objClass.getInterfaces();
        for (Class cls : classes) {
            if (cls.getName().equals("java.util." + className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个类是否是指定类的子类
     * @param objClass 要判断的类
     * @param className 父类名
     * @return 如果是,返回true,否则返回false
     */
    default boolean isSubClassOf(Class objClass, String className) {
        while (!isClassOrInterface(objClass, className)) {
            Class objClass2 = objClass.getSuperclass();
            objClass = Object.class;
            if (objClass2.equals(Object.class)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 将任意对象转换为字符串
     * @param obj 要转换的对象
     * @return 转换后的字符串,如果对象为null,返回null
     */
    default String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        Class objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang")) {
            return obj.toString();
        }
        StringBuffer result = new StringBuffer();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIterator(((Collection) obj).iterator(), objClass));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMap((Map) obj, objClass));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIterator((Iterator) obj, objClass));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumeration((Enumeration) obj, objClass));
        } else if (objClass.isAssignableFrom(new Object[0].getClass())) {
            Object[] array = (Object[]) obj;
            result.append("[");
            for (int i = 0; i < array.length; i++) {
                result.append(array[i] + ":" + (array[i] != null ? array[i].getClass().getName() : "NULL"));
                if (i < array.length - 1) {
                    result.append(",");
                }
            }
            result.append("]");
        } else {
            Method[] methods = (Method[]) null;
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(obj.getClass().getName()).append(":[");
                for (int i2 = 0; i2 < fields.length; i2++) {
                    result.append(fields[i2].getName()).append(":");
                    if (fields[i2].isAccessible()) {
                        try {
                            result.append(toString(fields[i2].get(obj)));
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    } else {
                        if (methods == null) {
                            methods = objClass.getMethods();
                        }
                        for (int j = 0; j < methods.length; j++) {
                            if (methods[j].getName().equalsIgnoreCase("get" + fields[i2].getName())) {
                                try {
                                    result.append(toString(methods[j].invoke(obj, null)));
                                } catch (IllegalAccessException iae2) {
                                    iae2.printStackTrace();
                                } catch (InvocationTargetException ite) {
                                    ite.printStackTrace();
                                }
                            }
                        }
                    }
                    result.append("; ");
                }
                result.append(']');
            } else {
                result.append(obj);
                return result.toString();
            }
        }
        return result.toString();
    }


    /**
     * 将Iterator转换成字符串
     * @param iterator 要转换的Iterator
     * @param objClass Iterator的类
     * @return 转换后的字符串
     */
    default String processIterator(Iterator iterator, Class objClass) {
        StringBuffer result = new StringBuffer();
        result.append(objClass.getName());
        result.append('{');
        while (iterator.hasNext()) {
            result.append(toString(iterator.next()));
            result.append("; ");
        }
        result.append('}');
        return result.toString();
    }



    /**
     * 将Enumeration转换成字符串
     * @param enumeration 要转换的Enumeration
     * @param objClass Enumeration的类
     * @return 转换后的字符串
     */
    default String processEnumeration(Enumeration enumeration, Class objClass) {
        StringBuffer result = new StringBuffer();
        result.append(objClass.getName());
        result.append('{');
        while (enumeration.hasMoreElements()) {
            result.append(toString(enumeration.nextElement()));
            result.append("; ");
        }
        result.append('}');
        return result.toString();
    }

    /**
     * 将Map转换成字符串
     * @param map 要转换的Map
     * @param objClass Map的类
     * @return 转换后的字符串
     */
    default String processMap(Map map, Class objClass) {
        StringBuffer result = new StringBuffer();
        Collection keys = map.keySet();
        result.append(objClass.getName());
        result.append('{');
        for (Object obj : keys) {
            result.append(obj).append('=').append(toString(map.get(obj))).append("; ");
        }
        result.append('}');
        return result.toString();
    }


}
