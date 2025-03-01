package com.virhuiai;

import org.apache.commons.logging.Log;

public class CshLogUtilsTest2 {


        public static void out(){
            CshLogUtils.testLog();

            Log log = CshLogUtils.getLog(true);
            log.info("测试日志输出");
            log.info("测试日志输出13行");
            log.debug("测试日志输出14行");

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