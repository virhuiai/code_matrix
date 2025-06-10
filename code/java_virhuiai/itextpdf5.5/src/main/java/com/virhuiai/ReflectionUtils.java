package com.virhuiai;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类
 * 提供对象字段访问、修改等反射相关操作
 */
public class ReflectionUtils {

    private static final Log logger = CshLogUtils.createLogExtended(ReflectionUtils.class);

    /**
     * 获取对象中的字段值
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @param <T> 返回值类型
     * @return 字段值
     * @throws CommonRuntimeException 获取失败时抛出异常
     */
    public static <T> T fetchObjResult(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        try {
            Class<?> clazz = obj.getClass();
            Field privateField = clazz.getDeclaredField(fieldName);
            privateField.setAccessible(true);
            @SuppressWarnings("unchecked")
            T value = (T) privateField.get(obj);
            return value;
        } catch (NoSuchFieldException e) {
            logger.warn("字段不存在: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_NOT_FOUND", "字段[" + fieldName + "]不存在");
        } catch (IllegalAccessException e) {
            logger.error("无法访问字段: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_ACCESS_ERROR", "无法访问字段[" + fieldName + "]");
        } catch (Exception e) {
            logger.error("获取字段值失败: " + fieldName, e);
            throw new CommonRuntimeException("FETCH_FIELD_ERROR", "获取字段[" + fieldName + "]值失败");
        }
    }


    /**
     * 从父类中获取字段值
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @param <T> 返回值类型
     * @return 字段值
     * @throws CommonRuntimeException 获取失败时抛出异常
     */
    public static <T> T fetchObjResultFromSuperClass(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        try {
            Class<?> superClass = obj.getClass().getSuperclass();
            if (superClass == null || superClass == Object.class) {
                throw new CommonRuntimeException("NO_SUPER_CLASS", "没有有效的父类");
            }

            Field privateField = superClass.getDeclaredField(fieldName);
            privateField.setAccessible(true);
            @SuppressWarnings("unchecked")
            T value = (T) privateField.get(obj);
            return value;
        } catch (NoSuchFieldException e) {
            logger.warn("父类中字段不存在: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_NOT_FOUND", "父类中字段[" + fieldName + "]不存在");
        } catch (IllegalAccessException e) {
            logger.error("无法访问父类字段: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_ACCESS_ERROR", "无法访问父类字段[" + fieldName + "]");
        } catch (Exception e) {
            logger.error("获取父类字段值失败: " + fieldName, e);
            throw new CommonRuntimeException("FETCH_FIELD_ERROR", "获取父类字段[" + fieldName + "]值失败");
        }
    }

