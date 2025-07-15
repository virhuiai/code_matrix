package com.virhuiai.time;

import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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


    /**
     * 格式化指定的时间为自定义格式的字符串。
     *
     * @param instant  要格式化的时间点
     * @param pattern  日期时间格式模式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的时间字符串
     * @throws IllegalArgumentException 如果 instant 为 null 或 pattern 无效
     */
    // 方法用于将指定的 Instant 时间点格式化为自定义格式的字符串
    // instant 参数表示需要格式化的时间点
    // pattern 参数指定输出的日期时间格式，如 "yyyy-MM-dd HH:mm:ss"
    // 返回值为 String 类型，表示格式化后的时间字符串
    // 如果 instant 为 null 或 pattern 无效，会抛出 IllegalArgumentException 异常
    default String formatDateTime(Instant instant, String pattern) {
        // 检查输入参数是否有效
        if (instant == null) {
            // 检查 instant 参数是否为 null
            LOGGER.error("时间点不能为null");
            // 记录错误日志，提示时间点为空
            throw new IllegalArgumentException("时间点不能为null");
            // 抛出非法参数异常，终止方法执行
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            // 检查 pattern 参数是否为 null 或空字符串
            LOGGER.error("格式模式不能为空");
            // 记录错误日志，提示格式模式无效
            throw new IllegalArgumentException("格式模式不能为空");
            // 抛出非法参数异常，终止方法执行
        }

        // 使用 DateTimeFormatter 格式化时间
        DateTimeFormatter formatter;
        try {
            // 尝试创建 DateTimeFormatter 对象
            formatter = DateTimeFormatter.ofPattern(pattern);
            // formatter 用于根据指定模式格式化时间
        } catch (IllegalArgumentException e) {
            // 捕获无效格式模式引发的异常
            LOGGER.error("无效的格式模式: " + pattern, e);
            // 记录错误日志，包含异常信息
            throw new IllegalArgumentException("无效的格式模式: " + pattern, e);
            // 抛出异常，提示格式模式无效
        }

        // 格式化 Instant 对象并返回结果
        String formattedTime = instant.atZone(java.time.ZoneId.systemDefault())
                .format(formatter);
        // 将 Instant 转换为 ZonedDateTime，使用系统默认时区，然后格式化为字符串
        // formattedTime 为最终的格式化时间字符串
        LOGGER.info("格式化时间结果: " + formattedTime);
        // 记录格式化的时间结果到日志，级别为 info
        return formattedTime;
        // 返回格式化后的时间字符串
    }

    /**
     * 判断第一个时间点是否早于第二个时间点。
     *
     * @param first   第一个时间点
     * @param second  第二个时间点
     * @return 如果第一个时间早于第二个时间，返回 true，否则返回 false
     * @throws IllegalArgumentException 如果任一时间点为 null
     */
    // 方法用于比较两个 Instant 时间点，判断第一个时间是否早于第二个时间
    // first 参数表示第一个时间点
    // second 参数表示第二个时间点
    // 返回值为 boolean 类型，true 表示 first 早于 second，false 表示不早于
    // 如果 first 或 second 为 null，会抛出 IllegalArgumentException 异常
    default boolean isDateBefore(Instant first, Instant second) {
        // 检查输入参数是否有效
        if (first == null || second == null) {
            // 检查 first 或 second 参数是否为 null
            LOGGER.error("时间点不能为null");
            // 记录错误日志，提示时间点为空
            throw new IllegalArgumentException("时间点不能为null");
            // 抛出非法参数异常，终止方法执行
        }

        // 比较两个时间点
        boolean isBefore = first.isBefore(second);
        // 使用 Instant 的 isBefore 方法比较时间点，判断 first 是否早于 second
        // isBefore 为 true 表示 first 早于 second
        LOGGER.info("比较结果: " + first + " 是否早于 " + second + ": " + isBefore);
        // 记录比较结果到日志，级别为 info
        return isBefore;
        // 返回比较结果
    }

    /**
     * 在指定时间点上添加指定的时间长度。
     *
     * @param instant  起始时间点
     * @param amount   要添加的时间量
     * @param unit     时间单位，如 ChronoUnit.HOURS、ChronoUnit.MINUTES 等
     * @return 添加时间后的新时间点
     * @throws IllegalArgumentException 如果 instant 或 unit 为 null
     */
    // 方法用于在指定时间点上添加一定的时间长度，返回新的时间点
    // instant 参数表示起始时间点
    // amount 参数表示要添加的时间量（可以为负数，表示减去时间）
    // unit 参数指定时间单位，如 ChronoUnit.HOURS、ChronoUnit.MINUTES 等
    // 返回值为 Instant 类型，表示添加时间后的新时间点
    // 如果 instant 或 unit 为 null，会抛出 IllegalArgumentException 异常
    default Instant addDuration(Instant instant, long amount, ChronoUnit unit) {
        // 检查输入参数是否有效
        if (instant == null) {
            // 检查 instant 参数是否为 null
            LOGGER.error("时间点不能为null");
            // 记录错误日志，提示时间点为空
            throw new IllegalArgumentException("时间点不能为null");
            // 抛出非法参数异常，终止方法执行
        }
        if (unit == null) {
            // 检查 unit 参数是否为 null
            LOGGER.error("时间单位不能为null");
            // 记录错误日志，提示时间单位为空
            throw new IllegalArgumentException("时间单位不能为null");
            // 抛出非法参数异常，终止方法执行
        }

        // 添加时间长度
        Instant result = instant.plus(amount, unit);
        // 使用 Instant 的 plus 方法添加指定时间量，生成新的时间点
        // result 为添加时间后的新 Instant 对象
        LOGGER.info("添加 " + amount + " " + unit + " 后的时间: " + result);
        // 记录添加时间后的结果到日志，级别为 info
        return result;
        // 返回新的时间点
    }

}