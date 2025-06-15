package com.virhuiai.string;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
     * @param className 类名或接口名（简单名或全限定名）
     * @return 如果是，返回true，否则返回false
     */
    default boolean isClassOrInterface(Class objClass, String className) {
         if (objClass == null || className == null) {
            return false;
        }

        if (objClass.getName().equals(className) || objClass.getSimpleName().equals(className)) {
            return true;
        }
        Class[] classes = objClass.getInterfaces();
        for (Class cls : classes) {
            if (cls.getSimpleName().equals(className) || cls.getName().equals(className)) {
                return true;
            }
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
        if (objClass == null || className == null) {
            return false;
        }

        Class<?> currentClass = objClass;
        while (currentClass != null) {
            if (isClassOrInterface(currentClass, className)) {
                return true;
            }
            currentClass = currentClass.getSuperclass();
        }
        return false;
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
     * @param iterator 要转换的Iterator
     * @param objClass Iterator的类
     * @return 转换后的字符串
     */
    default String processIterator(Iterator<?> iterator, Class<?> objClass) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{"); // Standardize to ArrayList for Arrays.asList
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
     * @param enumeration 要转换的Enumeration
     * @param objClass Enumeration的类
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
     * @param map 要转换的Map
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
        result.append("java.util.ArrayList").append(prefix); // Standardize for Arrays.asList
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
     * @param obj 要转换的对象
     * @param separator 字段或元素的分隔符
     * @param prefix 容器类型的前缀（如集合、数组）
     * @param suffix 容器类型的后缀
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

    /////////// 深度控制
    /**
     * 将对象转换为字符串，限制递归深度
     * @param obj 要转换的对象
     * @param maxDepth 最大递归深度
     * @return 转换后的字符串
     */
    default String toStringWithDepth(Object obj, int maxDepth) {
        if (obj == null) {
            return "null";
        }
        if (maxDepth <= 0) {
            return obj.toString();
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithDepth(((Collection<?>) obj).iterator(), objClass, maxDepth - 1));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithDepth((Map<?, ?>) obj, objClass, maxDepth - 1));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithDepth((Iterator<?>) obj, objClass, maxDepth - 1));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithDepth((Enumeration<?>) obj, objClass, maxDepth - 1));
        } else if (objClass.isArray()) {
            result.append(toStringArrayWithDepth(obj, maxDepth - 1));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithDepth(fields[i].get(obj), maxDepth - 1));
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
     * 将 Iterator 转换为字符串，限制递归深度
     */
    default String processIteratorWithDepth(Iterator<?> iterator, Class<?> objClass, int maxDepth) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithDepth(iterator.next(), maxDepth));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }


    /**
     * 将数组转换为字符串，限制递归深度
     */
    default String toStringArrayWithDepth(Object array, int maxDepth) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithDepth(array, maxDepth);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toStringWithDepth(arr[i], maxDepth));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，限制递归深度
     */
    default String processEnumerationWithDepth(Enumeration<?> enumeration, Class<?> objClass, int maxDepth) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithDepth(enumeration.nextElement(), maxDepth));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将Map转换为字符串，限制递归深度
     */
    default String processMapWithDepth(Map<?, ?> map, Class<?> objClass, int maxDepth) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithDepth(map.get(key), maxDepth));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    //    忽略特定字段或类型
    /**
     * 将对象转换为字符串，忽略指定的字段或类型
     * @param obj 要转换的对象
     * @param ignoreFields 忽略的字段名
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
                for (int i = 0; i < fields.length; i++) {
                    if (Arrays.asList(ignoreFields).contains(fields[i].getName())) {
                        continue;
                    }
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithIgnore(fields[i].get(obj), ignoreFields, ignoreClasses));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        result.append("null");
                    }
                    if (i < fields.length - 1 && !Arrays.asList(ignoreFields).contains(fields[i].getName())) {
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
     * 将 Iterator 转换为字符串，忽略指定字段或类型
     */
    default String processIteratorWithIgnore(Iterator<?> iterator, Class<?> objClass, String[] ignoreFields, Class<?>[] ignoreClasses) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
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

    //    处理循环引用


    /**
     * 将数组转换为字符串，处理循环引用
     */
    default String toStringArrayWithCycleDetection(Object array, Set<Object> visited) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithCycleDetection(array, visited);
        }
        if (!visited.add(array)) {
            return "[Cyclic Reference]";
        }
        try {
            StringBuilder result = new StringBuilder("[");
            Object[] arr = (Object[]) array;
            for (int i = 0; i < arr.length; i++) {
                result.append(toStringWithCycleDetection(arr[i], visited));
                if (i < arr.length - 1) {
                    result.append(", ");
                }
            }
            result.append("]");
            return result.toString();
        } finally {
            visited.remove(array);
        }
    }

  /**
     * 将对象转换为字符串，处理循环引用。
     *
     * 此方法递归地将一个对象及其成员转换为字符串表示。
     * 为了防止无限循环，它使用一个Set来跟踪已经访问过的对象，
     * 当检测到循环引用时，会返回 "[Cyclic Reference]"。
     *
     * @param obj 要转换的对象。
     * @param visited 已访问的对象集合，用于检测循环引用。
     * 首次调用时，建议传入一个新的HashSet或null。
     * @return 转换后的字符串表示。
     */
    default String toStringWithCycleDetection(Object obj, Set<Object> visited) {
        // 如果对象为null，直接返回 "null" 字符串
        if (obj == null) {
            return "null";
        }

        // 如果visited集合为null，则创建一个新的HashSet。
        // 这通常在首次调用时发生，或者当调用者未提供集合时。
        if (visited == null) {
            visited = new HashSet<>();
        }

        // 尝试将当前对象添加到已访问集合。
        // 如果添加失败（即对象已存在于集合中），则说明检测到循环引用。
        if (!visited.add(obj)) {
            return "[Cyclic Reference]";
        }

        try {
            Class<?> objClass = obj.getClass();

            // 对于基本类型（包括其包装类，如Integer、Double等）和java.lang包下的对象，
            // 直接调用其toString方法。这些类型通常不会导致循环引用，且其toString方法已提供有意义的输出。
            if (objClass.isPrimitive() || objClass.getName().startsWith("java.lang")) {
                return obj.toString();
            }

            StringBuilder result = new StringBuilder();

            // 根据对象的类型进行不同的处理
            if (isSubClassOf(objClass, String.valueOf(Collection.class))) { // 修正：直接使用Class对象而不是字符串
                // 如果是Collection类型，处理其迭代器
                result.append(processIteratorWithCycleDetection(((Collection<?>) obj).iterator(), objClass, visited));
            } else if (isSubClassOf(objClass, String.valueOf(Map.class))) { // 修正：直接使用Class对象而不是字符串
                // 如果是Map类型，处理其键值对
                result.append(processMapWithCycleDetection((Map<?, ?>) obj, objClass, visited));
            } else if (isSubClassOf(objClass, String.valueOf(Iterator.class))) { // 修正：直接使用Class对象而不是字符串
                // 如果是Iterator类型，处理其元素
                result.append(processIteratorWithCycleDetection((Iterator<?>) obj, objClass, visited));
            } else if (isSubClassOf(objClass, String.valueOf(Enumeration.class))) { // 修正：直接使用Class对象而不是字符串
                // 如果是Enumeration类型，处理其元素
                result.append(processEnumerationWithCycleDetection((Enumeration<?>) obj, objClass, visited));
            } else if (objClass.isArray()) {
                // 如果是数组类型，使用专门的方法处理
                result.append(toStringArrayWithCycleDetection(obj, visited));
            } else {
                // 对于自定义对象，通过反射获取其字段
                Field[] fields = objClass.getDeclaredFields();
                // 如果是自定义类且存在字段，则遍历字段并转换为字符串
                // 修正：这里不再需要判断!objClass.getName().startsWith("java")，因为上面已经处理了java.lang包的类。
                // 而是判断是否有字段，以及是否为自定义类（通常非java开头的类名代表自定义类）
                if (fields.length > 0) {
                    result.append(objClass.getName()).append(":[");
                    for (int i = 0; i < fields.length; i++) {
                        result.append(fields[i].getName()).append(":");
                        try {
                            // 设置可访问性，以便访问私有字段
                            fields[i].setAccessible(true);
                            // 递归调用自身，处理字段的值
                            result.append(toStringWithCycleDetection(fields[i].get(obj), visited));
                        } catch (IllegalAccessException e) {
                            // 捕获访问异常，打印堆栈跟踪并返回 "null"
                            e.printStackTrace();
                            result.append("null");
                        }
                        if (i < fields.length - 1) {
                            result.append("; ");
                        }
                    }
                    result.append("]");
                } else {
                    // 如果是自定义类但没有字段，或者是非java开头的其他类型，直接调用其toString方法
                    result.append(obj.toString());
                }
            }
            return result.toString();
        } finally {
            // 无论方法如何退出（正常返回或抛出异常），都将当前对象从已访问集合中移除。
            // 这允许在同一递归路径的后续调用中，当当前对象的引用再次出现时，可以被正确处理。
            // 否则，如果一个对象被多个路径引用，且其中一个路径先访问了它，
            // 另一个路径在不同分支访问时，会被误判为循环引用。
            visited.remove(obj);
        }
    }

    /**
     * 将 Iterator 转换为字符串，处理循环引用
     */
    default String processIteratorWithCycleDetection(Iterator<?> iterator, Class<?> objClass, Set<Object> visited) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithCycleDetection(iterator.next(), visited));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，处理循环引用
     */
    default String processEnumerationWithCycleDetection(Enumeration<?> enumeration, Class<?> objClass, Set<Object> visited) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithCycleDetection(enumeration.nextElement(), visited));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，处理循环引用
     */
    default String processMapWithCycleDetection(Map<?, ?> map, Class<?> objClass, Set<Object> visited) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithCycleDetection(map.get(key), visited));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    //支持 JSON 格式输出
    /**
     * 将对象转换为 JSON 格式的字符串
     * @param obj 要转换的对象
     * @return JSON 格式的字符串
     */
    default String toJsonString(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            if (obj instanceof String) {
                return "\"" + obj.toString().replace("\"", "\\\"") + "\"";
            }
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append("[");
            Iterator<?> iterator = ((Collection<?>) obj).iterator();
            while (iterator.hasNext()) {
                result.append(toJsonString(iterator.next()));
                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("]");
        } else if (isSubClassOf(objClass, "Map")) {
            result.append("{");
            Collection<?> keys = ((Map<?, ?>) obj).keySet();
            Iterator<?> it = keys.iterator();
            while (it.hasNext()) {
                Object key = it.next();
                result.append("\"").append(key == null ? "null" : key.toString().replace("\"", "\\\"")).append("\":");
                result.append(toJsonString(((Map<?, ?>) obj).get(key)));
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("}");
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append("[");
            Iterator<?> iterator = (Iterator<?>) obj;
            while (iterator.hasNext()) {
                result.append(toJsonString(iterator.next()));
                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("]");
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append("[");
            Enumeration<?> enumeration = (Enumeration<?>) obj;
            while (enumeration.hasMoreElements()) {
                result.append(toJsonString(enumeration.nextElement()));
                if (enumeration.hasMoreElements()) {
                    result.append(", ");
                }
            }
            result.append("]");
        } else if (objClass.isArray()) {
            result.append(toStringArray(obj));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append("{");
                for (int i = 0; i < fields.length; i++) {
                    result.append("\"").append(fields[i].getName()).append("\":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toJsonString(fields[i].get(obj)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        result.append("null");
                    }
                    if (i < fields.length - 1) {
                        result.append(", ");
                    }
                }
                result.append("}");
            } else {
                result.append("\"").append(obj.toString().replace("\"", "\\\"")).append("\"");
            }
        }
        return result.toString();
    }

    // 缓存
    /**
     * 将对象转换为字符串，使用缓存的反射元数据
     * @param obj 要转换的对象
     * @param cache 元数据缓存
     * @return 转换后的字符串
     */
    default String toStringWithCache(Object obj, Map<Class<?>, Field[]> cache) {
        if (obj == null) {
            return "null";
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithCache(((Collection<?>) obj).iterator(), objClass, cache));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithCache((Map<?, ?>) obj, objClass, cache));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithCache((Iterator<?>) obj, objClass, cache));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithCache((Enumeration<?>) obj, objClass, cache));
        } else if (objClass.isArray()) {
            result.append(toStringArrayWithCache(obj, cache));
        } else {
            Field[] fields = cache.computeIfAbsent(objClass, cls -> cls.getDeclaredFields());
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithCache(fields[i].get(obj), cache));
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
     * 将 Iterator 转换为字符串，使用缓存
     */
    default String processIteratorWithCache(Iterator<?> iterator, Class<?> objClass, Map<Class<?>, Field[]> cache) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithCache(iterator.next(), cache));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，使用缓存
     */
    default String processEnumerationWithCache(Enumeration<?> enumeration, Class<?> objClass, Map<Class<?>, Field[]> cache) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithCache(enumeration.nextElement(), cache));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，使用缓存
     */
    default String processMapWithCache(Map<?, ?> map, Class<?> objClass, Map<Class<?>, Field[]> cache) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithCache(map.get(key), cache));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将数组转换为字符串，使用缓存
     */
    default String toStringArrayWithCache(Object array, Map<Class<?>, Field[]> cache) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithCache(array, cache);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toStringWithCache(arr[i], cache));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /// 支持特定类型的自定义处理器
    /**
     * 将对象转换为字符串，使用自定义类型处理器
     * @param obj 要转换的对象
     * @param typeHandlers 类型到处理器的映射
     * @return 转换后的字符串
     */
    default String toStringWithHandlers(Object obj, Map<Class<?>, Function<Object, String>> typeHandlers) {
        if (obj == null) {
            return "null";
        }
        Function<Object, String> handler = typeHandlers.get(obj.getClass());
        if (handler != null) {
            return handler.apply(obj);
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithHandlers(((Collection<?>) obj).iterator(), objClass, typeHandlers));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithHandlers((Map<?, ?>) obj, objClass, typeHandlers));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithHandlers((Iterator<?>) obj, objClass, typeHandlers));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithHandlers((Enumeration<?>) obj, objClass, typeHandlers));
        } else if (objClass.isArray()) {
            result.append(toStringArrayWithHandlers(obj, typeHandlers));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithHandlers(fields[i].get(obj), typeHandlers));
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
     * 将 Iterator 转换为字符串，使用自定义处理器
     */
    default String processIteratorWithHandlers(Iterator<?> iterator, Class<?> objClass, Map<Class<?>, Function<Object, String>> typeHandlers) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithHandlers(iterator.next(), typeHandlers));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，使用自定义处理器
     */
    default String processEnumerationWithHandlers(Enumeration<?> enumeration, Class<?> objClass, Map<Class<?>, Function<Object, String>> typeHandlers) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithHandlers(enumeration.nextElement(), typeHandlers));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，使用自定义处理器
     */
    default String processMapWithHandlers(Map<?, ?> map, Class<?> objClass, Map<Class<?>, Function<Object, String>> typeHandlers) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithHandlers(map.get(key), typeHandlers));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }



    /**
     * 将数组转换为字符串，使用自定义处理器
     */
    default String toStringArrayWithHandlers(Object array, Map<Class<?>, Function<Object, String>> typeHandlers) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithHandlers(array, typeHandlers);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toStringWithHandlers(arr[i], typeHandlers));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    ////// 错误处理增强

    /**
     * 将对象转换为字符串，允许自定义错误处理
     * @param obj 要转换的对象
     * @param errorHandler 异常处理器
     * @return 转换后的字符串
     */
    default String toStringWithErrorHandler(Object obj, BiConsumer<Exception, Field> errorHandler) {
        if (obj == null) {
            return "null";
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIteratorWithErrorHandler(((Collection<?>) obj).iterator(), objClass, errorHandler));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMapWithErrorHandler((Map<?, ?>) obj, objClass, errorHandler));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIteratorWithErrorHandler((Iterator<?>) obj, objClass, errorHandler));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumerationWithErrorHandler((Enumeration<?>) obj, objClass, errorHandler));
        } else if (objClass.isArray()) {
            result.append(toStringArrayWithErrorHandler(obj, errorHandler));
        } else {
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(objClass.getName()).append(":[");
                for (int i = 0; i < fields.length; i++) {
                    result.append(fields[i].getName()).append(":");
                    try {
                        fields[i].setAccessible(true);
                        result.append(toStringWithErrorHandler(fields[i].get(obj), errorHandler));
                    } catch (IllegalAccessException e) {
                        errorHandler.accept(e, fields[i]);
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
     * 将 Iterator 转换为字符串，支持错误处理
     */
    default String processIteratorWithErrorHandler(Iterator<?> iterator, Class<?> objClass, BiConsumer<Exception, Field> errorHandler) {
        StringBuilder result = new StringBuilder();
        result.append("java.util.ArrayList").append("{");
        while (iterator.hasNext()) {
            result.append(toStringWithErrorHandler(iterator.next(), errorHandler));
            if (iterator.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，支持错误处理
     */
    default String processEnumerationWithErrorHandler(Enumeration<?> enumeration, Class<?> objClass, BiConsumer<Exception, Field> errorHandler) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        while (enumeration.hasMoreElements()) {
            result.append(toStringWithErrorHandler(enumeration.nextElement(), errorHandler));
            if (enumeration.hasMoreElements()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Map 转换为字符串，支持错误处理
     */
    default String processMapWithErrorHandler(Map<?, ?> map, Class<?> objClass, BiConsumer<Exception, Field> errorHandler) {
        StringBuilder result = new StringBuilder();
        Collection<?> keys = map.keySet();
        result.append(objClass.getName()).append("{");
        Iterator<?> it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.append(key == null ? "null" : key).append("=").append(toStringWithErrorHandler(map.get(key), errorHandler));
            if (it.hasNext()) {
                result.append("; ");
            }
        }
        result.append("}");
        return result.toString();
    }



    /**
     * 将数组转换为字符串，支持错误处理
     */
    default String toStringArrayWithErrorHandler(Object array, BiConsumer<Exception, Field> errorHandler) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithErrorHandler(array, errorHandler);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toStringWithErrorHandler(arr[i], errorHandler));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /// 支持数组的深度转换
    /**
     * 将数组转换为字符串，递归处理元素
     * @param array 要转换的数组
     * @return 转换后的字符串
     */
    default String toStringArray(Object array) {
        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toString(array);
        }
        StringBuilder result = new StringBuilder("[");
        Object[] arr = (Object[]) array;
        for (int i = 0; i < arr.length; i++) {
            result.append(toString(arr[i]));
            if (i < arr.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }


    //提供简化的字符串输出
    /**
     * 将对象转换为简化的字符串
     * @param obj 要转换的对象
     * @return 简化的字符串
     */
    default String toSimpleString(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang") || objClass.isPrimitive()) {
            return obj.toString();
        }
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append(":[");
        Field[] fields = objClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            result.append(fields[i].getName()).append(":");
            try {
                fields[i].setAccessible(true);
                Object value = fields[i].get(obj);
                result.append(value != null ? value.toString() : "null");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                result.append("null");
            }
            if (i < fields.length - 1) {
                result.append("; ");
            }
        }
        result.append("]");
        return result.toString();
    }



}
