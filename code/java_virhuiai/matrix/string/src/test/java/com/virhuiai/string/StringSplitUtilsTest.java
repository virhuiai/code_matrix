package com.virhuiai.string;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StringSplitUtilsTest {

    private Split utils = new Split() {};

    @Test
    public void testTokenizeToStringArray() {
        // 正案例
        String[] tokens1 = utils.tokenizeToStringArray("a,b,c", ",", true, true);
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens1);

        String[] tokens2 = utils.tokenizeToStringArray("a, b, c", ",", true, true);
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens2);

        String[] tokens3 = utils.tokenizeToStringArray("a, b, c", ",", false, true);
        Assert.assertArrayEquals(new String[]{"a", " b", " c"}, tokens3);

        String[] tokens4 = utils.tokenizeToStringArray("a,,c", ",", true, false);
        Assert.assertArrayEquals(new String[]{"a", "c"}, tokens4);

        // 反案例
        String[] tokens5 = utils.tokenizeToStringArray(null, ",", true, true);
        Assert.assertArrayEquals(new String[0], tokens5);

        String[] tokens6 = utils.tokenizeToStringArray("", ",", true, true);
        Assert.assertArrayEquals(new String[0], tokens6);

        // 边界条件
        String[] tokens7 = utils.tokenizeToStringArray("a,b,c", null, true, true);
        Assert.assertArrayEquals(new String[]{"a,b,c"}, tokens7);

        String[] tokens8 = utils.tokenizeToStringArray("a,b,c", "", true, true);
        Assert.assertArrayEquals(new String[]{"a,b,c"}, tokens8);
    }

    @Test
    public void testTokenizeToStringArrayWithDelimiters() {
        // 正案例
        String[] tokens1 = utils.tokenizeToStringArray("a,b,c", ",");
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens1);

        String[] tokens2 = utils.tokenizeToStringArray("a, b, c", ",");
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens2);

        // 反案例
        String[] tokens3 = utils.tokenizeToStringArray(null, ",");
        Assert.assertArrayEquals(new String[0], tokens3);

        String[] tokens4 = utils.tokenizeToStringArray("", ",");
        Assert.assertArrayEquals(new String[0], tokens4);

        // 边界条件
        String[] tokens5 = utils.tokenizeToStringArray("a,b,c", null);
        Assert.assertArrayEquals(new String[]{"a,b,c"}, tokens5);

        String[] tokens6 = utils.tokenizeToStringArray("a,b,c", "");
        Assert.assertArrayEquals(new String[]{"a,b,c"}, tokens6);
    }

    @Test
    public void testSplit() {
        // 正案例
        List<String> tokens1 = utils.split("a,b,c", ",");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), tokens1);

        List<String> tokens2 = utils.split("a, b, c", ", ");
        Assert.assertEquals(Arrays.asList("a", "b", "c"), tokens2);

        // 反案例
        List<String> tokens3 = utils.split(null, ",");
        Assert.assertTrue(tokens3.isEmpty());

        List<String> tokens4 = utils.split("", ",");
        Assert.assertTrue(tokens4.isEmpty());

        // 边界条件
        List<String> tokens5 = utils.split("a,b,c", "");
        Assert.assertEquals(Arrays.asList("a,b,c"), tokens5);
    }

    @Test
    public void testSplitByWhitespace() {
        // 正案例
        String[] tokens1 = utils.splitByWhitespace("a b c");
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens1);

        String[] tokens2 = utils.splitByWhitespace("a  b \t c");
        Assert.assertArrayEquals(new String[]{"a", "b", "c"}, tokens2);

        // 反案例
        String[] tokens3 = utils.splitByWhitespace(null);
        Assert.assertArrayEquals(new String[0], tokens3);

        String[] tokens4 = utils.splitByWhitespace("");
        Assert.assertArrayEquals(new String[0], tokens4);// todo

        // 边界条件
        String[] tokens5 = utils.splitByWhitespace("   ");
        Assert.assertArrayEquals(new String[0], tokens5);
    }

    @Test
    public void testSplitByLength() {
        // 正案例
        List<String> tokens1 = utils.splitByLength("abcdef", 2);
        Assert.assertEquals(Arrays.asList("ab", "cd", "ef"), tokens1);

        List<String> tokens2 = utils.splitByLength("abcdefg", 3);
        Assert.assertEquals(Arrays.asList("abc", "def", "g"), tokens2);

        // 反案例
        List<String> tokens3 = utils.splitByLength(null, 2);
        Assert.assertTrue(tokens3.isEmpty());

        List<String> tokens4 = utils.splitByLength("", 2);
        Assert.assertTrue(tokens4.isEmpty());

        // 边界条件
//        List<String> tokens5 = utils.splitByLength("abcdef", 0);
//        Assert.assertTrue(tokens5.isEmpty());
        try {
            List<String> tokens5 = utils.splitByLength("abcdef", 0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 期望抛出 IllegalArgumentException 异常
            Assert.assertEquals("Length must be greater than 0", e.getMessage());
        }

        List<String> tokens6 = utils.splitByLength("abcdef", 10);
        Assert.assertEquals(Arrays.asList("abcdef"), tokens6);
    }

    @Test
    public void testSplitCamelCase() {
        // 正案例
        List<String> tokens1 = utils.splitCamelCase("helloWorld");
        Assert.assertEquals(Arrays.asList("hello", "world"), tokens1);

        List<String> tokens2 = utils.splitCamelCase("HelloWorld");
        Assert.assertEquals(Arrays.asList("hello", "world"), tokens2);

        List<String> tokens3 = utils.splitCamelCase("helloWorld123");
        Assert.assertEquals(Arrays.asList("hello", "world123"), tokens3);

        // 反案例
        List<String> tokens4 = utils.splitCamelCase(null);
        Assert.assertTrue(tokens4.isEmpty());

        List<String> tokens5 = utils.splitCamelCase("");
        Assert.assertTrue(tokens5.isEmpty());

        // 边界条件
        List<String> tokens6 = utils.splitCamelCase("hello");
        Assert.assertEquals(Arrays.asList("hello"), tokens6);

        List<String> tokens7 = utils.splitCamelCase("H");
        Assert.assertEquals(Arrays.asList("h"), tokens7);
    }
}