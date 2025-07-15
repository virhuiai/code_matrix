package com.virhuiai.time;

import java.time.Instant;
import java.time.Duration;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

public interface Execution {
    static final Log LOGGER = LogFactory.getLog();
    /**
     * 打印执行时间。
     *
     * @param beginTime 开始时间
     * @return
     * @throws IllegalArgumentException 如果开始时间为null
     */
    default StringBuilder printExecutionTime(Instant beginTime) {
        //  添加了参数检查，防止传入null值。
        if (beginTime == null) {
            LOGGER.error("开始时间不能为null");
            throw new IllegalArgumentException("开始时间不能为null");
        }

        // 计算持续时间
        Duration duration = Duration.between(beginTime, Instant.now());

        // 提取各个时间单位
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();
        long millis = duration.minusSeconds(seconds).toMillis();

        // 构建执行时间字符串
        StringBuilder executionTime = new StringBuilder();
        if (hours > 0) {
            executionTime.append(hours).append("小时");
        }
        if (minutes > 0) {
            executionTime.append(minutes).append("分钟");
        }
        if (seconds > 0) {
            executionTime.append(seconds).append("秒");
        }
        if (millis > 0 || executionTime.length() == 0) {
            executionTime.append(millis).append("毫秒");
        }

        // 记录执行时间 直接使用StringBuilder的toString()方法，无需显式调用。
//        LOGGER.info("执行时间：" + executionTime);
        return executionTime;
    }

}
