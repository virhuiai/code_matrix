package com.virhuiai.string;

/**
 * 包含获取字符序列长度的方法
 *    - length
 */
public interface StringLengthUtils {

    /**
     * 返回字符序列的长度
     * @param cs 字符序列
     * @return 如果字符序列为null,返回0,否则返回字符序列的长度
     */
    default int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 获取字符序列的实际长度(去除前后空白字符)
     * @param cs 字符序列
     * @return 如果字符序列为null,返回0,否则返回去除前后空白字符后的实际长度
     */
    default int lengthWithoutWhitespace(CharSequence cs) {
        if (Str.Utils.isBlank(cs)) {
            return 0;
        }
        int start = 0;
        int end = cs.length();
        while (start < end && Character.isWhitespace(cs.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(cs.charAt(end - 1))) {
            end--;
        }
        return end - start;
    }
}
