package com.virhuiai;

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
}
