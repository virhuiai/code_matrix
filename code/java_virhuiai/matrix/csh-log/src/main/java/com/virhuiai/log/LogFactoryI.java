package com.virhuiai.log;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 日志助手接口 - 提供统一的日志获取方法
 * 封装了Apache Commons Logging的日志工厂，简化日志记录器的获取过程
 *
 */
public interface LogFactoryI {

    /**
     * 根据类获取日志记录器
     * 使用类的完全限定名作为日志记录器的名称
     *
     * @param cls 需要记录日志的类，不能为null
     * @return Log 日志记录器实例
     *
     */
    default Log getLogger(Class<?> cls) {
        // 通过Apache Commons Logging工厂获取指定类的日志记录器实例
        return LogFactory.getFactory().getInstance(cls);
    }

    /**
     * 根据名称获取日志记录器
     * 允许使用自定义的名称来标识日志记录器
     *
     * @param name 日志记录器的名称，可以为空字符串但不建议为null
     * @return Log 日志记录器实例
     *
     */
    default  Log getLogger(String name) {
        // 通过Apache Commons Logging工厂获取指定名称的日志记录器实例
        return LogFactory.getFactory().getInstance(name);
    }

    /**
     * 获取默认的日志记录器
     * 使用空字符串作为日志记录器名称，通常会使用根日志记录器
     *
     * @return Log 默认日志记录器实例
     *
     */
    default  Log getLogger() {
        // 调用重载方法，传入空字符串获取默认日志记录器
        return getLogger("");
    }

    /////

    /**
     * 获取默认的日志对象并记录警告信息
     *
     * @param warningMessage 需要记录的警告信息
     * @return 未经代理包装的原始日志对象
     */
    static Log getDefaultLogWithWarning(String warningMessage) {
        Log defaultLog = LogFactory.getLog(LogFactoryI.class);
        defaultLog.warn(warningMessage);
        return defaultLog;
    }

    /**
     * 获取默认的日志对象并记录警告信息
     * @param warningMessage
     * @param e
     * @return
     */
    static Log getDefaultLogWithWarning(String warningMessage, Exception e) {
        Log defaultLog = LogFactory.getLog(LogFactoryI.class);
        defaultLog.warn(warningMessage, e);
        return defaultLog;
    }



    /**
     * 创建简单的Log对象，不进行任何包装
     *
     * @param name 日志对象的名称标识
     * @return 原始的Log对象
     * @throws IllegalArgumentException 如果name为null或空字符串
     */
    static Log createLogSimple(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("日志名称不能为null或空");
        }
        return org.apache.commons.logging.LogFactory.getLog(name);
    }
}