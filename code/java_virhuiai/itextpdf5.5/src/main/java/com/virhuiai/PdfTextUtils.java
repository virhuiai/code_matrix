package com.virhuiai;


import TableStrategy.LocTextExtractionStrategy;
import TableStrategy.TextChunk;
import com.itextpdf.text.pdf.parser.Vector;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * PDF文本处理工具类
 * 提供文本过滤、排序等功能
 */
public class PdfTextUtils {
    private static final Log logger = CshLogUtils.createLogExtended(PdfTextUtils.class);

    /**
     * 通过反射获取TextChunk的方向向量
     */
    private static Vector getOrientationVector(TextChunk textChunk)
            throws Exception {
        return ReflectionUtils.fetchObjResult(textChunk.getLocation(), "orientationVector");
    }

    /**
     * 过滤掉倾斜的文字
     * PDFTextUtils.filterSkewedText
     * @param textChunks 原始文本块列表
     * @return 只包含非倾斜文字的列表
     */
    public static List<TextChunk> filterSkewedText(
            List<TextChunk> textChunks) {

        if (textChunks == null || textChunks.isEmpty()) {
            return new ArrayList<>();
        }

        List<TextChunk> filteredChunks = new ArrayList<>();

        for (TextChunk chunk : textChunks) {
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

    //////





    /**
     * 获取TextChunk的起始Y坐标
     */
    private static float getStartY(TextChunk chunk) {
        try {
            return chunk.getStartLocation().get(Vector.I2); // Y坐标
        } catch (Exception e) {
            logger.error("获取文本块Y坐标失败", e);
            return 0f;
        }
    }

    /**
     * 获取TextChunk的起始X坐标
     */
    private static float getStartX(TextChunk chunk) {
        try {
            return chunk.getStartLocation().get(Vector.I1); // X坐标
        } catch (Exception e) {
            logger.error("获取文本块X坐标失败", e);
            return 0f;
        }
    }


    /**
     * 按起始位置的Y坐标从大到小排序（从上到下）
     * 在PDF坐标系中，y轴是向上的，所以y值越大表示位置越高。如果要从大到小排序，就是从上到下排序。
     * @param locationalResult 需要排序的文本块列表
     * @return 排序后的新列表
     */
    public static List<TextChunk> sortByYDescending(
            List<TextChunk> locationalResult) {

        if (locationalResult == null || locationalResult.isEmpty()) {
            return new ArrayList<>();
        }

        // 创建新列表以避免修改原列表
        List<TextChunk> sortedList =
                new ArrayList<>(locationalResult);

        // 使用自定义比较器排序
        Collections.sort(sortedList, new Comparator<TextChunk>() {
            @Override
            public int compare(TextChunk chunk1,
                               TextChunk chunk2) {
                try {
                    float y1 = getStartY(chunk1);
                    float y2 = getStartY(chunk2);

                    // 从大到小排序（降序）
                    return Float.compare(y2, y1);
                } catch (Exception e) {
                    logger.warn("无法比较文本块Y坐标", e);
                    // 如果无法获取Y坐标，保持原顺序
                    return 0;
                }
            }
        });

        return sortedList;
    }

    /**
     * 按起始位置的X坐标从小到大排序（从左到右）
     * @param locationalResult 需要排序的文本块列表
     * @return 排序后的新列表
     */
    public static List<TextChunk> sortByXAscending(
            List<TextChunk> locationalResult) {

        if (locationalResult == null || locationalResult.isEmpty()) {
            return new ArrayList<>();
        }

        // 创建新列表以避免修改原列表
        List<TextChunk> sortedList =
                new ArrayList<>(locationalResult);

        // 使用自定义比较器排序
        Collections.sort(sortedList, new Comparator<TextChunk>() {
            @Override
            public int compare(TextChunk chunk1,
                               TextChunk chunk2) {
                try {
                    float x1 = getStartX(chunk1);
                    float x2 = getStartX(chunk2);

                    // 从小到大排序（升序）
                    return Float.compare(x1, x2);
                } catch (Exception e) {
                    logger.warn("无法比较文本块X坐标", e);
                    return 0;
                }
            }
        });

        return sortedList;
    }

    /**
     * 取所有位置结果
     *
     * @return
     */
    public static List<TextChunk> fetchLocationalResult(LocTextExtractionStrategy strategy) {
        try {
            Class clazz = strategy.getClass();
            // 获取私有字段
            Field privateField = clazz.getDeclaredField("locationalResult");
            // 设置可访问性为true，以绕过访问控制检查
            privateField.setAccessible(true);
            // 通过反射读取父类的私有字段值
            @SuppressWarnings("unchecked")
            List<TextChunk> value = (List<TextChunk>) privateField.get(strategy);
            return value;
        } catch (Exception e) {
            // todo LOG
            throw new CommonRuntimeException("XXXX", "获取位置信息列表失败");
        }
    }
}
