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

    /**
     * 点来源记录
     */
    private List<PathConstructionRenderInfo> pointSrcList = new ArrayList<>();

    /**
     * 线记录
     */
    private List<Line2D> currentLineList = new ArrayList<>();

    /**
     * 多个的线记录
     */
    private List<List<Line2D>> allLineListList = new ArrayList<>();

    /**
     * 处理之前的旧数据（最后一次信息存在current里，最终使用时需要处理 todo）
     */
    public void dealLastCurrentInfo(){
        if(null != currentLineList && !currentLineList.isEmpty()){
            allLineListList.add(currentLineList);
            currentLineList = new ArrayList<>();
        }

        if(null != currentPoint){
            currentPoint = null;
        }

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
     * 取得所有行列表的列表
     * @return
     */
    public List<List<Line2D>> getAllLineListList() {
        return allLineListList;
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
            pointSrcList.add(renderInfo);
            if(operation == PathConstructionRenderInfo.MOVETO){
                // 处理之前的旧数据（最后一次信息存在current里，最终使用时需要处理 todo）
                if(null != currentPoint || (null != currentLineList || !currentLineList.isEmpty())){
                    dealLastCurrentInfo();
                }

                this.currentPoint = point;
                System.out.print("路径起点");
            }else{
                if(null != currentPoint){
                    Line2D line = new Line2D.Double(currentPoint, point);
                    currentLineList.add(line);
                    this.currentPoint = point;

                }else{
                    System.out.println("没有起点，跳过");// todo LOG
                }
                System.out.print("线段终点");
            }
            System.out.printf(" - 线段端点: (%.2f, %.2f)%n", vec.get(0), vec.get(1));


//                - `MOVETO` (`1`): 路径起点
//                        - `LINETO` (`2`): 线段终点

//            pcRenderInfoList.add(renderInfo);

            System.out.println("modifyPath,renderInfo.getOperation():" + renderInfo.getOperation());
            System.out.println("modifyPath,renderInfo.getSegmentData().size():" + renderInfo.getSegmentData().size());
            System.out.println("modifyPath,renderInfo.getSegmentData():" + renderInfo.getSegmentData());
            System.out.println("modifyPath,renderInfo.getCtm():" + renderInfo.getCtm());

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
