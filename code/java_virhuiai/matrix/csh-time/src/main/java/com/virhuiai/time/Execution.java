package com.virhuiai.time;

import java.time.Instant;
import java.time.Duration;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

// 定义执行时间相关的接口，提供时间计算和打印功能
// 该接口主要用于规范实现类处理时间相关的操作
public interface Execution {
    // 日志对象，用于记录日志信息
    static final Log LOGGER = LogFactory.getLog();
    // 日志对象通过 LogFactory 获取，用于记录程序运行中的信息和错误
    // 静态常量确保接口中所有实现类共享同一日志实例

    /**
     * 打印执行时间。
     *
     * @param beginTime 开始时间
     * @return
     * @throws IllegalArgumentException 如果开始时间为null
     */
    // 方法用于计算从指定开始时间到当前时间的执行时长，并返回格式化的时间字符串
    // beginTime 参数表示程序或操作的起始时间点
    // 返回值为 StringBuilder，包含格式化的执行时间（如小时、分钟、秒、毫秒）
    // 如果 beginTime 为 null，会抛出 IllegalArgumentException 异常
    default StringBuilder printExecutionTime(Instant beginTime) {
        //  添加了参数检查，防止传入null值。
        // 检查 beginTime 参数是否为 null，确保输入有效性
        if (beginTime == null) {
            LOGGER.error("开始时间不能为null");
            // 记录错误日志，提示开始时间为空的错误信息
            throw new IllegalArgumentException("开始时间不能为null");
            // 抛出非法参数异常，终止方法执行，防止后续计算出错
        }

        // 计算持续时间
        // 使用 Duration.between 计算从 beginTime 到当前时间的时长
        Duration duration = Duration.between(beginTime, Instant.now());
        // duration 表示时间间隔，单位为纳秒，包含从开始到现在的总时长

        // 提取各个时间单位
        // 将总时长分解为小时、分钟、秒和毫秒，便于格式化输出
        long hours = duration.toHours();
        // 提取小时数，duration.toHours() 返回总时长的小时部分
        duration = duration.minusHours(hours);
        // 从总时长中减去小时部分，剩余部分用于计算分钟
        long minutes = duration.toMinutes();
        // 提取分钟数，duration.toMinutes() 返回剩余时长的分钟部分
        duration = duration.minusMinutes(minutes);
        // 从剩余时长中减去分钟部分，剩余部分用于计算秒
        long seconds = duration.getSeconds();
        // 提取秒数，duration.getSeconds() 返回剩余时长的秒部分
        long millis = duration.minusSeconds(seconds).toMillis();
        // 提取毫秒数，剩余时长转换为毫秒

        // 构建执行时间字符串
        // 使用 StringBuilder 动态构建格式化的时间字符串
        StringBuilder executionTime = new StringBuilder();
        // executionTime 用于存储最终的时间字符串，初始为空
        if (hours > 0) {
            // 如果小时数大于 0，追加小时信息
            executionTime.append(hours).append("小时");
        }
        if (minutes > 0) {
            // 如果分钟数大于 0，追加分钟信息
            executionTime.append(minutes).append("分钟");
        }
        if (seconds > 0) {
            // 如果秒数大于 0，追加秒信息
            executionTime.append(seconds).append("秒");
        }
        if (millis > 0 || executionTime.length() == 0) {
            // 如果毫秒数大于 0 或尚未添加任何时间单位，追加毫秒信息
            // 特殊处理：当总时长小于 1 秒时，确保至少输出毫秒
            executionTime.append(millis).append("毫秒");
        }

        // 记录执行时间 直接使用StringBuilder的toString()方法，无需显式调用。
        // 说明日志输出直接使用 executionTime 的 toString() 方法，符合 StringBuilder 的使用习惯
//        LOGGER.info("执行时间：" + executionTime);
        // 注释掉的代码，用于将执行时间记录到日志，输出格式为“执行时间：”+ executionTime 的内容
        // 日志级别为 info，表示正常的信息记录
        return executionTime;
        // 返回格式化的时间字符串，供调用者使用
    }

}