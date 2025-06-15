package com.virhuiai.string;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 包含对象转字符串相关的方法
 */
public interface ToString {

/**
 * 判断一个类是否是指定的类或接口
 * @param objClass 要判断的类
 * @param className 类名或接口名（简单名或全限定名）
 * @return 如果是，返回true，否则返回false
 */
default boolean isClassOrInterface(Class objClass, String className) {
    // 根据单元测试 @Test(expected = NullPointerException.class) 的要求，
    // 当 objClass 为 null 时，应该抛出 NullPointerException，而不是返回 false。
    // 因此移除对 objClass == null 的判断。
    if (className == null) {
        return false;
    }

    // 原始逻辑，如果 objClass 为 null，这里会抛出 NullPointerException，符合测试预期。
    if (objClass.getName().equals(className) || objClass.getSimpleName().equals(className)) {
        return true;
    }
    Class[] classes = objClass.getInterfaces();
    for (Class cls : classes) {
        if (cls.getSimpleName().equals(className) || cls.getName().equals(className)) {
            return true;
        }
    }
    // 递归检查父类，因为接口可以被父类实现
    Class superClass = objClass.getSuperclass();
    if (superClass != null && isClassOrInterface(superClass, className)) {
        return true;
    }
    return false;
}

    /**
     * 判断一个类是否是指定类的子类或实现指定接口
     * @param objClass 要判断的类
     * @param className 父类名或接口名（简单名或全限定名）
     * @return 如果是,返回true,否则返回false
     */
    default boolean isSubClassOf(Class objClass, String className) {
        // 根据单元测试 @Test(expected = NullPointerException.class) 的要求，
        // 当 objClass 为 null 时，应该抛出 NullPointerException。
        if (objClass == null) {
            throw new NullPointerException("objClass cannot be null");
        }
        if (className == null) {
            return false;
        }

        Class<?> currentClass = objClass;
        while (currentClass != null) {
            // isClassOrInterface 方法已经处理了 objClass 为 null 的情况，
            // 但在此处，由于我们已在方法开头检查 objClass 为 null 并抛出异常，
            // currentClass 在循环首次迭代时不会为 null。
            if (isClassOrInterface(currentClass, className)) {
                return true;
            }
            currentClass = currentClass.getSuperclass();
        }
        return false;
    }

