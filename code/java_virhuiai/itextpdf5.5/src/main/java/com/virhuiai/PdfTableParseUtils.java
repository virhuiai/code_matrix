package com.virhuiai;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.Vector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * PDF表格单元格分析工具类
 * 用于遍历线段、分组相连或相交的线段，并分析表格单元格
 */
public class PdfTableParseUtils {
    // 容差值，用于判断点是否相近
    private static final double TOLERANCE = 1e-6;
    private static final double TOLERANCE_INTERSECTS = 2;

    /**
     * 私有构造函数，防止实例化
     */
    private PdfTableParseUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 主方法：分析线段并提取表格单元格
     *
     * @param lineList 线段列表
     * @return 表格单元格分组列表
     */
    public static List<List<PdfCellPos>> analyzeTableCells(List<Line2D> lineList) {
        // 1. 将相连或相交的线段分组
        List<List<Line2D>> groupedLines = groupConnectedLines(lineList);

        // 2. 对每个分组分析表格单元格
        List<List<PdfCellPos>> allCells = new ArrayList<>();
        for (List<Line2D> group : groupedLines) {
            List<PdfCellPos> cells = extractCellsFromLineGroup(group);
            if (!cells.isEmpty()) {
                allCells.add(cells);
            }
        }

        return allCells;
    }

