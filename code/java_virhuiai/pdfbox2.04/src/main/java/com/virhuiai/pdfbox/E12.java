package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，在第一页插入图片并保存的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E12 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E9.pdf";
        String outPath = "/Volumes/RamDisk/E12.pdf";
        // 定义要插入的图片路径
        String imagePath = "/Volumes/RamDisk/logo.png";
        FilePermissionUtils.validateReadWriteFile(new File(outPath));

        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // 在PDF文档的第一页插入图片
                insertImageIntoDocument(document, imagePath);

                // 打印提示信息，确认图片已插入
                System.out.println("Image inserted");

                // 保存修改后的文档
                document.save(outPath);

                // 关闭文档
                document.close();
            } catch (IOException e) {
                // 捕获并处理IO异常，例如图片插入或保存失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 在PDF文档的第一页插入图片。
     *
     * @param document PDF文档对象
     * @param imagePath 要插入的图片文件路径
     * @throws IOException 如果图片插入操作失败
     */
    private static void insertImageIntoDocument(PDDocument document, String imagePath) throws IOException {
        // 获取PDF文档的第一页
        PDPage page = document.getPage(0);

        // 创建PDImageXObject对象
        PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);

        // 创建PDPageContentStream对象，用于绘制内容
        try (PDPageContentStream contents = new PDPageContentStream(document, page)) {
            // 在页面上绘制图片（位置：x=70, y=250）
            contents.drawImage(pdImage, 70, 250);
        }
    }
}