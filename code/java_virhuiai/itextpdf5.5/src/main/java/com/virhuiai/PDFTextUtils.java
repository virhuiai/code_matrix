package com.virhuiai;

import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.Vector;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * PDF文本处理工具类
 * 提供文本过滤、排序等功能
 */
public class PDFTextUtils {
    private static final Log logger = CshLogUtils.createLogExtended(PDFTextUtils.class);

    /**
     * 通过反射获取TextChunk的方向向量
     */
    private static Vector getOrientationVector(LocationTextExtractionStrategy.TextChunk textChunk)
            throws Exception {
        return ReflectionUtils.fetchObjResult(textChunk.getLocation(), "orientationVector");
    }

    /**
     * 过滤掉倾斜的文字
     * PDFTextUtils.filterSkewedText
     * @param textChunks 原始文本块列表
     * @return 只包含非倾斜文字的列表
     */
    public static List<LocationTextExtractionStrategy.TextChunk> filterSkewedText(
            List<LocationTextExtractionStrategy.TextChunk> textChunks) {

        if (textChunks == null || textChunks.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocationTextExtractionStrategy.TextChunk> filteredChunks = new ArrayList<>();

        for (LocationTextExtractionStrategy.TextChunk chunk : textChunks) {
            try {
                // 获取文本的方向向量
                Vector orientation = getOrientationVector(chunk);

                if (orientation != null) {
                    // 获取方向向量的角度
                    float angle = (float) Math.atan2(orientation.get(Vector.I2),
                            orientation.get(Vector.I1));
                    float angleDegrees = (float) Math.toDegrees(angle);

                    // 标准化角度到 -180 到 180 度
                    while (angleDegrees > 180) angleDegrees -= 360;
                    while (angleDegrees < -180) angleDegrees += 360;

                    // 判断是否为水平文本（允许小范围误差）
                    float tolerance = 1.0f; // 容差1度
                    if (Math.abs(angleDegrees) < tolerance ||
                            Math.abs(Math.abs(angleDegrees) - 180) < tolerance) {
                        filteredChunks.add(chunk);
                    } else {
                        logger.debug("过滤倾斜文本: {" + chunk.getText() + "} (角度: {" + angleDegrees + "}度)");
                    }
                }
            } catch (Exception e) {
                logger.warn("无法获取文本方向信息，保留该文本块", e);
                // 如果无法获取方向信息，保留该文本块
                filteredChunks.add(chunk);
            }
        }

        return filteredChunks;
    }
}
