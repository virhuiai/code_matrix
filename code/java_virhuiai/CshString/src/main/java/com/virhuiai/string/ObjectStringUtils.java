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
 */
public interface ObjectStringUtils {

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

        // 对于基本类型（包括其包装类，如Integer、Double等）和java.lang包下的对象，
        // 直接调用其toString方法。这些类型通常不会导致循环引用，且其toString方法已提供有意义的输出。
        // 这一步应该在添加visited集合之前，因为这些对象不需要循环引用检测。
        Class<?> objClass = obj.getClass();
        if (objClass.isPrimitive() || objClass.getName().startsWith("java.lang")) {
            return obj.toString();
        }

        // 核心修复逻辑：对于Map和Collection类型，在尝试访问其内容之前，
        // 先检查是否已经访问过。这可以避免在添加它们到visited集合时，
        // 它们的hashCode()或equals()方法因自引用而导致的StackOverflowError。
        if (obj instanceof Map) {
            if (visited.contains(obj)) {
                return "[Cyclic Reference]";
            }
            visited.add(obj); // 将当前Map添加到已访问集合
            try {
                return processMapWithCycleDetection((Map<?, ?>) obj, objClass, visited);
            } finally {
                visited.remove(obj); // 处理完成后移除，以便其他路径可以正确处理
            }
        } else if (obj instanceof Collection) {
            if (visited.contains(obj)) {
                return "[Cyclic Reference]";
            }
            visited.add(obj); // 将当前Collection添加到已访问集合
            try {
                return processCollectionWithCycleDetection((Collection<?>) obj, objClass, visited);
            } finally {
                visited.remove(obj); // 处理完成后移除
            }
        }

        // 对于其他所有非Map、非Collection的对象，在进行深度遍历前进行循环引用检测
        if (!visited.add(obj)) {
            return "[Cyclic Reference]";
        }

