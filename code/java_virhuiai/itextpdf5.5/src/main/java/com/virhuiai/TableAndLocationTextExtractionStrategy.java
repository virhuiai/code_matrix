package com.virhuiai;

import com.itextpdf.text.pdf.parser.ExtRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.Path;
import com.itextpdf.text.pdf.parser.PathConstructionRenderInfo;
import com.itextpdf.text.pdf.parser.PathPaintingRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableAndLocationTextExtractionStrategy extends LocationTextExtractionStrategy implements ExtRenderListener {

    /**
     * 当前点
     */
    private Point2D.Float currentPoint = null;

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
     * 判断两个点是否相等（在给定的精度范围内）
     * @param p1 第一个点
     * @param p2 第二个点
     * @param epsilon 允许的误差范围
     * @return 如果两个点在误差范围内相等，返回true；否则返回false
     */
    private boolean isPointsEqual(Point2D p1, Point2D p2, double epsilon) {
        // 分别比较x坐标和y坐标的差值是否都小于误差范围
        // 使用Math.abs获取绝对值，确保差值为正数
        return Math.abs(p1.getX() - p2.getX()) < epsilon &&
                Math.abs(p1.getY() - p2.getY()) < epsilon;
    }

    /**
     * 判断两条线段是否相连（即是否共享端点）
     * @param line1 第一条线段
     * @param line2 第二条线段
     * @return 如果两条线段共享至少一个端点，返回true；否则返回false
     */
    private boolean isConnected(Line2D line1, Line2D line2) {
        // 获取第一条线的起点和终点
        Point2D p1Start = line1.getP1();  // 线段1的起点
        Point2D p1End = line1.getP2();    // 线段1的终点

        // 获取第二条线的起点和终点
        Point2D p2Start = line2.getP1();  // 线段2的起点
        Point2D p2End = line2.getP2();    // 线段2的终点

        // 定义误差范围，用于处理浮点数比较时的精度问题
        // 当两个浮点数的差值小于epsilon时，认为它们相等
        double epsilon = 1e-6;

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
     * fromLineListToPointSet
     * 从线生成点的map
     * @return
     */
    public Set<String> fromLineListToPointSet(List<Line2D> lineList){
        Set<String> rs = new HashSet<>();
        if(null != lineList && !lineList.isEmpty()){
            for (Line2D line2D : lineList) {
                if(null != line2D){
                    rs.add("x:" + line2D.getX1() + ",y:"  + line2D.getY1());
                    rs.add("x:" + line2D.getX2() + ",y:"  + line2D.getY2());
                }
            }
        }

        return rs;
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
        // 检查是否为线段操作（MOVETO 或 LINETO）
        if (operation == PathConstructionRenderInfo.MOVETO ||
                operation == PathConstructionRenderInfo.LINETO) {

            // 获取线段端点坐标（相对坐标）
            List<Float> segments = renderInfo.getSegmentData();
            float x = segments.get(0);
            float y = segments.get(1);

            // 转换为绝对坐标（需结合变换矩阵）
            Matrix ctm = renderInfo.getCtm();
            Vector vec = new Vector(x, y, 1).cross(ctm);
            Point2D.Float point = new Point2D.Float(vec.get(Vector.I1),
                    vec.get(Vector.I2));

            // 点来源记录
//            pointSrcList.add(renderInfo);
            if(operation == PathConstructionRenderInfo.MOVETO){
                this.currentPoint = point;
//                System.out.print("路径起点");
            }else{
                if(null != currentPoint){
                    Line2D line = new Line2D.Double(currentPoint, point);
                    currentLineList.add(line);
                    this.currentPoint = point;

                }
//                else{
////                    System.out.println("没有起点，跳过");// todo LOG
//
//                }
//                System.out.print("线段终点");
            }
//            System.out.printf(" - 线段端点: (%.2f, %.2f)%n", vec.get(0), vec.get(1));


//                - `MOVETO` (`1`): 路径起点
//                        - `LINETO` (`2`): 线段终点

//            pcRenderInfoList.add(renderInfo);

//            System.out.println("modifyPath,renderInfo.getOperation():" + renderInfo.getOperation());
//            System.out.println("modifyPath,renderInfo.getSegmentData().size():" + renderInfo.getSegmentData().size());
//            System.out.println("modifyPath,renderInfo.getSegmentData():" + renderInfo.getSegmentData());
//            System.out.println("modifyPath,renderInfo.getCtm():" + renderInfo.getCtm());

        }
    }



    /**
     * Called when the current path should be rendered.
     *
     * @param renderInfo Contains information about the current path which should be rendered.
     * @return The path which can be used as a new clipping path.
     */
    @Override
    public Path renderPath(PathPaintingRenderInfo renderInfo) {
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


}
