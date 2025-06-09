package com.virhuiai;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ExtRenderListener;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.Path;
import com.itextpdf.text.pdf.parser.PathConstructionRenderInfo;
import com.itextpdf.text.pdf.parser.PathPaintingRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class PDFTableLineDetector3 {

    public static void main(String[] args) {
        String pdfPath = "H:\\AppData\\Roaming\\pjf-jxjy-cloud\\pjf\\user-yaole\\100021007\\files\\text\\25中核汇能SCP001_中国建设银行股份有限公司_中国建设银行股份有限公司_配售确认及缴款通知书.pdf";
        int pageNumber = 1;

        try {
            List<Line2D> horizontalLines = new ArrayList<>();
            List<Line2D> verticalLines = new ArrayList<>();

            extractTableLines(pdfPath, pageNumber, horizontalLines, verticalLines);

            // 打印检测结果
            printDetectedLines(horizontalLines, verticalLines);

            // 构建表格结构
            List<Table> tables = buildTables(horizontalLines, verticalLines);
            printTables(tables);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 核心解析方法
    public static void extractTableLines(String pdfPath, int pageNumber,
                                         List<Line2D> horizontalLines, List<Line2D> verticalLines)
            throws IOException {

        PdfReader reader = new PdfReader(pdfPath);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        LineDetectionStrategy strategy = new LineDetectionStrategy(horizontalLines, verticalLines);
        parser.processContent(pageNumber, strategy);

        reader.close();
    }

    // 表格线检测策略实现
    public static class LineDetectionStrategy implements ExtRenderListener {
        private final List<Line2D> horizontalLines;
        private final List<Line2D> verticalLines;
        private Path currentPath = new Path();

        private List<PathConstructionRenderInfo> pcRenderInfoList = new ArrayList<>();

        // 参数配置
        private static final float MERGE_THRESHOLD = 2.0f;
        private static final float MIN_LINE_LENGTH = 5.0f;

        public LineDetectionStrategy(List<Line2D> horizontalLines, List<Line2D> verticalLines) {
            this.horizontalLines = horizontalLines;
            this.verticalLines = verticalLines;
        }

        @Override
        public void beginTextBlock() {
            System.out.println("beginTextBlock");
        }
        @Override
        public void endTextBlock() {
            System.out.println("endTextBlock");
        }
        @Override
        public void renderText(TextRenderInfo renderInfo) {
            System.out.println("renderText:" + renderInfo.getText());
        }
        @Override
        public void renderImage(ImageRenderInfo renderInfo) {
            System.out.println("renderImage");
            int a =3;
        }

        @Override
        public void modifyPath(PathConstructionRenderInfo renderInfo) {
//            currentPath.add(renderInfo);



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
                Vector point = new Vector(x, y, 1).cross(ctm);

                if(operation == PathConstructionRenderInfo.MOVETO){
                    System.out.print("路径起点");
                }else{
                    System.out.print("线段终点");
                }
                System.out.printf(" - 线段端点: (%.2f, %.2f)%n", point.get(0), point.get(1));


//                - `MOVETO` (`1`): 路径起点
//                        - `LINETO` (`2`): 线段终点

                pcRenderInfoList.add(renderInfo);

                System.out.println("modifyPath,renderInfo.getOperation():" + renderInfo.getOperation());
                System.out.println("modifyPath,renderInfo.getSegmentData().size():" + renderInfo.getSegmentData().size());
                System.out.println("modifyPath,renderInfo.getSegmentData():" + renderInfo.getSegmentData());
                System.out.println("modifyPath,renderInfo.getCtm():" + renderInfo.getCtm());

            }



        }

        @Override
        public Path renderPath(PathPaintingRenderInfo renderInfo) {
            System.out.println("renderPath,renderInfo.getOperation():" + renderInfo.getOperation());
            System.out.println("renderPath,renderInfo.getLineWidth():" + renderInfo.getLineWidth());
            System.out.println("renderPath,renderInfo.getCtm():" + renderInfo.getCtm());


//            if (renderInfo.getOperation() == PathPaintingRenderInfo.STROKE) {
                processPathSegments();
                pcRenderInfoList = new ArrayList<>();
//            }



            currentPath = new Path();
            return currentPath;
        }

        /**
         * Called when the current path should be set as a new clipping path.
         *
         * @param rule Either {@link PathPaintingRenderInfo#EVEN_ODD_RULE} or {@link PathPaintingRenderInfo#NONZERO_WINDING_RULE}
         */
        @Override
        public void clipPath(int rule) {
            System.out.println("clipPath");
        }


        private void processPathSegments() {
            List<Point2D> points = extractPoints();
            convertPointsToLines(points);
        }

        private List<Point2D> extractPoints() {
            List<Point2D> points = new ArrayList<>();
            for (PathConstructionRenderInfo segment : pcRenderInfoList) {
                if (segment.getSegmentData() != null) {
                    List<Float> data = segment.getSegmentData();
                    for (int i = 0; i < data.size(); i += 2) {
                        if (i + 1 < data.size()) {
                            Vector point = new Vector(data.get(i), data.get(i+1), 1);
                            point = point.cross(segment.getCtm());
                            points.add(new Point2D.Double(
                                    point.get(Vector.I1),
                                    point.get(Vector.I2)
                            ));
                        }
                    }
                }
            }
            return points;
        }

        private void convertPointsToLines(List<Point2D> points) {
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p1 = points.get(i);
                Point2D p2 = points.get(i + 1);

                Line2D line = new Line2D.Double(p1, p2);

                // 过滤短线段
                if (line.getP1().distance(line.getP2()) < MIN_LINE_LENGTH) continue;

                // 分类合并线段
                classifyAndMergeLine(line);
            }
        }

        private void classifyAndMergeLine(Line2D line) {
            double deltaX = Math.abs(line.getX1() - line.getX2());
            double deltaY = Math.abs(line.getY1() - line.getY2());

            if (deltaY < MERGE_THRESHOLD && deltaX > MIN_LINE_LENGTH) {
                mergeLine(horizontalLines, line);
            } else if (deltaX < MERGE_THRESHOLD && deltaY > MIN_LINE_LENGTH) {
                mergeLine(verticalLines, line);
            }
        }

        private void mergeLine(List<Line2D> lines, Line2D newLine) {
            for (Line2D existing : lines) {
                if (canMerge(existing, newLine)) {
                    double minX = Math.min(existing.getBounds2D().getMinX(), newLine.getBounds2D().getMinX());
                    double maxX = Math.max(existing.getBounds2D().getMaxX(), newLine.getBounds2D().getMaxX());
                    double minY = Math.min(existing.getBounds2D().getMinY(), newLine.getBounds2D().getMinY());
                    double maxY = Math.max(existing.getBounds2D().getMaxY(), newLine.getBounds2D().getMaxY());

                    existing.setLine(minX, minY, maxX, maxY);
                    return;
                }
            }
            lines.add(newLine);
        }

        private boolean canMerge(Line2D l1, Line2D l2) {
            if (isHorizontal(l1) && isHorizontal(l2)) {
                return Math.abs(l1.getY1() - l2.getY1()) < MERGE_THRESHOLD &&
                        l1.getX1() <= l2.getX2() + MERGE_THRESHOLD &&
                        l2.getX1() <= l1.getX2() + MERGE_THRESHOLD;
            } else if (isVertical(l1) && isVertical(l2)) {
                return Math.abs(l1.getX1() - l2.getX1()) < MERGE_THRESHOLD &&
                        l1.getY1() <= l2.getY2() + MERGE_THRESHOLD &&
                        l2.getY1() <= l1.getY2() + MERGE_THRESHOLD;
            }
            return false;
        }

        private boolean isHorizontal(Line2D line) {
            return Math.abs(line.getY1() - line.getY2()) < MERGE_THRESHOLD;
        }

        private boolean isVertical(Line2D line) {
            return Math.abs(line.getX1() - line.getX2()) < MERGE_THRESHOLD;
        }
    }

    // 表格构建方法
    static List<Table> buildTables(List<Line2D> horizontal, List<Line2D> vertical) {
        if (horizontal.isEmpty() || vertical.isEmpty()) return Collections.emptyList();

        // 坐标排序
        Collections.sort(horizontal, Comparator.comparingDouble(l -> l.getY1()));
        Collections.sort(vertical, Comparator.comparingDouble(l -> l.getX1()));

        // 提取坐标轴
        TreeSet<Double> xCoords = new TreeSet<>();
        TreeSet<Double> yCoords = new TreeSet<>();

        vertical.forEach(v -> {
            xCoords.add(v.getX1());
            xCoords.add(v.getX2());
        });

        horizontal.forEach(h -> {
            yCoords.add(h.getY1());
            yCoords.add(h.getY2());
        });

        // 构建表格
        List<Table> tables = new ArrayList<>();
        Table table = new Table(
                xCoords.first(), yCoords.last(),
                xCoords.last() - xCoords.first(),
                yCoords.first() - yCoords.last()
        );

        // 创建单元格
        List<Double> xList = new ArrayList<>(xCoords);
        List<Double> yList = new ArrayList<>(yCoords);
        Collections.reverse(yList); // 从上到下排序

        for (int row = 0; row < yList.size() - 1; row++) {
            for (int col = 0; col < xList.size() - 1; col++) {
                table.addCell(new TableCell(
                        row, col,
                        xList.get(col), yList.get(row + 1),
                        xList.get(col + 1), yList.get(row)
                ));
            }
        }

        tables.add(table);
        return tables;
    }

    // 辅助方法：结果打印
    private static void printDetectedLines(List<Line2D> horizontal, List<Line2D> vertical) {
        System.out.println("水平线检测 (" + horizontal.size() + "条):");
        horizontal.forEach(line -> System.out.printf("  Y=%.2f 从 X=%.2f 到 %.2f%n",
                line.getY1(), line.getX1(), line.getX2()));

        System.out.println("\n垂直线检测 (" + vertical.size() + "条):");
        vertical.forEach(line -> System.out.printf("  X=%.2f 从 Y=%.2f 到 %.2f%n",
                line.getX1(), line.getY1(), line.getY2()));
    }

    private static void printTables(List<Table> tables) {
        System.out.println("\n表格识别 (" + tables.size() + "个):");
        tables.forEach(table -> {
            System.out.printf("位置: (%.2f, %.2f) 尺寸: %.2fx%.2f%n",
                    table.x, table.y, table.width, table.height);

            System.out.println("单元格:");
            table.cells.forEach(cell -> System.out.printf("  %d,%d (%.2f,%.2f)-%.2fx%.2f%n",
                    cell.row, cell.col, cell.minX, cell.minY,
                    cell.maxX - cell.minX, cell.maxY - cell.minY));
        });
    }

    // 数据结构
    static class Table {
        final double x, y, width, height;
        final List<TableCell> cells = new ArrayList<>();

        public Table(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        void addCell(TableCell cell) {
            cells.add(cell);
        }
    }

    static class TableCell {
        final int row, col;
        final double minX, minY, maxX, maxY;

        public TableCell(int row, int col, double minX, double minY, double maxX, double maxY) {
            this.row = row;
            this.col = col;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }
}