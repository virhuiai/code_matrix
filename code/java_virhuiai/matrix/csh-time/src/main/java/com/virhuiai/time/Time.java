package com.virhuiai.time;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.time.Instant;

public enum Time implements Execution{
    Utils;
    private static final Log LOGGER = LogFactory.getLog();

    private Time() {
        // 私有化构造函数
    }

    public static void main(String[] args) {
        // 记录开始时间
        Instant beginTime = Instant.now();

        // 模拟一些操作
        try {
            // Thread.sleep(1000); // 休眠 1 秒
            java.util.concurrent.TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LOGGER.error("休眠出问题了", e);
        }

        // 打印执行时间
        StringBuilder rs = Time.Utils.printExecutionTime(beginTime);
        // 记录执行时间 直接使用StringBuilder的toString()方法，无需显式调用。
        LOGGER.info("执行时间：" + rs);
    }
}
