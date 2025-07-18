package com.virhuiai.log.string;

import java.util.List;

/**
 * 包含字符串比较相关的方法
 *    - contains
 *    - compapre
 */
public interface Compare {

    /**
     * 判断字符序列是否包含指定的字符序列
     * @param seq 原字符序列
     * @param searchSeq 要查找的字符序列
     * @return 如果seq包含searchSeq,返回true,否则返回false
     */
    default boolean contains(CharSequence seq, CharSequence searchSeq) {
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
    default boolean contains(CharSequence seq, int searchChar) {
        if (Str.Utils.isEmpty(seq)) {
            return false;
        } else {
            return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
        }
    }

    /**
     * 判断字符序列是否包含指定的字符序列列表中的任意一个元素
     * @param seq 原字符序列
     * @param searchSeqs 要查找的字符序列列表
     * @return 如果seq包含searchSeqs中的任意一个元素,返回true,否则返回false
     */
    default boolean containsAny(CharSequence seq, CharSequence... searchSeqs) {
        if (seq != null && searchSeqs != null) {
            for (CharSequence searchSeq : searchSeqs) {
                if (contains(seq, searchSeq)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断字符序列是否包含指定的字符列表中的任意一个字符
     * @param seq 原字符序列
     * @param searchChars 要查找的字符列表
     * @return 如果seq包含searchChars中的任意一个字符,返回true,否则返回false
     */
    default boolean containsAny(CharSequence seq, char... searchChars) {
        if (seq != null && searchChars != null) {
            for (char searchChar : searchChars) {
                if (contains(seq, searchChar)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断字符串是否包含指定的字符串列表中的所有元素
     * @param souce 原字符串
     * @param strList 要查找的字符串列表
     * @return 如果souce包含strList中的所有元素,返回true,否则返回false
     */
    default boolean compapre(String souce, List<String> strList) {
        if (Str.Utils.isBlank(souce) || strList == null || strList.size() <= 0) {
            return false;
        }
        for (String str : strList) {
            if (!contains(souce, str)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断两个字符序列是否相等(忽略大小写)
     * @param seq1 第一个字符序列
     * @param seq2 第二个字符序列
     * @return 如果seq1和seq2相等(忽略大小写),返回true,否则返回false
     */
    default boolean equalsIgnoreCase(CharSequence seq1, CharSequence seq2) {
        if (seq1 == null || seq2 == null) {
            return seq1 == seq2;
        } else {
            return CharSequenceUtils.regionMatches(seq1, true, 0, seq2, 0, Math.max(seq1.length(), seq2.length()));
        }
    }

    /**
     * 判断字符序列是否以指定的字符序列开头
     * @param seq 原字符序列
     * @param prefix 要查找的前缀字符序列
     * @return 如果seq以prefix开头,返回true,否则返回false
     */
    default boolean startsWith(CharSequence seq, CharSequence prefix) {
        if (seq != null && prefix != null) {
            return CharSequenceUtils.regionMatches(seq, false, 0, prefix, 0, prefix.length());
        } else {
            return false;
        }
    }

    /**
     * 判断字符序列是否以指定的字符序列结尾
     * @param seq 原字符序列
     * @param suffix 要查找的后缀字符序列
     * @return 如果seq以suffix结尾,返回true,否则返回false
     */
    default boolean endsWith(CharSequence seq, CharSequence suffix) {
        if (seq != null && suffix != null) {
            return CharSequenceUtils.regionMatches(seq, false, seq.length() - suffix.length(), suffix, 0, suffix.length());
        } else {
            return false;
        }
    }




}
