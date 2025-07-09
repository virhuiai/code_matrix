package com.virhuiai.log;

/**
 * 调用栈信息工具类
 */
public final class StackTraceUtil {
    public static final String UNKNOWN_CLASS = "UnknownClass";

    private StackTraceUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * StackTraceUtil.getTargetStackTrace
     * 获取指定深度的调用栈帧信息
     *
     * @param targetIndex 目标栈帧索引
     * @return 目标位置的栈帧信息，如果索引无效则返回null
     */
    public static StackTraceElement getTargetStackTrace(int targetIndex) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length <= targetIndex) {
            return null;
        }

        return stackTrace[targetIndex];
    }

    /**
     * StackTraceUtil.getCallerClassName
     * 获取调用者的类名
     * 从指定深度开始向上遍历调用栈，找到第一个非指定类名的调用者
     *
     * @param startIndex 开始查找的调用栈深度
     * @param excludeClassName 需要排除的类名
     * @return 调用者的类名，如果未找到则返回默认值
     */
    public static String getCallerClassName(int startIndex, String excludeClassName) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length <= startIndex) {
            return UNKNOWN_CLASS;
        }

        for (int i = startIndex; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!excludeClassName.equals(className)) {
                return className;
            }
        }

        return UNKNOWN_CLASS;
    }
}