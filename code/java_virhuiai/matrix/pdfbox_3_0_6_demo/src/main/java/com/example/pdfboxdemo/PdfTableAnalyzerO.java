package com.example.pdfboxdemo;

import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.contentstream.PDFStreamEngine;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PDF表格分析器（基于线条）
 * 功能：通过分析PDF中的线条信息来识别表格结构
 */
public class PdfTableAnalyzerO {
    
    // 容差值定义
    private static final double COORD_TOLERANCE = 2.0;
    private static final double EXTEND_LENGTH = 50.0;
    private static final double INTERSECTION_TOLERANCE = 3.0;
    
    /**
     * 线条信息类
     */
    public static class LineInfo {
        public double startX, startY, endX, endY;
        public boolean isHorizontal;
        public double length;
        
        public LineInfo(double x1, double y1, double x2, double y2) {
            this.startX = Math.min(x1, x2);
            this.startY = Math.min(y1, y2);
            this.endX = Math.max(x1, x2);
            this.endY = Math.max(y1, y2);
            this.isHorizontal = Math.abs(y1 - y2) < 1.0; // 水平线判断阈值
            this.length = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        }
        
        public Point2D getStartPoint() {
            return new Point2D.Double(startX, startY);
        }
        
        public Point2D getEndPoint() {
            return new Point2D.Double(endX, endY);
        }
        
        @Override
        public String toString() {
            return String.format("Line[%s]: (%.2f,%.2f) -> (%.2f,%.2f) len=%.2f", 
                isHorizontal ? "H" : "V", startX, startY, endX, endY, length);
        }
    }
    
    /**
     * 表格单元格信息
     */
    public static class TableCell {
        public Rectangle2D bounds;
        public String content;
        public int row, col;
        
        public TableCell(Rectangle2D bounds, String content, int row, int col) {
            this.bounds = bounds;
            this.content = content != null ? content.trim() : "";
            this.row = row;
            this.col = col;
        }
        
        @Override
        public String toString() {
            return String.format("Cell[R%d,C%d]: '%s' at (%.1f,%.1f,%.1f,%.1f)", 
                row, col, content, 
                bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        }
    }
    
    /**
     * 表格信息
     */
    public static class Table {
        public List<TableCell> cells;
        public int rowCount, colCount;
        public Rectangle2D bounds; // 表格整体边界
        
        public Table() {
            this.cells = new ArrayList<>();
            this.rowCount = 0;
            this.colCount = 0;
        }
        
        public void addCell(TableCell cell) {
            cells.add(cell);
            rowCount = Math.max(rowCount, cell.row + 1);
            colCount = Math.max(colCount, cell.col + 1);
        }
        
        /**
         * 设置表格边界
         */
        public void setBounds(Rectangle2D bounds) {
            this.bounds = bounds;
        }
        
        /**
         * 转换为Map格式，按键名R{行}C{列}组织
         */
        public Map<String, String> toMap() {
            Map<String, String> tableMap = new LinkedHashMap<>();
            
            // 按行列排序
            cells.sort((a, b) -> {
                if (a.row != b.row) return Integer.compare(a.row, b.row);
                return Integer.compare(a.col, b.col);
            });
            
            for (TableCell cell : cells) {
                String key = String.format("R%dC%d", cell.row + 1, cell.col + 1);
                tableMap.put(key, cell.content);
            }
            return tableMap;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("表格结构 (%dx%d):\n", rowCount, colCount));
            if (bounds != null) {
                sb.append(String.format("边界: (%.1f,%.1f,%.1f,%.1f)\n", 
                    bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
            }
            
            // 创建二维数组便于显示
            String[][] grid = new String[rowCount][colCount];
            for (int i = 0; i < rowCount; i++) {
                Arrays.fill(grid[i], "");
            }
            
            for (TableCell cell : cells) {
                if (cell.row < rowCount && cell.col < colCount) {
                    grid[cell.row][cell.col] = cell.content;
                }
            }
            
            // 输出表格
            for (int i = 0; i < rowCount; i++) {
                sb.append("  Row ").append(i + 1).append(": ");
                for (int j = 0; j < colCount; j++) {
                    sb.append("| ").append(grid[i][j]).append(" ");
                }
                sb.append("|\n");
            }
            return sb.toString();
        }
    }
    
