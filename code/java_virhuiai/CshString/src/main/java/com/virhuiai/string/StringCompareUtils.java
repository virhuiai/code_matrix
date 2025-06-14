package com.virhuiai.string;

import java.util.List;

/**
 * 包含字符串比较相关的方法
 *    - contains
 *    - compapre
 */
public interface StringCompareUtils {

    /**
     * 判断字符序列是否包含指定的字符序列
     * @param seq 原字符序列
     * @param searchSeq 要查找的字符序列
     * @return 如果seq包含searchSeq,返回true,否则返回false
     */
    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
        } else {
            return false;
        }
    }



    /**
     * 判断字符序列是否包含指定的字符
     * @param seq 原字符序列
     * @param searchChar 要查找的字符
     * @return 如果seq包含searchChar,返回true,否则返回false
     */
    public static boolean contains(CharSequence seq, int searchChar) {
        if (StringValidationUtils.isEmpty(seq)) {
            return false;
        } else {
            return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
        }
    }


    /**
     * 判断字符串是否包含指定的字符串列表中的所有元素
     * @param souce 原字符串
     * @param strList 要查找的字符串列表
     * @return 如果souce包含strList中的所有元素,返回true,否则返回false
     */
    public static boolean compapre(String souce, List<String> strList) {
        if (StringValidationUtils.isBlank(souce) || strList == null || strList.size() <= 0) {
            return false;
        }
        for (String str : strList) {
            if (!contains(souce, str)) {
                return false;
            }
        }
        return true;
    }


}
