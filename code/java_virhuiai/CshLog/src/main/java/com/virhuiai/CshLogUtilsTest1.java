package com.virhuiai;

import org.apache.commons.logging.Log;

public class CshLogUtilsTest1 {
    public static void main(String[] args) {
        // 测试正常情况
        Log log1 = CshLogUtils.getLog(CshLogUtilsTest1.class);
        log1.info("这是通过 CshLogUtils.getLog(Class<?>) 获取的日志对象");

        // 测试传入 null
        try {
            CshLogUtils.getLog(null);
        } catch (IllegalArgumentException e) {
            System.out.println("捕获到预期的异常：" + e.getMessage());
        }

        // 测试不同类的日志对象
        Log log2 = CshLogUtils.getLog(String.class);
        log2.info("这是 String 类的日志对象");

        // 验证日志对象是否与类关联
        System.out.println("log1 的名称: " + log1.getClass().getName());
        System.out.println("log2 的名称: " + log2.getClass().getName());
    }
}