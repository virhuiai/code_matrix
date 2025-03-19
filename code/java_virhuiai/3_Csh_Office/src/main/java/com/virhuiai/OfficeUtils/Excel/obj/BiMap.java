package com.virhuiai.OfficeUtils.Excel.obj;

import java.util.*;

/**
 * 双向映射数据结构
 * 用于存储Excel列索引和列名的对应关系
 * 实现了从列索引到列名和从列名到列索引的双向查找
 *
 * 此类是不可变的，一旦创建就不能修改其内容
 */
public class BiMap {
    // 双向映射
    private final Map<Integer, String> indexToNameMap;  // 存储列索引到列名的映射
    private final Map<String, Integer> nameToIndexMap;  // 存储列名到列索引的映射

    /**
     * 构造函数
     *
     * @param indexToNameMap 列索引到列名的映射
     * @param nameToIndexMap 列名到列索引的映射
     */
    public BiMap(Map<Integer, String> indexToNameMap, Map<String, Integer> nameToIndexMap) {
        // 使用不可修改的映射包装输入映射，确保BiMap实例的不可变性
        this.indexToNameMap = Collections.unmodifiableMap(new LinkedHashMap<>(indexToNameMap));
        this.nameToIndexMap = Collections.unmodifiableMap(new HashMap<>(nameToIndexMap));
    }

    /**
     * 获取索引到列名的映射
     *
     * @return 索引到列名的不可修改映射
     */
    public Map<Integer, String> getIndexToNameMap() {
        return indexToNameMap;
    }

    /**
     * 获取列名到索引的映射
     *
     * @return 列名到索引的不可修改映射
     */
    public Map<String, Integer> getNameToIndexMap() {
        return nameToIndexMap;
    }

    /**
     * 根据列索引获取列名
     *
     * @param columnIndex Excel的列索引
     * @return 对应的列名，如果不存在返回null
     */
    public String getColumnName(int columnIndex) {
        return indexToNameMap.get(columnIndex);
    }

    /**
     * 根据列名获取列索引
     *
     * @param columnName Excel的列名
     * @return 对应的列索引，如果不存在返回null
     */
    public Integer getColumnIndex(String columnName) {
        return nameToIndexMap.get(columnName);
    }

    /**
     * 判断是否包含指定的列索引
     *
     * @param columnIndex 要检查的列索引
     * @return 是否存在该列索引
     */
    public boolean containsColumnIndex(int columnIndex) {
        return indexToNameMap.containsKey(columnIndex);
    }

    /**
     * 判断是否包含指定的列名
     *
     * @param columnName 要检查的列名
     * @return 是否存在该列名
     */
    public boolean containsColumnName(String columnName) {
        return nameToIndexMap.containsKey(columnName);
    }

    /**
     * 获取所有列索引
     *
     * @return 列索引集合
     */
    public Set<Integer> getAllColumnIndices() {
        return indexToNameMap.keySet();
    }

    /**
     * 获取所有列名
     *
     * @return 列名集合
     */
    public Set<String> getAllColumnNames() {
        return nameToIndexMap.keySet();
    }

    /**
     * 获取映射大小
     *
     * @return 映射中的条目数
     */
    public int getSize() {
        return indexToNameMap.size();
    }

    /**
     * 判断映射是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return indexToNameMap.isEmpty();
    }

    /**
     * 获取列索引和列名的映射关系的字符串表示
     *
     * @return 映射关系的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BiMap{\n");
        indexToNameMap.forEach((index, name) ->
                sb.append(String.format("  %d -> %s%n", index, name)));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 创建BiMap构建器
     *
     * @return 新的构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * BiMap构建器
     * 用于分步构建BiMap实例
     * 提供流式API支持链式调用
     */
    public static class Builder {
        private final Map<Integer, String> indexToNameMap = new LinkedHashMap<>();
        private final Map<String, Integer> nameToIndexMap = new HashMap<>();

        /**
         * 添加映射关系
         *
         * @param index 列索引
         * @param name 列名
         * @return 构建器本身，支持链式调用
         */
        public Builder addMapping(int index, String name) {
            indexToNameMap.put(index, name);
            nameToIndexMap.put(name, index);
            return this;
        }

        /**
         * 构建BiMap实例
         *
         * @return 新的BiMap实例
         */
        public BiMap build() {
            return new BiMap(indexToNameMap, nameToIndexMap);
        }
    }
}