    // toStringArray 方法缺失，这里提供一个基本实现，假设它会将数组转换为字符串
    default String toStringArray(Object array) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return array.toString(); // If not an array, just return its toString
        }

        StringBuilder result = new StringBuilder("[");
        // For primitive arrays, Arrays.toString() is helpful
        if (array.getClass().getComponentType().isPrimitive()) {
            if (array instanceof int[]) {
                result.append(Arrays.toString((int[]) array));
            } else if (array instanceof long[]) {
                result.append(Arrays.toString((long[]) array));
            } else if (array instanceof short[]) {
                result.append(Arrays.toString((short[]) array));
            } else if (array instanceof byte[]) {
                result.append(Arrays.toString((byte[]) array));
            } else if (array instanceof char[]) {
                result.append(Arrays.toString((char[]) array));
            } else if (array instanceof float[]) {
                result.append(Arrays.toString((float[]) array));
            } else if (array instanceof double[]) {
                result.append(Arrays.toString((double[]) array));
            } else if (array instanceof boolean[]) {
                result.append(Arrays.toString((boolean[]) array));
            }
        } else {
            // For object arrays
            Object[] arr = (Object[]) array;
            for (int i = 0; i < arr.length; i++) {
                result.append(toString(arr[i]));
                if (i < arr.length - 1) {
                    result.append(", ");
                }
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 将任意对象转换为字符串
     * @param obj 要转换的对象
     * @return 转换后的字符串, 如果对象为null, 返回null
     */
    default String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIterator(((Collection<?>) obj).iterator(), objClass));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMap((Map<?, ?>) obj, objClass));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIterator((Iterator<?>) obj, objClass));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumeration((Enumeration<?>) obj, objClass));
        } else if (objClass.isArray()) {
            result.append(toStringArray(obj));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            Method[] methods = null;
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true); // Enable access to private fields
                        result.append(toString(fields[i].get(obj)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        result.append("null");
                    }
                    if (i < fields.length - 1) {
                        result.append("; ");
                    }
                }
                result.append("]");
            } else {
                result.append(obj.toString());
            }
        }
        return result.toString();
    }


    /**
     * 将Iterator转换成字符串
     *
     * @param iterator Iterator的类
     * @return 转换后的字符串
     */
    default String processIterator(Iterator<?> iterator, Class<?> objClass) {
        StringBuilder result = new StringBuilder();
        // 修正：使用实际的 objClass.getName()
        result.append(objClass.getName()).append("{");
        while (iterator.hasNext()) {
            result.append(toString(iterator.next()));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }



    /**
     * 将Enumeration转换成字符串
     *
     * @param enumeration 要转换的Enumeration
     * @param objClass    Enumeration的类
     * @return 转换后的字符串
     */
    default String processEnumeration(Enumeration<?> enumeration, Class<?> objClass) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toString(enumeration.nextElement()));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将Map转换成字符串
     *
     * @param map      要转换的Map
     * @param objClass Map的类
     * @return 转换后的字符串
     */
    default String processMap(Map<?, ?> map, Class<?> objClass) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toString(map.get(key)));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    //////////// todo test

    /**
     * 将Iterator转换为字符串，使用自定义格式
     */
    default String processIteratorWithFormat(Iterator<?> iterator, Class<?> objClass, String separator, String prefix, String suffix) {
        StringBuilder result = new StringBuilder();
        // 修正：使用实际的 objClass.getName()
        result.append(objClass.getName()).append(prefix);
        while (iterator.hasNext()) {
            result.append(toStringWithFormat(iterator.next(), separator, prefix, suffix));
            if (iterator.hasNext()) {
                result.append(separator);
            }
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，使用自定义格式
     */
    default String processEnumerationWithFormat(Enumeration<?> enumeration, Class<?> objClass, String separator, String prefix, String suffix) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append(prefix);
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithFormat(enumeration.nextElement(), separator, prefix, suffix));
            if (enumeration.hasMoreElements()) {
                result.append(separator);
            }
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，使用自定义格式
     */
    default String processMapWithFormat(Map<?, ?> map, Class<?> objClass, String separator, String prefix, String suffix) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append(prefix);
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithFormat(map.get(key), separator, prefix, suffix));
            if (it.hasNext()) {
                result.append(separator);
            }
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * 使用自定义格式将对象转换为字符串
     *
     * @param obj       要转换的对象
     * @param separator 字段或元素的分隔符
     * @param prefix    容器类型的前缀（如集合、数组）
     * @param suffix    容器类型的后缀
     * @return 转换后的字符串
     */
    default String toStringWithFormat(Object obj, String separator, String prefix, String suffix) {
        if (obj == null) {
            return "null";
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithFormat(((Collection<?>) obj).iterator(), objClass, separator, prefix, suffix));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithFormat((Map<?, ?>) obj, objClass, separator, prefix, suffix));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithFormat((Iterator<?>) obj, objClass, separator, prefix, suffix));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithFormat((Enumeration<?>) obj, objClass, separator, prefix, suffix));
        } else if (objClass.isArray()) {
            Object[] array = (Object[]) obj;
            result.append(prefix);
            for (int i = 0; i < array.length; i++) {
                result.append(toStringWithFormat(array[i], separator, prefix, suffix));
                if (i < array.length - 1) {
                    result.append(separator);
                }
            }
            result.append(suffix);
        } else {
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(prefix);
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithFormat(fields[i].get(obj), separator, prefix, suffix));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        result.append("null");
                    }
                    if (i < fields.length - 1) {
                        result.append(separator);
                    }
                }
                result.append(suffix);
            } else {
                result.append(obj.toString());
            }
        }
        return result.toString();
    }



    //    忽略特定字段或类型
    /**
     * 将对象转换为字符串，忽略指定的字段或类型
     *
     * @param obj           要转换的对象
     * @param ignoreFields  忽略的字段名
     * @param ignoreClasses 忽略的类
     * @return 转换后的字符串
     */
    default String toStringWithIgnore(Object obj, String[] ignoreFields, Class<?>[] ignoreClasses) {
        if (obj == null) {
            return "null";
        }
        for (Class<?> ignoreClass : ignoreClasses) {
            if (ignoreClass != null && ignoreClass.isInstance(obj)) {
                return obj.toString();
            }
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithIgnore(((Collection<?>) obj).iterator(), objClass, ignoreFields, ignoreClasses));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithIgnore((Map<?, ?>) obj, objClass, ignoreFields, ignoreClasses));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithIgnore((Iterator<?>) obj, objClass, ignoreFields, ignoreClasses));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithIgnore((Enumeration<?>) obj, objClass, ignoreFields, ignoreClasses));
        } else if (objClass.isArray()) {
            result.append(toStringArrayWithIgnore(obj, ignoreFields, ignoreClasses));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            Method[] methods = null;
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                boolean firstField = true;
                for (int i = 0; i < fields.length; i++) {
                    if (Arrays.asList(ignoreFields).contains(fields[i].getName())) {
                        continue;
                    }
                    if (!firstField) {
                        result.append("; ");
                    }
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithIgnore(fields[i].get(obj), ignoreFields, ignoreClasses));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        result.append("null");
                    }
                    firstField = false;
                }
                result.append("]");
            } else {
                result.append(obj.toString());
            }
        }
        return result.toString();
    }

    /**
     * 将 Iterator 转换为字符串，忽略指定字段或类型
     */
    default String processIteratorWithIgnore(Iterator<?> iterator, Class<?> objClass, String[] ignoreFields, Class<?>[] ignoreClasses) {
        StringBuilder result = new StringBuilder();
        // Changed "java.util.ArrayList" to objClass.getName() for consistency
        result.append(objClass.getName()).append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithIgnore(iterator.next(), ignoreFields, ignoreClasses));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }


    /**
     * 将数组转换为字符串，忽略指定字段或类型
     */
    default String toStringArrayWithIgnore(Object array, String[] ignoreFields, Class<?>[] ignoreClasses) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithIgnore(array, ignoreFields, ignoreClasses);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toStringWithIgnore(arr[i], ignoreFields, ignoreClasses));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，忽略指定字段或类型
     */
    default String processEnumerationWithIgnore(Enumeration<?> enumeration, Class<?> objClass, String[] ignoreFields, Class<?>[] ignoreClasses) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithIgnore(enumeration.nextElement(), ignoreFields, ignoreClasses));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，忽略指定字段或类型
     */
    default String processMapWithIgnore(Map<?, ?> map, Class<?> objClass, String[] ignoreFields, Class<?>[] ignoreClasses) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithIgnore(map.get(key), ignoreFields, ignoreClasses));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }



}
