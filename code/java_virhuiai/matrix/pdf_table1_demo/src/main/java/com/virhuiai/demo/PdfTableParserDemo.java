package com.virhuiai.demo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import pdftable.PdfTableReader;
import pdftable.models.ParsedTablePage;
import pdftable.models.ParsedTablePage.ParsedTableRow;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * PDF表格解析Demo
 * 使用pdf-table库解析PDF文件中的表格内容
 * 
 * @author virhuiai
 * @since 1.0
 */
public class PdfTableParserDemo {

    /**
     * 主方法 - 解析指定PDF文件第一页的表格
     * 
     * @param args 命令行参数（可选）
     */
    public static void main(String[] args) {
        // PDF文件路径
        String pdfFilePath = "/Volumes/RamDisk/test.pdf";
        
        System.out.println("开始解析PDF文件: " + pdfFilePath);
        System.out.println(createRepeatedString("=", 50));
        
        try {
            parsePdfTable(pdfFilePath);
        } catch (Exception e) {
            System.err.println("解析PDF表格时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析PDF文件中的表格
     * 
     * @param pdfFilePath PDF文件路径
     * @throws IOException 文件读取异常
     */
    public static void parsePdfTable(String pdfFilePath) throws IOException {
        // 检查文件是否存在
        File pdfFile = new File(pdfFilePath);
        if (!pdfFile.exists()) {
            throw new IOException("PDF文件不存在: " + pdfFilePath);
        }

        // 加载PDF文档
        try (PDDocument pdfDoc = PDDocument.load(pdfFile)) {
            // 创建表格读取器
            PdfTableReader reader = new PdfTableReader();
            
            // 解析第一页（页码从1开始）
            System.out.println("正在解析第1页...");
            List<ParsedTablePage> tablePages = reader.parsePdfTablePages(pdfDoc, 1, 1);
            
            if (tablePages == null || tablePages.isEmpty()) {
                System.out.println("第1页未找到表格");
                return;
            }

            System.out.println("解析成功!");
//            System.out.println("页码: " + tablePages.get(0).getPageNumber());
            System.out.println("行数: " + tablePages.get(0).getRows().size());
            System.out.println();

            // 显示表格内容
            displayTableContent(tablePages.get(0));
        }
    }

    /**
     * 显示表格内容
     * 
     * @param page 解析后的表格页
     */
    private static void displayTableContent(ParsedTablePage page) {
        System.out.println("表格内容详情:");
        System.out.println(createRepeatedString("-", 30));

        int cellIndex = 1;
        List<ParsedTableRow> rows = page.getRows();
        
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ParsedTableRow row = rows.get(rowIndex);
            List<String> cells = row.getCells();
            int cellCount = cells.size();
            
            System.out.println("第 " + (rowIndex + 1) + " 行 (" + cellCount + " 列):");
            
            for (int colIndex = 0; colIndex < cellCount; colIndex++) {
                String cellContent = cells.get(colIndex);
                
                // 处理空单元格
                if (cellContent == null || cellContent.trim().isEmpty()) {
                    cellContent = "[空]";
                } else {
                    // 清理内容，去除多余空白字符和换行符
                    cellContent = cellContent.trim()
                        .replaceAll("[\\r\\n]+", " ")
                        .replaceAll("\\s+", " ");
                }
                
                System.out.printf("  第%d个单元格 [%d,%d]: %s%n", 
                    cellIndex, rowIndex + 1, colIndex + 1, cellContent);
                cellIndex++;
            }
            System.out.println();
        }
        
        System.out.println("表格解析完成! 总共 " + (cellIndex - 1) + " 个单元格");
    }

    /**
     * 创建重复字符串（替代Java 8中不存在的String.repeat方法）
     * 
     * @param str 要重复的字符串
     * @param times 重复次数
     * @return 重复后的字符串
     */
    private static String createRepeatedString(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 获取PDF文件的基本信息（辅助方法）
     * 
     * @param pdfFilePath PDF文件路径
     */
    public static void getPdfInfo(String pdfFilePath) {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            int pageCount = document.getNumberOfPages();
            System.out.println("PDF文件信息:");
            System.out.println("总页数: " + pageCount);
            
            // 获取第一页文本内容预览
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            String text = stripper.getText(document);
            
            System.out.println("第一页文本预览 (前200字符):");
            System.out.println(text.substring(0, Math.min(200, text.length())) + "...");
            
        } catch (IOException e) {
            System.err.println("获取PDF信息失败: " + e.getMessage());
        }
    }
}