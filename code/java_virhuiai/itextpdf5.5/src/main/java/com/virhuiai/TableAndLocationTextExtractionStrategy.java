package com.virhuiai;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.parser.*;
import com.itextpdf.text.pdf.parser.Vector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 这个类用于从PDF中提取表格和文本位置信息
 */
public class TableAndLocationTextExtractionStrategy extends LocationTextExtractionStrategy implements ExtRenderListener {

    /**
     * 当前点
     */
    private Point2D.Float currentPoint = null;

    /**
     * 记录路径起始点，用于闭合路径
     */
    private Point2D.Float pathStartPoint = null;

//    /**
//     * 点来源记录
//     */
//    private List<PathConstructionRenderInfo> pointSrcList = new ArrayList<>();

    /**
     * 线记录
     */
    private List<Line2D> currentLineList = new ArrayList<>();

    /**
     * getCurrentLineList
     * @return
     */
    public List<Line2D> getCurrentLineList() {
        return currentLineList;
    }













    /**
     * Called when the current path is being modified. E.g. new segment is being added,
     * new subpath is being started etc.
     *
     * @param renderInfo Contains information about the path segment being added to the current path.
     */
    @Override
    public void modifyPath(PathConstructionRenderInfo renderInfo) {
        int operation = renderInfo.getOperation();

        // 获取线段端点坐标（相对坐标）
        List<Float> segments = renderInfo.getSegmentData();
        float x = segments.get(0);
        float y = segments.get(1);

        // 转换为绝对坐标（需结合变换矩阵）
        Matrix ctm = renderInfo.getCtm();
        Vector vec = new Vector(x, y, 1).cross(ctm);
        Point2D.Float point = new Point2D.Float(vec.get(Vector.I1),
                vec.get(Vector.I2));

        // 处理线段操作
        if (operation == PathConstructionRenderInfo.MOVETO ||
                operation == PathConstructionRenderInfo.LINETO) {



            if(operation == PathConstructionRenderInfo.MOVETO){
                // MOVETO操作：设置新的路径起点
                this.currentPoint = point;
                this.pathStartPoint = point; // 记录路径起始点，用于闭合路径
            }else{
                // LINETO操作：创建线段
                if(null != currentPoint){
                    Line2D line = new Line2D.Double(currentPoint, point);
                    currentLineList.add(line);
                    this.currentPoint = point;
                }
            }
        }
        // 处理闭合路径操作
        else if (operation == PathConstructionRenderInfo.CLOSE) {
            // 如果有当前点和路径起始点，创建闭合线段
            if (point != null && pathStartPoint != null &&
                    !GeometryUtils.isPointsEqual(point, pathStartPoint, 1e-6)) {
                Line2D line = new Line2D.Double(point, pathStartPoint);
                currentLineList.add(line);
                currentPoint = point;
            }
        }
        // 处理矩形操作（PDF中表格经常使用矩形绘制）
        else if (operation == PathConstructionRenderInfo.RECT) {
            segments = renderInfo.getSegmentData();
            if (segments.size() >= 4) {
                x = segments.get(0);
                y = segments.get(1);
                float width = segments.get(2);
                float height = segments.get(3);

                // 转换矩形的四个顶点坐标
                ctm = renderInfo.getCtm();
                Vector bottomLeft = new Vector(x, y, 1).cross(ctm);
                Vector bottomRight = new Vector(x + width, y, 1).cross(ctm);
                Vector topRight = new Vector(x + width, y + height, 1).cross(ctm);
                Vector topLeft = new Vector(x, y + height, 1).cross(ctm);

                // 创建矩形的四条边
                Point2D.Float p1 = new Point2D.Float(bottomLeft.get(Vector.I1), bottomLeft.get(Vector.I2));
                Point2D.Float p2 = new Point2D.Float(bottomRight.get(Vector.I1), bottomRight.get(Vector.I2));
                Point2D.Float p3 = new Point2D.Float(topRight.get(Vector.I1), topRight.get(Vector.I2));
                Point2D.Float p4 = new Point2D.Float(topLeft.get(Vector.I1), topLeft.get(Vector.I2));

                currentLineList.add(new Line2D.Double(p1, p2)); // 底边
                currentLineList.add(new Line2D.Double(p2, p3)); // 右边
                currentLineList.add(new Line2D.Double(p3, p4)); // 顶边
                currentLineList.add(new Line2D.Double(p4, p1)); // 左边
            }
        }// 处理贝塞尔曲线操作 - 简化处理：只连接起点和终点
//        else if (operation == PathConstructionRenderInfo.CURVE_123 ||
//                operation == PathConstructionRenderInfo.CURVE_23 ||
//                operation == PathConstructionRenderInfo.CURVE_13) {
//
//            if (currentPoint != null) {
//                List<Float> segments = renderInfo.getSegmentData();
//                Matrix ctm = renderInfo.getCtm();
//
//                // 获取曲线的终点坐标
//                float endX, endY;
//
//                switch (operation) {
//                    case PathConstructionRenderInfo.CURVE_123:
//                        // 完整的贝塞尔曲线：6个参数 (x1, y1, x2, y2, x3, y3)
//                        // 终点是 (x3, y3)
//                        if (segments.size() >= 6) {
//                            endX = segments.get(4);
//                            endY = segments.get(5);
//                        } else {
//                            return;
//                        }
//                        break;
//
//                    case PathConstructionRenderInfo.CURVE_23:
//                        // 省略第一个控制点的贝塞尔曲线：4个参数 (x2, y2, x3, y3)
//                        // 终点是 (x3, y3)
//                        if (segments.size() >= 4) {
//                            endX = segments.get(2);
//                            endY = segments.get(3);
//                        } else {
//                            return;
//                        }
//                        break;
//
//                    case PathConstructionRenderInfo.CURVE_13:
//                        // 省略第二个控制点的贝塞尔曲线：4个参数 (x1, y1, x3, y3)
//                        // 终点是 (x3, y3)
//                        if (segments.size() >= 4) {
//                            endX = segments.get(2);
//                            endY = segments.get(3);
//                        } else {
//                            return;
//                        }
//                        break;
//
//                    default:
//                        return;
//                }
//
//                // 转换终点坐标为绝对坐标
//                Vector endVec = new Vector(endX, endY, 1).cross(ctm);
//                Point2D.Float endPoint = new Point2D.Float(endVec.get(Vector.I1),
//                        endVec.get(Vector.I2));
//
//                // 简化处理：将曲线近似为直线（从当前点到终点）
//                Line2D line = new Line2D.Double(currentPoint, endPoint);
//                currentLineList.add(line);
//
//                // 更新当前点为曲线终点
//                currentPoint = endPoint;
//
//                // 如果需要更精确的曲线表示，可以将曲线分段为多条直线
//                // 这里提供一个可选的更精确的实现（注释掉）
//            /*
//            // 将贝塞尔曲线分段为多条直线
//            int segments = 10; // 分段数
//            List<Point2D.Float> curvePoints = calculateBezierPoints(
//                currentPoint, controlPoints, endPoint, segments);
//
//            for (int i = 0; i < curvePoints.size() - 1; i++) {
//                Line2D segment = new Line2D.Double(
//                    curvePoints.get(i), curvePoints.get(i + 1));
//                currentLineList.add(segment);
//            }
//            */
//            }
//        }
    }

