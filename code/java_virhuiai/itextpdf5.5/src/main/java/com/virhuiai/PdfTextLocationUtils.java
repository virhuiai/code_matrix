package com.virhuiai;

import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PDF文本位置工具类
 */
public class PdfTextLocationUtils {

    /**
     * 取第一个获取到的文字的位置
     *
     * @param locationalResult 位置结果列表
     * @param containText 包含的文本
     * @return 文本位置向量
     */
    public static Vector getFstContainTextVec(
            List<LocationTextExtractionStrategy.TextChunk> locationalResult,
            String containText) {
        Iterator<LocationTextExtractionStrategy.TextChunk> var1 = locationalResult.iterator();
        while (var1.hasNext()) {
            LocationTextExtractionStrategy.TextChunk location = var1.next();
            Vector startLocation = location.getStartLocation();
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
     * @param locationalResult 位置结果列表
     * @param beforeVec 上边界向量
     * @param afterVec 下边界向量
     * @return 范围内的位置结果列表
     */
    public static List<LocationTextExtractionStrategy.TextChunk> getLocationalResultBetween(
            List<LocationTextExtractionStrategy.TextChunk> locationalResult,
            Vector beforeVec,
            Vector afterVec) {
        List<LocationTextExtractionStrategy.TextChunk> rsList = new ArrayList<>();

        Iterator<LocationTextExtractionStrategy.TextChunk> var1 = locationalResult.iterator();
        while (var1.hasNext()) {
            LocationTextExtractionStrategy.TextChunk location = var1.next();
            Vector startLocation = location.getStartLocation();
            float startY = startLocation.get(Vector.I2);

            float beforeY = beforeVec.get(Vector.I2);
            float afterY = afterVec.get(Vector.I2);
            if (startY < beforeY && startY > afterY) {
                rsList.add(location);
            }
        }

        return rsList;
    }
}
