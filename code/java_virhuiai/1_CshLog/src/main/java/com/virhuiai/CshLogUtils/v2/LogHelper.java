package com.virhuiai.CshLogUtils.v2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 日志助手接口 - 提供统一的日志获取方法
 * 封装了Apache Commons Logging的日志工厂，简化日志记录器的获取过程
 *
 * TODO: 考虑改为抽象类或工具类，因为接口中包含静态方法实现不是最佳实践
 * TODO: 可以考虑添加日志级别检查方法，如isDebugEnabled()等
 * TODO: 可以考虑添加异常处理，防止LogFactory初始化失败
 */
public interface LogHelper {

    /**
     * 根据类获取日志记录器
     * 使用类的完全限定名作为日志记录器的名称
     *
     * @param cls 需要记录日志的类，不能为null
     * @return Log 日志记录器实例
     *
     * TODO: 添加参数校验，检查cls是否为null
     */
    public static Log getLogger(Class<?> cls) {
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
     * TODO: 添加参数校验，检查name是否为null
     * TODO: 可以考虑对空字符串给出默认名称
     */
    public static Log getLogger(String name) {
        // 通过Apache Commons Logging工厂获取指定名称的日志记录器实例
        return LogFactory.getFactory().getInstance(name);
    }

    /**
     * 获取默认的日志记录器
     * 使用空字符串作为日志记录器名称，通常会使用根日志记录器
     *
     * @return Log 默认日志记录器实例
     *
     * TODO: 考虑使用更有意义的默认名称，如"DEFAULT"或"ROOT"
     */
    public static Log getLogger() {
        // 调用重载方法，传入空字符串获取默认日志记录器
        return getLogger("");
    }
}