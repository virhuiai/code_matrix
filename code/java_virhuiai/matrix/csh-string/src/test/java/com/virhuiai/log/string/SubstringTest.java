package com.virhuiai.log.string;

import org.junit.Assert;
import org.junit.Test;

public class SubstringTest {

    private Substring utils = new Substring() {};
    @Test
    public void testSubstringWithStartIndex() {
        // 正案例
        Assert.assertEquals("World!", utils.substring("Hello, World!", 7));
        Assert.assertEquals("World!", utils.substring("Hello, World!", -6));

        // 反案例
        Assert.assertEquals("", utils.substring("Hello, World!", 100));
        Assert.assertEquals("Hello, World!", utils.substring("Hello, World!", -100));

        // 边界条件
        Assert.assertEquals("Hello, World!", utils.substring("Hello, World!", 0));
        Assert.assertEquals("", utils.substring("Hello, World!", 13));
        Assert.assertNull(utils.substring(null, 0));
    }

    @Test
    public void testSubstringWithStartEndIndex() {
        // 正案例
        Assert.assertEquals("World", utils.substring("Hello, World!", 7, 12));
        Assert.assertEquals("World", utils.substring("Hello, World!", -6, -1));

        // 反案例
        Assert.assertEquals("", utils.substring("Hello, World!", 7, 6));
        Assert.assertEquals("", utils.substring("Hello, World!", 100, 200));
        Assert.assertEquals("Hello, World!", utils.substring("Hello, World!", -100, 200));

        // 边界条件
        Assert.assertEquals("Hello", utils.substring("Hello, World!", 0, 5));
        Assert.assertEquals("World!", utils.substring("Hello, World!", 7, 13));
        Assert.assertNull(utils.substring(null, 0, 5));
    }

    @Test
    public void testLeft() {
        // 正案例
        Assert.assertEquals("Hello", utils.left("Hello, World!", 5));

        // 反案例
        Assert.assertEquals("", utils.left("Hello, World!", -5));
        Assert.assertEquals("Hello, World!", utils.left("Hello, World!", 100));

        // 边界条件
        Assert.assertEquals("", utils.left("Hello, World!", 0));
        Assert.assertEquals("Hello, World!", utils.left("Hello, World!", 13));
        Assert.assertNull(utils.left(null, 5));
    }

    @Test
    public void testRight() {
        // 正案例
        Assert.assertEquals("World!", utils.right("Hello, World!", 6));

        // 反案例
        Assert.assertEquals("", utils.right("Hello, World!", -5));
        Assert.assertEquals("Hello, World!", utils.right("Hello, World!", 100));

        // 边界条件
        Assert.assertEquals("", utils.right("Hello, World!", 0));
        Assert.assertEquals("Hello, World!", utils.right("Hello, World!", 13));
        Assert.assertNull(utils.right(null, 5));
    }

    @Test
    public void testMid() {
        // 正案例
        Assert.assertEquals("World", utils.mid("Hello, World!", 7, 12));
        Assert.assertEquals("World", utils.mid("Hello, World!", -6, -1));

        // 反案例
        Assert.assertEquals("", utils.mid("Hello, World!", 7, 6));
        Assert.assertEquals("", utils.mid("Hello, World!", 100, 200));
        Assert.assertEquals("Hello, World!", utils.mid("Hello, World!", -100, 200));

        // 边界条件
        Assert.assertEquals("Hello", utils.mid("Hello, World!", 0, 5));
        Assert.assertEquals("World!", utils.mid("Hello, World!", 7, 13));
        Assert.assertNull(utils.mid(null, 0, 5));
    }

    @Test
    public void testSubstringBefore() {
        // 正案例
        Assert.assertEquals("Hello", utils.substringBefore("Hello, World!", ", "));

        // 反案例
        Assert.assertEquals("Hello, World!", utils.substringBefore("Hello, World!", "Java"));
        Assert.assertEquals("", utils.substringBefore("Hello, World!", ""));

        // 边界条件
        Assert.assertEquals("", utils.substringBefore("Hello, World!", "H"));
        Assert.assertEquals("Hello", utils.substringBefore("Hello, World!", ", World!"));
        Assert.assertNull(utils.substringBefore(null, ", "));
    }

    @Test
    public void testSubstringAfterLast() {
        // 正案例
        Assert.assertEquals("World!", utils.substringAfterLast("Hello, World!", ", "));

        // 反案例
        Assert.assertEquals("", utils.substringAfterLast("Hello, World!", "Java"));
        Assert.assertEquals("", utils.substringAfterLast("Hello, World!", "World!"));
        Assert.assertEquals("", utils.substringAfterLast("Hello, World!", ""));

        // 边界条件
        Assert.assertEquals("ello, World!", utils.substringAfterLast("Hello, World!", "H"));
        Assert.assertNull(utils.substringAfterLast(null, ", "));
    }
}