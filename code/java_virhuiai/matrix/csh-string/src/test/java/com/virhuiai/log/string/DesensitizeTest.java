package com.virhuiai.log.string;

import org.junit.Assert;
import org.junit.Test;

public class DesensitizeTest {

    private Desensitize utils = new Desensitize() {};

    @Test
    public void testStrDesensitize() {
        // 正案例
        Assert.assertEquals("123***789", utils.strDesensitize("123456789", 3, 3, "*"));
        Assert.assertEquals("123---789", utils.strDesensitize("123456789", 3, 3, "-"));

        // 反案例
        Assert.assertEquals("", utils.strDesensitize(null, 3, 3, "*"));
        Assert.assertEquals("", utils.strDesensitize("", 3, 3, "*"));
        Assert.assertEquals("123456", utils.strDesensitize("123456", 3, 3, "*"));

        // 边界条件
        Assert.assertEquals("1****6", utils.strDesensitize("123456", 1, 1, "*"));
        Assert.assertEquals("123456", utils.strDesensitize("123456", 6, 6, "*"));
    }

    @Test
    public void testStrRightDesensitize() {
        // 正案例
        Assert.assertEquals("12345***", utils.strRightDesensitize("12345678", 3, "*"));
        Assert.assertEquals("12345---", utils.strRightDesensitize("12345678", 3, "-"));

        // 反案例
        Assert.assertEquals("", utils.strRightDesensitize(null, 3, "*"));
        Assert.assertEquals("", utils.strRightDesensitize("", 3, "*"));
        Assert.assertEquals("123***", utils.strRightDesensitize("123456", 3, "*"));

        // 边界条件
        Assert.assertEquals("1*", utils.strRightDesensitize("12", 1, "*"));
        Assert.assertEquals("******", utils.strRightDesensitize("123456", 6, "*"));
    }

    @Test
    public void testStrLeftDesensitize() {
        // 正案例
        Assert.assertEquals("***45678", utils.strLeftDesensitize("12345678", 3, "*"));
        Assert.assertEquals("---45678", utils.strLeftDesensitize("12345678", 3, "-"));

        // 反案例
        Assert.assertEquals("", utils.strLeftDesensitize(null, 3, "*"));
        Assert.assertEquals("", utils.strLeftDesensitize("", 3, "*"));
        Assert.assertEquals("***456", utils.strLeftDesensitize("123456", 3, "*"));

        // 边界条件
        Assert.assertEquals("*2", utils.strLeftDesensitize("12", 1, "*"));
        Assert.assertEquals("******", utils.strLeftDesensitize("123456", 6, "*"));
    }

    @Test
    public void testStrAllDesensitize() {
        // 正案例
        Assert.assertEquals("********", utils.strAllDesensitize("12345678", "*"));
        Assert.assertEquals("--------", utils.strAllDesensitize("12345678", "-"));

        // 反案例
        Assert.assertEquals("", utils.strAllDesensitize(null, "*"));
        Assert.assertEquals("", utils.strAllDesensitize("", "*"));

        // 边界条件
        Assert.assertEquals("*", utils.strAllDesensitize("1", "*"));
    }

    @Test
    public void testMobileDesensitize() {
        // 正案例
        Assert.assertEquals("136****1234", utils.mobileDesensitize("13612341234", "*"));
        Assert.assertEquals("136----1234", utils.mobileDesensitize("13612341234", "-"));

        // 反案例
        Assert.assertEquals("", utils.mobileDesensitize(null, "*"));
        Assert.assertEquals("", utils.mobileDesensitize("", "*"));
        Assert.assertEquals("1234", utils.mobileDesensitize("1234", "*"));
    }

    @Test
    public void testIdCardDesensitize() {
        // 正案例
        Assert.assertEquals("123456********1234", utils.idCardDesensitize("123456789012341234", "*"));
        Assert.assertEquals("123456--------1234", utils.idCardDesensitize("123456789012341234", "-"));

        // 反案例
        Assert.assertEquals("", utils.idCardDesensitize(null, "*"));
        Assert.assertEquals("", utils.idCardDesensitize("", "*"));
        Assert.assertEquals("1234", utils.idCardDesensitize("1234", "*"));
    }

    @Test
    public void testBankCardDesensitize() {
        // 正案例
        Assert.assertEquals("123456******3456", utils.bankCardDesensitize("1234567890123456", "*"));
        Assert.assertEquals("123456------3456", utils.bankCardDesensitize("1234567890123456", "-"));

        // 反案例
        Assert.assertEquals("", utils.bankCardDesensitize(null, "*"));
        Assert.assertEquals("", utils.bankCardDesensitize("", "*"));
        Assert.assertEquals("1234", utils.bankCardDesensitize("1234", "*"));
    }

    @Test
    public void testNameDesensitize() {
        // 正案例
        Assert.assertEquals("张*", utils.nameDesensitize("张三", "*"));
        Assert.assertEquals("L----", utils.nameDesensitize("LiLei", "-"));

        // 反案例
        Assert.assertEquals("", utils.nameDesensitize(null, "*"));
        Assert.assertEquals("", utils.nameDesensitize("", "*"));

        // 边界条件
        Assert.assertEquals("张", utils.nameDesensitize("张", "*"));
    }

    @Test
    public void testEmailDesensitize() {
        // 正案例
        Assert.assertEquals("z*******@email.com", utils.emailDesensitize("zhangsan@email.com", "*"));
        Assert.assertEquals("l----@email.com", utils.emailDesensitize("lilei@email.com", "-"));

        // 反案例
        Assert.assertEquals("", utils.emailDesensitize(null, "*"));
        Assert.assertEquals("", utils.emailDesensitize("", "*"));
        Assert.assertEquals("@email.com", utils.emailDesensitize("@email.com", "*"));

        // 边界条件
        Assert.assertEquals("a@email.com", utils.emailDesensitize("a@email.com", "*"));
    }
}