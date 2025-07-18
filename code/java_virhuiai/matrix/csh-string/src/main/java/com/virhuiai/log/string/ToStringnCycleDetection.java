package com.virhuiai.log.string;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ：处理带循环引用检测的对象到字符串转换。
 */
public interface ToStringnCycleDetection {

    //    处理循环引用


    /**
     * 将数组转换为字符串，处理循环引用
     */
    default String toStringArrayWithCycleDetection(Object array, IdentityHashMap<Object, Boolean> visited) {

        if (array == null) {
            return "null";
        }
        if (!array.getClass().isArray()) {
            return toStringWithCycleDetection(array, visited);
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
     * <p>
     * 此方法递归地将一个对象及其成员转换为字符串表示。
     * 为了防止无限循环，它使用一个Set来跟踪已经访问过的对象，
     * 当检测到循环引用时，会返回 "[Cyclic Reference]"。
     *
     * @param obj     要转换的对象。
     * @param visited 已访问的对象集合，用于检测循环引用。
     *                首次调用时，建议传入一个新的HashSet或null。
     * @return 转换后的字符串表示。
     */
    default String toStringWithCycleDetection(Object obj, IdentityHashMap<Object, Boolean> visited) {
        // 如果visited集合为null，则创建一个新的HashSet
        if (visited == null) {
            visited = new IdentityHashMap<Object, Boolean>();
        }

        // 如果对象为null，直接返回 "null" 字符串
        if (obj == null) {
            return "null";
        }

        // 对于基本类型或java.lang包下的对象，直接调用其toString方法
        Class<?> objClass = obj.getClass();
        if (objClass.isPrimitive() || objClass.getName().startsWith("java.lang")) {
            return obj.toString();
        }

        // 检查循环引用，并在必要时添加到visited集合
        // 对于自定义对象，先检查循环引用
        if(visited.containsKey(obj)){
            return "[Cyclic Reference]";
        }else{
            visited.put(obj, true);
        }

        if (obj instanceof Map) {
            return processMapWithCycleDetection((Map<?, ?>) obj, objClass, visited);
        } else if (obj instanceof Collection) {
            return processCollectionWithCycleDetection((Collection<?>) obj, objClass, visited);
        } else if (obj instanceof Iterator) {
            return processIteratorWithCycleDetection((Iterator<?>) obj, objClass, visited);
        } else if (obj instanceof Enumeration) {
            return processEnumerationWithCycleDetection((Enumeration<?>) obj, objClass, visited);
        } else if (objClass.isArray()) {
            return toStringArrayWithCycleDetection(obj, visited);
        } else {

            try {
                StringBuilder result = new StringBuilder();
                Field[] fields = objClass.getDeclaredFields();
                if (!objClass.getName().startsWith("java") && fields.length > 0) {
                    result.append(objClass.getName()).append("{");
                    boolean firstField = true;
                    for (Field field : fields) {
                        if (!firstField) {
                            result.append("; ");
                        }
                        result.append(field.getName()).append("=");
                        try {
                            field.setAccessible(true);
                            result.append(toStringWithCycleDetection(field.get(obj), visited));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            result.append("null");
                        }
                        firstField = false;
                    }
                    result.append("}");
                } else {
                    result.append(obj.toString());
                }
                return result.toString();
            } finally {
                visited.remove(obj);
            }
        }
    }



    /**
     * 将 Collection 转换为字符串，处理循环引用
     * @param collection 要转换的集合
     * @param objClass 集合的类
     * @param visited 已访问的对象集合
     * @return 转换后的字符串
     */
    default String processCollectionWithCycleDetection(Collection<?> collection, Class<?> objClass, IdentityHashMap<Object, Boolean> visited) {

        try {
            StringBuilder result = new StringBuilder();
            result.append(objClass.getName()).append("{");
            Iterator<?> iterator = collection.iterator();
            boolean first = true;
            while (iterator.hasNext()) {
                if (!first) {
                    result.append("; ");
                }
                result.append(toStringWithCycleDetection(iterator.next(), visited));
                first = false;
            }
            result.append("}");
            return result.toString();
        }catch (Exception e){
            return "[Process Error]";
        }
    }

    /**
     * 将 Iterator 转换为字符串，处理循环引用
     */
    default String processIteratorWithCycleDetection(Iterator<?> iterator, Class<?> objClass, IdentityHashMap<Object, Boolean> visited) {
        // As discussed, Iterator itself is generally not the object causing the cycle,
        // but its elements. The parent collection/map that provided this iterator
        // would have been added to 'visited'. So we just iterate and process elements.
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        boolean first = true;
        while (iterator.hasNext()) {
            if (!first) {
                result.append("; ");
            }
            result.append(toStringWithCycleDetection(iterator.next(), visited));
            first = false;
        }
        result.append("}");
        return result.toString();
    }

    /**
     * 将 Enumeration 转换为字符串，处理循环引用
     */
    default String processEnumerationWithCycleDetection(Enumeration<?> enumeration, Class<?> objClass, IdentityHashMap<Object, Boolean> visited) {
        // Similar to Iterator, Enumeration itself isn't usually the cyclic object.
        StringBuilder result = new StringBuilder();
        result.append(objClass.getName()).append("{");
        boolean first = true;
        while (enumeration.hasMoreElements()) {
            if (!first) {
                result.append("; ");
            }
            result.append(toStringWithCycleDetection(enumeration.nextElement(), visited));
            first = false;
        }
        result.append("}");
        return result.toString();
    }



    /**
     * 将 Map 转换为字符串，处理循环引用
     * @param map 要转换的Map
     * @param objClass Map的类
     * @param visited 已访问的对象集合
     * @return 转换后的字符串
     */
    default String processMapWithCycleDetection(Map<?, ?> map, Class<?> objClass, IdentityHashMap<Object, Boolean> visited) {

        try {
            StringBuilder result = new StringBuilder();
            result.append(objClass.getName()).append("{");
            Iterator<?> it = map.keySet().iterator();
            boolean first = true;
            while (it.hasNext()) {
                if (!first) {
                    result.append("; ");
                }
                Object key = it.next();
                result.append(key == null ? "null" : key).append("=");
                result.append(toStringWithCycleDetection(map.get(key), visited));
                first = false;
            }
            result.append("}");
            return result.toString();
        }catch (Exception e){
            return "[Process Error]";
        }

    }
}
