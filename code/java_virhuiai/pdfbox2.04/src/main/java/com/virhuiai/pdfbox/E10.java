package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，向指定页面添加多行文本内容，并保存为新文件的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E10 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E8.pdf";
        // 定义输出PDF文件的路径
        String outputPath = "/Volumes/RamDisk/E10.pdf";
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
     * 向PDF文档的指定页面添加多行文本内容。
     *
     * @param document PDF文档对象
     * @throws IOException 如果内容流操作失败
     */
    private static void addTextToPage(PDDocument document) throws IOException {
        // 获取文档的第1页（页面索引从0开始）
        PDPage page = PDDocumentUtils.getValidatedPage(document, 0);

        // 创建PDPageContentStream对象，用于向页面添加内容
        // 使用try-with-resources确保内容流自动关闭
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // 开始文本内容流
            contentStream.beginText();

            // 设置文本字体和大小，TIMES_ROMAN为标准字体，16为字体大小
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);

            // 设置行间距
            contentStream.setLeading(14.5f);

            // 设置文本在页面中的位置，坐标(25, 725)表示距离左边25单位、顶部725单位
            contentStream.newLineAtOffset(25, 725);

            // 定义要添加的多行文本内容
            String text1 = "This is an example of adding text to a page in the pdf document. we can add as many lines ;This is an example of adding text to a page in the pdf document. we can add as many lines";
            String text2 = "as we want like this using the ShowText() method of the ContentStream class";

            // 将文本内容插入到页面
            contentStream.showText(text1);
            contentStream.newLine();
            contentStream.showText(text2);

            // 结束文本内容流
            contentStream.endText();
        }
    }
}