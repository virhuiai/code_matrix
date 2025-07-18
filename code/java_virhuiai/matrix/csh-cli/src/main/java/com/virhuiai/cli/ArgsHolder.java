package com.virhuiai.cli;
import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Arrays;

/**
 * 使用枚举实现的单例模式，用于管理命令行参数。
 * 枚举单例是 Java 中实现单例的最佳实践，因为它天生线程安全，且简单易用。
 * 枚举单例在类加载时即创建实例，确保了全局唯一性和线程安全性。
 */
public enum ArgsHolder {
    INSTANCE;

    /**
     * 日志对象，使用静态 final 字段确保每个实例共享同一个日志对象。
     * 这是一种常见的 Java 日志配置方式。
     */
    private static final Log LOGGER = LogFactory.getLog();

    /**
     * 存储命令行参数的数组，声明为 volatile 以确保在多线程环境下可见。
     * 尽管 initializeArgs 方法是 synchronized 的，但 volatile 仍有助于清晰性。
     */
    private volatile String[] args = null;

    /**
     * 初始化命令行参数的方法。
     * 该方法被声明为 synchronized，以确保在多线程环境下，只有单个线程能执行初始化逻辑，避免竞态条件。
     *
     * @param commandLineArgs 命令行参数数组
     */
    public synchronized void initArgs(String[] commandLineArgs) {
        // 检查是否已初始化，如果已初始化，则记录警告并返回
        if (args != null) {
            // 检查日志级别是否启用警告日志，以避免不必要的字符串构造，提高性能
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("命令行参数已初始化，禁止重复调用，当前参数: " + Arrays.toString(args));
            }
            return;
        }
        // 检查命令行参数是否为 null，如果是，则记录错误并抛出异常
        if (commandLineArgs == null) {
            // 检查日志级别是否启用错误日志，以避免不必要的字符串构造，提高性能
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("命令行参数不能为 null");
            }
            throw new IllegalArgumentException("命令行参数不能为空，预期为非空字符串数组");
        }

        // 克隆命令行参数数组以防止外部修改，这是防御性编程的好实践
        args = commandLineArgs.clone();
        // 检查日志级别是否启用信息日志，以避免不必要的字符串构造，提高性能
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("命令行参数初始化成功，当前参数: " + Arrays.toString(args));
        }
    }

    /**
     * 获取命令行参数的方法。
     * 返回 args 的克隆，以防止外部代码修改内部状态。
     *
     * @return 命令行参数数组的克隆，或者 null 如果未初始化
     */
    public String[] getArgs() {
        return args != null ? args.clone() : null; // 返回副本，防止外部修改
    }

    public boolean argsInitialized(){
        return null != args;
    }

}
