package com.virhuiai.string;

import java.util.ArrayList;
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
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
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
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (!StringValidationUtils.hasText(str)) {
            return null;
        }
        if (!StringValidationUtils.hasText(delimiters)) {
            return new String[]{str};
        }
        return tokenizeToStringArray(str, delimiters, true, true);
    }
}
