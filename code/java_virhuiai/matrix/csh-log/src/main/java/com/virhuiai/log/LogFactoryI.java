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
}