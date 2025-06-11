package TableStrategy;

import com.itextpdf.text.pdf.parser.*;
import com.virhuiai.GeometryUtils;
import com.virhuiai.PdfPathUtils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 路径处理监听器接口，提供路径处理的默认实现
 * 实现类需要提供路径存储的具体实现
 */
public interface PathProcessingListener extends ExtRenderListener {

    /**
     * 获取当前点
     */
    Point2D.Float getCurrentPoint();

    /**
     * 设置当前点
     */
    void setCurrentPoint(Point2D.Float point);

    /**
     * 获取路径起始点
     */
    Point2D.Float getPathStartPoint();

    /**
     * 设置路径起始点
     */
    void setPathStartPoint(Point2D.Float point);

    /**
     * 添加线段到线段列表
     */
    void addLine(Line2D line);

    /**
     * 添加多条线段到线段列表
     */
    void addLines(Collection<Line2D> lines);

    /**
     * 默认的路径修改处理实现
     * 处理各种路径操作：移动、画线、闭合路径、矩形
     */
    @Override
    default void modifyPath(PathConstructionRenderInfo renderInfo) {
        int operation = renderInfo.getOperation();
        // 转换为点坐标
        Point2D.Float point = PdfPathUtils.convertToPoint(renderInfo);

        // 处理线段操作
        if (PdfPathUtils.isMoveTo(operation) || PdfPathUtils.isLineTo(operation)) {
            if (PdfPathUtils.isMoveTo(operation)) {
                // MOVETO操作：设置新的路径起点
                setCurrentPoint(point);
                setPathStartPoint(point); // 记录路径起始点，用于闭合路径
            } else {
                // LINETO操作：创建线段
                Point2D.Float currentPoint = getCurrentPoint();
                if (currentPoint != null && point != null) {
                    Line2D line = new Line2D.Double(currentPoint, point);
                    addLine(line);
                    setCurrentPoint(point);
                }
            }
        }
        // 处理闭合路径操作
        else if (PdfPathUtils.isClosePath(operation)) {
            // 如果有当前点和路径起始点，创建闭合线段
            Point2D.Float currentPoint = getCurrentPoint();
            Point2D.Float pathStartPoint = getPathStartPoint();
            if (currentPoint != null && pathStartPoint != null &&
                    !GeometryUtils.isPointsEqual(currentPoint, pathStartPoint)) {
                Line2D line = new Line2D.Double(currentPoint, pathStartPoint);
                addLine(line);
            }
        }
        // 处理矩形操作（PDF中表格经常使用矩形绘制）
        else if (PdfPathUtils.isRectangle(operation)) {
            Line2D[] rectangleLines = PdfPathUtils.createRectangleLines(renderInfo);
            if (rectangleLines != null) {
                addLines(Arrays.asList(rectangleLines));
            }
        }
    }

    /**
     * 默认的路径渲染处理实现
     * 路径渲染完成后，重置当前点和路径起始点
     */
    @Override
    default Path renderPath(PathPaintingRenderInfo renderInfo) {
        // 路径渲染完成后，重置当前点和路径起始点
        setCurrentPoint(null);
        setPathStartPoint(null);
        return null;
    }
}
