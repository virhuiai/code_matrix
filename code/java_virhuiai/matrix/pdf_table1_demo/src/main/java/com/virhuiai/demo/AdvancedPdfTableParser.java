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
 * 高级PDF表格解析器
 * 提供更多解析选项和详细的错误处理
 * 
 * @author virhuiai
 * @since 1.0
 */
public class AdvancedPdfTableParser {

    private final String pdfFilePath;

    /**
     * 构造函数
     * 
     * @param pdfFilePath PDF文件路径
     */
    public AdvancedPdfTableParser(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    /**
     * 解析PDF表格并输出详细信息
     */
    public void parseAndDisplay() {
        System.out.println("=== PDF表格解析器 ===");
        System.out.println("文件路径: " + pdfFilePath);
        System.out.println();

        // 显示PDF基本信息
        displayPdfInfo();

        // 解析表格
        try {
            parseTableFromFirstPage();
        } catch (Exception e) {
            System.err.println("表格解析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 显示PDF文件基本信息
     */
    private void displayPdfInfo() {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            System.out.println("📄 PDF文件信息:");
            System.out.println("  • 总页数: " + document.getNumberOfPages());
            
            // 获取文档信息
            if (document.getDocumentInformation() != null) {
                System.out.println("  • 标题: " + document.getDocumentInformation().getTitle());
                System.out.println("  • 作者: " + document.getDocumentInformation().getAuthor());
                System.out.println("  • 创建时间: " + document.getDocumentInformation().getCreationDate());
            }

            // 第一页文本预览
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            String firstPageText = stripper.getText(document);
            
            System.out.println("  • 第一页字符数: " + firstPageText.length());
            System.out.println("  • 第一页文本预览: " + 
                (firstPageText.length() > 100 ? firstPageText.substring(0, 100) + "..." : firstPageText));
            System.out.println();

        } catch (IOException e) {
            System.err.println("❌ 无法读取PDF文件信息: " + e.getMessage());
        }
    }

    /**
     * 解析第一页的表格
     */
    private void parseTableFromFirstPage() throws IOException {
        System.out.println("🔍 正在解析第一页表格...");
        
        File pdfFile = new File(pdfFilePath);
        if (!pdfFile.exists()) {
            throw new IOException("文件不存在: " + pdfFilePath);
        }

        try (PDDocument pdfDoc = PDDocument.load(pdfFile)) {
            PdfTableReader reader = new PdfTableReader();
            
            // 解析第一页（页码从1开始）
            List<ParsedTablePage> tablePages = reader.parsePdfTablePages(pdfDoc, 1, 1);
            
            if (tablePages == null || tablePages.isEmpty()) {
                System.out.println("⚠️  第一页未找到表格");
                return;
            }

            System.out.println("✅ 表格解析成功");
            displayTableDetails(tablePages.get(0));
        }
    }

    /**
     * 显示表格详细信息
     * 
     * @param page 解析后的表格页
     */
    private void displayTableDetails(ParsedTablePage page) {
        System.out.println("📋 表格详情:");
//        System.out.println("  • 页码: " + page.getPageNumber());
        System.out.println("  • 行数: " + page.getRows().size());
        
        // 计算总单元格数
        int totalCells = 0;
        for (ParsedTableRow row : page.getRows()) {
            totalCells += row.getCells().size();
        }
        System.out.println("  • 总单元格数: " + totalCells);
        System.out.println();

        System.out.println("📊 表格内容详情:");
        System.out.println(createRepeatedString("=", 60));

        int cellCounter = 1;
        List<ParsedTableRow> rows = page.getRows();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ParsedTableRow row = rows.get(rowIndex);
            List<String> cells = row.getCells();
            int cellCount = cells.size();
            
            System.out.println("第 " + (rowIndex + 1) + " 行 (" + cellCount + " 列):");
            
            for (int colIndex = 0; colIndex < cellCount; colIndex++) {
                String content = cells.get(colIndex);
                displayCellInfo(cellCounter, rowIndex + 1, colIndex + 1, content);
                cellCounter++;
            }
            System.out.println();
        }
    }

    /**
     * 显示单个单元格的信息
     * 
     * @param cellIndex 单元格序号
     * @param row 行号
     * @param col 列号
     * @param content 单元格内容
     */
    private void displayCellInfo(int cellIndex, int row, int col, String content) {
        // 处理单元格内容
        String displayContent;
        if (content == null || content.trim().isEmpty()) {
            displayContent = "[空单元格]";
        } else {
            // 清理内容
            displayContent = content.trim()
                .replaceAll("\\s+", " ")  // 合并多个空白字符
                .replaceAll("[\\r\\n]+", " "); // 移除换行符
            
            // 如果内容过长则截断
            if (displayContent.length() > 50) {
                displayContent = displayContent.substring(0, 47) + "...";
            }
        }

        System.out.printf("  单元格 #%d [%d,%d]: %s%n", 
            cellIndex, row, col, displayContent);
    }

    /**
     * 创建重复字符串（替代Java 8中不存在的String.repeat方法）
     * 
     * @param str 要重复的字符串
     * @param times 重复次数
     * @return 重复后的字符串
     */
    private String createRepeatedString(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 主方法 - 程序入口点
     */
    public static void main(String[] args) {
        String pdfFilePath = "/Volumes/RamDisk/test.pdf";
        
        AdvancedPdfTableParser parser = new AdvancedPdfTableParser(pdfFilePath);
        parser.parseAndDisplay();
    }
}