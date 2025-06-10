package com.virhuiai;

import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.Vector;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class PdfTableParseUtils {

    /**
     * 分析复杂表格结构
     * @param filePath
     * @param pageNumber
     */
    public static void analyzeComplexTable(String filePath, int pageNumber) {
        // todo ValidationUtils.checkFileSize3(filePath)
        //在try-with-resources语句中使用
        try (CloseablePdfReader reader = new CloseablePdfReader(filePath);) {
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TableAndLocationTextExtractionStrategy strategy = parser.processContent(pageNumber, new TableAndLocationTextExtractionStrategy());
            // 取得所有线段
            List<Line2D> lineList = strategy.getCurrentLineList();


            // 分析表格单元格
            List<List<PdfCellPos>> cellGroups = TableCellAnalyzer.analyzeTableCells(lineList);

            List<LocationTextExtractionStrategy.TextChunk> locationalResult = ReflectionUtils.fetchObjResultFromSuperClass(strategy, "locationalResult");
//             过滤掉非黑色的文字 todo  TextRenderInfo 的需要单独保存
//            locationalResult = strategy.filterNonBlackText(locationalResult);
            // 过滤掉倾斜的文字
            locationalResult = PdfTextUtils.filterSkewedText(locationalResult);
            // 按起始位置的Y坐标从大到小排序（从上到下）  用于换行
            locationalResult = PdfTextUtils.sortByYDescending(locationalResult);

            // 输出结果
            for (int i = 0; i < cellGroups.size(); i++) {
                List<PdfCellTextPos> cellTextList = new ArrayList<>();

                List<PdfCellPos> cells = cellGroups.get(i);
//                cells = PdfCellSorter.sortCells(cells);
//                cells = PdfCellSorter.sortCellsAdvanced(cells);

                for (int j = 0; j < cells.size(); j++) {
                    PdfCellPos cell = cells.get(j);
//                    System.out.println("  单元格 " + (j + 1) + " 的四个坐标点:");
//                    System.out.println("  单元格 " + (j + 1) + " getxLeft:" + cell.getxLeft());
//                    System.out.println("  单元格 " + (j + 1) + " getxRight:" + cell.getxRight());
//                    System.out.println("  单元格 " + (j + 1) + " getyTop:" + cell.getyTop());
//                    System.out.println("  单元格 " + (j + 1) + " getyBtm:" + cell.getyBtm());


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


                                textPos.xLeft(cell.getxLeft());// 使用最后一个即可
                                textPos.xRight(cell.getxRight());
                                textPos.yTop(cell.getyTop());
                                textPos.yBtm(cell.getyBtm());

                            }

                        }catch (Exception e){
                            System.out.println("出错:" + textChunk);
                        }



                    }
                    textPos.text(textSb.toString());
                    System.out.println("  Text:" + textSb.toString());
                    System.out.println("  存最后位置x:" + sx);
                    System.out.println("  存最后位置y:" + sy);
                    cellTextList.add(textPos.build());




                }


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
}
