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

    ///////// todo 以下代码待。。。 根据您的数据量大小，可以选择使用普通版本（数据量较小，上面）或优化版本（数据量较大，下面）。

    /**
     * 使用网格索引高效添加点
     * <p>
     * 该方法通过空间网格索引来优化点的去重操作，避免了暴力O(n²)的查找复杂度
     *
     * @param points    存储唯一点的列表，新的点将被添加到这个列表中
     * @param grid      网格索引映射表，key是网格坐标字符串"x,y"，value是该网格内的点列表
     * @param newPoint  待添加的新点
     * @param tolerance 容差值，两点距离小于此值时被认为是同一个点
     * @param gridSize  网格单元的大小，通常设为tolerance的2倍
     */
    private static void addPointWithGrid(List<Point2D> points, Map<String, List<Point2D>> grid,
                                         Point2D newPoint, double tolerance, double gridSize) {
        // 计算新点所在的网格坐标
        // 通过将实际坐标除以网格大小并取整，得到网格索引
        int gridX = (int) (newPoint.getX() / gridSize);
        int gridY = (int) (newPoint.getY() / gridSize);

        // 检查当前网格和相邻网格中的点
        // 为什么要检查相邻网格？
        // 因为一个点可能与相邻网格边界附近的点距离很近（小于tolerance）
        // 例如：如果点A在网格(0,0)的右边界，点B在网格(1,0)的左边界，
        // 它们可能距离很近但在不同网格中
        boolean foundSimilar = false;

        // 遍历3x3的网格区域（当前网格及其8个相邻网格）
        for (int dx = -1; dx <= 1 && !foundSimilar; dx++) {
            for (int dy = -1; dy <= 1 && !foundSimilar; dy++) {
                // 构造网格键值，格式为"x,y"
                String gridKey = (gridX + dx) + "," + (gridY + dy);
                // 获取该网格中的所有点
                List<Point2D> cellPoints = grid.get(gridKey);

                if (cellPoints != null) {
                    // 遍历网格中的每个点，检查是否与新点足够接近
                    for (Point2D existingPoint : cellPoints) {
                        // 使用欧几里得距离判断两点是否相似
                        if (existingPoint.distance(newPoint) <= tolerance) {
                            foundSimilar = true;
                            break;  // 找到相似点，停止搜索
                        }
                    }
                }
            }
        }

        // 如果在所有相关网格中都没有找到相似的点，则添加新点
        if (!foundSimilar) {
            // 1. 将点添加到结果列表
            points.add(newPoint);

            // 2. 将点添加到对应的网格索引中
            String gridKey = gridX + "," + gridY;
            // computeIfAbsent: 如果键不存在，则创建新的ArrayList
            // 如果键已存在，则直接返回对应的列表
            grid.computeIfAbsent(gridKey, k -> new ArrayList<>()).add(newPoint);
        }
    }


    /**
     * 高效版本：使用网格索引优化查找性能
     * <p>
     * 该方法将线段列表转换为去重后的点集合，使用网格索引来加速去重过程
     * <p>
     * 算法复杂度分析：
     * - 传统方法：O(n²) - 每个新点都要与所有已存在的点比较
     * - 网格索引方法：平均O(n) - 每个点只需与其所在网格及相邻网格中的点比较
     * <p>
     * 使用示例：
     * Set<Point2D> points3 = fromLineListToPointSetOptimized(lineList, 1.0);
     *
     *

     **算法原理详解：**

     1. **网格索引的核心思想**：
     - 将二维平面划分成规则的网格
     - 每个点根据其坐标被分配到特定的网格单元中
     - 查找相似点时，只需要搜索有限的网格区域

     2. **为什么网格大小是tolerance的2倍**：
     - 如果网格大小等于tolerance，可能出现两个距离小于tolerance的点分别位于不相邻的网格中
     - 设为2倍可以保证：任意两个距离小于tolerance的点，最多相隔一个网格

     3. **性能优化效果**：
     - 传统方法：每个新点需要与所有已存在的点比较，复杂度O(n²)
     - 网格方法：每个点只需与最多9个网格中的点比较，平均复杂度O(n)
     - 特别适合处理大量点的场景

     4. **适用场景**：
     - PDF表格解析中，需要从大量线段中提取顶点
     - 需要去除近似重复的点（由于精度问题产生的）
     - 点的分布相对均匀的情况下效果最佳
     *
     * @param lineList  输入的线段列表
     * @param tolerance 点去重的容差值，两点距离小于此值时被认为是同一个点
     * @return 去重后的点集合
     */
    public static List<Point2D> fromLineListToPointListOptimized(List<Line2D> lineList, double tolerance) {
        // 处理空输入
        if (lineList == null || lineList.isEmpty()) {
            return new ArrayList<>();
        }

        // 存储去重后的唯一点列表
        List<Point2D> uniquePoints = new ArrayList<>();

        // 网格大小设为容差的2倍
        // 这样可以确保：如果两个点的距离小于tolerance，
        // 它们要么在同一个网格中，要么在相邻的网格中
        double gridSize = tolerance * 2;

        // 网格索引映射表
        // key: 网格坐标字符串 "x,y"
        // value: 该网格内的所有点
        Map<String, List<Point2D>> grid = new HashMap<>();

        // 遍历所有线段
        for (Line2D line : lineList) {
            if (line == null) continue;

            // 提取线段的两个端点
            Point2D p1 = new Point2D.Double(line.getX1(), line.getY1());
            Point2D p2 = new Point2D.Double(line.getX2(), line.getY2());

            // 使用网格索引方法添加两个端点
            // 如果点已存在（在tolerance范围内有相似点），则不会重复添加
            addPointWithGrid(uniquePoints, grid, p1, tolerance, gridSize);
            addPointWithGrid(uniquePoints, grid, p2, tolerance, gridSize);
        }

        return uniquePoints;
    }




}
