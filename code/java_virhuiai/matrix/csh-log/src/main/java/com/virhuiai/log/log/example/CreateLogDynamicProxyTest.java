package com.virhuiai.log.log.example;

import com.virhuiai.log.log.logdpp.LogFactory;
import org.apache.commons.logging.Log;

public class CreateLogDynamicProxyTest {


        public static void out(){
//            CshLogUtils.testLog();

            Log log = LogFactory.createLogDynamicProxy();
            log.info("测试日志输出");
            log.info("测试日志输出14行");
            log.debug("测试日志输出15行");

            Log log1 = LogFactory.createLogDynamicProxy(CreateLogDynamicProxyTest.class);
            log1.info("测试日志输出");

//            String classNameFull0 = Thread.currentThread().getStackTrace()[0].getClassName();
//            System.out.println(classNameFull0);
//            String classNameFull1 = Thread.currentThread().getStackTrace()[1].getClassName();
//            System.out.println(classNameFull1);
//            String classNameFull2 = Thread.currentThread().getStackTrace()[2].getClassName();
//            System.out.println(classNameFull2);
        }


    public static void main(String[] args) {
        out();
    }
}