package com.virhuiai.log.string;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TransformationTest {
    private Transformation utils = new Transformation() {};

    @Test
    public void testReplace() {
        // 正案例
        assertEquals("Hello, World!", utils.replace("Hello, Java!", "Java", "World"));
        // 反案例
        assertEquals("Hello, Java!", utils.replace("Hello, Java!", "", "World"));
        assertEquals("Hello, Java!", utils.replace("Hello, Java!", null, "World"));
    }

    @Test
    public void testTrimText() {
        // 正案例
        assertEquals("Hello, World!", utils.trimText("  Hello, World!  "));
        // 反案例
        assertEquals("", utils.trimText("   "));
        assertEquals("", utils.trimText(null));
    }

    @Test
    public void testTrimLeft() {
        // 正案例
        assertEquals("Hello, World!  ", utils.trimLeft("  Hello, World!  "));
        // 反案例
        assertEquals("", utils.trimLeft("   "));
        assertEquals("", utils.trimLeft(null));
    }

    @Test
    public void testTrimRight() {
        // 正案例
        assertEquals("  Hello, World!", utils.trimRight("  Hello, World!  "));
        // 反案例
        assertEquals("", utils.trimRight("   "));
        assertEquals("", utils.trimRight(null));
    }

    /**
     * convertEncode 方法在编码转换时,如果源编码和目标编码相同,会直接返回原字符串,而不是进行无效的转换。
     * 此外,当编码参数为 null 时,也会根据方法的逻辑返回原字符串。
     *
     * 在正案例中:
     *
     * 第一个断言将 "你好，世界！" 从 UTF-8 转换为 GBK 编码,预期得到 GBK 编码的结果。
     * 第二个断言将 GBK 编码的 "你好，世界！" 转换为 UTF-8,预期得到原 UTF-8 字符串。
     * 在反案例中:
     *
     * 第一个断言测试源编码和目标编码相同的情况,预期返回原字符串。
     * 第二个断言测试两个编码参数都为 null 的情况,预期返回原字符串。
     * 第三个断言测试目标编码为 null 的情况,预期返回原字符串。
     * 第四个断言测试源编码为 null 的情况,预期返回原字符串。
     * 第五个断言测试空字符串的转换,预期返回空字符串。
     * 第六个断言测试输入为 null 的情况,预期返回 null。
     * 这样修改后,测试案例应该可以全部通过。
     */
    @Test
    public void testConvertEncode() {
        // 正案例
        assertEquals("浣犲ソ锛屼笘鐣岋紒", utils.convertEncode("你好，世界！", "UTF-8", "GBK"));
        assertEquals("你好，世界！", utils.convertEncode("浣犲ソ锛屼笘鐣岋紒", "GBK", "UTF-8"));

        // 反案例
        assertEquals("你好，世界！", utils.convertEncode("你好，世界！", "UTF-8", "UTF-8"));
        assertEquals("你好，世界！", utils.convertEncode("你好，世界！", null, null));
        assertEquals("你好，世界！", utils.convertEncode("你好，世界！", "UTF-8", null));
        assertEquals("你好，世界！", utils.convertEncode("你好，世界！", null, "UTF-8"));
        assertEquals("", utils.convertEncode("", "UTF-8", "GBK"));
        assertEquals(null, utils.convertEncode(null, "UTF-8", "GBK"));
    }

    @Test
    public void testParseString() {
        // 正案例
        assertEquals("123", utils.parseString(123, ""));
        // 反案例
        assertEquals("", utils.parseString(null, ""));
    }

    @Test
    public void testGetString() {
        // 正案例
        assertEquals("123", utils.getString(123, ""));
        // 反案例
        assertEquals("", utils.getString(null, ""));
        assertEquals("", utils.getString("   ", ""));
    }

    @Test
    public void testEncodeString() {
        // 正案例
        assertEquals("Hello%2C%20World%21", utils.encodeString("Hello, World!"));
        // 反案例
        assertEquals("", utils.encodeString(""));
        assertEquals(null, utils.encodeString(null));
    }

    @Test
    public void testCapitalize() {
        // 正案例
        assertEquals("Hello", utils.capitalize("hello"));
        // 反案例
        assertEquals("", utils.capitalize(""));
        assertEquals(null, utils.capitalize(null));
    }

    @Test
    public void testReverse() {
        // 正案例
        assertEquals("!dlroW ,olleH", utils.reverse("Hello, World!"));
        // 反案例
        assertEquals("", utils.reverse(""));
        assertEquals(null, utils.reverse(null));
    }

    @Test
    public void testToLowerCase() {
        // 正案例
        assertEquals("hello, world!", utils.toLowerCase("Hello, World!"));
        // 反案例
        assertEquals("", utils.toLowerCase(""));
        assertEquals(null, utils.toLowerCase(null));
    }

    @Test
    public void testToUpperCase() {
        // 正案例
        assertEquals("HELLO, WORLD!", utils.toUpperCase("Hello, World!"));
        // 反案例
        assertEquals("", utils.toUpperCase(""));
        assertEquals(null, utils.toUpperCase(null));
    }

    @Test
    public void testCountMatches() {
        // 正案例
        assertEquals(2, utils.countMatches("Hello, World! Hello, Java!", "Hello"));
        // 反案例
        assertEquals(0, utils.countMatches("", "Hello"));
        assertEquals(0, utils.countMatches("Hello, World!", ""));
        assertEquals(0, utils.countMatches(null, "Hello"));
    }

    @Test
    public void testUnderscoreToCamelCase() {
        // 正案例
        assertEquals("helloWorld", utils.underscoreToCamelCase("hello_world"));
        // 反案例
        assertEquals("", utils.underscoreToCamelCase(""));
        assertEquals(null, utils.underscoreToCamelCase(null));
    }

    @Test
    public void testUnderscoreToPascalCase() {
        // 正案例
        assertEquals("HelloWorld", utils.underscoreToPascalCase("hello_world"));
        // 反案例
        assertEquals("", utils.underscoreToPascalCase(""));
        assertEquals(null, utils.underscoreToPascalCase(null));
    }

    @Test
    public void testCamelCaseToUnderscore() {
        // 正案例
        assertEquals("hello_world", utils.camelCaseToUnderscore("helloWorld"));
        // 反案例
        assertEquals("", utils.camelCaseToUnderscore(""));
        assertEquals(null, utils.camelCaseToUnderscore(null));
    }

    @Test
    public void testPascalCaseToUnderscore() {
        // 正案例
        assertEquals("hello_world", utils.pascalCaseToUnderscore("HelloWorld"));
        // 反案例
        assertEquals("", utils.pascalCaseToUnderscore(""));
        assertEquals(null, utils.pascalCaseToUnderscore(null));
    }
}