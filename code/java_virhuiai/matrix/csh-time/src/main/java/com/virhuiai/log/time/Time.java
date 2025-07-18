package com.virhuiai.log.time;
import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.time.Instant;

// 基于 Java 的 java.time 包（如 LocalDate、LocalTime、ZonedDateTime）实现工具类，因为这是 Java 8 引入的现代化时间日期 API，避免使用过时的 java.util.Date 和 Calendar。
// 时间工具相关的枚举类，实现了 Execution 接口
// 主要用于提供时间相关的功能，如记录和打印执行时间
public enum Time implements Execution {
    // 枚举单例实例，命名为 Utils
    // 用于实现单例模式，确保全局只有一个 Time 实例
    Utils;

    // 日志对象，用于记录日志信息
    private static final Log LOGGER = LogFactory.getLog();
    // 日志对象用于记录程序运行中的信息和错误
    // 通过 LogFactory 获取日志实例，便于统一管理日志输出

    // 私有构造函数，防止外部实例化
    private Time() {
        // 私有化构造函数
        // 确保枚举类不能被外部直接实例化，符合单例模式设计
    }

    // 主方法，程序入口，用于测试时间记录功能
    // 展示了如何记录操作的开始时间、模拟操作并打印执行时间
    public static void main(String[] args) {
        // 记录开始时间
        // 获取当前时间的 Instant 对象，用于后续计算执行时间
        Instant beginTime = Instant.now();
        // beginTime 是一个时间戳，表示程序开始执行的时刻

        // 模拟一些操作
        // 通过休眠模拟耗时操作，以便测试时间记录功能
        try {
            // Thread.sleep(1000); // 休眠 1 秒
            // 原始代码中被注释的休眠方法，使用 Thread.sleep 模拟 1 秒延迟
            java.util.concurrent.TimeUnit.SECONDS.sleep(1);
            // 使用 TimeUnit 提供的休眠方法，休眠 1 秒，效果等同于 Thread.sleep(1000)
            // TimeUnit.SECONDS.sleep 提供了更现代化的时间单位管理方式
        } catch (InterruptedException e) {
            // 捕获休眠过程中可能发生的 InterruptedException 异常
            LOGGER.error("休眠出问题了", e);
            // 记录错误日志，包含异常信息，便于调试和问题追踪
        }

        // 打印执行时间
        // 调用 printExecutionTime 方法，传入 null 参数，获取执行时间信息
        StringBuilder rs = Time.Utils.printExecutionTime(null);
        // rs 是一个 StringBuilder 对象，包含格式化的执行时间信息
        // 传入 null 表示不使用特定的开始时间，可能返回默认信息或当前时间
//        StringBuilder rs = Time.Utils.printExecutionTime(beginTime);
        // 注释掉的代码，展示了如何传入 beginTime 参数来计算从 beginTime 到现在的执行时间
        // 记录执行时间 直接使用StringBuilder的toString()方法，无需显式调用。
        // 说明日志输出直接使用 rs 的 toString() 方法，符合 StringBuilder 的使用习惯
        LOGGER.info("执行时间：" + rs);
        // 将执行时间信息记录到日志，输出格式为“执行时间：”+ rs 的内容
        // 日志级别为 info，表示正常的信息记录
    }
}
