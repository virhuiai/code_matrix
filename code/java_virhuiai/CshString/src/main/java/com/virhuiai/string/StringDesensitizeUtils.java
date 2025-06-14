package com.virhuiai.string;

/**
 * 包含字符串脱敏相关的方法
 *    - strDesensitize
 *    - strRightDesensitize
 *    - strLeftDesensitize
 *    - strAllDesensitize
 */
public interface StringDesensitizeUtils {


    /**
     * 对字符串进行脱敏处理
     * @param oriStr 原字符串
     * @param preLen 保留的前面字符数
     * @param afterLen 保留的后面字符数
     * @param desSymP 用来替换中间字符的字符
     * @return 脱敏后的字符串,如果原字符串为null或空,返回空字符串
     */
    public static String strDesensitize(String oriStr, int preLen, int afterLen, String desSymP) {
        String desSym = desSymP;
        if (StringValidationUtils.isBlank(oriStr)) {
            return "";
        }
        if (StringValidationUtils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < preLen + afterLen) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = StringSubstringUtils.substring(oriStr, 0, preLen);
        str.append(pre);
        for (int i = 0; i < oriStr.length() - (preLen + afterLen); i++) {
            str.append(desSym);
        }
        String aft = StringSubstringUtils.substring(oriStr, oriStr.length() - afterLen);
        str.append(aft);
        return str.toString();
    }

    /**
     * 对字符串末尾进行脱敏处理
     * @param oriStr 原字符串
     * @param lastbit 脱敏的末尾字符数
     * @param desSymP 用来替换末尾字符的字符
     * @return 脱敏后的字符串,如果原字符串为null或空,返回空字符串
     */
    public static String strRightDesensitize(String oriStr, int lastbit, String desSymP) {
        String desSym = desSymP;
        if (StringValidationUtils.isBlank(oriStr)) {
            return "";
        }
        if (StringValidationUtils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < lastbit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = StringSubstringUtils.substring(oriStr, 0, oriStr.length() - lastbit);
        str.append(pre);
        for (int i = 0; i < lastbit; i++) {
            str.append(desSym);
        }
        return str.toString();
    }

    /**
     * 对字符串开头进行脱敏处理
     * @param oriStr 原字符串
     * @param prebit 脱敏的开头字符数
     * @param desSymP 用来替换开头字符的字符
     * @return 脱敏后的字符串,如果原字符串为null或空,返回空字符串
     */
    public static String strLeftDesensitize(String oriStr, int prebit, String desSymP) {
        String desSym = desSymP;
        if (StringValidationUtils.isBlank(oriStr)) {
            return "";
        }
        if (StringValidationUtils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < prebit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < prebit; i++) {
            str.append(desSym);
        }
        String last = StringSubstringUtils.substring(oriStr, prebit, oriStr.length());
        str.append(last);
        return str.toString();
    }

    /**
     * 对整个字符串进行脱敏处理
     * @param oriStr 原字符串
     * @param desSymP 用来替换所有字符的字符
     * @return 脱敏后的字符串,如果原字符串为null或空,返回空字符串
     */
    public static String strAllDesensitize(String oriStr, String desSymP) {
        String desSym = desSymP;
        if (StringValidationUtils.isBlank(oriStr)) {
            return "";
        }
        if (StringValidationUtils.isBlank(desSym)) {
            desSym = "*";
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < oriStr.length(); i++) {
            str.append(desSym);
        }
        return str.toString();
    }
}
