package com.virhuiai.OfficeUtils.Excel.obj;


import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * 双向映射数据结构
 * 用于存储Excel列索引和列名的对应关系
 * 实现了从列索引到列名和从列名到列索引的双向查找
 */
public class BiMap {
    // 双向映射
    private final Map<Integer, String> 索引到值映射;  // 存储列索引到列名的映射
    private final Map<String, Integer> 值到索引映射;  // 存储列名到列索引的映射

    /**
     * 构造函数
     * @param 索引到值映射 列索引到列名的映射
     * @param 值到索引映射 列名到列索引的映射
     */
    public BiMap(Map<Integer, String> 索引到值映射, Map<String, Integer> 值到索引映射) {
        // 使用不可修改的映射包装输入映射，确保BiMap实例的不可变性
        this.索引到值映射 = Collections.unmodifiableMap(new LinkedHashMap<>(索引到值映射));
        this.值到索引映射 = Collections.unmodifiableMap(new HashMap<>(值到索引映射));
    }

    /**
     * 获取索引到值的映射
     * @return 索引到值的不可修改映射
     */
    public Map<Integer, String> get索引到值映射() {
        return 索引到值映射;
    }

    /**
     * 获取值到索引的映射
     * @return 值到索引的不可修改映射
     */
    public Map<String, Integer> get值到索引映射() {
        return 值到索引映射;
    }

    /**
     * 根据列索引获取列名
     * @param 列索引 Excel的列索引
     * @return 对应的列名，如果不存在返回null
     */
    public String 获取列名(int 列索引) {
        return 索引到值映射.get(列索引);
    }

    /**
     * 根据列名获取列索引
     * @param 列名 Excel的列名
     * @return 对应的列索引，如果不存在返回null
     */
    public Integer 获取列索引(String 列名) {
        return 值到索引映射.get(列名);
    }

    /**
     * 判断是否包含指定的列索引
     * @param 列索引 要检查的列索引
     * @return 是否存在该列索引
     */
    public boolean 包含列索引(int 列索引) {
        return 索引到值映射.containsKey(列索引);
    }

    /**
     * 判断是否包含指定的列名
     * @param 列名 要检查的列名
     * @return 是否存在该列名
     */
    public boolean 包含列名(String 列名) {
        return 值到索引映射.containsKey(列名);
    }

    /**
     * 获取所有列索引
     * @return 列索引集合
     */
    public Set<Integer> 获取所有列索引() {
        return 索引到值映射.keySet();
    }

    /**
     * 获取所有列名
     * @return 列名集合
     */
    public Set<String> 获取所有列名() {
        return 值到索引映射.keySet();
    }

    /**
     * 获取映射大小
     * @return 映射中的条目数
     */
    public int 获取大小() {
        return 索引到值映射.size();
    }

    /**
     * 判断映射是否为空
     * @return 是否为空
     */
    public boolean 是否为空() {
        return 索引到值映射.isEmpty();
    }

    /**
     * 获取列索引和列名的映射关系
     * @return 映射关系的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BiMap{\n");
        索引到值映射.forEach((索引, 值) ->
                sb.append(String.format("  %d -> %s%n", 索引, 值)));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 创建BiMap构建器
     * @return 新的构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * BiMap构建器
     * 用于分步构建BiMap实例
     */
    public static class Builder {
        private final Map<Integer, String> 索引到值映射 = new LinkedHashMap<>();
        private final Map<String, Integer> 值到索引映射 = new HashMap<>();

        /**
         * 添加映射关系
         * @param 索引 列索引
         * @param 值 列名
         * @return 构建器本身，支持链式调用
         */
        public Builder 添加映射(int 索引, String 值) {
            索引到值映射.put(索引, 值);
            值到索引映射.put(值, 索引);
            return this;
        }

        /**
         * 构建BiMap实例
         * @return 新的BiMap实例
         */
        public BiMap build() {
            return new BiMap(索引到值映射, 值到索引映射);
        }
    }
}