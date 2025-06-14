package com.virhuiai.string;



/**
 // CharSequence相关的工具类，提供了对CharSequence的各种操作方法
 // 将这个类声明为final，防止被继承
 */
public final class CharSequenceUtils {
    // 表示未找到的常量值
    private static final int NOT_FOUND = -1;
    // toString方法的字符限制，用于性能优化
    // 当字符串长度小于等于16时，使用特定的优化策略
    static final int TO_STRING_LIMIT = 16;
    // 1114111 是Unicode的最大值
    static final int MAX_CODE_POINT = 0x10FFFF;

    // 空字符数组常量，避免重复创建空数组
    public static  final char[] EMPTY_CHAR_ARRAY = new char[0];

    /**
     * 检查从指定位置开始的字符序列是否与搜索字符序列匹配
     * 该方法从第二个字符开始比较（索引1），同时从两端向中间比较以提高效率
     *
     * @param cs 源字符序列
     * @param searchChar 要搜索的字符序列
     * @param len2 搜索字符序列的长度
     * @param start1 源字符序列的起始位置
     * @return 如果匹配返回true，否则返回false
     */
    private static boolean checkLaterThan1(CharSequence cs, CharSequence searchChar, int len2, int start1) {
        // 从索引1开始（跳过第一个字符，因为调用前已经检查过）
        int i = 1;

        // 从两端向中间比较，提高比较效率
        for(int j = len2 - 1; i <= j; --j) {
            // 同时比较前后两个位置的字符
            if (cs.charAt(start1 + i) != searchChar.charAt(i) || cs.charAt(start1 + j) != searchChar.charAt(j)) {
                return false;
            }

            ++i;
        }

        return true;
    }

