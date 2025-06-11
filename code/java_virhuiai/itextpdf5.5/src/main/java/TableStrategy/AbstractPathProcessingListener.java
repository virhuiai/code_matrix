package TableStrategy;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 提供一些通用的状态管理，创建的一个抽象基类
 *
 * 路径处理监听器的抽象基类
 * 提供基本的状态管理实现
 */
public abstract class AbstractPathProcessingListener implements PathProcessingListener {

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

    /**
     * 清空线段列表
     */
    public void clearLineList() {
        currentLineList.clear();
    }
}