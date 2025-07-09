package com.virhuiai.log.logdpp;

import com.virhuiai.log.LogFactoryI;
import com.virhuiai.log.StackTraceUtil;
import com.virhuiai.log.wrapper.DynamicProxyLogWrapper;
import org.apache.commons.logging.Log;

// LogFactory 类是一个日志工厂，用于获取 Log 实例。
public class LogFactory implements LogFactoryI {
    /**
     * 在方法开始处添加了栈深度检查，防止可能的数组越界异常。
     */
    private static final int MIN_STACK_DEPTH = 2;

    /**
     * 类名常量，用于性能优化
     */
    private static final String CLASS_NAME = LogFactory.class.getName();

    private LogFactory() {
        // 私有构造函数，防止实例化
        // 确保 LogFactory 只能通过其静态方法访问。
    }

    /**
     * 获取一个通用的 Log 实例，该实例通过动态代理包装，并自动识别调用者的类名。
     *
     * @return 经过动态代理包装的 Log 实例。
     */
    public static Log getLog(){
        // 该方法用于获取一个通用的 Log 实例。
        return createLogDynamicProxy();
        // 调用 createLogDynamicProxy 方法，该方法会自动解析调用栈并创建相应的动态代理 Log 实例。
    }

    /**
     * 根据指定的 Class 对象获取 Log 实例，该实例通过动态代理包装。
     *
     * @param clazz 需要为其创建 Log 实例的类。
     * @return 绑定到指定类的、经过动态代理包装的 Log 实例。
     */
    public static Log getLog(Class<?> clazz){
        // 参数 clazz：需要为其创建 Log 实例的类。
        // 返回值：一个绑定到指定类的 Log 实例。
        return createLogDynamicProxy(clazz);
        // 调用 createLogDynamicProxy 方法，并传入指定的 Class 对象，以创建相应的动态代理 Log 实例。
    }

    /**
     * 根据指定的类名字符串获取 Log 实例，该实例通过动态代理包装。
     *
     * @param clsNm 日志记录器的名称（通常是类名）。
     * @return 绑定到指定类名的、经过动态代理包装的 Log 实例。
     */
    public static Log getLog(String clsNm){
        // 参数 clsNm：需要为其创建 Log 实例的类名字符串。
        // 返回值：一个绑定到指定类名的 Log 实例。
        return createLogDynamicProxy(clsNm);
        // 调用 createLogDynamicProxy 方法，并传入指定的类名字符串，以创建相应的动态代理 Log 实例。
    }

    /////////////

    /**
     * 使用动态代理包装指定类的日志对象。
     *
     * @param clazz 需要获取日志对象的类。
     * @return 返回经过动态代理包装的Log对象。
     */
    public static Log createLogDynamicProxy(Class<?> clazz) {
        // 获取指定类的原始日志对象
        Log log = org.apache.commons.logging.LogFactory.getLog(clazz);
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
        Log log = org.apache.commons.logging.LogFactory.getLog(name);
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
            return com.virhuiai.log.logspl.LogFactory.getDefaultLogWithWarning("无法确定调用者");
        }
//        System.out.println("className....:" + rsCallerClassName);
        try {
            // 尝试加载调用者类并创建其动态代理日志对象
            Class<?> callerClass = Class.forName(rsCallerClassName);
            return createLogDynamicProxy(callerClass);
        } catch (ClassNotFoundException e) {
            // 返回未经代理包装的原始日志对象
            return com.virhuiai.log.logspl.LogFactory.getDefaultLogWithWarning(String.format("无法加载类: %s", rsCallerClassName), e);
        }
    }

}