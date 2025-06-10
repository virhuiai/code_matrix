package com.virhuiai;

/**
 * 并查集实现，用于分组相连的线段
 */
public class LineUnionFind {
    private int[] parent;  // 存储每个节点的父节点
    // parent[i]：节点i的父节点索引，如果parent[i] == i则i是根节点
    private int[] rank;    // 存储以该节点为根的树的高度（秩）
    //rank[i]：以节点i为根的树的高度，用于优化合并操作

    /**
     * 初始化时，每个元素都是独立的集合，自己是自己的根节点。
     * @param n
     */
    public LineUnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;    // 初始时每个节点都是自己的父节点
            rank[i] = 0;      // 初始高度为0
        }
    }

    /**
     * 查找操作（路径压缩优化）
     *
     这里使用了路径压缩优化：

     在查找根节点的过程中，将路径上的所有节点直接连接到根节点
     大大减少了后续查找的时间复杂度
     例如：查找路径 5→3→1→0，压缩后变成 5→0, 3→0, 1→0
     * @param x
     * @return
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);//// 路径压缩
        }
        return parent[x];
    }

    /**
     * 合并操作（按秩合并优化）
     *
     使用了按秩合并优化：

     总是将高度较小的树接到高度较大的树下面
     保持整体树的高度尽可能小
     避免树退化成链表
     *
     * @param x
     * @param y
     */
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;        // 低树接到高树下
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;        // 低树接到高树下
            } else {
                parent[rootY] = rootX;        // 高度相同时任选一个
                rank[rootX]++;                // 增加根节点的高度
            }
        }
    }
}
