package com.virhuiai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 日志工具类
 * 提供了获取日志对象的静态方法
 * 此类为final类，不可被继承
 * 所有方法都是静态的，不需要实例化
 */
public final class CshLogUtils {

    /**
     * 类名常量，用于性能优化
     */
    private static final String CLASS_NAME = CshLogUtils.class.getName();

    /**
     * 未知调用者的日志标识符
     */
    private static final String UNKNOWN_CALLER = "未知的调用者";

    /**
     * 在方法开始处添加了栈深度检查，防止可能的数组越界异常。
     */
    private static final int MIN_STACK_DEPTH = 2;

    /**
     * 私有构造函数，防止类被实例化
     * 工具类应该是静态的，不需要实例化
     */
    private CshLogUtils() {
        throw new AssertionError("CshLogUtils 工具类不应被实例化");
    }

    /**
     * 获取指定类的日志对象
     *
     * @param clazz 需要获取日志对象的类
     * @param useWrapper 是否使用LogWrapper
     * @return 返回与指定类关联的 Log 对象
     * @throws NullPointerException 如果传入的类对象为null
     */
    public static Log getLog(Class<?> clazz, boolean useWrapper) {
        if (clazz == null) {
            throw new NullPointerException("传入的类对象不能为null");
        }
        Log log = LogFactory.getLog(clazz);
        return useWrapper ? new LogWrapper(log) : log;
    }

    /**
     * 获取指定类的日志对象（不使用LogWrapper）
     *
     * @param clazz 需要获取日志对象的类
     * @return 返回与指定类关联的 Log 对象
     * @throws NullPointerException 如果传入的类对象为null
     */
    public static Log getLog(Class<?> clazz) {
        return getLog(clazz, false);
    }

    /**
     * 获取调用者类的日志对象
     *
     * @param useWrapper 是否使用LogWrapper
     * @return 返回与调用者类关联的 Log 对象
     */
    public static Log getLog(boolean useWrapper) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 检查栈深度是否足够
        if (stackTrace.length <= MIN_STACK_DEPTH) {
            LogFactory.getLog(CshLogUtils.class).warn("调用栈深度不足，无法确定调用者");
            return createLog(UNKNOWN_CALLER, useWrapper);
        }

        // 从索引2开始遍历调用栈
        // 索引0通常是getStackTrace方法本身
        // 索引1是当前方法（getLog）
        // 索引2通常是调用此方法的类，但为了更加健壮，我们从这里开始遍历
        for (int i = MIN_STACK_DEPTH; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            // String className = stackTrace[i].getClassName(); // 定义log的位置
            System.out.println("className....:" + className);

            // 如果找到的类名不是当前工具类的名称，则认为找到了调用者
            if (!CLASS_NAME.equals(className)) {
                try {
                    // 尝试加载类并获取其日志对象
                    Class<?> callerClass = Class.forName(className);
                    return createLog(callerClass, useWrapper);
                } catch (ClassNotFoundException e) {
                    // 如果找不到类，记录异常并继续查找下一个调用者
                    // 这种情况极少发生，但为了健壮性我们需要处理它
                    LogFactory.getLog(CshLogUtils.class).warn("无法加载类: " + className, e);
                }
            }
        }

        // 如果无法确定调用者，则返回一个通用的日志对象
        // 这种情况在正常使用中几乎不会发生，但为了代码的完整性和健壮性，我们仍然处理它
        LogFactory.getLog(CshLogUtils.class).warn("无法确定调用者，使用默认日志对象");
        return createLog(UNKNOWN_CALLER, useWrapper);
    }

    public static Log getLog2(boolean useWrapper) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 检查栈深度是否足够
        if (stackTrace.length <= MIN_STACK_DEPTH) {
            LogFactory.getLog(CshLogUtils.class).warn("调用栈深度不足，无法确定调用者");
            return createLog2(UNKNOWN_CALLER, useWrapper);
        }

        // 从索引2开始遍历调用栈
        // 索引0通常是getStackTrace方法本身
        // 索引1是当前方法（getLog）
        // 索引2通常是调用此方法的类，但为了更加健壮，我们从这里开始遍历
        for (int i = MIN_STACK_DEPTH; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            // String className = stackTrace[i].getClassName(); // 定义log的位置
            System.out.println("className....:" + className);

            // 如果找到的类名不是当前工具类的名称，则认为找到了调用者
            if (!CLASS_NAME.equals(className)) {
                try {
                    // 尝试加载类并获取其日志对象
                    Class<?> callerClass = Class.forName(className);
                    return createLog2(callerClass, useWrapper);
                } catch (ClassNotFoundException e) {
                    // 如果找不到类，记录异常并继续查找下一个调用者
                    // 这种情况极少发生，但为了健壮性我们需要处理它
                    LogFactory.getLog(CshLogUtils.class).warn("无法加载类: " + className, e);
                }
            }
        }

        // 如果无法确定调用者，则返回一个通用的日志对象
        // 这种情况在正常使用中几乎不会发生，但为了代码的完整性和健壮性，我们仍然处理它
        LogFactory.getLog(CshLogUtils.class).warn("无法确定调用者，使用默认日志对象");
        return createLog2(UNKNOWN_CALLER, useWrapper);
    }

    /**
     * 获取调用者类的日志对象（不使用LogWrapper）
     *
     * @return 返回与调用者类关联的 Log 对象
     */
    public static Log getLog() {
        return getLog(false);
    }

    public static Log getLog2() {
        return getLog2(false);
    }

    /**
     * 创建Log对象
     */
    private static Log createLog(Class<?> clazz, boolean useWrapper) {
        Log log = LogFactory.getLog(clazz);
        return useWrapper ? new LogWrapper(log) : log;
    }

    private static Log createLog2(Class<?> clazz, boolean useWrapper) {
        Log log = LogFactory.getLog(clazz);
        return useWrapper ? LogWrapper2.wrap(log) : log;
    }

    /**
     * 创建Log对象
     */
    private static Log createLog(String name, boolean useWrapper) {
        Log log = LogFactory.getLog(name);
        return useWrapper ? new LogWrapper(log) : log;
    }

    private static Log createLog2(String name, boolean useWrapper) {
        Log log = LogFactory.getLog(name);
        return useWrapper ? LogWrapper2.wrap(log) : log;
    }

    // testLog 方法保持不变
    public static void testLog() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 检查栈深度是否足够
        if (stackTrace.length < 3) {
            System.out.println("调用栈深度不足，无法获取类名");
        } else {
            // 获取类名
            String classNameFull0 = stackTrace[0].getClassName();
            System.out.println("当前方法的类名： " + classNameFull0);
            String classNameFull1 = stackTrace[1].getClassName();
            System.out.println("调用当前方法的类名： " + classNameFull1);
            String classNameFull2 = stackTrace[2].getClassName();
            System.out.println("调用当前方法的父类名： " + classNameFull2);
        }
    }

//    public static void testLog2() {
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//
//        // 检查栈深度是否足够
//        if (stackTrace.length < 3) {
//            System.out.println("调用栈深度不足，无法获取类名");
//        } else {
//            // 获取类名
//            String classNameFull0 = stackTrace[0].getClassName();
//            System.out.println("当前方法的类名： " + classNameFull0);
//            String classNameFull1 = stackTrace[1].getClassName();
//            System.out.println("调用当前方法的类名： " + classNameFull1);
//            String classNameFull2 = stackTrace[2].getClassName();
//            System.out.println("调用当前方法的父类名： " + classNameFull2);
//        }
//    }
}