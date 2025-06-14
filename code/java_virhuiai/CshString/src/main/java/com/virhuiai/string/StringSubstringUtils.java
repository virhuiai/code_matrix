package com.virhuiai.string;

/**
 * 包含字符串截取相关的方法
 *    - substring
 */
public interface StringSubstringUtils {

    /**
     * 返回字符串从指定位置开始的子字符串
     * @param str 原字符串
     * @param start 起始位置,如果小于0,从字符串末尾开始算起
     * @return 子字符串,如果原字符串为null,返回null
     */
    default String substring(String str, int start) {
        if (str == null) {
            return null;
        } else {
            if (start < 0) {
                start += str.length();
            }

            if (start < 0) {
                start = 0;
            }

            return start > str.length() ? "" : str.substring(start);
        }
    }


    /**
     * 返回字符串指定范围的子字符串
     * @param str 原字符串
     * @param start 起始位置,如果小于0,从字符串末尾开始算起
     * @param end 结束位置,如果小于0,从字符串末尾开始算起
     * @return 子字符串,如果原字符串为null,返回null
     */
    default String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }

                return str.substring(start, end);
            }
        }
    }


}