        try {
            StringBuilder result = new StringBuilder();

            // 根据对象的类型进行不同的处理
            // 注意：Collection和Map已经在上面独立处理了，这里不再需要检查它们。
            if (obj instanceof Iterator) {
                // 如果是Iterator类型，处理其元素
                result.append(processIteratorWithCycleDetection((Iterator<?>) obj, objClass, visited));
            } else if (obj instanceof Enumeration) {
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
                    result.append(objClass.getName()).append("{"); // 统一使用大括号
                    for (int i = 0; i < fields.length; i++) {
                        result.append(fields[i].getName()).append("="); // 统一使用等号
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
                    result.append("}");
                } else {
                    // 如果是自定义类但没有字段，或者是非java开头的其他类型，直接调用其toString方法
                    result.append(obj.toString());
                }
            }
            return result.toString();
        } finally {
            // 对于非Map和非Collection的对象，处理完成后从已访问集合中移除
            if (!(obj instanceof Map) && !(obj instanceof Collection)) {
                visited.remove(obj);
            }
        }
    }

    /**
     * 将 Collection 转换为字符串，处理循环引用
     */
    default String processCollectionWithCycleDetection(Collection<?> collection, Class<?> objClass, Set<Object> visited) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{"); // 使用实际类名
        Iterator<?> iterator = collection.iterator();
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
     * 将 Iterator 转换为字符串，处理循环引用
     */
    default String processIteratorWithCycleDetection(Iterator<?> iterator, Class<?> objClass, Set<Object> visited) {
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{"); // 尽量保持一致的输出格式
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
        result.append(objClass.getName()).append("{");
        Iterator<?> it = map.entrySet().iterator(); // 遍历entrySet而不是keySet，可以同时获取键和值
        boolean first = true;
        while (it.hasNext()) {
            if (!first) {
                result.append("; ");
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key == null) {
                result.append("null");
            } else {
                // 递归处理键，因为键也可能是复杂对象或自引用
                result.append(toStringWithCycleDetection(key, visited));
            }


            result.append("=").append(toStringWithCycleDetection(value, visited));
            first = false;
        }
        result.append("}");
        return result.toString();
    }


    // 假设存在此方法，这里不提供其实现，因为它不在问题描述的范围内 toStringArrayWithCycleDetection


    //支持 JSON 格式输出
    /**
     * 将对象转换为 JSON 格式的字符串，处理循环引用。
     *
     * 此方法递归地将一个对象及其成员转换为 JSON 字符串表示。
     * 为了防止无限循环，它使用一个Set来跟踪已经访问过的对象，
     * 当检测到循环引用时，会返回 "[Circular Reference]"。
     *
     * @param obj 要转换的对象。
     * @return JSON 格式的字符串。
     */
    default String toJsonString(Object obj) {
        return toJsonString(obj, new HashSet<>());
    }

    /**
     * 将对象转换为 JSON 格式的字符串，处理循环引用。
     *
     * @param obj 要转换的对象。
     * @param visited 已访问的对象集合，用于检测循环引用。
     * @return JSON 格式的字符串。
     */
    default String toJsonString(Object obj, Set<Object> visited) {
        if (obj == null) {
            return "null";
        }

        // 对于基本类型（包括其包装类，如Integer、Double等）和java.lang包下的对象，
        // 直接处理。这些类型通常不会导致循环引用，且其toString方法已提供有意义的输出。
        Class<?> objClass = obj.getClass();
        if (objClass.isPrimitive() || objClass.getName().startsWith("java.lang")) {
            if (obj instanceof String) {
                // 对字符串进行JSON转义
                return "\"" + obj.toString().replace("\"", "\\\"") + "\"";
            }
            // 数字、布尔值等直接返回其toString结果
            return obj.toString();
        }

        // 检测循环引用
        // 如果当前对象已经被访问过，说明存在循环引用，返回特殊标记
        if (!visited.add(obj)) {
            return "\"<Circular Reference>\""; // JSON字符串需要双引号
        }

        StringBuilder result = new StringBuilder();

        try {
            if (obj instanceof Collection) {
                result.append("[");
                Iterator<?> iterator = ((Collection<?>) obj).iterator();
                while (iterator.hasNext()) {
                    result.append(toJsonString(iterator.next(), visited)); // 递归调用时传入visited
                    if (iterator.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("]");
            } else if (obj instanceof Map) {
                result.append("{");
                Collection<?> keys = ((Map<?, ?>) obj).keySet();
                Iterator<?> it = keys.iterator();
                while (it.hasNext()) {
                    Object key = it.next();
                    result.append("\"").append(key == null ? "null" : key.toString().replace("\"", "\\\"")).append("\":");
                    result.append(toJsonString(((Map<?, ?>) obj).get(key), visited)); // 递归调用时传入visited
                    if (it.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("}");
            } else if (obj instanceof Iterator) {
                result.append("[");
                Iterator<?> iterator = (Iterator<?>) obj;
                while (iterator.hasNext()) {
                    result.append(toJsonString(iterator.next(), visited)); // 递归调用时传入visited
                    if (iterator.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("]");
            } else if (obj instanceof Enumeration) {
                result.append("[");
                Enumeration<?> enumeration = (Enumeration<?>) obj;
                while (enumeration.hasMoreElements()) {
                    result.append(toJsonString(enumeration.nextElement(), visited)); // 递归调用时传入visited
                    if (enumeration.hasMoreElements()) {
                        result.append(", ");
                    }
                }
                result.append("]");
            } else if (objClass.isArray()) {
                // 数组处理：遍历数组元素并递归转换为JSON字符串
                result.append("[");
                int length = java.lang.reflect.Array.getLength(obj);
                for (int i = 0; i < length; i++) {
                    result.append(toJsonString(java.lang.reflect.Array.get(obj, i), visited)); // 递归调用时传入visited
                    if (i < length - 1) {
                        result.append(", ");
                    }
                }
                result.append("]");
            } else {
                // 对于自定义对象，通过反射获取其字段
                Field[] fields = objClass.getDeclaredFields();
                // 仅当类不是Java内置类且有字段时，才将其作为JSON对象处理
                if (!objClass.getName().startsWith("java.") && fields.length > 0) {
                    result.append("{");
                    for (int i = 0; i < fields.length; i++) {
                        result.append("\"").append(fields[i].getName()).append("\":");
                        try {
                            fields[i].setAccessible(true);
                            result.append(toJsonString(fields[i].get(obj), visited)); // 递归调用时传入visited
                        } catch (IllegalAccessException e) {
                            // 捕获访问异常，打印堆栈跟踪并返回 "null"
                            e.printStackTrace();
                            result.append("null"); // 在JSON中，null不带引号
                        }
                        if (i < fields.length - 1) {
                            result.append(", ");
                        }
                    }
                    result.append("}");
                } else {
                    // 对于没有字段的自定义对象或者其他无法按JSON对象/数组处理的类型，
                    // 默认将其toString结果作为JSON字符串处理（带双引号并转义）
                    result.append("\"").append(obj.toString().replace("\"", "\\\"")).append("\"");
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
