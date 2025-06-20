package com.virhuiai.pdfbox;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * 用于加载现有PDF文档，嵌入JavaScript动作并保存的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E15 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/t.pdf";




        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // Instantiating Splitter class
                Splitter splitter = new Splitter();

                // Splitting the pages of a PDF document
                List<PDDocument> Pages = splitter.split(document);

                // Creating an iterator
                Iterator<PDDocument> iterator = Pages.listIterator();

                // Saving each page as an individual document
                int i = 1;
                while (iterator.hasNext()) {
                    PDDocument pd = iterator.next();
                    pd.save("/Volumes/RamDisk/E15Dot" + i++ + ".pdf");
                }
                System.out.println("Multiple PDFs created");

            } catch (IOException e) {
                // 捕获并处理IO异常，例如JavaScript嵌入或保存失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }
}