    /**
     * 将相连或相交的线段分组
     *
     * @param lineList 线段列表
     * @return 分组后的线段列表
     */
    private static List<List<Line2D>> groupConnectedLines(List<Line2D> lineList) {
        int n = lineList.size();
        UnionFind uf = new UnionFind(n);

        // 检查每对线段是否相连或相交
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnectedOrIntersecting(lineList.get(i), lineList.get(j))) {
                    uf.union(i, j);
                }
            }
        }

        // 根据并查集结果分组
        Map<Integer, List<Line2D>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            //如果groups中已存在键root，直接返回对应的List
            //如果不存在键root，则创建一个新的ArrayList并放入map中，然后返回这个新列表
            //k -> new ArrayList<>()是Lambda表达式，等价于创建新的空列表
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(lineList.get(i));
        }

        return new ArrayList<>(groups.values());
    }

    /**
     * 判断两条线段是否相连或相交
     *
     * @param line1 第一条线段
     * @param line2 第二条线段
     * @return 是否相连或相交
     */
    private static boolean isConnectedOrIntersecting(Line2D line1, Line2D line2) {
        // 检查是否相交
        if (line1.intersectsLine(line2)) {
            return true;
        }

        // 检查端点是否相连
        Point2D p1Start = line1.getP1();
        Point2D p1End = line1.getP2();
        Point2D p2Start = line2.getP1();
        Point2D p2End = line2.getP2();

        return isPointsClose(p1Start, p2Start) || isPointsClose(p1Start, p2End) ||
                isPointsClose(p1End, p2Start) || isPointsClose(p1End, p2End);
    }

    /**
     * 判断两个点是否足够接近
     *
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 是否接近
     */
    private static boolean isPointsClose(Point2D p1, Point2D p2) {
        return p1.distance(p2) < TOLERANCE;
    }

    /**
     * 这段代码的目的是从一组线段中识别并提取出表格的单元格位置。
     *
     * 具体步骤
     *
     * 线段分类
     *
     * 遍历所有输入的线段
     * 将水平线和垂直线分别存储到不同的列表中
     * 线段排序
     *
     * 水平线按Y坐标从大到小排序（因为PDF坐标系左下角为原点）
     * 垂直线按X坐标从小到大排序（从左到右）
     * 查找单元格
     *
     * 遍历所有相邻的水平线对（从上到下）
     * 对于每一对水平线：
     * 如果两条线的Y坐标相同，跳过（避免重复处理）
     * 找出所有同时与这两条水平线相交的垂直线
     * 将找到的垂直线按顺序存储
     * 生成单元格
     *
     * 对于每对相邻的垂直线：
     * 使用上下两条水平线的Y坐标作为单元格的上下边界
     * 使用左右两条垂直线的X坐标作为单元格的左右边界
     * 创建一个矩形单元格对象
     * 核心原理
     *
     * 通过寻找由两条水平线和两条垂直线围成的矩形区域来识别表格单元格。每个单元格必须满足：
     *
     * 有上下两条水平边界线
     * 有左右两条垂直边界线
     * 这四条线相互相交形成封闭矩形
     * @param lines
     * @return
     */
    private static List<PdfCellPos> extractCellsFromLineGroup(List<Line2D> lines) {
        // 分离水平线和垂直线
        List<Line2D> horizontalLines = new ArrayList<>();
        List<Line2D> verticalLines = new ArrayList<>();

        for (Line2D line : lines) {
            if (isHorizontal(line)) {
                horizontalLines.add(line);
            } else if (isVertical(line)) {
                verticalLines.add(line);
            }
        }

        // 按坐标排序
        horizontalLines.sort((a, b) -> Double.compare(b.getY1(), a.getY1()));
        verticalLines.sort((a, b) -> Double.compare(a.getX1(), b.getX1()));

        List<PdfCellPos> cells = new ArrayList<>();

        // 遍历相邻的水平线对
        for (int i = 0; i < horizontalLines.size() - 1; i++) {
            Line2D topLine = horizontalLines.get(i);
            Line2D bottomLine = horizontalLines.get(i + 1);

            if (Math.abs(topLine.getY1() - bottomLine.getY1()) < TOLERANCE) {
                continue;  // 跳过y坐标相同的线
            }

            // 找出所有与这两条水平线都相交的垂直线
            List<Line2D> intersectingVerticals = new ArrayList<>();

            // 修正：遍历所有垂直线
            for (int i1 = 0; i1 < verticalLines.size(); i1++) {
                Line2D vertical = verticalLines.get(i1);
                if (intersectsWithTolerance(topLine,vertical) &&
                        intersectsWithTolerance(bottomLine, vertical)) {
                    intersectingVerticals.add(vertical);
                }
            }

            // 根据相邻的垂直线创建单元格
            for (int j = 0; j < intersectingVerticals.size() - 1; j++) {
                Line2D leftLine = intersectingVerticals.get(j);
                Line2D rightLine = intersectingVerticals.get(j + 1);

                PdfCellPos cell = PdfCellPos.builder()
                        .yTop(topLine.getY1())
                        .yBtm(bottomLine.getY1())
                        .xLeft(leftLine.getX1())
                        .xRight(rightLine.getX1())
                        .build();
                cells.add(cell);

//                System.out.printf("\n行：" + i);
//                System.out.printf("格：" + j);
//                System.out.printf("topLine.getY1()：" + topLine.getY1());
//                System.out.printf("bottomLine.getY1()：" + bottomLine.getY1());
//                System.out.printf("leftLine.getX1()：" + leftLine.getX1());
//                System.out.printf("rightLine.getX1()：" + rightLine.getX1());
            }
        }

        return cells;
    }

    /**
     * 判断垂直线是否与水平线相交（带误差容许）
     *
     * @param horizontal 水平线
     * @param vertical 垂直线
     * @return 是否相交
     */
    private static boolean intersectsWithTolerance(Line2D horizontal, Line2D vertical) {
        // 获取垂直线的X坐标
        double verticalX = vertical.getX1();

        // 获取垂直线的Y范围
        double verticalMinY = Math.min(vertical.getY1(), vertical.getY2());
        double verticalMaxY = Math.max(vertical.getY1(), vertical.getY2());

        // 获取水平线的X范围
        double horizontalMinX = Math.min(horizontal.getX1(), horizontal.getX2());
        double horizontalMaxX = Math.max(horizontal.getX1(), horizontal.getX2());

        // 获取水平线的Y坐标
        double horizontalY = horizontal.getY1();

        // 判断是否相交（带误差容许）
        boolean xInRange = verticalX >= horizontalMinX - TOLERANCE_INTERSECTS &&
                verticalX <= horizontalMaxX + TOLERANCE_INTERSECTS;
        boolean yInRange = horizontalY >= verticalMinY - TOLERANCE_INTERSECTS &&
                horizontalY <= verticalMaxY + TOLERANCE_INTERSECTS;

        return xInRange && yInRange;
    }

    /**
     * 判断线段是否水平
     *
     * @param line 线段
     * @return 是否水平
     */
    private static boolean isHorizontal(Line2D line) {
        return Math.abs(line.getY1() - line.getY2()) < TOLERANCE;
    }

    /**
     * 判断线段是否垂直
     *
     * @param line 线段
     * @return 是否垂直
     */
    private static boolean isVertical(Line2D line) {
        return Math.abs(line.getX1() - line.getX2()) < TOLERANCE;
    }

    /**
     * 并查集实现，用于分组相连的线段
     */
    private static class UnionFind {
        private int[] parent;  // 存储每个节点的父节点
        // parent[i]：节点i的父节点索引，如果parent[i] == i则i是根节点
        private int[] rank;    // 存储以该节点为根的树的高度（秩）
        //rank[i]：以节点i为根的树的高度，用于优化合并操作

        /**
         * 初始化时，每个元素都是独立的集合，自己是自己的根节点。
         * @param n
         */
        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;    // 初始时每个节点都是自己的父节点
                rank[i] = 0;      // 初始高度为0
            }
        }

        /**
         * 查找操作（路径压缩优化）
         *
         这里使用了路径压缩优化：

         在查找根节点的过程中，将路径上的所有节点直接连接到根节点
         大大减少了后续查找的时间复杂度
         例如：查找路径 5→3→1→0，压缩后变成 5→0, 3→0, 1→0
         * @param x
         * @return
         */
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);//// 路径压缩
            }
            return parent[x];
        }

        /**
         * 合并操作（按秩合并优化）
         *
         使用了按秩合并优化：

         总是将高度较小的树接到高度较大的树下面
         保持整体树的高度尽可能小
         避免树退化成链表
         *
         * @param x
         * @param y
         */
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;        // 低树接到高树下
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;        // 低树接到高树下
                } else {
                    parent[rootY] = rootX;        // 高度相同时任选一个
                    rank[rootX]++;                // 增加根节点的高度
                }
            }
        }
    }

    ////////


    /**
     * 提取和处理文本块 过滤 排序
     */
    private static List<LocationTextExtractionStrategy.TextChunk> extractAndProcessTextChunks(
            TableAndLocationTextExtractionStrategy strategy) {
        List<LocationTextExtractionStrategy.TextChunk> locationalResult =
                ReflectionUtils.fetchObjResultFromSuperClass(strategy, "locationalResult");

        // 过滤掉倾斜的文字
        locationalResult = PdfTextUtils.filterSkewedText(locationalResult);

        // 按起始位置的Y坐标从大到小排序（从上到下）
        locationalResult = PdfTextUtils.sortByYDescending(locationalResult);

        return locationalResult;
    }




    /**
     * 判断文本块是否在单元格内
     */
    private static boolean isTextInCell(LocationTextExtractionStrategy.TextChunk textChunk,
                                        PdfCellPos cell) {
        com.itextpdf.text.pdf.parser.Vector sl = textChunk.getStartLocation();
        com.itextpdf.text.pdf.parser.Vector el = textChunk.getEndLocation();

        return sl.get(com.itextpdf.text.pdf.parser.Vector.I1) > cell.getxLeft()
                && sl.get(com.itextpdf.text.pdf.parser.Vector.I2) < cell.getyTop()
                && el.get(com.itextpdf.text.pdf.parser.Vector.I1) < cell.getxRight()
                && el.get(com.itextpdf.text.pdf.parser.Vector.I2) > cell.getyBtm();
    }

    /**
     * 从单个单元格中提取文本
     */
    private static PdfCellTextPos extractTextFromCell(
            PdfCellPos cell,
            List<LocationTextExtractionStrategy.TextChunk> locationalResult) {

        PdfCellTextPos.Builder textPos = new PdfCellTextPos.Builder();
        StringBuilder textSb = new StringBuilder();

        // 设置单元格边界
        textPos.xLeft(cell.getxLeft())
                .xRight(cell.getxRight())
                .yTop(cell.getyTop())
                .yBtm(cell.getyBtm());

        // 遍历所有文本块，找出在单元格内的文本
        for (LocationTextExtractionStrategy.TextChunk textChunk : locationalResult) {
            try {
                if (isTextInCell(textChunk, cell)) {
                    textSb.append(textChunk.getText());
                }
            } catch (Exception e) {
                System.out.println("出错:" + textChunk);
            }
        }

        textPos.text(textSb.toString());
        return textPos.build();
    }

    /**
     * 从单元格中提取文本
     */
    private static List<PdfCellTextPos> extractCellTexts(
            List<PdfCellPos> cells,
            List<LocationTextExtractionStrategy.TextChunk> locationalResult) {

        List<PdfCellTextPos> cellTextList = new ArrayList<>();

        for (PdfCellPos cell : cells) {
            PdfCellTextPos cellText = extractTextFromCell(cell, locationalResult);
            cellTextList.add(cellText);
        }

        return cellTextList;
    }

    /**
     * 清理文本，去除前后空格和不可见字符
     */
    private static String cleanText(String text) {
        if (text == null) {
            return "";
        }

        // 方案1：简单的trim（只去除前后空格）
//         return text.trim();


        // 方案2：更全面的清理，去除所有不可见字符
        return text.replaceAll("^[\\s\\p{C}]+|[\\s\\p{C}]+$", "");
    }

    /**
     * 构建结果映射（两列表格，或者两个两个是对应的键和值的情况）
     */
    private static Map<String, String> buildResultMap(List<PdfCellTextPos> cellTextList) {
        // 使用LinkedHashMap保持插入顺序
        Map<String, String> rsItem = new LinkedHashMap<>();

        for (int i = 0; i < cellTextList.size(); i += 2) {
            PdfCellTextPos loc1 = cellTextList.get(i);
            PdfCellTextPos loc2 = cellTextList.get(i + 1);

            // 获取并清理文本
            String text1 = cleanText(loc1.getText());
            String text2 = cleanText(loc2.getText());

            // x的比较，左边的作为key，右边的作为value
            if (loc1.getxLeft() < loc2.getxLeft()) {
                rsItem.put(text1, text2);
            } else {
                rsItem.put(text2, text1);
            }
        }

        return rsItem;
    }

    /**
     * 处理单个单元格组
     */
    private static Map<String, String> processSingleCellGroup(
            List<PdfCellPos> cells,
            List<LocationTextExtractionStrategy.TextChunk> locationalResult) {

        List<PdfCellTextPos> cellTextList = extractCellTexts(cells, locationalResult);

        // 检查是否为偶数
        if (cellTextList.size() % 2 != 0) {
            throw new CommonRuntimeException("XXXX", "位置信息列表数量需要为偶数");
        }

        return buildResultMap(cellTextList);
    }


    /**
     * 处理所有单元格组
     */
    private static List<Map<String, String>> processCellGroups(
            List<List<PdfCellPos>> cellGroups,
            List<LocationTextExtractionStrategy.TextChunk> locationalResult) {

        List<Map<String, String>> rs = new ArrayList<>();

        for (List<PdfCellPos> cells : cellGroups) {
            Map<String, String> rsItem = processSingleCellGroup(cells, locationalResult);
            if (rsItem != null && !rsItem.isEmpty()) {
                rs.add(rsItem);
            }
        }

        return rs;
    }
    //////////////
    /**
     * 分析复杂表格结构
     它做了很多事情：
     > 1. 读取PDF文件
     > 2. 提取线段
     > 3. 分析表格单元格
     > 4. 获取文本位置信息
     > 5. 过滤文本
     > 6. 匹配文本到单元格
     > 7. 构建结果
     *
     将其拆分成多个更小的方法，每个方法负责一个特定的任务。这样会让代码更容易理解和维护。
     >
     > 主要可以拆分为：
     > 1. 主方法 - analyzeComplexTable
     > 2. 提取和处理文本块 - extractAndProcessTextChunks
     > 3. 处理单元格组 - processCellGroups
     > 4. 匹配文本到单元格 - matchTextToCell
     > 5. 构建结果映射 - buildResultMap
     * @param filePath
     * @param pageNumber
     * @return
     */
    public static List<Map<String, String>> analyzeComplexTable(String filePath, int pageNumber) {
        List<Map<String, String> > rs = new ArrayList<>();
        // todo ValidationUtils.checkFileSize3(filePath)
        //在try-with-resources语句中使用
        try (CloseablePdfReader reader = new CloseablePdfReader(filePath);) {
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TableAndLocationTextExtractionStrategy strategy = parser.processContent(pageNumber, new TableAndLocationTextExtractionStrategy());
            // 取得所有线段
            List<Line2D> lineList = strategy.getCurrentLineList();
            // 分析表格单元格
            List<List<PdfCellPos>> cellGroups = PdfTableParseUtils.analyzeTableCells(lineList);
            // 提取和处理文本块 过滤 排序
            List<LocationTextExtractionStrategy.TextChunk> locationalResult = extractAndProcessTextChunks(strategy);

            // 处理所有单元格组
            return processCellGroups(cellGroups, locationalResult);
        } catch (Exception e) {
//            LOG.error("失败：" + filePath, e);
            throw new CommonRuntimeException("XXXX", "解析失败");
        }

    }

    /**
     * 分析复杂表格并返回指定下标的Map
     *
     * @param filePath 文件路径
     * @param pageNumber 页码
     * @param index 指定的下标
     * @return 指定下标的表格数据
     */
    public static Map<String, String> analyzeComplexTableByIndex(String filePath, int pageNumber, int index) {
        List<Map<String, String>> allTables = analyzeComplexTable(filePath, pageNumber);

        if (allTables == null || allTables.isEmpty()) {
            throw new CommonRuntimeException("XXXX", "未找到任何表格数据");
        }

        if (index < 0 || index >= allTables.size()) {
            throw new CommonRuntimeException("XXXX",
                    String.format("指定的下标 %d 超出范围，有效范围为 0-%d", index, allTables.size() - 1));
        }

        return allTables.get(index);
    }

