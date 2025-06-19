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

    /**
     * todo 测试 中文文本没有空格分隔，需要逐字符处理。
     * @param text
     * @param maxWidth
     * @param font
     * @param fontSize
     * @return
     * @throws IOException
     */
    public static List<String> wrapTextChinese(String text, float maxWidth, PDType1Font font, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        float currentWidth = 0;

        // 逐字符处理
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);

            // 计算当前字符宽度
            float charWidth = font.getStringWidth(charStr) / 1000 * fontSize;

            // 检查是否需要换行
            if (currentWidth + charWidth <= maxWidth) {
                currentLine.append(charStr);
                currentWidth += charWidth;
            } else {
                // 如果超出，保存当前行并开始新行
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(charStr);
                currentWidth = charWidth;
            }
        }

        // 添加最后一行
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    // 判断是否为标点符号（简化版）
    private static boolean isPunctuation(char c) {
        return "，。；：、？！\"'（）【】《》".indexOf(c) != -1;
    }

    // 在换行逻辑中添加标点符号处理
//if (isPunctuation(c) && currentLine.length() == 0) {
//        // 如果是标点符号且位于行首，尝试将其添加到上一行
//        if (!lines.isEmpty()) {
//            String lastLine = lines.get(lines.size() - 1);
//            if (lastLine.length() * charWidth < maxWidth * 0.9) {
//                // 如果上一行还有空间，将标点添加到上一行
//                lines.set(lines.size() - 1, lastLine + charStr);
//                continue;
//            }
//        }
//    }

//    // 检测连续的英文字符
//    private static boolean isEnglishChar(char c) {
//        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
//    }
//
//// 在主循环中添加英文单词完整性处理
//if (isEnglishChar(c)) {
//        // 尝试获取完整单词
//        int wordEnd = i;
//        while (wordEnd < text.length() && isEnglishChar(text.charAt(wordEnd))) {
//            wordEnd++;
//        }
//        String word = text.substring(i, wordEnd);
//        float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
//
//        // 处理单词整体...
//        i = wordEnd - 1; // 调整索引
//    }
}
