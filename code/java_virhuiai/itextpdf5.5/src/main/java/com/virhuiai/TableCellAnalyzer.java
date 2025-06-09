package com.virhuiai;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 遍历线段、分组相连或相交的线段，并分析表格单元格的
 */
public class TableCellAnalyzer {
    // 容差值，用于判断点是否相近
    private static final double TOLERANCE = 1e-6;

    // 主方法：分析线段并提取表格单元格
    public static List<List<Rectangle2D>> analyzeTableCells(List<Line2D> lineList) {
        // 1. 将相连或相交的线段分组 ok
        List<List<Line2D>> groupedLines = groupConnectedLines(lineList);

        // 2. 对每个分组分析表格单元格
        List<List<Rectangle2D>> allCells = new ArrayList<>();
        for (List<Line2D> group : groupedLines) {
            List<Rectangle2D> cells = extractCellsFromLineGroup(group);
            if (!cells.isEmpty()) {
                allCells.add(cells);
            }
        }

        return allCells;
    }

    // 将相连或相交的线段分组
    private static List<List<Line2D>> groupConnectedLines(List<Line2D> lineList) {
        int n = lineList.size();
        UnionFind uf = new UnionFind(n);

        // 检查每对线段是否相连或相交
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnectedOrIntersecting(lineList.get(i), lineList.get(j))) {
                    uf.union(i, j);
                }
            }
        }

        // 根据并查集结果分组
        Map<Integer, List<Line2D>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            //如果groups中已存在键root，直接返回对应的List
            //如果不存在键root，则创建一个新的ArrayList并放入map中，然后返回这个新列表
            //k -> new ArrayList<>()是Lambda表达式，等价于创建新的空列表
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(lineList.get(i));
        }

        return new ArrayList<>(groups.values());
    }

    // 判断两条线段是否相连或相交
    private static boolean isConnectedOrIntersecting(Line2D line1, Line2D line2) {
        // 检查是否相交
        if (line1.intersectsLine(line2)) {
            return true;
        }

        // 检查端点是否相连
        Point2D p1Start = line1.getP1();
        Point2D p1End = line1.getP2();
        Point2D p2Start = line2.getP1();
        Point2D p2End = line2.getP2();

        return isPointsClose(p1Start, p2Start) || isPointsClose(p1Start, p2End) ||
                isPointsClose(p1End, p2Start) || isPointsClose(p1End, p2End);
    }

    // 判断两个点是否足够接近
    private static boolean isPointsClose(Point2D p1, Point2D p2) {
        return p1.distance(p2) < TOLERANCE;
    }

    // 从一组线段中提取表格单元格
    private static List<Rectangle2D> extractCellsFromLineGroup(List<Line2D> lines) {
        // 分离水平线和垂直线
        List<Line2D> horizontalLines = new ArrayList<>();
        List<Line2D> verticalLines = new ArrayList<>();

        for (Line2D line : lines) {
            if (isHorizontal(line)) {
                horizontalLines.add(line);
            } else if (isVertical(line)) {
                verticalLines.add(line);
            }
        }

        // 按坐标排序
        horizontalLines.sort((a, b) -> Double.compare(a.getY1(), b.getY1()));
        verticalLines.sort((a, b) -> Double.compare(a.getX1(), b.getX1()));

        // 查找所有可能的矩形单元格
        List<Rectangle2D> cells = new ArrayList<>();

        for (int i = 0; i < horizontalLines.size() - 1; i++) {
            for (int j = 0; j < verticalLines.size() - 1; j++) {
                Rectangle2D cell = findRectangle(
                        horizontalLines.get(i), horizontalLines.get(i + 1),
                        verticalLines.get(j), verticalLines.get(j + 1)
                );

                if (cell != null) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    // 判断线段是否水平
    private static boolean isHorizontal(Line2D line) {
        return Math.abs(line.getY1() - line.getY2()) < TOLERANCE;
    }

    // 判断线段是否垂直
    private static boolean isVertical(Line2D line) {
        return Math.abs(line.getX1() - line.getX2()) < TOLERANCE;
    }

    // 根据四条边查找矩形
    private static Rectangle2D findRectangle(Line2D top, Line2D bottom,
                                             Line2D left, Line2D right) {
        // 获取交点
        Point2D topLeft = getIntersection(top, left);
        Point2D topRight = getIntersection(top, right);
        Point2D bottomLeft = getIntersection(bottom, left);
        Point2D bottomRight = getIntersection(bottom, right);

        // 验证所有交点都存在
        if (topLeft == null || topRight == null ||
                bottomLeft == null || bottomRight == null) {
            return null;
        }

        // 创建矩形
        double x = Math.min(topLeft.getX(), bottomLeft.getX());
        double y = Math.min(topLeft.getY(), topRight.getY());
        double width = Math.abs(topRight.getX() - topLeft.getX());
        double height = Math.abs(bottomLeft.getY() - topLeft.getY());

        if (width > TOLERANCE && height > TOLERANCE) {
            return new Rectangle2D.Double(x, y, width, height);
        }

        return null;
    }

    // 获取两条线段的交点
    private static Point2D getIntersection(Line2D line1, Line2D line2) {
        // 使用线段参数方程计算交点
        double x1 = line1.getX1(), y1 = line1.getY1();
        double x2 = line1.getX2(), y2 = line1.getY2();
        double x3 = line2.getX1(), y3 = line2.getY1();
        double x4 = line2.getX2(), y4 = line2.getY2();

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (Math.abs(denom) < TOLERANCE) {
            return null; // 平行线
        }

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            double x = x1 + t * (x2 - x1);
            double y = y1 + t * (y2 - y1);
            return new Point2D.Double(x, y);
        }

        return null;
    }

    // 并查集实现
    static class UnionFind {
        private int[] parent;  // 存储每个节点的父节点
        // parent[i]：节点i的父节点索引，如果parent[i] == i则i是根节点
        private int[] rank;    // 存储以该节点为根的树的高度（秩）
        //rank[i]：以节点i为根的树的高度，用于优化合并操作

        /**
         * 初始化时，每个元素都是独立的集合，自己是自己的根节点。
         * @param n
         */
        public UnionFind(int n) {
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

    // 使用示例
    public static void main(String[] args) {
        // 获取线段列表
//        List<Line2D> lineListList = strategy.getCurrentLineList();

//        // 分析表格单元格
//        List<List<Rectangle2D>> cellGroups = analyzeTableCells(lineListList);
//
//        // 输出结果
//        for (int i = 0; i < cellGroups.size(); i++) {
//            System.out.println("表格组 " + (i + 1) + ":");
//            List<Rectangle2D> cells = cellGroups.get(i);
//
//            for (int j = 0; j < cells.size(); j++) {
//                Rectangle2D cell = cells.get(j);
//                System.out.println("  单元格 " + (j + 1) + " 的四个坐标点:");
//                System.out.println("    左上角: (" + cell.getX() + ", " + cell.getY() + ")");
//                System.out.println("    右上角: (" + (cell.getX() + cell.getWidth()) + ", " + cell.getY() + ")");
//                System.out.println("    左下角: (" + cell.getX() + ", " + (cell.getY() + cell.getHeight()) + ")");
//                System.out.println("    右下角: (" + (cell.getX() + cell.getWidth()) + ", " + (cell.getY() + cell.getHeight()) + ")");
//            }
//        }
    }
}
