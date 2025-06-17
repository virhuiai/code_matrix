package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，向指定页面添加文本内容，并保存为新文件的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E9 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E8.pdf";
        FilePermissionUtils.validateReadableFile(new File(inputPath));
        // 定义输出PDF文件的路径
        String outputPath = "/Volumes/RamDisk/E9.pdf";
        FilePermissionUtils.validateReadWriteFile(new File(outputPath));

        // 使用try-with-resources加载现有PDF文档并确保资源自动关闭
        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            // 获取文档的指定页面（索引1，对应第二页）
            addTextToPage(document);

            // 保存修改后的PDF文档到指定输出路径
            document.save(new File(outputPath));

            // 打印提示信息，确认内容已成功添加
            System.out.println("内容已添加");

        } catch (IOException e) {
            // 捕获并处理IO异常，例如文件加载或保存失败
            throw new RuntimeException("无法加载或保存PDF文档: " + e.getMessage(), e);
        }
    }

    /**
     * 向PDF文档的指定页面添加文本内容。
     *
     * @param document PDF文档对象
     * @throws IOException 如果内容流操作失败
     */
    private static void addTextToPage(PDDocument document) throws IOException {
        // 获取文档的第1页（页面索引从0开始）
        PDPage page = document.getPage(0);

        // 创建PDPageContentStream对象，用于向页面添加内容
        // 使用try-with-resources确保内容流自动关闭
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // 开始文本内容流
            contentStream.beginText();

            // 设置文本字体和大小，TIMES_ROMAN为标准字体，12为字体大小
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

            // 设置文本在页面中的位置，坐标(25, 500)表示距离左边25单位、底部500单位
            contentStream.newLineAtOffset(25, 500);

            // 定义要添加的文本内容
            String text = "This is the sample document and we are adding content to it.";

            // 将文本内容插入到页面
            contentStream.showText(text);

            // 结束文本内容流
            contentStream.endText();
        }
    }
}