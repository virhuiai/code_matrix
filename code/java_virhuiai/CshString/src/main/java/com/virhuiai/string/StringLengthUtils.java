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
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
