package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于加载现有PDF文档，向指定页面添加多行文本内容，并保存为新文件的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E10Dot2 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E8.pdf";
        // 定义输出PDF文件的路径
        String outputPath = "/Volumes/RamDisk/E10Dot2.pdf";
        FilePermissionUtils.validateReadWriteFile(new File(outputPath));

        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // 获取文档的指定页面并添加多行文本
                addTextToPage(document);

                // 保存修改后的PDF文档到指定输出路径
                document.save(new File(outputPath));

                // 打印提示信息，确认内容已成功添加
                System.out.println("Content added");

                // 关闭文档
                document.close();
            } catch (IOException e) {
                // 捕获并处理IO异常，例如页面操作或保存失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 向PDF文档的指定页面添加自动换行的文本内容。
     *
     * @param document PDF文档对象
     * @throws IOException 如果内容流操作失败
     */
    private static void addTextToPage(PDDocument document) throws IOException {
        // 获取文档的第0页（页面索引从0开始）
        PDPage page = PDDocumentUtils.getValidatedPage(document, 0);

        // 获取页面宽度，用于计算文本换行
        float pageWidth = page.getMediaBox().getWidth();
        // 定义文本区域的可用宽度（页面宽度减去左右边距）
        float margin = 25;
        float maxWidth = pageWidth - 2 * margin;

        // 创建PDPageContentStream对象，用于向页面添加内容
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // 开始文本内容流
            contentStream.beginText();

            // 设置文本字体和大小
            PDType1Font font = PDType1Font.TIMES_ROMAN;
            float fontSize = 16;
            contentStream.setFont(font, fontSize);

            // 设置行间距
            float leading = 14.5f;
            contentStream.setLeading(leading);

            // 设置文本起始位置，坐标(25, 725)表示距离左边25单位、顶部725单位
            contentStream.newLineAtOffset(margin, 725);

            // 定义要添加的文本内容
            String text = "This is an example of adding text to a page in the pdf document. we can add as many lines; This is an example of adding text to a page in the pdf document. we can add as many lines as we want like this using the ShowText() method of the ContentStream class.";

            // 将文本按行分割以实现自动换行
            List<String> lines = wrapText(text, maxWidth, font, fontSize);

            // 逐行写入文本
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            // 结束文本内容流
            contentStream.endText();
        }
    }

    /**
     * 将长文本按指定宽度进行分割，实现自动换行。（适合英文版本）
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
    private static List<String> wrapText(String text, float maxWidth, PDType1Font font, float fontSize) throws IOException {
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