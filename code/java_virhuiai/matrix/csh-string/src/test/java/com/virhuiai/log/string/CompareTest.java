package com.virhuiai.log.string;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompareTest {

    private Compare utils = new Compare() {};

    @Test
    public void testContainsCharSequence() {
        // 正案例
        Assert.assertTrue(utils.contains("Hello World", "World"));
        Assert.assertTrue(utils.contains("Hello World", ""));

        // 反案例
        Assert.assertFalse(utils.contains("Hello World", "world"));
        Assert.assertFalse(utils.contains("Hello World", "Java"));
        Assert.assertFalse(utils.contains("", "World"));
        Assert.assertFalse(utils.contains(null, "World"));
        Assert.assertFalse(utils.contains("Hello World", null));

        // 边界条件
        Assert.assertTrue(utils.contains("Hello World", "H"));
        Assert.assertTrue(utils.contains("Hello World", "d"));
        Assert.assertFalse(utils.contains("Hello World", "Java"));
    }

    @Test
    public void testContainsChar() {
        // 正案例
        Assert.assertTrue(utils.contains("Hello World", 'W'));
        Assert.assertTrue(utils.contains("Hello World", 'o'));

        // 反案例
        Assert.assertFalse(utils.contains("Hello World", 'w'));
        Assert.assertFalse(utils.contains("Hello World", 'J'));
        Assert.assertFalse(utils.contains("", 'W'));
        Assert.assertFalse(utils.contains(null, 'W'));

        // 边界条件
        Assert.assertTrue(utils.contains("Hello World", 'H'));
        Assert.assertTrue(utils.contains("Hello World", 'd'));
    }

    @Test
    public void testContainsAnyCharSequence() {
        // 正案例
        Assert.assertTrue(utils.containsAny("Hello World", "World", "Java"));
        Assert.assertTrue(utils.containsAny("Hello World", "", "World"));

        // 反案例
        Assert.assertFalse(utils.containsAny("Hello World", "world", "java"));
        Assert.assertFalse(utils.containsAny("Hello World", "Java", "Python"));
        Assert.assertFalse(utils.containsAny("", "World", "Java"));
        Assert.assertFalse(utils.containsAny(null, "World", "Java"));

        // 边界条件
        Assert.assertTrue(utils.containsAny("Hello World", "H", "Java"));
        Assert.assertTrue(utils.containsAny("Hello World", "d", "Java"));
    }

    @Test
    public void testContainsAnyChar() {
        // 正案例
        Assert.assertTrue(utils.containsAny("Hello World", 'W', 'J'));
        Assert.assertTrue(utils.containsAny("Hello World", 'o', 'J'));

        // 反案例
        Assert.assertFalse(utils.containsAny("Hello World", 'w', 'j'));
        Assert.assertFalse(utils.containsAny("Hello World", 'J', 'P'));
        Assert.assertFalse(utils.containsAny("", 'W', 'J'));
        Assert.assertFalse(utils.containsAny(null, 'W', 'J'));

        // 边界条件
        Assert.assertTrue(utils.containsAny("Hello World", 'H', 'J'));
        Assert.assertTrue(utils.containsAny("Hello World", 'd', 'J'));
    }

    @Test
    public void testCompare() {
        // 正案例
        List<String> list1 = new ArrayList<>();
        list1.add("Hello");
        list1.add("World");
        Assert.assertTrue(utils.compapre("Hello World", list1));

        List<String> list2 = new ArrayList<>();
        list2.add("");
        Assert.assertTrue(utils.compapre("Hello World", list2));

        // 反案例
        List<String> list3 = new ArrayList<>();
        list3.add("hello");
        list3.add("world");
        Assert.assertFalse(utils.compapre("Hello World", list3));

        List<String> list4 = new ArrayList<>();
        list4.add("Java");
        list4.add("Python");
        Assert.assertFalse(utils.compapre("Hello World", list4));

        Assert.assertFalse(utils.compapre("", list1));
        Assert.assertFalse(utils.compapre(null, list1));
        Assert.assertFalse(utils.compapre("Hello World", null));
        Assert.assertFalse(utils.compapre("Hello World", new ArrayList<>()));

        // 边界条件
        List<String> list5 = new ArrayList<>();
        list5.add("H");
        list5.add("d");
        Assert.assertTrue(utils.compapre("Hello World", list5));
    }

    @Test
    public void testEqualsIgnoreCase() {
        // 正案例
        Assert.assertTrue(utils.equalsIgnoreCase("Hello", "hello"));
        Assert.assertTrue(utils.equalsIgnoreCase("Hello", "HELLO"));
        Assert.assertTrue(utils.equalsIgnoreCase("", ""));

        // 反案例
        Assert.assertFalse(utils.equalsIgnoreCase("Hello", "World"));
        Assert.assertFalse(utils.equalsIgnoreCase("", "Hello"));
        Assert.assertFalse(utils.equalsIgnoreCase(null, "Hello"));
        Assert.assertFalse(utils.equalsIgnoreCase("Hello", null));

        // 边界条件
        Assert.assertTrue(utils.equalsIgnoreCase("Hello", "Hello"));
        Assert.assertTrue(utils.equalsIgnoreCase(null, null));
    }

    @Test
    public void testStartsWith() {
        // 正案例
        Assert.assertTrue(utils.startsWith("Hello World", "Hello"));
        Assert.assertTrue(utils.startsWith("Hello World", ""));

        // 反案例
        Assert.assertFalse(utils.startsWith("Hello World", "World"));
        Assert.assertFalse(utils.startsWith("Hello World", "hello"));
        Assert.assertFalse(utils.startsWith("", "Hello"));
        Assert.assertFalse(utils.startsWith(null, "Hello"));
        Assert.assertFalse(utils.startsWith("Hello World", null));

        // 边界条件
        Assert.assertTrue(utils.startsWith("Hello World", "H"));
        Assert.assertFalse(utils.startsWith("Hello World", "o"));
    }

    @Test
    public void testEndsWith() {
        // 正案例
        Assert.assertTrue(utils.endsWith("Hello World", "World"));
        Assert.assertTrue(utils.endsWith("Hello World", ""));

        // 反案例
        Assert.assertFalse(utils.endsWith("Hello World", "Hello"));
        Assert.assertFalse(utils.endsWith("Hello World", "world"));
        Assert.assertFalse(utils.endsWith("", "World"));
        Assert.assertFalse(utils.endsWith(null, "World"));
        Assert.assertFalse(utils.endsWith("Hello World", null));

        // 边界条件
        Assert.assertTrue(utils.endsWith("Hello World", "d"));
        Assert.assertFalse(utils.endsWith("Hello World", "W"));
    }
}