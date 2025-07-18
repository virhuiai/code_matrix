package com.virhuiai.log.log;

/**
 * 调用栈信息工具类
 */
// 这是一个工具类，用于处理和获取调用栈信息。
// 标记为final，表示该类不能被继承。
public final class StackTraceUtil {
    public static final String UNKNOWN_CLASS = "UnknownClass";
    // 定义一个公共的静态常量，表示未知类名。

    private StackTraceUtil() {
        // 私有构造函数，防止实例化
    }
    // 将构造函数设为私有，确保该工具类不能被实例化，只能通过静态方法访问。

    /**
     * StackTraceUtil.getTargetStackTrace
     * 获取指定深度的调用栈帧信息
     *
     * @param targetIndex 目标栈帧索引
     * @return 目标位置的栈帧信息，如果索引无效则返回null
     */
    // 该方法用于获取指定深度的调用栈帧信息。
    public static StackTraceElement getTargetStackTrace(int targetIndex) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 获取当前线程的完整调用栈信息，返回一个StackTraceElement数组。

        if (stackTrace.length <= targetIndex) {
            // 如果目标索引超出栈帧数组的长度。
            return null;
            // 返回null，表示无法获取到指定位置的栈帧信息。
        }

        return stackTrace[targetIndex];
        // 返回指定索引位置的StackTraceElement对象，代表该位置的调用栈帧。
    }

    /**
     // 检查目标字符串是否在提供的字符串列表中。
     // 这个方法会遍历 searchList，看 targetString 是否存在其中（忽略大小写）。
     * @param targetString
     * @param searchList
     * @return
     */
    public static boolean isStringInList(String targetString, String... searchList) {
        // 如果 searchList 为 null 或为空，则目标字符串不可能在其中。
        if (searchList == null || searchList.length == 0) {
            return false;
        }

        // 遍历所有在 searchList 中需要检查的字符串。
        for (String listItem : searchList) {
            // 比较列表中的当前项与目标字符串，忽略大小写。
            if (listItem.equalsIgnoreCase(targetString)) {
                // 如果找到匹配项，立即返回 true。
                return true;
            }
        }
        // 如果遍历完整个列表都没有找到匹配项，则返回 false。
        return false;
    }

    /**
     * StackTraceUtil.getCallerClassName
     * 获取调用者的类名
     * 从指定深度开始向上遍历调用栈，找到第一个非指定类名的调用者
     *
     * @param startIndex 开始查找的调用栈深度
     * @param excludeClassNames 需要排除的类名
     * @return 调用者的类名，如果未找到则返回默认值
     */
    // 该方法用于获取调用方的类名，会从指定深度开始向上遍历调用栈，并排除指定的类名。
    public static String getCallerClassName(int startIndex, String... excludeClassNames) {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    // 获取当前线程的完整调用栈信息。

        if (stackTrace.length <= startIndex) {
            // 如果起始索引超出栈帧数组的长度。
            return UNKNOWN_CLASS;
            // 返回默认的未知类名。
        }

        for (int i = startIndex; i < stackTrace.length; i++) {
            // 从起始索引开始遍历调用栈。
            String className = stackTrace[i].getClassName();
            // 获取当前栈帧的类名。
            if (!isStringInList(className, excludeClassNames)) {
                // 如果当前类名与需要排除的类名不相等。
                return className;
                // 返回当前类名，即为调用者的类名。
            }
        }

        return UNKNOWN_CLASS;
        // 如果遍历完所有栈帧都没有找到符合条件的调用者，则返回默认的未知类名。
    }
}