    /**
     * 分析PDF文档中的表格（基于线条）
     */
    public static List<Table> analyzeTables(PDDocument document) throws IOException {
        System.out.println("=== 开始基于线条的PDF表格分析 ===");
        
        List<Table> tables = new ArrayList<>();
        
        // 1. 提取所有线条
        List<LineInfo> lines = extractLinesFromDocument(document);
        System.out.println("提取到 " + lines.size() + " 条线条");
        
        if (lines.isEmpty()) {
            System.out.println("未找到任何线条");
            return tables;
        }
        
        // 2. 将相交的线条分为一组
        List<List<LineInfo>> lineGroups = groupIntersectingLines(lines);
        System.out.println("识别出 " + lineGroups.size() + " 个线条组");
        
        // 3. 为每组线条延长并计算相交点
        for (int i = 0; i < lineGroups.size(); i++) {
            List<LineInfo> lineGroup = lineGroups.get(i);
            System.out.println("处理线条组 " + (i + 1) + "，包含 " + lineGroup.size() + " 条线条");
            
            // 延长线条
            List<LineInfo> extendedLines = extendLines(lineGroup);
            
            // 计算相交点
            Set<Point2D> intersectionPoints = calculateIntersectionPoints(extendedLines);
            System.out.println("  找到 " + intersectionPoints.size() + " 个相交点");
            
            if (intersectionPoints.size() >= 4) { // 至少需要4个点才能构成表格
                // 4. 根据相交点确定表格单元格
                Table table = createTableFromIntersections(intersectionPoints, extendedLines);
                if (table != null && !table.cells.isEmpty()) {
                    tables.add(table);
                    System.out.println("  成功创建表格，尺寸: " + table.rowCount + "x" + table.colCount);
                }
            }
        }
        
        return tables;
    }
    
    /**
     * 从PDF文档中提取所有线条
     */
    private static List<LineInfo> extractLinesFromDocument(PDDocument document) throws IOException {
        List<LineInfo> lines = new ArrayList<>();
        
        for (int pageNum = 0; pageNum < document.getNumberOfPages(); pageNum++) {
            PDPage page = document.getPage(pageNum);
            
            try {
                // 创建自定义的线条提取引擎
                LineExtractionEngine engine = new LineExtractionEngine();
                engine.processPage(page);
                lines.addAll(engine.getPageLines());

                engine.getOperationSet();
                
            } catch (Exception e) {
                System.err.println("处理页面 " + (pageNum + 1) + " 时出错: " + e.getMessage());
            }
        }
        
        return lines;
    }
    
    /**
     * 线条提取引擎
     */
    private static class LineExtractionEngine extends PDFStreamEngine {
        private double currentX = 0, currentY = 0;
        private List<LineInfo> pageLines = new ArrayList<>();
        private Set<String> operationSet = new HashSet<>();
        
        @Override
        protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
            String operation = operator.getName();
            operationSet.add(operation);
            
            switch (operation) {
                case OperatorName.MOVE_TO: // 移动到m"
                    if (operands.size() >= 2) {
                        COSBase xObj = operands.get(0);
                        COSBase yObj = operands.get(1);
                        if (xObj instanceof COSNumber && yObj instanceof COSNumber) {
                            // 使用PDFBox 3.0.6正确的数值获取方式
                            currentX = ((COSNumber) xObj).floatValue();
                            currentY = ((COSNumber) yObj).floatValue();
                        }
                    }
                    break;
                    
                case OperatorName.LINE_TO : // 画线到 "l"
                    if (operands.size() >= 2) {
                        COSBase xObj = operands.get(0);
                        COSBase yObj = operands.get(1);
                        if (xObj instanceof COSNumber && yObj instanceof COSNumber) {
                            // 使用PDFBox 3.0.6正确的数值获取方式
                            double endX = ((COSNumber) xObj).floatValue();
                            double endY = ((COSNumber) yObj).floatValue();
                            
                            // 创建线条（过滤掉很短的线条）
                            LineInfo line = new LineInfo(currentX, currentY, endX, endY);
                            if (line.length > 5.0) { // 最小长度阈值
                                pageLines.add(line);
                            }
                            
                            currentX = endX;
                            currentY = endY;
                        }
                    }
                    break;
            }
            
