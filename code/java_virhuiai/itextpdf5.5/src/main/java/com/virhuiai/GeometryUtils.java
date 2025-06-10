package com.virhuiai;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 几何运算工具类
 * 提供点、线等几何对象的操作方法
 */
public class GeometryUtils {
  /**
     * 默认的浮点数比较精度
     */
    private static final double DEFAULT_EPSILON = 1e-6;

    /**
     * 判断两个点是否相等（在给定的精度范围内）
     * @param p1 第一个点
     * @param p2 第二个点
     * @param epsilon 允许的误差范围
     * @return 如果两个点在误差范围内相等，返回true；否则返回false
     */
    public static boolean isPointsEqual(Point2D p1, Point2D p2, double epsilon) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        // 分别比较x坐标和y坐标的差值是否都小于误差范围
        // 使用Math.abs获取绝对值，确保差值为正数
        return Math.abs(p1.getX() - p2.getX()) < epsilon &&
                Math.abs(p1.getY() - p2.getY()) < epsilon;
    }

    /**
     * 判断两个点是否相等（使用默认精度）
     * GeometryUtils.isPointsEqual
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 如果两个点在误差范围内相等，返回true；否则返回false
     */
    public static boolean isPointsEqual(Point2D p1, Point2D p2) {
        return isPointsEqual(p1, p2, DEFAULT_EPSILON);
    }

    /////////////

    /**
     * 判断两条线段是否相连（即是否共享端点）
     * @param line1 第一条线段
     * @param line2 第二条线段
     * @param epsilon 允许的误差范围
     * @return 如果两条线段共享至少一个端点，返回true；否则返回false
     */
    public static boolean isConnected(Line2D line1, Line2D line2, double epsilon) {
        if (line1 == null || line2 == null) {
            return false;
        }

        // 获取第一条线的起点和终点
        Point2D p1Start = line1.getP1();  // 线段1的起点
        Point2D p1End = line1.getP2();    // 线段1的终点

        // 获取第二条线的起点和终点
        Point2D p2Start = line2.getP1();  // 线段2的起点
        Point2D p2End = line2.getP2();    // 线段2的终点

        // 检查两条线段是否有任意一个端点重合
        // 四种可能的情况：
        // 1. 线段1的起点 = 线段2的起点
        // 2. 线段1的起点 = 线段2的终点
        // 3. 线段1的终点 = 线段2的起点
        // 4. 线段1的终点 = 线段2的终点
        return isPointsEqual(p1Start, p2Start, epsilon) ||
                isPointsEqual(p1Start, p2End, epsilon) ||
                isPointsEqual(p1End, p2Start, epsilon) ||
                isPointsEqual(p1End, p2End, epsilon);
    }


    /**
     * 判断两条线段是否相连（即是否共享端点）
     * GeometryUtils.isConnected
     * @param line1 第一条线段
     * @param line2 第二条线段
     * @return 如果两条线段共享至少一个端点，返回true；否则返回false
     */
    public static boolean isConnected(Line2D line1, Line2D line2) {
        return isConnected(line1, line2, DEFAULT_EPSILON);
    }

    //////////


    /**
     * 在容差范围内添加点，如果已存在相近的点则不添加
     * @param points 现有点集合
     * @param newPoint 要添加的新点
     * @param tolerance 容差范围
     */
    private static void addPointWithTolerance(List<Point2D> points, Point2D newPoint, double tolerance) {
        boolean foundSimilar = false;

        for (Point2D existingPoint : points) {
            if (existingPoint.distance(newPoint) <= tolerance) {
                foundSimilar = true;
                break;
            }
        }

        if (!foundSimilar) {
            points.add(newPoint);
        }
    }


    /**
     * 从线段列表生成点的集合，支持容差范围内的点合并
     * @param lineList 线段列表
     * @param tolerance 容差范围，默认1.0
     * @return 包含所有端点的Point2D集合
     */
    public static List<Point2D> fromLineListToPointSet(List<Line2D> lineList, double tolerance) {
        if (lineList == null || lineList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point2D> uniquePoints = new ArrayList<>();

        for (Line2D line : lineList) {
            if (line == null) continue;

            Point2D p1 = new Point2D.Double(line.getX1(), line.getY1());
            Point2D p2 = new Point2D.Double(line.getX2(), line.getY2());

            addPointWithTolerance(uniquePoints, p1, tolerance);
            addPointWithTolerance(uniquePoints, p2, tolerance);
        }

        return uniquePoints;
    }


    /**
     * 从线段列表生成点的集合，支持容差范围内的点合并
     * 重载方法，使用默认容差1.0
     */
    public static List<Point2D> fromLineListToPointSet(List<Line2D> lineList) {
        return fromLineListToPointSet(lineList, 1.0);
    }

    ///////// todo 以下代码待。。。

    /**
     * 使用网格索引高效添加点
     */
    private static void addPointWithGrid(List<Point2D> points, Map<String, List<Point2D>> grid,
                                         Point2D newPoint, double tolerance, double gridSize) {
        int gridX = (int) (newPoint.getX() / gridSize);
        int gridY = (int) (newPoint.getY() / gridSize);

        // 检查当前网格和相邻网格中的点
        boolean foundSimilar = false;
        for (int dx = -1; dx <= 1 && !foundSimilar; dx++) {
            for (int dy = -1; dy <= 1 && !foundSimilar; dy++) {
                String gridKey = (gridX + dx) + "," + (gridY + dy);
                List<Point2D> cellPoints = grid.get(gridKey);

                if (cellPoints != null) {
                    for (Point2D existingPoint : cellPoints) {
                        if (existingPoint.distance(newPoint) <= tolerance) {
                            foundSimilar = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!foundSimilar) {
            points.add(newPoint);
            String gridKey = gridX + "," + gridY;
            grid.computeIfAbsent(gridKey, k -> new ArrayList<>()).add(newPoint);
        }
    }

    /**
     * 高效版本：使用网格索引优化查找性能
     * 使用示例：
     *
     * Set<Point2D> points3 = fromLineListToPointSetOptimized(lineList, 1.0);
     */
    public static Set<Point2D> fromLineListToPointSetOptimized(List<Line2D> lineList, double tolerance) {
        if (lineList == null || lineList.isEmpty()) {
            return new HashSet<>();
        }

        List<Point2D> uniquePoints = new ArrayList<>();
        double gridSize = tolerance * 2; // 网格大小设为容差的2倍
        Map<String, List<Point2D>> grid = new HashMap<>();

        for (Line2D line : lineList) {
            if (line == null) continue;

            Point2D p1 = new Point2D.Double(line.getX1(), line.getY1());
            Point2D p2 = new Point2D.Double(line.getX2(), line.getY2());

            addPointWithGrid(uniquePoints, grid, p1, tolerance, gridSize);
            addPointWithGrid(uniquePoints, grid, p2, tolerance, gridSize);
        }

        return new HashSet<>(uniquePoints);
    }





}