    /**
     * 通过反射获取TextChunk的方向向量
     */
    private static Vector getOrientationVector(LocationTextExtractionStrategy.TextChunk textChunk)
            throws Exception {
        return ReflectionUtils.fetchObjResult(textChunk.getLocation(), "orientationVector");
    }




//    /**
//     * 在类层次结构中查找字段
//     */
//    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
//        Class<?> current = clazz;
//        while (current != null) {
//            try {
//                return current.getDeclaredField(fieldName);
//            } catch (NoSuchFieldException e) {
//                current = current.getSuperclass();
//            }
//        }
//        return null;
//    }


//    /**
//     * 通过反射获取TextChunk的填充颜色
//     */
//    private static BaseColor getFillColor(LocationTextExtractionStrategy.TextChunk chunk)
//            throws Exception {
//
//        // 首先获取TextRenderInfo
//        Field textRenderInfoField = findFieldInHierarchy(chunk.getClass(), "renderInfo");
//        if (textRenderInfoField == null) {
//            return null;
//        }
//        textRenderInfoField.setAccessible(true);
//        TextRenderInfo renderInfo = (TextRenderInfo) textRenderInfoField.get(chunk);
//
//        if (renderInfo != null) {
//            // 获取填充颜色
//            return renderInfo.getFillColor();
//        }
//
//        return null;
//    }

//    /**
//     * 过滤掉非黑色的文字
//     * @param textChunks 原始文本块列表
//     * @return 只包含黑色文字的列表
//     */
//    public static List<LocationTextExtractionStrategy.TextChunk> filterNonBlackText(
//            List<LocationTextExtractionStrategy.TextChunk> textChunks) {
//
//        List<LocationTextExtractionStrategy.TextChunk> filteredChunks = new ArrayList<>();
//
//        for (LocationTextExtractionStrategy.TextChunk chunk : textChunks) {
//            try {
//                BaseColor fillColor = getFillColor(chunk);
//
//                if (fillColor != null) {
//                    // 检查是否为黑色（RGB都接近0）
//                    int tolerance = 10; // 容差值
//                    if (fillColor.getRed() <= tolerance &&
//                            fillColor.getGreen() <= tolerance &&
//                            fillColor.getBlue() <= tolerance) {
//                        filteredChunks.add(chunk);
//                    }
//                    else{
//                        System.out.println(chunk.getText());
//                    }
//                } else {
//                    // 如果无法获取颜色信息，默认为黑色
//                    filteredChunks.add(chunk);
//                }
//            } catch (Exception e) {
//                // 错误处理：保留该文本块
//                filteredChunks.add(chunk);
//            }
//        }
//
//        return filteredChunks;
//    }



