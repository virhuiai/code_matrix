package com.virhuiai.log;

import com.virhuiai.log.logdpp.LogFactory;
import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CshLogUtils 工具类的单元测试
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CshLogUtilsTest {

    private Log log;

    @Before
    public void setUp() {
        log = com.virhuiai.log.logext.LogFactory.getLog(CshLogUtilsTest.class);
    }

//    @Test
//    public void testPrivateConstructor() {
//        try {
//            CshLogUtils.class.getDeclaredConstructor().newInstance();
//            fail("应该抛出AssertionError - 工具类不应被实例化");
//        } catch (Exception e) {
//            assertTrue(e.getCause() instanceof AssertionError);
//        }
//    }

    @Test
    public void testCreateLogWithValidClass() {
        Log log = com.virhuiai.log.logext.LogFactory.getLog(String.class);
        assertNotNull("使用有效类创建的日志对象不应为null", log);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateLogWithNullClass() {
        com.virhuiai.log.logext.LogFactory.getLog((Class<?>) null);
    }

    @Test
    public void testCreateDynamicProxyLog() {
        Log log = LogFactory.getLog();
        assertNotNull("创建的动态代理日志对象不应为null", log);
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

    @Test
    public void testGetDefaultLog() {
        Log log = com.virhuiai.log.logext.LogFactory.getLog();
        assertNotNull("默认日志对象不应为null", log);
    }


}