    /**
     * 在字符序列中查找指定字符序列的位置
     * 针对不同的CharSequence实现类型进行优化处理
     *
     * @param cs 源字符序列
     * @param searchChar 要搜索的字符序列
     * @param start 搜索的起始位置
     * @return 找到的位置索引，如果未找到返回-1
     */
    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        // 针对不同类型的CharSequence使用各自的indexOf方法，提高性能
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder)cs).indexOf(searchChar.toString(), start);
        } else {
            // TODO: 可以考虑使用if-else if结构替代嵌套的三元运算符，提高可读性
            return cs instanceof StringBuffer ? ((StringBuffer)cs).indexOf(searchChar.toString(), start) : cs.toString().indexOf(searchChar.toString(), start);
        }
    }

    /**
     * 在字符序列中查找指定字符（支持Unicode补充字符）
     *
     * @param cs 源字符序列
     * @param searchChar 要搜索的字符（int类型以支持Unicode补充字符）
     * @param start 搜索的起始位置
     * @return 找到的位置索引，如果未找到返回-1
     */
    static int indexOf(CharSequence cs, int searchChar, int start) {
        // 如果是String类型，直接使用String的indexOf方法
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar, start);
        } else {
            int sz = cs.length();
            // 处理负数起始位置
            if (start < 0) {
                start = 0;
            }

            // 基本多文种平面（BMP）中的字符（U+0000到U+FFFF）
            if (searchChar < 65536) {
                // 简单的逐字符搜索
                for(int i = start; i < sz; ++i) {
                    if (cs.charAt(i) == searchChar) {
                        return i;
                    }
                }

                return -1;
            } else {
                // 补充字符（U+10000到U+10FFFF）
                if (searchChar <= MAX_CODE_POINT) {
                    // 将补充字符转换为代理对（高位代理和低位代理）
                    char[] chars = Character.toChars(searchChar);

                    // 搜索代理对
                    for(int i = start; i < sz - 1; ++i) {
                        char high = cs.charAt(i);
                        char low = cs.charAt(i + 1);
                        if (high == chars[0] && low == chars[1]) {
                            return i;
                        }
                    }
                }

                return -1;
            }
        }
    }

    /**
     * 在字符序列中反向查找指定字符序列的位置
     *
     * @param cs 源字符序列
     * @param searchChar 要搜索的字符序列
     * @param start 搜索的起始位置（从此位置向前搜索）
     * @return 找到的位置索引，如果未找到返回-1
     */
    static int lastIndexOf(CharSequence cs, CharSequence searchChar, int start) {
        // 空值检查
        if (searchChar != null && cs != null) {
            // 如果searchChar是String类型，针对不同的cs类型使用优化的方法
            if (searchChar instanceof String) {
                if (cs instanceof String) {
                    return ((String)cs).lastIndexOf((String)searchChar, start);
                }

                if (cs instanceof StringBuilder) {
                    return ((StringBuilder)cs).lastIndexOf((String)searchChar, start);
                }

                if (cs instanceof StringBuffer) {
                    return ((StringBuffer)cs).lastIndexOf((String)searchChar, start);
                }
            }

            int len1 = cs.length();
            int len2 = searchChar.length();
            // 调整起始位置，确保不超出范围
            if (start > len1) {
                start = len1;
            }

            // 基本的边界条件检查
            if (start >= 0 && len2 <= len1) {
                // 空字符串的特殊处理
                if (len2 == 0) {
                    return start;
                } else {
                    // 对于短字符串（长度<=16），使用原生方法可能更高效
                    if (len2 <= TO_STRING_LIMIT) {
                        if (cs instanceof String) {
                            return ((String)cs).lastIndexOf(searchChar.toString(), start);
                        }

                        if (cs instanceof StringBuilder) {
                            return ((StringBuilder)cs).lastIndexOf(searchChar.toString(), start);
                        }

                        if (cs instanceof StringBuffer) {
                            return ((StringBuffer)cs).lastIndexOf(searchChar.toString(), start);
                        }
                    }

                    // 确保搜索不会越界
                    if (start + len2 > len1) {
                        start = len1 - len2;
                    }

                    // 获取搜索字符串的第一个字符，用于快速匹配
                    char char0 = searchChar.charAt(0);
                    int i = start;

                    // 从后向前搜索
                    do {
                        // 先找到第一个字符匹配的位置
                        while(cs.charAt(i) == char0) {
                            // 检查剩余字符是否匹配
                            if (checkLaterThan1(cs, searchChar, len2, i)) {
                                return i;
                            }

                            --i;
                            if (i < 0) {
                                return -1;
                            }
                        }

                        --i;
                    } while(i >= 0);

                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * 在字符序列中反向查找指定字符（支持Unicode补充字符）
     *
     * @param cs 源字符序列
     * @param searchChar 要搜索的字符（int类型以支持Unicode补充字符）
     * @param start 搜索的起始位置（从此位置向前搜索）
     * @return 找到的位置索引，如果未找到返回-1
     */
    static int lastIndexOf(CharSequence cs, int searchChar, int start) {
        // 如果是String类型，直接使用String的lastIndexOf方法
        if (cs instanceof String) {
            return ((String)cs).lastIndexOf(searchChar, start);
        } else {
            int sz = cs.length();
            // 处理负数起始位置
            if (start < 0) {
                return -1;
            } else {
                // 调整起始位置，确保不超出范围
                if (start >= sz) {
                    start = sz - 1;
                }

                // 基本多文种平面（BMP）中的字符
                if (searchChar < 65536) {
                    // 简单的反向逐字符搜索
                    for(int i = start; i >= 0; --i) {
                        if (cs.charAt(i) == searchChar) {
                            return i;
                        }
                    }

                    return -1;
                } else {
                    // 补充字符（U+10000到U+10FFFF）
                    // T 1114111是Unicode的最大值，定义为常量 MAX_CODE_POINT = 0x10FFFF
                    if (searchChar <= MAX_CODE_POINT) {
                        // 将补充字符转换为代理对
                        char[] chars = Character.toChars(searchChar);
                        // 如果起始位置是最后一个字符，无法匹配代理对
                        if (start == sz - 1) {
                            return -1;
                        }

                        // 反向搜索代理对
                        for(int i = start; i >= 0; --i) {
                            char high = cs.charAt(i);
                            char low = cs.charAt(i + 1);
                            if (chars[0] == high && chars[1] == low) {
                                return i;
                            }
                        }
                    }

                    return -1;
                }
            }
        }
    }

    /**
     * 比较两个字符序列的指定区域是否相等
     * 支持大小写敏感和不敏感的比较
     *
     * @param cs 第一个字符序列
     * @param ignoreCase 是否忽略大小写
     * @param thisStart cs的起始位置
     * @param substring 第二个字符序列
     * @param start substring的起始位置
     * @param length 要比较的长度
     * @return 如果指定区域相等返回true，否则返回false
     */
    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        // 如果两个都是String类型，使用String的regionMatches方法
        if (cs instanceof String && substring instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        } else {
            // 初始化索引和临时长度变量
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;
            // 计算两个序列的剩余长度
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length() - start;
            // 参数有效性检查
            if (thisStart >= 0 && start >= 0 && length >= 0) {
                // 确保两个序列都有足够的字符进行比较
                if (srcLen >= length && otherLen >= length) {
                    // 逐字符比较
                    while(tmpLen-- > 0) {
                        char c1 = cs.charAt(index1++);
                        char c2 = substring.charAt(index2++);
                        // 如果字符不相等
                        if (c1 != c2) {
                            // 如果不忽略大小写，直接返回false
                            if (!ignoreCase) {
                                return false;
                            }

                            // 忽略大小写的比较
                            // 先转换为大写比较
                            char u1 = Character.toUpperCase(c1);
                            char u2 = Character.toUpperCase(c2);
                            // 如果大写不相等，再比较小写
                            // TODO: 对于某些特殊的Unicode字符，这种比较方式可能不够准确
                            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                                return false;
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 获取字符序列从指定位置到末尾的子序列
     *
     * @param cs 源字符序列
     * @param start 起始位置
     * @return 子序列，如果cs为null则返回null
     */
    public static  CharSequence subSequence(CharSequence cs, int start) {
        // 空值检查，如果为null直接返回null
        return cs == null ? null : cs.subSequence(start, cs.length());
    }

    /**
     * 将字符序列转换为字符数组
     *
     * @param source 源字符序列
     * @return 字符数组，如果源为空则返回空数组
     */
    public static  char[] toCharArray(CharSequence source) {
        // 获取长度（使用StringUtils处理null的情况）
        int len = StringUtils.INSTANCE.length(source);
        // 长度为0时返回预定义的空数组，避免创建新对象
        if (len == 0) {
            return EMPTY_CHAR_ARRAY;
        } else if (source instanceof String) {
            // 如果是String类型，直接使用String的toCharArray方法
            return ((String)source).toCharArray();
        } else {
            // 其他类型的CharSequence，手动创建数组并逐字符复制
            char[] array = new char[len];

            for(int i = 0; i < len; ++i) {
                array[i] = source.charAt(i);
            }

            return array;
        }
    }

    /**
     *
     防止实例化: 构造函数应该声明为private而不是public，或者抛出异常防止实例化
     该类设计为工具类，只包含静态方法
     *
     *
     */
    private CharSequenceUtils() {
    }
}
