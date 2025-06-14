package com.virhuiai.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 包含字符串分割相关的方法
 *    - tokenizeToStringArray
 */
public interface StringSplitUtils {



    /**
     * 将字符串按指定分隔符分割成字符串数组
     * @param str 要分割的字符串
     * @param delimiters 分隔符
     * @param trimTokens 是否去除分割后每个元素两端的空白字符
     * @param ignoreEmptyTokens 是否忽略空元素
     * @return 分割后的字符串数组
     */
    default String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (!Str.Utils.hasText(str)) {
            return new String[0];
        }
        if (!Str.Utils.hasText(delimiters)) {
            return new String[]{str};
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    /**
     * 将字符串按指定分隔符分割成字符串数组,去除每个元素两端空白字符,忽略空元素
     * @param str 要分割的字符串
     * @param delimiters 分隔符
     * @return 分割后的字符串数组,如果原字符串为空或分隔符为空,返回null
     */
    default String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * 将字符串按指定分隔符分割成字符串列表
     * @param str 要分割的字符串
     * @param delimiter 分隔符
     * @return 分割后的字符串列表,如果原字符串为空,返回空列表
     */
    default List<String> split(String str, String delimiter) {
        if (Str.Utils.isBlank(str)) {
            return new ArrayList<>();
        }
        if (Str.Utils.isBlank(delimiter)) {
            return Arrays.asList(str);
        }
        String[] tokens = str.split(delimiter);
        return Arrays.asList(tokens);
    }

    /**
     * 将字符串按空白字符分割成字符串数组
     * @param str 要分割的字符串
     * @return 分割后的字符串数组,如果原字符串为空,返回空数组
     */
    default String[] splitByWhitespace(String str) {
        if (Str.Utils.isBlank(str)) {
            return new String[0];
        }
        return str.split("\\s+");
    }

    /**
     * 将字符串按指定长度分割成字符串列表
     * @param str 要分割的字符串
     * @param length 分割后每个字符串的长度
     * @return 分割后的字符串列表,如果原字符串为空,返回空列表
     * 在 length 为0或小于0时选择抛出 IllegalArgumentException 异常
     */
    default List<String> splitByLength(String str, int length) {
        if (Str.Utils.isBlank(str)) {
            return new ArrayList<>();
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        List<String> list = new ArrayList<>();
        int index = 0;
        while (index < str.length()) {
            list.add(str.substring(index, Math.min(index + length, str.length())));
            index += length;
        }
        return list;
    }

    /**
     * 将驼峰式命名的字符串分割成单词列表
     * @param str 要分割的字符串
     * @return 分割后的单词列表,如果原字符串为空,返回空列表
     */
    default List<String> splitCamelCase(String str) {
        if (Str.Utils.isBlank(str)) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            }
            sb.append(Character.toLowerCase(c));
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }

}
