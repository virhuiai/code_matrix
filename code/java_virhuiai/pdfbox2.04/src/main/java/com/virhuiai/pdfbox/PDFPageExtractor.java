package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PDFPageExtractor {

    /**
     * 提取PDF文件的指定页数内容（文本）
     */
    public static String extractTextFromPages(String pdfPath, int startPage, int endPage) {
        // 验证文件是否存在
        File file = new File(pdfPath);
        if (!file.exists()) {
            System.err.println("PDF文件不存在: " + pdfPath);
            return null;
        }

        try (PDDocument document = PDDocument.load(file)) {
            // 检查页数范围
            int totalPages = document.getNumberOfPages();
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                System.err.println(String.format("无效的页数范围。PDF共有%d页，请输入1-%d之间的页数",
                        totalPages, totalPages));
                return null;
            }

            PDFTextStripper textStripper = new PDFTextStripper();
            // 设置要提取的页数范围（PDFBox页数从1开始）
            textStripper.setStartPage(startPage);
            textStripper.setEndPage(endPage);

            return textStripper.getText(document);

        } catch (IOException e) {
            System.err.println("提取PDF文本时出错: " + e.getMessage());
            return null;
        }
    }

    /**
     * 提取单页内容
     */
    public static String extractSinglePageText(String pdfPath, int pageNumber) {
        return extractTextFromPages(pdfPath, pageNumber, pageNumber);
    }

    /**
     * 将指定页数提取并保存为新的PDF文件
     */
    public static boolean extractPagesToNewPDF(String sourcePdfPath, String outputPdfPath,
                                               int startPage, int endPage) {
        // 验证文件是否存在
        File sourceFile = new File(sourcePdfPath);
        if (!sourceFile.exists()) {
            System.err.println("源PDF文件不存在: " + sourcePdfPath);
            return false;
        }

        try (PDDocument sourceDoc = PDDocument.load(sourceFile);
             PDDocument newDoc = new PDDocument()) {

            // 获取总页数
            int totalPages = sourceDoc.getNumberOfPages();

            // 检查页数范围（用户输入从1开始）
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                System.err.println(String.format("无效的页数范围。PDF共有%d页，请输入1-%d之间的页数",
                        totalPages, totalPages));
                return false;
            }

            // 提取指定范围的页面（转换为0基索引）
            PDPageTree pages = sourceDoc.getPages();
            for (int i = startPage - 1; i < endPage; i++) {
                PDPage page = pages.get(i);
                // 使用importPage方法正确导入页面
                PDPage importedPage = newDoc.importPage(page);
                // 如果需要，可以在这里对导入的页面进行修改
            }

            // 保存新文档
            newDoc.save(outputPdfPath);
            System.out.println("成功提取第" + startPage + "-" + endPage +
                    "页到文件: " + outputPdfPath);
            return true;

        } catch (IOException e) {
            System.err.println("处理PDF文件时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("发生未知错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 提取单页并保存为新的PDF文件
     */
    public static boolean extractSinglePageToPDF(String sourcePdfPath, String outputPdfPath,
                                                 int pageNumber) {
        return extractPagesToNewPDF(sourcePdfPath, outputPdfPath, pageNumber, pageNumber);
    }

    /**
     * 获取PDF总页数
     */
    public static int getTotalPages(String pdfPath) {
        File file = new File(pdfPath);
        if (!file.exists()) {
            System.err.println("PDF文件不存在: " + pdfPath);
            return -1;
        }

        try (PDDocument document = PDDocument.load(file)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            System.err.println("读取PDF文件时出错: " + e.getMessage());
            return -1;
        }
    }

    /**
     * 获取PDF文件的页面信息（如页面大小）
     */
    public static void printPageInfo(String pdfPath) {
        File file = new File(pdfPath);
        if (!file.exists()) {
            System.err.println("PDF文件不存在: " + pdfPath);
            return;
        }

        try (PDDocument document = PDDocument.load(file)) {
            PDPageTree pages = document.getPages();
            int pageNum = 1;

            System.out.println("PDF页面信息:");
            for (PDPage page : pages) {
                PDRectangle pageSize = page.getMediaBox();
                System.out.printf("第%d页: 宽=%.2f, 高=%.2f (单位:点，1点=1/72英寸)\n",
                        pageNum++, pageSize.getWidth(), pageSize.getHeight());
            }
        } catch (IOException e) {
            System.err.println("读取PDF文件时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 使用示例文件路径，请根据实际情况修改
        String pdfPath = "/Volumes/RamDisk/tzs书.pdf";

        // 首先检查文件是否存在
        File file = new File(pdfPath);
        if (!file.exists()) {
            System.err.println("示例PDF文件不存在: " + pdfPath);
            System.out.println("请将PDF文件放在当前目录或修改文件路径");
            return;
        }

        System.out.println("===== PDF页面提取工具演示 =====\n");

        // 示例1: 获取PDF总页数
        int totalPages = getTotalPages(pdfPath);
        if (totalPages > 0) {
            System.out.println("PDF总页数: " + totalPages);

            // 示例2: 显示页面信息
            printPageInfo(pdfPath);
            System.out.println();

            // 示例3: 提取第1页的文本内容（如果存在）
            if (totalPages >= 1) {
                String pageText = extractSinglePageText(pdfPath, 1);
                if (pageText != null) {
                    System.out.println("第1页内容预览（前200字符）:");
                    String preview = pageText.length() > 200 ?
                            pageText.substring(0, 200) + "..." : pageText;
                    System.out.println(preview);
                    System.out.println();
                }
            }

            // 示例4: 提取页面范围的文本
            if (totalPages >= 2) {
                int endPage = Math.min(3, totalPages); // 最多提取到第3页
                String rangeText = extractTextFromPages(pdfPath, 1, endPage);
                if (rangeText != null) {
                    System.out.println("第1-" + endPage + "页内容预览（前300字符）:");
                    String preview = rangeText.length() > 300 ?
                            rangeText.substring(0, 300) + "..." : rangeText;
                    System.out.println(preview);
                    System.out.println();
                }
            }

            // 示例5: 将第1页提取到新的PDF文件
            System.out.println("正在提取第1页到新PDF文件...");
            boolean success = extractSinglePageToPDF(pdfPath, "page1_extracted.pdf", 1);
            if (success) {
                System.out.println("提取成功！");
            }

            // 示例6: 提取多页到新PDF（如果有多页）
//            if (totalPages >= 2) {
                int endPage = Math.min(3, totalPages);
                System.out.println("\n正在提取第1-" + endPage + "页到新PDF文件...");
//                success = extractPagesToNewPDF(pdfPath, "pages1to" + endPage + "_extracted.pdf",
//                        1, endPage);
                success = extractPagesToNewPDF(pdfPath, "/Volumes/RamDisk/tzs书2.pdf",
                        1, endPage);
                if (success) {
                    System.out.println("提取成功！");
                }
//            }
        }
    }
}