package com.virhuiai.string;

/**
 * 该接口包含一系列字符串验证相关的方法，用于判断字符串的各种状态，
 * 如是否为空、是否包含有效文本、是否为特定格式等。
 */
public interface Validation {
    /**
     * 判断字符序列是否为空。
     * 
     * @param cs 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null 或长度为 0，返回 true；否则返回 false
     */
    default boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符序列是否不为空。
     * 
     * @param cs 要判断的字符序列，可以为 null
     * @return 如果字符序列不为 null 且长度不为 0，返回 true；否则返回 false
     */
    default boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否包含有效文本。
     * 有效文本指非 null、长度不为 0 且包含非空白字符的字符串。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或全部由空白字符组成，返回 false；
     *         否则返回 true
     */
    default boolean hasText(CharSequence str) {
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
     * 判断字符序列是否为空或仅包含空白字符。
     * 
     * @param cs 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或全部由空白字符组成，返回 true；否则返回 false
     * 
     * 说明：hasText 方法判断字符串是否有实际内容（非空白字符），
     *      而 isBlank 方法判断字符序列是否为空或只包含空白字符。
     * 这种互补的方法设计在实际开发中很常见，提供了两种不同的【视角】来查看同一个条件，
     * 增加了代码的可读性和便利性，开发者可以根据具体场景和个人偏好选择使用。
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
     * 判断字符序列是否不为空且包含非空白字符。
     * 
     * @param cs 要判断的字符序列，可以为 null
     * @return 如果字符序列不为 null 且不全为空白字符，返回 true；
     *         否则返回 false
     */
    default boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 判断字符串是否表示一个整数。
     * 支持带正负号的整数格式。
     * 
     * @param str 要判断的字符串，可以为 null
     * @return 如果字符串是有效的整数表示，返回 true；否则返回 false
     */
    default boolean isInteger(String str) {
        // 调用工具类方法去除字符串前后空白字符
        String str2 = Str.Utils.trimText(str);
        // 获取处理后字符串的长度
        int len = str2.length();
        // 获取字符串第一个字符
        char c = str2.charAt(0);
        // 如果第一个字符是正负号，则从第二个字符开始检查，否则从第一个字符开始检查
        int i = (c == '-' || c == '+') ? 1 : 0;
        // 如果索引超出字符串长度，说明只有正负号没有数字，返回 false
        if (i >= len) {
            return false;
        }
        // 循环检查剩余字符是否都是数字
        while (Character.isDigit(str2.charAt(i))) {
            i++;
            // 如果遍历完所有字符都为数字，返回 true
            if (i >= len) {
                return true;
            }
        }
        // 存在非数字字符，返回 false
        return false;
    }

    /**
     * 判断字符序列是否只包含数字字符。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或包含非数字字符，返回 false；
     *         否则返回 true
     */
    default boolean isNumeric(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否只包含字母字符。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或包含非字母字符，返回 false；否则返回 true
     */
    default boolean isAlpha(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否只包含字母和数字字符。
     *
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或包含非字母和数字的字符，返回 false；否则返回 true
     */
    default boolean isAlphanumeric(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否全部由小写字母组成。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或包含非小写字母的字符，返回 false；
     *         否则返回 true
     */
    default boolean isLowerCase(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLowerCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否全部由大写字母组成。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @return 如果字符序列为 null、长度为 0 或包含非大写字母的字符，返回 false；否则返回 true
     */
    default boolean isUpperCase(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isUpperCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否以指定的前缀开头。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @param prefix 要匹配的前缀字符序列，可以为 null
     * @return 如果任一参数为 null 或字符序列不以指定前缀开头，返回 false；
     *         否则返回 true
     */
    default boolean startsWith(CharSequence str, CharSequence prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (i >= str.length() || str.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        // return str.startsWith(prefix);// String 参数时
        return true;
    }

    /**
     * 判断字符序列是否以指定的后缀结尾。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @param suffix 要匹配的后缀字符序列，可以为 null
     * @return 如果任一参数为 null 或字符序列不以指定后缀结尾，返回 false；
     *         否则返回 true
     */
    default boolean endsWith(CharSequence str, CharSequence suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        int strLen = str.length();
        int suffixLen = suffix.length();
        if (suffixLen > strLen) {
            return false;
        }
        for (int i = 0; i < suffixLen; i++) {
            if (str.charAt(strLen - suffixLen + i) != suffix.charAt(i)) {
                return false;
            }
        }
        return true;
        // return str.endsWith(suffix); //参数为String时
    }

    /**
     * 判断字符序列是否包含指定的子字符序列。
     * 
     * @param str 要判断的字符序列，可以为 null
     * @param searchStr 要查找的子字符序列，可以为 null
     * @return 如果任一参数为 null 或字符序列不包含指定子字符序列，返回 false；
     *         否则返回 true
     */
    default boolean contains(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int strLen = str.length();
        int searchLen = searchStr.length();
        for (int i = 0; i <= strLen - searchLen; i++) {
            int j;
            for (j = 0; j < searchLen; j++) {
                if (str.charAt(i + j) != searchStr.charAt(j)) {
                    break;
                }
            }
            if (j == searchLen) {
                return true;
            }
        }
        return false;
        // return str.contains(searchStr); // 参数为String时
    }

    /**
     * 主方法，用于测试本接口中的方法。当前为空实现。
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {

    }
}
