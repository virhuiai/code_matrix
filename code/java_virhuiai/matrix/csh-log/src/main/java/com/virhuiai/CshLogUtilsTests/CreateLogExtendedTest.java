package com.virhuiai.CshLogUtilsTests;

import com.virhuiai.log.CshLogUtils;
import org.apache.commons.logging.Log;

public class CreateLogExtendedTest {
    public static void main(String[] args) {

        Log log0 =  CshLogUtils.createLogExtended();
        log0.info("hik,10");

        // 测试正常情况
        Log log1 = CshLogUtils.createLogExtended(CreateLogExtendedTest.class);
        log1.info("这是通过 CshLogUtils.getLog(Class<?>) 获取的日志对象");
        // 14:47:20.838 [main] INFO  com.virhuiai.CshLogUtilsTests.LogUtilsClassBasedTest - 这是通过 CshLogUtils.getLog(Class<?>) 获取的日志对象

        // 测试不同类的日志对象
        Log log2 = CshLogUtils.createLogExtended(String.class);
        log2.info("这是 String 类的日志对象");
        //14:47:20.840 [main] INFO  java.lang.String - 这是 String 类的日志对象

        // 验证日志对象是否与类关联
        System.out.println("log1 的名称: " + log1.getClass().getName());
        //log1 的名称: org.apache.logging.log4j.jcl.Log4jLog
        System.out.println("log2 的名称: " + log2.getClass().getName());
        //log2 的名称: org.apache.logging.log4j.jcl.Log4jLog

        // 测试传入 null
        try {
            CshLogUtils.createLogExtended(null);
        } catch (IllegalArgumentException e) {
            System.out.println("捕获到预期的异常：" + e.getMessage());
        }


    }
}