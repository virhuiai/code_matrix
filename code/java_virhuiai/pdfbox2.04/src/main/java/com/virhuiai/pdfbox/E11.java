package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，提取文本内容并打印到控制台的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E11 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E9.pdf";

        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // 提取PDF文档中的文本内容
                extractTextFromDocument(document);

                // 打印提示信息，确认文本已提取
                System.out.println("Text extracted");

                // 关闭文档
                document.close();
            } catch (IOException e) {
                // 捕获并处理IO异常，例如文本提取失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 从PDF文档中提取文本内容并打印到控制台。
     *
     * @param document PDF文档对象
     * @throws IOException 如果文本提取操作失败
     */
    private static void extractTextFromDocument(PDDocument document) throws IOException {
        // 实例化PDFTextStripper类
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // 从PDF文档中提取文本
        String text = pdfStripper.getText(document);

        // 打印提取的文本内容
        System.out.println(text);
    }
}