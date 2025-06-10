package com.virhuiai;

import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.Vector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PdfUtils {


    /**
     * 取所有位置结果
     *
     * @return
     */
    public static List<LocationTextExtractionStrategy.TextChunk> fetchLocationalResult(LocationTextExtractionStrategy strategy) {
        try {
            Class clazz = strategy.getClass();
            // 获取私有字段
            Field privateField = clazz.getDeclaredField("locationalResult");
            // 设置可访问性为true，以绕过访问控制检查
            privateField.setAccessible(true);
            // 通过反射读取父类的私有字段值
            @SuppressWarnings("unchecked")
            List<LocationTextExtractionStrategy.TextChunk> value = (List<LocationTextExtractionStrategy.TextChunk>) privateField.get(strategy);
            return value;
        } catch (Exception e) {
            // todo LOG
            throw new CommonRuntimeException("XXXX", "获取位置信息列表失败");
        }
    }


    /**
     * 取第一个获取到的文字的位置
     *
     * @param containText
     * @return
     */
    public static Vector getFstContainTextVec(List<LocationTextExtractionStrategy.TextChunk> locationalResult, String containText) {
        Iterator var1 = locationalResult.iterator();
        while (var1.hasNext()) {
            LocationTextExtractionStrategy.TextChunk location = (LocationTextExtractionStrategy.TextChunk) var1.next();
            // 文本开始位置的坐标
            Vector startLocation = location.getStartLocation();
            //x 坐标是从左到右增加的
            float startX = startLocation.get(Vector.I1);
            //y 坐标是从下到上增加的
            float startY = startLocation.get(Vector.I2);
            String text = location.getText();
            if (text.contains(containText)) {
                return startLocation;
            }


        }
        return null;
    }

    /**
     * 取在指定坐标范围内的位置结果
     *
     * @param beforeVec
     * @param afterVec
     * @return
     */
    public static List<LocationTextExtractionStrategy.TextChunk> getLocationalResultBetween(List<LocationTextExtractionStrategy.TextChunk> locationalResult, Vector beforeVec, Vector afterVec) {
        List<LocationTextExtractionStrategy.TextChunk> rsList = new ArrayList<>();

        Iterator var1 = locationalResult.iterator();
        while (var1.hasNext()) {
            LocationTextExtractionStrategy.TextChunk location = (LocationTextExtractionStrategy.TextChunk) var1.next();
            // 文本开始位置的坐标
            Vector startLocation = location.getStartLocation();
            //x 坐标是从左到右增加的
            float startX = startLocation.get(Vector.I1);
            //y 坐标是从下到上增加的
            float startY = startLocation.get(Vector.I2);

            //y 坐标是从下到上增加的
            float beforeY = beforeVec.get(Vector.I2);
            float afterY = afterVec.get(Vector.I2);
            if (startY < beforeY && startY > afterY) {
                rsList.add(location);
            }
        }


        return rsList;
    }

    /**
     * 解析二维表结果（需要不换行）
     * @param locationalResultBetween
     * @return
     */
    public static Map<String, String> parseMapOfTwoColumnTableByLocationalResult(List<LocationTextExtractionStrategy.TextChunk> locationalResultBetween){
        if(null == locationalResultBetween || locationalResultBetween.isEmpty()){
            throw new CommonRuntimeException("XXXX","位置信息列表不能为空");
        }

        boolean isEven = locationalResultBetween.size() % 2 == 0;
        if (!isEven) {
            throw new CommonRuntimeException("XXXX","位置信息列表数量需要为偶数");
        }

        // List<MyLocationTextExtractionStrategy.TextChunk> locationalResultBetween
        // 先按y轴降序
        Collections.sort(locationalResultBetween, (chunk1, chunk2) -> {
            Vector startLocation1 = chunk1.getStartLocation();
            Vector startLocation2 = chunk2.getStartLocation();

            float startY1 = startLocation1.get(Vector.I2);
            float startY2 = startLocation2.get(Vector.I2);
            return Float.compare(startY2, startY1);
        });

        Map<String,String> rs = new HashMap<>();
        for(int i=0;i<locationalResultBetween.size();i=i+2){
            LocationTextExtractionStrategy.TextChunk loc1 = locationalResultBetween.get(i);
            LocationTextExtractionStrategy.TextChunk loc2 = locationalResultBetween.get(i+1);
            // x的比较
            if(loc1.getStartLocation().get(Vector.I1) < loc2.getStartLocation().get(Vector.I1)){
                rs.put(loc1.getText(), loc2.getText());
            }else{
                rs.put(loc2.getText(), loc1.getText());
            }

        }

        return rs;
    }

    // y最接近的当同一行
    // 解析二维表
    public static void analyzeTwoColumnTable(String filePath, int pageNumber) {
        // todo ValidationUtils.checkFileSize3(filePath)

//        CloseablePdfReader reader = null;
        //在try-with-resources语句中使用
        try (CloseablePdfReader reader = new CloseablePdfReader(filePath);) {
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            LocationTextExtractionStrategy strategy = parser.processContent(pageNumber, new LocationTextExtractionStrategy());

            List<LocationTextExtractionStrategy.TextChunk> locationalResult = fetchLocationalResult(strategy);

            Vector beforeVec = getFstContainTextVec(locationalResult, "xxxx");
            Vector afterVec = getFstContainTextVec(locationalResult, "xxxx");
            if (null == beforeVec || null == afterVec) {
                throw new CommonRuntimeException("XXXX", "未找到边界文字");
            }

            List<LocationTextExtractionStrategy.TextChunk> locationalResultBetween = getLocationalResultBetween(locationalResult, beforeVec, afterVec);
            if (null == locationalResultBetween || locationalResultBetween.isEmpty()) {
                throw new CommonRuntimeException("XXXX", "未找到边界内的文字");
            }
//
//
            Map<String, String> mapOfTwoColumnTable = parseMapOfTwoColumnTableByLocationalResult(locationalResultBetween);


        } catch (Exception e) {
//            LOG.error("失败：" + filePath, e);
            throw new CommonRuntimeException("XXXX", "解析失败");
        }

    }


    ////////////////////




    public static void try2(String filePath, int pageNumber) {
        // todo ValidationUtils.checkFileSize3(filePath)

//        CloseablePdfReader reader = null;
        //在try-with-resources语句中使用
        try (CloseablePdfReader reader = new CloseablePdfReader(filePath);) {
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TableAndLocationTextExtractionStrategy strategy = parser.processContent(pageNumber, new TableAndLocationTextExtractionStrategy());



            List<Line2D> lineList = strategy.getCurrentLineList();

//            List<Point2D.Float> points = new ArrayList<>();
            // 方法5：详细信息输出（包含线段长度）
//            System.out.println("\n=== 详细线段信息 ===");
//            for (int i = 0; i < lineList.size(); i++) {
//                Line2D line = lineList.get(i);
//
//                double x1 = line.getX1();
//                double y1 = line.getY1();
//                double x2 = line.getX2();
//                double y2 = line.getY2();
//
//                // 计算线段长度
//                double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
//
//                System.out.println("线段 " + i + ":");
//                System.out.printf("  起点: (%.2f, %.2f)%n", x1, y1);
//                System.out.printf("  终点: (%.2f, %.2f)%n", x2, y2);
//                System.out.printf("  长度: %.2f%n", length);
//                System.out.println();
//
////                points.add(new Point2D.Float((float)x1, (float)y1));
////                points.add(new Point2D.Float((float)x2, (float)y2));
//            }


//            PdfCircleDrawer.drawCirclesOnPdf("/Volumes/RamDisk/tzs书.pdf", "/Volumes/RamDisk/tzs书222.pdf", points, 1);

            // 将相连的线段分组
//            List<List<Line2D>> groupConnectedLines = strategy.groupConnectedLines(lineList);

            // 分析表格单元格
            List<List<PdfCellPos>> cellGroups = new TableCellAnalyzer().analyzeTableCells(lineList);

            List<LocationTextExtractionStrategy.TextChunk> locationalResult = ReflectionUtils.fetchObjResultFromSuperClass(strategy, "locationalResult");
//             过滤掉非黑色的文字 todo  TextRenderInfo 的需要单独保存
//            locationalResult = strategy.filterNonBlackText(locationalResult);
            // 过滤掉倾斜的文字
            locationalResult = PDFTextUtils.filterSkewedText(locationalResult);
            // 按起始位置的Y坐标从大到小排序（从上到下）  用于换行
            locationalResult = strategy.sortByYDescending(locationalResult);






            // 输出结果
            for (int i = 0; i < cellGroups.size(); i++) {
                System.out.println("表格组 " + (i + 1) + ":");
                List<PdfCellTextPos> cellTextList = new ArrayList<>();

                List<PdfCellPos> cells = cellGroups.get(i);
//                cells = PdfCellSorter.sortCells(cells);
//                cells = PdfCellSorter.sortCellsAdvanced(cells);

                for (int j = 0; j < cells.size(); j++) {
                    PdfCellPos cell = cells.get(j);
                    System.out.println("  单元格 " + (j + 1) + " 的四个坐标点:");
                    System.out.println("  单元格 " + (j + 1) + " getxLeft:" + cell.getxLeft());
                    System.out.println("  单元格 " + (j + 1) + " getxRight:" + cell.getxRight());
                    System.out.println("  单元格 " + (j + 1) + " getyTop:" + cell.getyTop());
                    System.out.println("  单元格 " + (j + 1) + " getyBtm:" + cell.getyBtm());


                    PdfCellTextPos.Builder textPos = new PdfCellTextPos.Builder();

                    StringBuilder textSb = new StringBuilder();

                    float sx = 0f;// 存最后
                    float sy = 0f;

                    //List<LocationTextExtractionStrategy.TextChunk> locationalResult = strategy.getLocationalResult();
                    for (LocationTextExtractionStrategy.TextChunk textChunk : locationalResult) {
                        try{
                            Vector sl = textChunk.getStartLocation();


                            Vector el = textChunk.getEndLocation();

//                            if(textChunk.getText().equals("债务融资工具名称")){
//                                int a = 3;
//                            }




                            // 块的起始位置x
                            if(sl.get(Vector.I1) > cell.getxLeft()
                            &&
                              //块的起始位置y
                                    sl.get(Vector.I2) < cell.getyTop() //

                                    && el.get(Vector.I1) < cell.getxRight()
                                    && el.get(Vector.I2) > cell.getyBtm()
                            ){
                                textSb.append(textChunk.getText());
                                sx = sl.get(Vector.I1);
                                sy = sl.get(Vector.I2);


//                                textPos.xLeft(sl.get(Vector.I1));// 使用最后一个即可
//                                textPos.xRight(el.get(Vector.I1));
//                                textPos.yTop(sl.get(Vector.I2));
//                                textPos.yBtm(el.get(Vector.I2));
                                textPos.xLeft(cell.getxLeft());// 使用最后一个即可
                                textPos.xRight(cell.getxRight());
                                textPos.yTop(cell.getyTop());
                                textPos.yBtm(cell.getyBtm());

                            }








//                            System.out.println("块的方向上的单位向量orientationVector:" + orientationVector);
//
//                            int orientationMagnitude = fetchObjResult(textChunk.getLocation(), "orientationMagnitude");
//                            System.out.println("方向的标量值，用于快速排序orientationMagnitude:" + orientationMagnitude);
//
//                            int distPerpendicular = fetchObjResult(textChunk.getLocation(), "distPerpendicular");
//                            System.out.println("到方向单位向量的垂直距离（即在未旋转坐标系统中的Y位置）我们将其四舍五入到最接近的整数:" + distPerpendicular);
//
//
//                            float distParallelStart = fetchObjResult(textChunk.getLocation(), "distParallelStart");
//                            System.out.println("块起点沿方向单位向量的距离（即在未旋转坐标系统中的X位置）distParallelStart:" + distParallelStart);
//
//
//                            float distParallelEnd = fetchObjResult(textChunk.getLocation(), "distParallelEnd");
//                            System.out.println("块终点沿方向单位向量的距离（即在未旋转坐标系统中的X位置）distParallelEnd:" + distParallelEnd);
//
//                            float charSpaceWidth = fetchObjResult(textChunk.getLocation(), "charSpaceWidth");
//                            System.out.println("字块中单个空格字符在字体中的宽度charSpaceWidth:" + charSpaceWidth);
//
//                            System.out.println("");
//                            System.out.println("");
                        }catch (Exception e){
                            System.out.println("出错:" + textChunk);
                        }





                        ///** 块起点沿方向单位向量的距离（即在未旋转坐标系统中的X位置） */
                        //private final float distParallelStart;
                        ///** 块终点沿方向单位向量的距离（即在未旋转坐标系统中的X位置） */
                        //private final float distParallelEnd;
                        ///** 字块中单个空格字符在字体中的宽度 */
                        //private final float charSpaceWidth;


//                4. **orientationMagnitude**: 这个值表示文本方向向量的长度。在这个例子中，长度为 785。
//                5. **distPerpendicular**: 这个值表示文本相对于某个参考线的垂直距离。在这个例子中，距离为 -87。
//                6. **distParallelStart**: 这个值表示文本开始位置相对于某个参考点的平行距离。在这个例子中，距离为 223.04976。
//                7. **distParallelEnd**: 这个值表示文本结束位置相对于某个参考点的平行距离。在这个例子中，距离为 793.05237。
//                8. **charSpaceWidth**: 这个值表示字符之间的间距。在这个例子中，间距为 30.000141。

                    }
                    textPos.text(textSb.toString());
                    System.out.println("  Text:" + textSb.toString());
                    System.out.println("  存最后位置x:" + sx);
                    System.out.println("  存最后位置y:" + sy);
                    cellTextList.add(textPos.build());



//                    System.out.println("块的起始位置x:" + sl.get(Vector.I1));
//                    System.out.println("块的起始位置y:" + sl.get(Vector.I2));
//                    System.out.println("块的结束位置x:" + el.get(Vector.I1));
//                    System.out.println("块的结束位置y:" + el.get(Vector.I2));

//                    if(0==i && 0==j){
//                        List<Point2D.Float> points = new ArrayList<>();
//                        points.add(new Point2D.Float((float)cell.getX(), (float)cell.getY()));
//                        points.add(new Point2D.Float((float)(cell.getX() + cell.getWidth()), (float)cell.getY()));
//                        points.add(new Point2D.Float((float)cell.getX(), (float)(cell.getY() + cell.getHeight())));
//                        points.add(new Point2D.Float((float)(cell.getX() + cell.getWidth()), (float)(cell.getY())));
//
//                        PdfCircleDrawer.drawCirclesOnPdf("/Volumes/RamDisk/tzs书.pdf", "/Volumes/RamDisk/tzs书222.pdf", points, 1);
//                    }


                }

                List<PdfCellTextPos>  cellTextList2 = PdfCellSorter.sortCells(cellTextList);
//                System.out.println("最终输出：");
//                for (PdfCellTextPos pdfCellTextPos : cellTextList2) {
//                    System.out.printf(String.valueOf(pdfCellTextPos));
//                }
            }




        } catch (Exception e) {
//            LOG.error("失败：" + filePath, e);
            throw new CommonRuntimeException("XXXX", "解析失败");
        }

    }


    public static void main(String[] args) {
        try2("/Volumes/RamDisk/tzs书.pdf", 1);
        System.out.println("abc");
    }
}
