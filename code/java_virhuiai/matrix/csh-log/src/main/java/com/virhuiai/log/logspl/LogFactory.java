package com.virhuiai.log.logspl;

import com.virhuiai.log.LogFactoryI;
import com.virhuiai.log.StackTraceUtil;
import com.virhuiai.log.wrapper.ExtendedLogWrapper;
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
    }

    public static Log getLog(){
        // 该方法用于获取一个通用的 Log 实例。
        return createLogSimple();
        // 调用 CshLogUtils 工具类的 createLogExtended 方法创建并返回一个扩展的 Log 实例。
    }

    //LogFactory.getLog(xxxx.class);
    // 这是一个重载方法，用于根据指定的类获取 Log 实例。
    public static Log getLog(Class<?> clazz){
        // 参数 clazz：需要为其创建 Log 实例的类。
        // 返回值：一个绑定到指定类的 Log 实例。
        return createLogSimple(clazz);
        // 调用 CshLogUtils 工具类的 createLogExtended 方法，并传入指定的 Class 对象，
        // 以创建并返回一个与该类相关的扩展 Log 实例。
    }

    public static Log getLog(String clsNm){
        // 参数 clazz：需要为其创建 Log 实例的类。
        // 返回值：一个绑定到指定类的 Log 实例。
        return createLogSimple(clsNm);
        // 调用 CshLogUtils 工具类的 createLogExtended 方法，并传入指定的 Class 对象，
        // 以创建并返回一个与该类相关的扩展 Log 实例。
    }

    /////////////


    /**
     * 获取指定类的日志对象
     *
     * @param clazz 需要获取日志对象的类
     * @return 返回与指定类关联的 Log 对象
     * @throws NullPointerException 如果传入的类对象为null
     */
    private static Log createLogSimple(Class<?> clazz) {
        if (null == clazz) {
            throw new NullPointerException("传入的类对象不能为null");
        }
        Log log = org.apache.commons.logging.LogFactory.getLog(clazz);
        return new ExtendedLogWrapper(log);
    }


    /**
     * 创建Log对象
     */
    private static Log createLogSimple(String name) {
        Log log = org.apache.commons.logging.LogFactory.getLog(name);
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
    private static Log createLogSimple() {
        String rsCallerClassName = StackTraceUtil.getCallerClassName(MIN_STACK_DEPTH
                , CLASS_NAME, LogFactory.class.getName());
        if(StackTraceUtil.UNKNOWN_CLASS.equals(rsCallerClassName)){
            // 返回未经代理包装的原始日志对象
            return LogFactory.getDefaultLogWithWarning("无法确定调用者");
        }

        return org.apache.commons.logging.LogFactory.getLog(rsCallerClassName);
    }

    ////////
/////

    /**
     * 获取默认的日志对象并记录警告信息
     *
     * @param warningMessage 需要记录的警告信息
     * @return 未经代理包装的原始日志对象
     */
    public static Log getDefaultLogWithWarning(String warningMessage) {
        Log defaultLog = getLog(LogFactoryI.class);
        defaultLog.warn(warningMessage);
        return defaultLog;
    }

    /**
     * 获取默认的日志对象并记录警告信息
     * @param warningMessage
     * @param e
     * @return
     */
    public  static Log getDefaultLogWithWarning(String warningMessage, Exception e) {
        Log defaultLog = getLog(LogFactoryI.class);
        defaultLog.warn(warningMessage, e);
        return defaultLog;
    }


}