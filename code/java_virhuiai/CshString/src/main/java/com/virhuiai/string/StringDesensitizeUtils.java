package com.virhuiai.string;

/**
 * 包含字符串脱敏相关的方法
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
    default String strDesensitize(String oriStr, int preLen, int afterLen, String desSymP) {
        String desSym = desSymP;
        if (Str.Utils.isBlank(oriStr)) {
            return "";
        }
        if (Str.Utils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < preLen + afterLen) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = Str.Utils.substring(oriStr, 0, preLen);
        str.append(pre);
        for (int i = 0; i < oriStr.length() - (preLen + afterLen); i++) {
            str.append(desSym);
        }
        String aft = Str.Utils.substring(oriStr, oriStr.length() - afterLen);
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
    default String strRightDesensitize(String oriStr, int lastbit, String desSymP) {
        String desSym = desSymP;
        if (Str.Utils.isBlank(oriStr)) {
            return "";
        }
        if (Str.Utils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < lastbit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = Str.Utils.substring(oriStr, 0, oriStr.length() - lastbit);
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
    default String strLeftDesensitize(String oriStr, int prebit, String desSymP) {
        String desSym = desSymP;
        if (Str.Utils.isBlank(oriStr)) {
            return "";
        }
        if (Str.Utils.isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < prebit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < prebit; i++) {
            str.append(desSym);
        }
        String last = Str.Utils.substring(oriStr, prebit, oriStr.length());
        str.append(last);
        return str.toString();
    }

    /**
     * 对整个字符串进行脱敏处理
     * @param oriStr 原字符串
     * @param desSymP 用来替换所有字符的字符
     * @return 脱敏后的字符串,如果原字符串为null或空,返回空字符串
     */
    default String strAllDesensitize(String oriStr, String desSymP) {
        String desSym = desSymP;
        if (Str.Utils.isBlank(oriStr)) {
            return "";
        }
        if (Str.Utils.isBlank(desSym)) {
            desSym = "*";
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < oriStr.length(); i++) {
            str.append(desSym);
        }
        return str.toString();
    }

    /**
     * 脱敏手机号码,保留前3位和后4位,中间用指定字符替换
     * @param mobile 手机号码
     * @param desSymP 用来替换中间字符的字符
     * @return 脱敏后的手机号码,如果手机号码为null或空,返回空字符串
     */
    default String mobileDesensitize(String mobile, String desSymP) {
        return strDesensitize(mobile, 3, 4, desSymP);
    }

    /**
     * 脱敏身份证号码,保留前6位和后4位,中间用指定字符替换
     * @param idCard 身份证号码
     * @param desSymP 用来替换中间字符的字符
     * @return 脱敏后的身份证号码,如果身份证号码为null或空,返回空字符串
     */
    default String idCardDesensitize(String idCard, String desSymP) {
        return strDesensitize(idCard, 6, 4, desSymP);
    }

    /**
     * 脱敏银行卡号,保留前6位和后4位,中间用指定字符替换
     * @param bankCard 银行卡号
     * @param desSymP 用来替换中间字符的字符
     * @return 脱敏后的银行卡号,如果银行卡号为null或空,返回空字符串
     */
    default String bankCardDesensitize(String bankCard, String desSymP) {
        return strDesensitize(bankCard, 6, 4, desSymP);
    }

    /**
     * 脱敏姓名,保留第一个字符,其余字符用指定字符替换
     * @param name 姓名
     * @param desSymP 用来替换其余字符的字符
     * @return 脱敏后的姓名,如果姓名为null或空,返回空字符串
     */
    default String nameDesensitize(String name, String desSymP) {
        if(Str.Utils.isBlank(name)){
            return "";
        }
        return strRightDesensitize(name, name.length() - 1, desSymP);
    }

    /**
     * 脱敏邮箱,保留@符号前的第一个字符和@符号后的内容,其余字符用指定字符替换
     * @param email 邮箱
     * @param desSymP 用来替换其余字符的字符
     * @return 脱敏后的邮箱,如果邮箱为null或空,返回空字符串
     */
    default String emailDesensitize(String email, String desSymP) {
        if (Str.Utils.isBlank(email)) {
            return "";
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return strDesensitize(email, 1, email.length() - atIndex, desSymP);
    }
}
