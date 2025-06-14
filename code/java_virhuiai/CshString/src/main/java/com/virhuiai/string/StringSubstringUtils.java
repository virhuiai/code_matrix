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

    /**
     * 从字符串的左边截取指定长度的子字符串
     * @param str 原字符串
     * @param len 要截取的长度,如果小于0,则返回空字符串
     * @return 子字符串,如果原字符串为null,返回null
     */
    default String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * 从字符串的右边截取指定长度的子字符串
     * @param str 原字符串
     * @param len 要截取的长度,如果小于0,则返回空字符串
     * @return 子字符串,如果原字符串为null,返回null
     */
    default String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }



    /**
     * 截取字符串中间部分的子字符串
     * @param str 原字符串
     * @param startInclude 起始位置(包含),如果小于0,从字符串末尾开始算起
     * @param endExclude 结束位置(不包含),如果小于0,从字符串末尾开始算起
     * @return 子字符串,如果原字符串为null,返回null;如果起始位置大于结束位置,返回空字符串
     */
    default String mid(String str, int startInclude, int endExclude) {
        if (str == null) {
            return null;
        }
        if (endExclude < 0) {
            endExclude += str.length();
        }
        if (startInclude < 0) {
            startInclude += str.length();
        }
        if (endExclude > str.length()) {
            endExclude = str.length();
        }
        if (startInclude > endExclude) {
            return "";
        }
        if (startInclude < 0) {
            startInclude = 0;
        }
        if (endExclude < 0) {
            endExclude = 0;
        }
        return str.substring(startInclude, endExclude);
    }



    /**
     * 截取字符串第一次出现分隔符之前的子字符串
     * @param str 原字符串
     * @param separator 分隔符
     * @return 子字符串,如果原字符串为null或未找到分隔符,返回原字符串
     */
    default String substringBefore(String str, String separator) {
        if (str == null || separator == null || str.length() == 0) {
            return str;
        }
        if (separator.length() == 0) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取字符串最后一次出现分隔符之后的子字符串
     * @param str 原字符串
     * @param separator 分隔符
     * @return 子字符串,如果原字符串为null或未找到分隔符,返回原字符串
     */
    default String substringAfterLast(String str, String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

}
