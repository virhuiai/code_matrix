package com.virhuiai;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.PathConstructionRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * PDF路径处理工具类
 * 提供路径转换相关的功能
 */
public class PDFPathUtils {


    /**
     * 将路径构造信息转换为点坐标
     * @param renderInfo 路径构造信息
     * @return 转换后的点坐标，如果无法转换则返回null
     */
    public static Point2D.Float convertToPoint(PathConstructionRenderInfo renderInfo) {
        if (renderInfo == null) {
            return null;
        }

        List<Float> segments = renderInfo.getSegmentData();
        if (segments == null || segments.size() < 2) {
            return null;
        }

        float x = segments.get(0);
        float y = segments.get(1);

        // 转换为绝对坐标（需结合变换矩阵）
        Matrix ctm = renderInfo.getCtm();
        Vector vec = new Vector(x, y, 1).cross(ctm);

        return new Point2D.Float(vec.get(Vector.I1), vec.get(Vector.I2));
    }

    /**
     * 创建矩形的四条边
     * @param renderInfo 包含矩形信息的路径构造信息
     * @return 矩形的四条边组成的数组，如果无法创建则返回null
     */
    public static Line2D[] createRectangleLines(PathConstructionRenderInfo renderInfo) {
        if (renderInfo == null || renderInfo.getOperation() != PathConstructionRenderInfo.RECT) {
            return null;
        }

        List<Float> segments = renderInfo.getSegmentData();
        if (segments == null || segments.size() < 4) {
            return null;
        }

        float x = segments.get(0);
        float y = segments.get(1);
        float width = segments.get(2);
        float height = segments.get(3);

        // 转换矩形的四个顶点坐标
        Matrix ctm = renderInfo.getCtm();
        Vector bottomLeft = new Vector(x, y, 1).cross(ctm);
        Vector bottomRight = new Vector(x + width, y, 1).cross(ctm);
        Vector topRight = new Vector(x + width, y + height, 1).cross(ctm);
        Vector topLeft = new Vector(x, y + height, 1).cross(ctm);

        // 创建矩形的四条边
        Point2D.Float p1 = new Point2D.Float(bottomLeft.get(Vector.I1), bottomLeft.get(Vector.I2));
        Point2D.Float p2 = new Point2D.Float(bottomRight.get(Vector.I1), bottomRight.get(Vector.I2));
        Point2D.Float p3 = new Point2D.Float(topRight.get(Vector.I1), topRight.get(Vector.I2));
        Point2D.Float p4 = new Point2D.Float(topLeft.get(Vector.I1), topLeft.get(Vector.I2));

        return new Line2D[]{
                new Line2D.Double(p1, p2), // 底边
                new Line2D.Double(p2, p3), // 右边
                new Line2D.Double(p3, p4), // 顶边
                new Line2D.Double(p4, p1)  // 左边
        };
    }


    /**
     * 判断路径操作是否为移动操作
     * @param operation 路径操作类型
     * @return 是否为移动操作
     */
    public static boolean isMoveTo(int operation) {
        return operation == PathConstructionRenderInfo.MOVETO;
    }

    /**
     * 判断路径操作是否为画线操作
     * @param operation 路径操作类型
     * @return 是否为画线操作
     */
    public static boolean isLineTo(int operation) {
        return operation == PathConstructionRenderInfo.LINETO;
    }

    /**
     * 判断路径操作是否为闭合路径操作
     * @param operation 路径操作类型
     * @return 是否为闭合路径操作
     */
    public static boolean isClosePath(int operation) {
        return operation == PathConstructionRenderInfo.CLOSE;
    }

    /**
     * 判断路径操作是否为矩形操作
     * @param operation 路径操作类型
     * @return 是否为矩形操作
     */
    public static boolean isRectangle(int operation) {
        return operation == PathConstructionRenderInfo.RECT;
    }
}
