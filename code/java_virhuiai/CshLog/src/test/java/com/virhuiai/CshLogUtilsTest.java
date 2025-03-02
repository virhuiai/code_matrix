package com.virhuiai;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * CshLogUtils 工具类的单元测试
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CshLogUtilsTest {

//    @Test
//    public void testPrivateConstructor() {
//        try {
//            CshLogUtils.class.getDeclaredConstructor().newInstance();
//            fail("应该抛出AssertionError - 工具类不应被实例化");
//        } catch (Exception e) {
//            assertTrue(e.getCause() instanceof AssertionError);
//        }
//    }
//
//    public static class CreateLogExtendedTests {
//
//        @Test
//        public void testCreateLogWithValidClass() {
//            Log log = CshLogUtils.createLogExtended(String.class);
//            assertNotNull("使用有效类创建的日志对象不应为null", log);
//        }
//
//        @Test(expected = NullPointerException.class)
//        public void testCreateLogWithNullClass() {
//            CshLogUtils.createLogExtended((Class<?>) null);
//        }
//    }
//
//    public static class CreateLogDynamicProxyTests {
//
//        @Test
//        public void testCreateDynamicProxyLog() {
//            Log log = CshLogUtils.createLogDynamicProxy();
//            assertNotNull("创建的动态代理日志对象不应为null", log);
//        }
//    }
//
    public static class LogBehaviorTests {
        private Log log;

        @Before
        public void setUp() {
            log = CshLogUtils.createLogExtended(CshLogUtilsTest.class);
        }

        @Test
        public void testLoggingDoesNotThrowException() {
            try {
                log.info("测试信息");
                log.warn("警告信息");
                log.error("错误信息");
            } catch (Exception e) {
                fail("日志记录不应抛出异常: " + e.getMessage());
            }
        }

        @Test
        public void testLogLevels() {
            assertNotNull("日志对象不应为null", log);
            assertTrue("应支持INFO级别", log.isInfoEnabled());
            assertTrue("应支持WARN级别", log.isWarnEnabled());
            assertTrue("应支持ERROR级别", log.isErrorEnabled());
        }
    }
//
//    @Test
//    public void testGetDefaultLog() {
//        Log log = CshLogUtils.createLogExtended();
//        log.info("testGetDefaultLog");// ok
//        assertNotNull("默认日志对象不应为null", log);
//    }
}