package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextUtils {


    /**
     * 将长文本按指定宽度进行分割，实现自动换行。（适合英文版本）
     * TextUtils.wrapText
     * todo 中文版本，
     * todo 英文单词连词处理
     * todo 标点符号弹性处理
     *
     * @param text 要分割的文本
     * @param maxWidth 每行的最大宽度（单位：点）
     * @param font 使用的字体
     * @param fontSize 字体大小
     * @return 分割后的文本行列表
     * @throws IOException 如果字体计算失败
     */
    public static List<String> wrapText(String text, float maxWidth, PDType1Font font, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        float currentWidth = 0;

        // 按单词分割文本（以空格为分隔符）
        String[] words = text.split(" ");
        for (String word : words) {
            // 计算当前单词的宽度
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;

            // 检查当前行加上该单词后是否超出最大宽度
            if (currentWidth + wordWidth <= maxWidth) {
                // 如果未超出，添加单词到当前行
                currentLine.append(word).append(" ");
                currentWidth += wordWidth + font.getStringWidth(" ") / 1000 * fontSize;
            } else {
                // 如果超出，保存当前行并开始新行
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString().trim());
                }
                currentLine = new StringBuilder(word + " ");
                currentWidth = wordWidth + font.getStringWidth(" ") / 1000 * fontSize;
            }
        }

        // 添加最后一行（如果有内容）
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
}
