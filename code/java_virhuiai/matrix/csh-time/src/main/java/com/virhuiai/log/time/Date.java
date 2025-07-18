package com.virhuiai.log.time;

import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

public enum Date implements DateUtils{
    // 枚举单例实例，命名为 Utils
    // 用于实现单例模式，确保全局只有一个 Time 实例
    Utils;

    // 日志对象，用于记录日志信息
    private static final Log LOGGER = LogFactory.getLog();
    // 日志对象用于记录程序运行中的信息和错误
    // 通过 LogFactory 获取日志实例，便于统一管理日志输出


}