            super.processOperator(operator, operands);
        }
        
        public List<LineInfo> getPageLines() {
            return pageLines;
        }

        public Set<String> getOperationSet(){
            return operationSet;
        }
    }
    
    /**
     * 将相交的线条分为一组（使用并查集算法）
     */
    private static List<List<LineInfo>> groupIntersectingLines(List<LineInfo> lines) {
        List<List<LineInfo>> groups = new ArrayList<>();
        
        if (lines.isEmpty()) {
            return groups;
        }
        
        // 初始化并查集
        int[] parent = new int[lines.size()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
        
        // 检查线条是否相交并合并集合
        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                if (linesIntersect(lines.get(i), lines.get(j))) {
                    union(parent, i, j);
                }
            }
        }
        
        // 根据父节点分组
        Map<Integer, List<LineInfo>> groupMap = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            int root = find(parent, i);
            groupMap.computeIfAbsent(root, k -> new ArrayList<>()).add(lines.get(i));
        }
        
        groups.addAll(groupMap.values());
        return groups;
    }
    
    /**
     * 延长线条
     */
    private static List<LineInfo> extendLines(List<LineInfo> lines) {
        List<LineInfo> extendedLines = new ArrayList<>();
        
        for (LineInfo line : lines) {
            if (line.isHorizontal) {
                // 延长水平线
                LineInfo extended = new LineInfo(
                    line.startX - EXTEND_LENGTH, line.startY,
                    line.endX + EXTEND_LENGTH, line.endY
                );
                extendedLines.add(extended);
            } else {
                // 延长垂直线
                LineInfo extended = new LineInfo(
                    line.startX, line.startY - EXTEND_LENGTH,
                    line.endX, line.endY + EXTEND_LENGTH
                );
                extendedLines.add(extended);
            }
        }
        
        return extendedLines;
    }
    
    /**
     * 计算线条相交点
     */
    private static Set<Point2D> calculateIntersectionPoints(List<LineInfo> lines) {
        Set<Point2D> intersections = new HashSet<>();
        
        // 分离水平线和垂直线
        List<LineInfo> horizontalLines = lines.stream()
            .filter(line -> line.isHorizontal)
            .collect(Collectors.toList());
        
        List<LineInfo> verticalLines = lines.stream()
            .filter(line -> !line.isHorizontal)
            .collect(Collectors.toList());
        
        // 计算水平线和垂直线的交点
        for (LineInfo hLine : horizontalLines) {
            for (LineInfo vLine : verticalLines) {
                Point2D intersection = getLineIntersection(hLine, vLine);
                if (intersection != null) {
                    intersections.add(intersection);
                }
            }
        }
        
        return intersections;
    }
    
    /**
     * 根据相交点创建表格
     */
    private static Table createTableFromIntersections(Set<Point2D> intersectionPoints, List<LineInfo> lines) {
        Table table = new Table();
        
        if (intersectionPoints.size() < 4) {
            return null;
        }
        
        // 将交点按坐标排序
        List<Point2D> sortedPoints = new ArrayList<>(intersectionPoints);
        sortedPoints.sort((p1, p2) -> {
            if (Math.abs(p1.getY() - p2.getY()) < COORD_TOLERANCE) {
                return Double.compare(p1.getX(), p2.getX());
            }
            return Double.compare(p1.getY(), p2.getY());
        });
        
        // 提取唯一的X和Y坐标
        Set<Double> xCoords = new TreeSet<>();
        Set<Double> yCoords = new TreeSet<>();
        
        for (Point2D point : sortedPoints) {
            xCoords.add(point.getX());
            yCoords.add(point.getY());
        }
        
        List<Double> xList = new ArrayList<>(xCoords);
        List<Double> yList = new ArrayList<>(yCoords);
        
        if (xList.size() < 2 || yList.size() < 2) {
            return null;
        }
        
        // 创建单元格
        for (int row = 0; row < yList.size() - 1; row++) {
            for (int col = 0; col < xList.size() - 1; col++) {
                double x1 = xList.get(col);
                double y1 = yList.get(row);
                double x2 = xList.get(col + 1);
                double y2 = yList.get(row + 1);
                
                Rectangle2D cellBounds = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
                TableCell cell = new TableCell(cellBounds, "", row, col);
                table.addCell(cell);
            }
        }
        
        // 设置表格边界
        double minX = Collections.min(xList);
        double maxX = Collections.max(xList);
        double minY = Collections.min(yList);
        double maxY = Collections.max(yList);
        table.setBounds(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
        
        return table;
    }
    
    // 辅助方法
    
    /**
     * 判断两条线条是否相交
     */
    private static boolean linesIntersect(LineInfo line1, LineInfo line2) {
        // 简单的距离判断
        double distance = pointToLineDistance(
            (line1.startX + line1.endX) / 2,
            (line1.startY + line1.endY) / 2,
            line2.startX, line2.startY, line2.endX, line2.endY
        );
        return distance < INTERSECTION_TOLERANCE;
    }
    
    /**
     * 计算点到线段的距离
     */
    private static double pointToLineDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        if (length == 0) return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        
        double t = Math.max(0, Math.min(1, ((px - x1) * dx + (py - y1) * dy) / (length * length)));
        double projX = x1 + t * dx;
        double projY = y1 + t * dy;
        
        return Math.sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY));
    }
    
    /**
     * 计算两条线段的交点
     */
    private static Point2D getLineIntersection(LineInfo line1, LineInfo line2) {
        // 简化处理：假设一条是水平线，一条是垂直线
        if (line1.isHorizontal && !line2.isHorizontal) {
            // 水平线和垂直线的交点
            if (line2.startX >= Math.min(line1.startX, line1.endX) && 
                line2.startX <= Math.max(line1.startX, line1.endX) &&
                line1.startY >= Math.min(line2.startY, line2.endY) && 
                line1.startY <= Math.max(line2.startY, line2.endY)) {
                return new Point2D.Double(line2.startX, line1.startY);
            }
        } else if (!line1.isHorizontal && line2.isHorizontal) {
            // 垂直线和水平线的交点
            if (line1.startX >= Math.min(line2.startX, line2.endX) && 
                line1.startX <= Math.max(line2.startX, line2.endX) &&
                line2.startY >= Math.min(line1.startY, line1.endY) && 
                line2.startY <= Math.max(line1.startY, line1.endY)) {
                return new Point2D.Double(line1.startX, line2.startY);
            }
        }
        return null;
    }
    
    /**
     * 并查集查找根节点
     */
    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }
    
    /**
     * 并查集合并集合
     */
    private static void union(int[] parent, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);
        if (rootX != rootY) {
            parent[rootY] = rootX;
        }
    }
    
    /**
     * 输出表格分析结果
     */
    public static void printTableAnalysis(List<Table> tables) {
        System.out.println("\n=== 表格分析结果 ===");
        
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            System.out.println("\n--- 表格 " + (i + 1) + " ---");
            System.out.println(table.toString());
            
            // 输出Map格式
            System.out.println("\nMap格式输出:");
            Map<String, String> tableMap = table.toMap();
            for (Map.Entry<String, String> entry : tableMap.entrySet()) {
                System.out.println("  " + entry.getKey() + " => \"" + entry.getValue() + "\"");
            }
            
            // 输出每个单元格的详细信息
            System.out.println("\n单元格详细信息:");
            for (TableCell cell : table.cells) {
                System.out.println("  " + cell.toString());
            }
        }
        
        if (tables.isEmpty()) {
            System.out.println("未检测到表格结构");
        }
    }
}