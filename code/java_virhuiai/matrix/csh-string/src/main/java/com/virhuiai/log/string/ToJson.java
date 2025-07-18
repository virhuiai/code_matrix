package com.virhuiai.log.string;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 将对象转换为 JSON 格式字符串的功能。
 */
public interface ToJson {



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
        return toJsonString(obj, new IdentityHashMap<Object, Boolean>());
    }


    /**
     * 将对象转换为 JSON 格式的字符串，处理循环引用。
     *
     * @param obj 要转换的对象。
     * @param visited 已访问的对象集合，用于检测循环引用。
     * @return JSON 格式的字符串。
     */
    default String toJsonString(Object obj, IdentityHashMap<Object, Boolean> visited) {
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
        if(visited.containsKey(obj)){
            return "\"<Circular Reference>\""; // JSON字符串需要双引号
        }else{
            visited.put(obj, true);
        }

        try {
            StringBuilder result = new StringBuilder();
            if (obj instanceof Map) {
                result.append("{");
                Map<?, ?> map = (Map<?, ?>) obj;
                boolean first = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (!first) {
                        result.append(",");
                    }
                    result.append(toJsonString(entry.getKey(), visited)).append(":");
                    result.append(toJsonString(entry.getValue(), visited));
                    first = false;
                }
                result.append("}");
            } else if (obj instanceof Collection) {
                result.append("[");
                Collection<?> collection = (Collection<?>) obj;
                boolean first = true;
                for (Object item : collection) {
                    if (!first) {
                        result.append(",");
                    }
                    result.append(toJsonString(item, visited));
                    first = false;
                }
                result.append("]");
            } else if (objClass.isArray()) {
                result.append("[");
                Object[] array = (Object[]) obj;
                boolean first = true;
                for (Object item : array) {
                    if (!first) {
                        result.append(",");
                    }
                    result.append(toJsonString(item, visited));
                    first = false;
                }
                result.append("]");
            } else {
                // 处理普通对象（字段）
                result.append("{");
                Field[] fields = objClass.getDeclaredFields();
                boolean firstField = true;
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        if (!firstField) {
                            result.append(",");
                        }
                        result.append("\"").append(field.getName()).append("\":");
                        result.append(toJsonString(value, visited));
                        firstField = false;
                    } catch (IllegalAccessException e) {
                        // 忽略无法访问的字段或处理异常
                        e.printStackTrace();
                    }
                }
                result.append("}");
            }
            return result.toString();
        } finally {
            visited.remove(obj); // 确保在方法返回前移除当前对象，以便在其他引用路径中可以再次访问
        }
    }
}