    /**
     * Called when the current path should be rendered.
     *
     * @param renderInfo Contains information about the current path which should be rendered.
     * @return The path which can be used as a new clipping path.
     */
    @Override
    public Path renderPath(PathPaintingRenderInfo renderInfo) {
        // 路径渲染完成后，重置当前点和路径起始点
        this.currentPoint = null;
        this.pathStartPoint = null;


        return null;
    }

    /**
     * Called when the current path should be set as a new clipping path.
     *
     * @param rule Either {@link PathPaintingRenderInfo#EVEN_ODD_RULE} or {@link PathPaintingRenderInfo#NONZERO_WINDING_RULE}
     */
    @Override
    public void clipPath(int rule) {

    }



    /**
     * 获取TextChunk的起始Y坐标
     */
    private static float getStartY(LocationTextExtractionStrategy.TextChunk chunk){
        try{
            return chunk.getStartLocation().get(Vector.I2); // Y坐标
        }catch (Exception e){
            // todo Log
            return 0f;
        }
    }

    //@@@@@@@@@

    /**
     * 按起始位置的Y坐标从大到小排序（从上到下）
     * 在PDF坐标系中，y轴是向上的，所以y值越大表示位置越高。如果要从大到小排序，就是从上到下排序。
     * @param locationalResult 需要排序的文本块列表
     * @return 排序后的新列表
     */
    public static List<LocationTextExtractionStrategy.TextChunk> sortByYDescending(
            List<LocationTextExtractionStrategy.TextChunk> locationalResult) {

        // 创建新列表以避免修改原列表
        List<LocationTextExtractionStrategy.TextChunk> sortedList =
                new ArrayList<>(locationalResult);

        // 使用自定义比较器排序
        Collections.sort(sortedList, new Comparator<LocationTextExtractionStrategy.TextChunk>() {
            @Override
            public int compare(LocationTextExtractionStrategy.TextChunk chunk1,
                               LocationTextExtractionStrategy.TextChunk chunk2) {
                try {
                    float y1 = getStartY(chunk1);
                    float y2 = getStartY(chunk2);

                    // 从大到小排序（降序）
                    return Float.compare(y2, y1);
                } catch (Exception e) {
                    // 如果无法获取Y坐标，保持原顺序
                    return 0;
                }
            }
        });

        return sortedList;
    }



}
