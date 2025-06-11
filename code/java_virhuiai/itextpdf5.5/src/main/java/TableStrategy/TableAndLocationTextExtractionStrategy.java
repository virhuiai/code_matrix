package TableStrategy;

import com.itextpdf.text.pdf.parser.*;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 这个类用于从PDF中提取表格和文本位置信息
 */
public class TableAndLocationTextExtractionStrategy extends LocTextExtractionStrategy
        implements PathProcessingListener {

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

    // 实现PathProcessingListener接口的方法
    @Override
    public Point2D.Float getCurrentPoint() {
        return currentPoint;
    }

    @Override
    public void setCurrentPoint(Point2D.Float point) {
        this.currentPoint = point;
    }

    @Override
    public Point2D.Float getPathStartPoint() {
        return pathStartPoint;
    }

    @Override
    public void setPathStartPoint(Point2D.Float point) {
        this.pathStartPoint = point;
    }

    @Override
    public void addLine(Line2D line) {
        currentLineList.add(line);
    }

    @Override
    public void addLines(Collection<Line2D> lines) {
        currentLineList.addAll(lines);
    }

    /**
     * 获取当前线段列表
     */
    public List<Line2D> getCurrentLineList() {
        return currentLineList;
    }

    // modifyPath 和 renderPath 方法现在使用接口的默认实现
    // 如果需要自定义行为，可以覆盖这些方法

    /**
     * Called when the current path should be set as a new clipping path.
     *
     * @param rule Either {@link PathPaintingRenderInfo#EVEN_ODD_RULE} or {@link PathPaintingRenderInfo#NONZERO_WINDING_RULE}
     */
    @Override
    public void clipPath(int rule) {
        // 实现clipPath方法
    }




    //@@@@@@@@@





}
