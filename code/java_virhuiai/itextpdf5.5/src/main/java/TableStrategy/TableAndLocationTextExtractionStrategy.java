package TableStrategy;

import com.itextpdf.text.pdf.parser.*;
import com.virhuiai.GeometryUtils;
import com.virhuiai.PdfPathUtils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 这个类用于从PDF中提取表格和文本位置信息
 */
public class TableAndLocationTextExtractionStrategy extends LocTextExtractionStrategy implements ExtRenderListener {

    /**
     * 当前点
     */
    private Point2D.Float currentPoint = null;

    /**
     * 记录路径起始点，用于闭合路径
     */
    private Point2D.Float pathStartPoint = null;


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
        // 转换为点坐标
        Point2D.Float point = PdfPathUtils.convertToPoint(renderInfo);

        // 处理线段操作
        if (PdfPathUtils.isMoveTo(operation) || PdfPathUtils.isLineTo(operation)) {
            if (PdfPathUtils.isMoveTo(operation)) {
                // MOVETO操作：设置新的路径起点
                this.currentPoint = point;
                this.pathStartPoint = point; // 记录路径起始点，用于闭合路径
            } else {
                // LINETO操作：创建线段
                if (currentPoint != null && point != null) {
                    Line2D line = new Line2D.Double(currentPoint, point);
                    currentLineList.add(line);
                    this.currentPoint = point;
                }
            }
        }
        // 处理闭合路径操作
        else if (PdfPathUtils.isClosePath(operation)) {
            // 如果有当前点和路径起始点，创建闭合线段
            if (currentPoint != null && pathStartPoint != null && !GeometryUtils.isPointsEqual(currentPoint, pathStartPoint)) {
                Line2D line = new Line2D.Double(currentPoint, pathStartPoint);
                currentLineList.add(line);
            }
        }
        // 处理矩形操作（PDF中表格经常使用矩形绘制）
        else if (PdfPathUtils.isRectangle(operation)) {
            Line2D[] rectangleLines = PdfPathUtils.createRectangleLines(renderInfo);
            if (rectangleLines != null) {
                currentLineList.addAll(Arrays.asList(rectangleLines));
            }
        }
    }






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




    //@@@@@@@@@





}
