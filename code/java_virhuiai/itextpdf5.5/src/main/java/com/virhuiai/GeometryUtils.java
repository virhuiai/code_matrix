package com.virhuiai;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

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




}
