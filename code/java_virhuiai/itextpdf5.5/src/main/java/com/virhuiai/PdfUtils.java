package com.virhuiai;

import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PdfUtils {





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

            List<LocationTextExtractionStrategy.TextChunk> locationalResult = PdfTextUtils.fetchLocationalResult(strategy);

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









    public static void main(String[] args) {
        PdfTableParseUtils.analyzeComplexTable("/Volumes/RamDisk/tzs书.pdf", 1);
        System.out.println("abc");
    }
}
