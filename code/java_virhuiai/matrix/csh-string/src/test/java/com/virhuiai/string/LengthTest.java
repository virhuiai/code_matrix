package com.virhuiai.string;

import org.junit.Assert;
import org.junit.Test;

public class LengthTest {

    private Length utils = new Length() {};

    @Test
    public void testLength() {
        // 正案例
        Assert.assertEquals(5, utils.length("Hello"));
        Assert.assertEquals(0, utils.length(""));

        // 反案例
        Assert.assertEquals(0, utils.length(null));

        // 边界条件
        Assert.assertEquals(1, utils.length("a"));
        Assert.assertEquals(Integer.MAX_VALUE, utils.length(new CharSequence() {
            @Override
            public int length() {
                return Integer.MAX_VALUE;
            }

            @Override
            public char charAt(int index) {
                return 'a';
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }
        }));
    }

    @Test
    public void testLengthWithoutWhitespace() {
        // 正案例
        Assert.assertEquals(5, utils.lengthWithoutWhitespace("Hello"));
        Assert.assertEquals(5, utils.lengthWithoutWhitespace("  Hello  "));
        Assert.assertEquals(5, utils.lengthWithoutWhitespace("\tHello\n"));
        Assert.assertEquals(0, utils.lengthWithoutWhitespace(""));
        Assert.assertEquals(0, utils.lengthWithoutWhitespace("   "));

        // 反案例
        Assert.assertEquals(0, utils.lengthWithoutWhitespace(null));

        // 边界条件
        Assert.assertEquals(1, utils.lengthWithoutWhitespace("a"));
        Assert.assertEquals(1, utils.lengthWithoutWhitespace(" a "));
        Assert.assertEquals(Integer.MAX_VALUE, utils.lengthWithoutWhitespace(new CharSequence() {
            @Override
            public int length() {
                return Integer.MAX_VALUE;
            }

            @Override
            public char charAt(int index) {
                return 'a';
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }
        }));
    }
}