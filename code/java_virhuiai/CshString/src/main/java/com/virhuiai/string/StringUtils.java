package com.virhuiai.string;

import com.virhuiai.CshLogUtils.v2.LoggerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;
import java.io.UnsupportedEncodingException;

public class StringUtils {
    private static final LoggerUtils logger = LoggerUtils.getLog((Class<?>) StringUtils.class);


    public static boolean hasText(String str) {
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



    public static String trimText(String str) {
        if (!hasText(str)) {
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


    public static String trimLeft(String str) {
        if (!hasText(str)) {
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



    public static String trimRight(String str) {
        if (!hasText(str)) {
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

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (!hasText(str)) {
            return null;
        }
        if (!hasText(delimiters)) {
            return new String[]{str};
        }
        return tokenizeToStringArray(str, delimiters, true, true);
    }



    private static boolean isClassOrInterface(Class objClass, String className) {
        if (objClass.getClass().getName().equals(className)) {
            return true;
        }
        Class[] classes = objClass.getInterfaces();
        for (Class cls : classes) {
            if (cls.getName().equals("java.util." + className)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSubClassOf(Class objClass, String className) {
        while (!isClassOrInterface(objClass, className)) {
            Class objClass2 = objClass.getSuperclass();
            objClass = Object.class;
            if (objClass2.equals(Object.class)) {
                return false;
            }
        }
        return true;
    }


    private static String processIterator(Iterator iterator, Class objClass) {
        StringBuffer result = new StringBuffer();
        result.append(objClass.getName());
        result.append('{');
        while (iterator.hasNext()) {
            result.append(toString(iterator.next()));
            result.append("; ");
        }
        result.append('}');
        return result.toString();
    }



    private static String processEnumeration(Enumeration enumeration, Class objClass) {
        StringBuffer result = new StringBuffer();
        result.append(objClass.getName());
        result.append('{');
        while (enumeration.hasMoreElements()) {
            result.append(toString(enumeration.nextElement()));
            result.append("; ");
        }
        result.append('}');
        return result.toString();
    }

    private static String processMap(Map map, Class objClass) {
        StringBuffer result = new StringBuffer();
        Collection keys = map.keySet();
        result.append(objClass.getName());
        result.append('{');
        for (Object obj : keys) {
            result.append(obj).append('=').append(toString(map.get(obj))).append("; ");
        }
        result.append('}');
        return result.toString();
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        Class objClass = obj.getClass();
        if (objClass.getName().startsWith("java.lang")) {
            return obj.toString();
        }
        StringBuffer result = new StringBuffer();
        if (isSubClassOf(objClass, "Collection")) {
            result.append(processIterator(((Collection) obj).iterator(), objClass));
        } else if (isSubClassOf(objClass, "Map")) {
            result.append(processMap((Map) obj, objClass));
        } else if (isSubClassOf(objClass, "Iterator")) {
            result.append(processIterator((Iterator) obj, objClass));
        } else if (isSubClassOf(objClass, "Enumeration")) {
            result.append(processEnumeration((Enumeration) obj, objClass));
        } else if (objClass.isAssignableFrom(new Object[0].getClass())) {
            Object[] array = (Object[]) obj;
            result.append("[");
            for (int i = 0; i < array.length; i++) {
                result.append(array[i] + ":" + (array[i] != null ? array[i].getClass().getName() : "NULL"));
                if (i < array.length - 1) {
                    result.append(",");
                }
            }
            result.append("]");
        } else {
            Method[] methods = (Method[]) null;
            Field[] fields = objClass.getDeclaredFields();
            if (!objClass.getName().startsWith("java") && fields.length > 0) {
                result.append(obj.getClass().getName()).append(":[");
                for (int i2 = 0; i2 < fields.length; i2++) {
                    result.append(fields[i2].getName()).append(":");
                    if (fields[i2].isAccessible()) {
                        try {
                            result.append(toString(fields[i2].get(obj)));
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    } else {
                        if (methods == null) {
                            methods = objClass.getMethods();
                        }
                        for (int j = 0; j < methods.length; j++) {
                            if (methods[j].getName().equalsIgnoreCase("get" + fields[i2].getName())) {
                                try {
                                    result.append(toString(methods[j].invoke(obj, null)));
                                } catch (IllegalAccessException iae2) {
                                    iae2.printStackTrace();
                                } catch (InvocationTargetException ite) {
                                    ite.printStackTrace();
                                }
                            }
                        }
                    }
                    result.append("; ");
                }
                result.append(']');
            } else {
                result.append(obj);
                return result.toString();
            }
        }
        return result.toString();
    }



    public static boolean isInteger(String str) {
        String str2 = trimText(str);
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




    public static String convertEncode(String strIn, String encoding, String targetEncoding) {
        if (!hasText(strIn)) {
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



    public static String parseString(Object obj, String defalut) {
        if (!hasText(defalut)) {
            defalut = "";
        }
        if (obj != null) {
            return obj.toString();
        }
        return defalut;
    }


    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
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


    public static String substring(String str, int start) {
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

    public static String substring(String str, int start, int end) {
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

    public static String strDesensitize(String oriStr, int preLen, int afterLen, String desSymP) {
        String desSym = desSymP;
        if (isBlank(oriStr)) {
            return "";
        }
        if (isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < preLen + afterLen) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = substring(oriStr, 0, preLen);
        str.append(pre);
        for (int i = 0; i < oriStr.length() - (preLen + afterLen); i++) {
            str.append(desSym);
        }
        String aft = substring(oriStr, oriStr.length() - afterLen);
        str.append(aft);
        return str.toString();
    }

    public static String strRightDesensitize(String oriStr, int lastbit, String desSymP) {
        String desSym = desSymP;
        if (isBlank(oriStr)) {
            return "";
        }
        if (isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < lastbit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        String pre = substring(oriStr, 0, oriStr.length() - lastbit);
        str.append(pre);
        for (int i = 0; i < lastbit; i++) {
            str.append(desSym);
        }
        return str.toString();
    }




    public static String strLeftDesensitize(String oriStr, int prebit, String desSymP) {
        String desSym = desSymP;
        if (isBlank(oriStr)) {
            return "";
        }
        if (isBlank(desSym)) {
            desSym = "*";
        }
        if (oriStr.length() < prebit) {
            return oriStr;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < prebit; i++) {
            str.append(desSym);
        }
        String last = substring(oriStr, prebit, oriStr.length());
        str.append(last);
        return str.toString();
    }

    public static String strAllDesensitize(String oriStr, String desSymP) {
        String desSym = desSymP;
        if (isBlank(oriStr)) {
            return "";
        }
        if (isBlank(desSym)) {
            desSym = "*";
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < oriStr.length(); i++) {
            str.append(desSym);
        }
        return str.toString();
    }


    public static String encodeURL(String s) {
        String encodedString;
        try {
            encodedString = URLEncoder.encode(s, "UTF-8");
        } catch (Exception var3) {
            encodedString = URLEncoder.encode(s);
        }

        return encodedString.replaceAll("\\+", "%20");
    }


    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
        } else {
            return false;
        }
    }

    public static boolean contains(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return false;
        } else {
            return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
        }
    }

    public static boolean compapre(String souce, List<String> strList) {
        if (isBlank(souce) || strList == null || strList.size() <= 0) {
            return false;
        }
        for (String str : strList) {
            if (!contains(souce, str)) {
                return false;
            }
        }
        return true;
    }

    public static String getString(Object obj, String defalut) {
        if (obj == null) {
            return defalut;
        }
        String temp = obj.toString().trim();
        if (isBlank(temp)) {
            return defalut;
        }
        return temp;
    }

    public static String encodeString(String str) {
        return isNotBlank(str) ? encodeURL(str) : str;
    }


    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }




}