//    /**
//     * 分析复杂表格并返回指定下标的Map，如果不存在则返回空Map
//     *
//     * @param filePath 文件路径
//     * @param pageNumber 页码
//     * @param index 指定的下标
//     * @return 指定下标的表格数据，如果不存在则返回空Map
//     */
//    public static Map<String, String> analyzeComplexTableByIndexOrEmpty(String filePath, int pageNumber, int index) {
//        try {
//            List<Map<String, String>> allTables = analyzeComplexTable(filePath, pageNumber);
//
//            if (allTables != null && index >= 0 && index < allTables.size()) {
//                return allTables.get(index);
//            }
//        } catch (Exception e) {
//            // 记录日志但不抛出异常
//            // LOG.warn("获取指定下标的表格数据失败", e);
//        }
//
//        return new LinkedHashMap<>(); // 返回空的有序Map
//    }

    /**
     * 分析复杂表格并返回最后一个Map
     *
     * @param filePath 文件路径
     * @param pageNumber 页码
     * @return 最后一个表格数据
     */
    public static Map<String, String> analyzeComplexTableLast(String filePath, int pageNumber) {
        List<Map<String, String>> allTables = analyzeComplexTable(filePath, pageNumber);

        if (allTables == null || allTables.isEmpty()) {
            throw new CommonRuntimeException("XXXX", "未找到任何表格数据");
        }

        return allTables.get(allTables.size() - 1);
    }

    /**
     * 分析复杂表格并返回第一个Map
     *
     * @param filePath 文件路径
     * @param pageNumber 页码
     * @return 第一个表格数据
     */
    public static Map<String, String> analyzeComplexTableFirst(String filePath, int pageNumber) {
        List<Map<String, String>> allTables = analyzeComplexTable(filePath, pageNumber);

        if (allTables == null || allTables.isEmpty()) {
            throw new CommonRuntimeException("XXXX", "未找到任何表格数据");
        }

        return allTables.get(0);
    }

}

