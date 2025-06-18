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
            List<String> lines = TextUtils.wrapText(text, maxWidth, font, fontSize);

            // 逐行写入文本
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            // 结束文本内容流
            contentStream.endText();
        }
    }

}