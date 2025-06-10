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


}
