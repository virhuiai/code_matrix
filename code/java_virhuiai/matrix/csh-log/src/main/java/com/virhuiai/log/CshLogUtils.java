package com.virhuiai.log;

import com.virhuiai.log.wrapper.DynamicProxyLogWrapper;
import com.virhuiai.log.wrapper.ExtendedLogWrapper;
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
     * 获取默认的日志对象并记录警告信息
     *
     * @param warningMessage 需要记录的警告信息
     * @return 未经代理包装的原始日志对象
     */
    private static Log getDefaultLogWithWarning(String warningMessage) {
        Log defaultLog = LogFactory.getLog(CshLogUtils.class);
        defaultLog.warn(warningMessage);
        return defaultLog;
    }

    /**
     * 获取默认的日志对象并记录警告信息
     * @param warningMessage
     * @param e
     * @return
     */
    private static Log getDefaultLogWithWarning(String warningMessage,Exception e) {
        Log defaultLog = LogFactory.getLog(CshLogUtils.class);
        defaultLog.warn(warningMessage, e);
        return defaultLog;
    }

    /**
     * 获取指定类的日志对象
     *
     * @param clazz 需要获取日志对象的类
     * @return 返回与指定类关联的 Log 对象
     * @throws NullPointerException 如果传入的类对象为null
     */
    public static Log createLogExtended(Class<?> clazz) {
        if (null == clazz) {
            throw new NullPointerException("传入的类对象不能为null");
        }
        Log log = LogFactory.getLog(clazz);
        return new ExtendedLogWrapper(log);
    }


    /**
     * 创建Log对象
     */
    private static Log createLogExtended(String name) {
        Log log = LogFactory.getLog(name);
        return new ExtendedLogWrapper(log);
    }

    /**
     * 获取调用者类的扩展日志对象
     * 通过分析调用栈自动获取调用者类，并返回对应的扩展Log对象
     *
     * 工作流程:
     * 1. 获取调用者类名
     * 2. 如果无法获取调用者，返回默认日志对象
     * 3. 尝试加载调用者类并创建扩展日志对象
     * 4. 如果类加载失败，返回默认日志对象
     *
     * @return 返回扩展Log对象
     */
    public static Log createLogExtended() {
        String rsCallerClassName = StackTraceUtil.getCallerClassName(MIN_STACK_DEPTH, CLASS_NAME);
        if(StackTraceUtil.UNKNOWN_CLASS.equals(rsCallerClassName)){
            // 返回未经代理包装的原始日志对象
            return getDefaultLogWithWarning("无法确定调用者");
        }


        try {
            // 尝试加载类并获取其日志对象
            Class<?> callerClass = Class.forName(rsCallerClassName);
            return createLogExtended(callerClass);
        } catch (ClassNotFoundException e) {
            // 返回未经代理包装的原始日志对象
            return getDefaultLogWithWarning(String.format("无法加载类: %s", rsCallerClassName), e);
        }
    }

    /**
     * 使用动态代理包装指定类的日志对象
     *
     * @param clazz 需要获取日志对象的类
     * @return 返回经过动态代理包装的Log对象
     */
    public static Log createLogDynamicProxy(Class<?> clazz) {
        // 获取指定类的原始日志对象
        Log log = LogFactory.getLog(clazz);
        // 使用动态代理对日志对象进行包装增强
        return DynamicProxyLogWrapper.wrap(log);
    }

    /**
     * 使用动态代理包装指定名称的日志对象
     *
     * @param name 日志对象的名称标识
     * @return 返回经过动态代理包装的Log对象
     * @throws IllegalArgumentException 如果name为null或空字符串
     */
    private static Log createLogDynamicProxy(String name) {
        // 复用createLogSimple方法获取原始日志对象
        Log log = createLogSimple(name);
        // 使用动态代理对日志对象进行包装增强
        return DynamicProxyLogWrapper.wrap(log);
    }


    /**
     * 获取使用动态代理实现的日志对象
     * 通过分析调用栈自动获取调用者类的日志对象
     * <p>
     * 工作流程:
     * 1. 分析调用栈获取调用者类名
     * 2. 如果无法获取调用者,返回当前工具类的日志对象
     * 3. 根据调用者类名创建并返回动态代理包装的日志对象
     * 4. 如果类加载失败,返回当前工具类的日志对象并记录警告信息
     *
     * @return 返回动态代理Log对象，如果无法确定调用者则返回当前工具类的日志对象
     */
    public static Log createLogDynamicProxy() {
        // 获取调用者的类名
        String rsCallerClassName = StackTraceUtil.getCallerClassName(MIN_STACK_DEPTH, CLASS_NAME);
        // 如果无法获取有效的调用者类名
        if (StackTraceUtil.UNKNOWN_CLASS.equals(rsCallerClassName)) {
            // 返回未经代理包装的原始日志对象
            return getDefaultLogWithWarning("无法确定调用者");
        }
//        System.out.println("className....:" + rsCallerClassName);
        try {
            // 尝试加载调用者类并创建其动态代理日志对象
            Class<?> callerClass = Class.forName(rsCallerClassName);
            return createLogDynamicProxy(callerClass);
        } catch (ClassNotFoundException e) {
            // 返回未经代理包装的原始日志对象
            return getDefaultLogWithWarning(String.format("无法加载类: %s", rsCallerClassName), e);
        }
    }


    /**
     * 创建简单的Log对象，不进行任何包装
     *
     * @param name 日志对象的名称标识
     * @return 原始的Log对象
     * @throws IllegalArgumentException 如果name为null或空字符串
     */
    private static Log createLogSimple(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("日志名称不能为null或空");
        }
        return LogFactory.getLog(name);
    }



}