package com.virhuiai.string;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 字符串转换相关的方法
 *    - replace
 *    - trimText
 *    - trimLeft
 *    - trimRight
 *    - convertEncode 依赖 StringValidationUtils
 *    - parseString 依赖 StringValidationUtils
 *    - getString 依赖 StringValidationUtils
 *    - encodeString 依赖 StringValidationUtils
 *    - encodeURL
 */
public interface StringTransformationUtils {

    /**
     * 替换字符串
     * @param str 原字符串
     * @param strSrc 要替换的字符串
     * @param strTarget 替换后的字符串
     * @return 替换后的字符串
     */
    public static String replace(String str, String strSrc, String strTarget) {
        int s = 0;
        StringBuffer result = new StringBuffer();
        if (strSrc == null || strSrc.equals("")) {
            return str;
        }
        while (true) {
            int e = str.indexOf(strSrc, s);
            if (e >= 0) {
                result.append(str.substring(s, e));
                result.append(strTarget);
                s = e + strSrc.length();
            } else {
                result.append(str.substring(s));
                return result.toString();
            }
        }
    }

    /**
     * 去除字符串两端的空白字符
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String trimText(String str) {
        if (!StringValidationUtils.hasText(str)) {
            return "";
        }
        char[] c = str.toCharArray();
        int offset = 0;
        int end = 0;
        int i = 0;
        while (true) {
            if (i >= c.length) {
                break;
            }
            if (Character.isWhitespace(c[i])) {
                i++;
            } else {
                offset = i;
                break;
            }
        }
        int i2 = c.length;
        while (true) {
            if (i2 <= offset) {
                break;
            }
            if (Character.isWhitespace(c[i2 - 1])) {
                i2--;
            } else {
                end = i2;
                break;
            }
        }
        return new String(c, offset, end - offset);
    }

    /**
     * 去除字符串左边的空白字符
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String trimLeft(String str) {
        if (!StringValidationUtils.hasText(str)) {
            return "";
        }
        char[] c = str.toCharArray();
        int offset = 0;
        int i = 0;
        while (true) {
            if (i >= c.length) {
                break;
            }
            if (Character.isWhitespace(c[i])) {
                i++;
            } else {
                offset = i;
                break;
            }
        }
        return new String(c, offset, c.length - offset);
    }

    /**
     * 去除字符串右边的空白字符
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String trimRight(String str) {
        if (!StringValidationUtils.hasText(str)) {
            return "";
        }
        char[] c = str.toCharArray();
        int end = 0;
        int i = c.length;
        while (true) {
            if (i <= 0) {
                break;
            }
            if (Character.isWhitespace(c[i - 1])) {
                i--;
            } else {
                end = i;
                break;
            }
        }
        return new String(c, 0, end);
    }

    /**
     * 转换字符编码
     * @param strIn 要转换的字符串
     * @param encoding 原编码
     * @param targetEncoding 目标编码
     * @return 转换后的字符串,如果原字符串为空,返回原字符串
     */
    public static String convertEncode(String strIn, String encoding, String targetEncoding) {
        if (!StringValidationUtils.hasText(strIn)) {
            return strIn;
        }
        try {
            if (encoding != null && targetEncoding != null) {
                if (encoding.equalsIgnoreCase(targetEncoding)) {
                    return strIn;
                }
                String strOut = new String(strIn.getBytes(encoding), targetEncoding);
                return strOut;
            }
            if (encoding != null) {
                String strOut2 = new String(strIn.getBytes(encoding));
                return strOut2;
            }
            if (targetEncoding != null) {
                String strOut3 = new String(strIn.getBytes(), targetEncoding);
                return strOut3;
            }
            return strIn;
        } catch (UnsupportedEncodingException e) {
            return strIn;
        }
    }

    /**
     * 将对象转换成字符串
     * @param obj 要转换的对象
     * @param defalut 默认值
     * @return 如果对象不为null,返回对象的字符串表示,否则返回默认值
     */
    public static String parseString(Object obj, String defalut) {
        if (!StringValidationUtils.hasText(defalut)) {
            defalut = "";
        }
        if (obj != null) {
            return obj.toString();
        }
        return defalut;
    }

    /**
     * 将对象转换为字符串
     * @param obj 要转换的对象
     * @param defalut 默认值
     * @return 如果对象不为null且不为空白,返回对象的字符串表示,否则返回默认值
     */
    public static String getString(Object obj, String defalut) {
        if (obj == null) {
            return defalut;
        }
        String temp = obj.toString().trim();
        if (StringValidationUtils.isBlank(temp)) {
            return defalut;
        }
        return temp;
    }

    /**
     * 对字符串进行URL编码
     * @param str 要编码的字符串
     * @return 编码后的字符串,如果原字符串为null或空白,返回原字符串
     */
    public static String encodeString(String str) {
        return StringValidationUtils.isNotBlank(str) ? encodeURL(str) : str;
    }


    /**
     * 对URL进行编码
     * @param s 要编码的URL
     * @return 编码后的URL
     */
    public static String encodeURL(String s) {
        String encodedString;
        try {
            encodedString = URLEncoder.encode(s, "UTF-8");
        } catch (Exception var3) {
            encodedString = URLEncoder.encode(s);
        }

        return encodedString.replaceAll("\\+", "%20");
    }

}