    /**
     * 递归查找字段值（从当前类开始，逐级向上查找父类）
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @param <T> 返回值类型
     * @return 字段值
     * @throws CommonRuntimeException 获取失败时抛出异常
     */
    public static <T> T fetchObjResultRecursive(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                T value = (T) field.get(obj);
                return value;
            } catch (NoSuchFieldException e) {
                // 当前类中没有找到，继续查找父类
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                logger.error("无法访问字段: " + fieldName, e);
                throw new CommonRuntimeException("FIELD_ACCESS_ERROR", "无法访问字段[" + fieldName + "]");
            }
        }

        logger.warn("在类继承链中未找到字段: " + fieldName);
        throw new CommonRuntimeException("FIELD_NOT_FOUND", "在类继承链中未找到字段[" + fieldName + "]");
    }


    /**
     * 设置对象字段值
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @param value 要设置的值
     * @throws CommonRuntimeException 设置失败时抛出异常
     */
    public static void setObjField(Object obj, String fieldName, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
            logger.debug("成功设置字段[{}]的值" + fieldName);
        } catch (NoSuchFieldException e) {
            logger.warn("字段不存在: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_NOT_FOUND", "字段[" + fieldName + "]不存在");
        } catch (IllegalAccessException e) {
            logger.error("无法设置字段: " + fieldName, e);
            throw new CommonRuntimeException("FIELD_ACCESS_ERROR", "无法设置字段[" + fieldName + "]");
        } catch (Exception e) {
            logger.error("设置字段值失败: " + fieldName, e);
            throw new CommonRuntimeException("SET_FIELD_ERROR", "设置字段[" + fieldName + "]值失败");
        }
    }


    /**
     * 递归设置字段值（从当前类开始，逐级向上查找父类）
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @param value 要设置的值
     * @throws CommonRuntimeException 设置失败时抛出异常
     */
    public static void setObjFieldRecursive(Object obj, String fieldName, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, value);
                logger.debug("成功设置字段[{}]的值" + fieldName);
                return;
            } catch (NoSuchFieldException e) {
                // 当前类中没有找到，继续查找父类
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                logger.error("无法设置字段: " + fieldName, e);
                throw new CommonRuntimeException("FIELD_ACCESS_ERROR", "无法设置字段[" + fieldName + "]");
            }
        }

        logger.warn("在类继承链中未找到字段: " + fieldName);
        throw new CommonRuntimeException("FIELD_NOT_FOUND", "在类继承链中未找到字段[" + fieldName + "]");
    }

    /**
     * 获取类的所有字段（包括父类）
     * @param clazz 目标类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    /**
     * 调用对象的方法
     * @param obj 目标对象
     * @param methodName 方法名
     * @param paramTypes 参数类型数组
     * @param params 参数值数组
     * @param <T> 返回值类型
     * @return 方法返回值
     * @throws CommonRuntimeException 调用失败时抛出异常
     */
    public static <T> T invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] params) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("方法名不能为空");
        }

        try {
            Class<?> clazz = obj.getClass();
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(obj, params);
            logger.debug("成功调用方法[{}]" +  methodName);
            return result;
        } catch (NoSuchMethodException e) {
            logger.warn("方法不存在: " + methodName, e);
            throw new CommonRuntimeException("METHOD_NOT_FOUND", "方法[" + methodName + "]不存在");
        } catch (Exception e) {
            logger.error("调用方法失败: " + methodName, e);
            throw new CommonRuntimeException("INVOKE_METHOD_ERROR", "调用方法[" + methodName + "]失败");
        }
    }

    /**
     * 递归调用方法（从当前类开始，逐级向上查找父类）
     * @param obj 目标对象
     * @param methodName 方法名
     * @param paramTypes 参数类型数组
     * @param params 参数值数组
     * @param <T> 返回值类型
     * @return 方法返回值
     * @throws CommonRuntimeException 调用失败时抛出异常
     */
    public static <T> T invokeMethodRecursive(Object obj, String methodName, Class<?>[] paramTypes, Object[] params) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("方法名不能为空");
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, paramTypes);
                method.setAccessible(true);
                @SuppressWarnings("unchecked")
                T result = (T) method.invoke(obj, params);
                logger.debug("成功调用方法[{}]" +  methodName);
                return result;
            } catch (NoSuchMethodException e) {
                // 当前类中没有找到，继续查找父类
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                logger.error("调用方法失败: " + methodName, e);
                throw new CommonRuntimeException("INVOKE_METHOD_ERROR", "调用方法[" + methodName + "]失败");
            }
        }

        logger.warn("在类继承链中未找到方法: " + methodName);
        throw new CommonRuntimeException("METHOD_NOT_FOUND", "在类继承链中未找到方法[" + methodName + "]");
    }


    /**
     * 检查对象是否包含指定字段
     * @param obj 目标对象
     * @param fieldName 字段名
     * @return 是否包含该字段
     */
    public static boolean hasField(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.trim().isEmpty()) {
            return false;
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                clazz.getDeclaredField(fieldName);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return false;
    }

    /**
     * 获取字段的类型
     * @param obj 目标对象
     * @param fieldName 字段名
     * @return 字段类型
     * @throws CommonRuntimeException 获取失败时抛出异常
     */
    public static Class<?> getFieldType(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException("目标对象不能为空");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                return field.getType();
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        logger.warn("在类继承链中未找到字段: " + fieldName);
        throw new CommonRuntimeException("FIELD_NOT_FOUND", "在类继承链中未找到字段[" + fieldName + "]");
    }

    /**
     * 复制对象字段值到另一个对象
     * @param source 源对象
     * @param target 目标对象
     * @param fieldName 字段名
     * @throws CommonRuntimeException 复制失败时抛出异常
     */
    public static void copyField(Object source, Object target, String fieldName) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("源对象和目标对象不能为空");
        }

        try {
            Object value = fetchObjResultRecursive(source, fieldName);
            setObjFieldRecursive(target, fieldName, value);
            logger.debug("成功复制字段[{}]的值" + fieldName);
        } catch (Exception e) {
            logger.error("复制字段失败: " + fieldName, e);
            throw new CommonRuntimeException("COPY_FIELD_ERROR", "复制字段[" + fieldName + "]失败", e);
        }
    }


    /**
     * 复制多个字段值
     * @param source 源对象
     * @param target 目标对象
     * @param fieldNames 字段名数组
     * @throws CommonRuntimeException 复制失败时抛出异常
     */
    public static void copyFields(Object source, Object target, String... fieldNames) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("源对象和目标对象不能为空");
        }
        if (fieldNames == null || fieldNames.length == 0) {
            throw new IllegalArgumentException("字段名数组不能为空");
        }

        for (String fieldName : fieldNames) {
            copyField(source, target, fieldName);
        }
    }





}
