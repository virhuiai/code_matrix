package com.virhuiai.string;

/**
 * 包含字符串验证相关的方法
 *    - hasText
 *    - isInteger  依赖
 *    - isBlank 依赖
 *    - isNotBlank
 *    - isEmpty
 */
public interface StringValidationUtils {




    /**
     * 判断字符序列是否为空
     * @param cs 要判断的字符序列
     * @return 如果字符序列为null或长度为0,返回true,否则返回false
     */
    default boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    /**
     * 判断字符串是否有内容
     * @param str 要判断的字符串
     * @return
     *  如果字符串为null, 返回false。
     *  如果字符串长度为0,返回false。
     *  如果字符串全部由空白字符组成,返回false。
     *  否则,返回true。
     */
    default boolean hasText(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }



    /**
     * 判断字符序列是否为空
     * @param cs 要判断的字符序列
     * @return
     * 如果字符序列为null, 返回true。
     * 如果字符序列长度为0,返回true。
     * 如果字符序列全部由空白字符组成,返回true。
     * 否则,返回false。
     *
     *
     * 可以看出,hasText方法判断字符串是否有实际内容(非空白字符),而isBlank方法判断字符序列是否为空或只包含空白字符。
     这种互补的方法设计在实际开发中很常见,它提供了两种不同的视角来查看同一个条件,增加了代码的可读性和便利性。
    开发者可以根据具体的场景和个人偏好选择使用hasText或isBlank。
     */
    default boolean isBlank(CharSequence cs) {
        int strLen = Str.Utils.length(cs);
        if (strLen == 0) {
            return true;
        } else {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }


    /**
     * 判断字符序列是否不为空
     * @param cs 要判断的字符序列
     * @return 如果字符序列不为null且不全为空白字符,返回true,否则返回false
     */
    default boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    /**
     * 判断字符串是否是整数
     * @param str 要判断的字符串
     * @return 如果是整数返回true,否则返回false
     */
    default boolean isInteger(String str) {
        String str2 = Str.Utils.trimText(str);
        int len = str2.length();
        char c = str2.charAt(0);
        int i = (c == '-' || c == '+') ? 1 : 0;
        if (i >= len) {
            return false;
        }
        while (Character.isDigit(str2.charAt(i))) {
            i++;
            if (i >= len) {
                return true;
            }
        }
        return false;
    }



}
