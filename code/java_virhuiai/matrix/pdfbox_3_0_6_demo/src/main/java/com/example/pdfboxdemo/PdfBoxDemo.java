package com.example.pdfboxdemo;

import com.virhuiai.pdfbox_3_0_6_utils.PrepareUtil;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfBoxDemo {
    public static void main(String[] args) {
        PrepareUtil.document("/Volumes/RamDisk/test.pdf", document->{



            // Check if document has pages
            if (document.getNumberOfPages() > 0) {
                // Get the first page
                PDPage page = document.getPage(0);
                System.out.println("Processing page: " + page.getMediaBox());

                // Extract text and its positions
                PDFTextStripper stripper = new PDFTextStripper() {
                    @Override
                    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                        // TODO 1: text 输出
                        System.out.println("=== 文本内容输出 ===");
                        System.out.println("完整文本: '" + text + "'");
                        System.out.println("文本长度: " + text.length() + " 字符");
                        System.out.println("-------------------");

                        for (TextPosition textPosition : textPositions) {
                            System.out.println("Text: '" + textPosition.getUnicode() +
                                    "', Font: " + textPosition.getFont().getName() +
                                    ", Size: " + textPosition.getFontSize() +
                                    ", Position: (" + textPosition.getX() + ", " + textPosition.getY() + ")");
                            
                            // TODO 2: text 输出 textPosition.getEndX() 和 textPosition.getEndY() getTextMatrix() 并注释说明什么用
                            /*
                             * 坐标系统说明：
                             * - getX(), getY(): 文本开始位置的坐标（左下角）
                             * - getEndX(), getEndY(): 文本结束位置的坐标（右下角）
                             * - getTextMatrix(): 返回文本的变换矩阵，包含位置、缩放、旋转等信息
                             * 
                             * 坐标用途：
                             * 1. 精确定位文本在页面中的位置
                             * 2. 计算文本框的大小和边界
                             * 3. 分析文本布局和排版信息
                             * 4. 实现文本高亮、注释等高级功能
                             */
                            System.out.println("  → 开始坐标: (" + textPosition.getX() + ", " + textPosition.getY() + ")");
                            System.out.println("  → 结束坐标: (" + textPosition.getEndX() + ", " + textPosition.getEndY() + ")");
                            System.out.println("  → 文本矩阵: " + textPosition.getTextMatrix());
                            
                            // TODO 3: 使用 getX() getY() getEndX() getEndY() 计算倾斜角度
                            /*
                             * 倾斜角度计算原理：
                             * - 通过起始点(X,Y)和结束点(EndX,EndY)构成的向量
                             * - 计算该向量与水平轴的夹角
                             * - 用于检测文本是否倾斜排列
                             */
                            double deltaX = textPosition.getEndX() - textPosition.getX();
                            double deltaY = textPosition.getEndY() - textPosition.getY();
                            
                            // 避免除零错误
                            if (Math.abs(deltaX) > 0.001) {
                                double slope = deltaY / deltaX;
                                double angleRadians = Math.atan(slope);
                                double angleDegrees = Math.toDegrees(angleRadians);
                                
                                System.out.println("  → 倾斜角度: " + String.format("%.2f", angleDegrees) + "°");
                                System.out.println("  → 倾斜弧度: " + String.format("%.4f", angleRadians) + " rad");
                                
                                // 判断倾斜方向
                                if (Math.abs(angleDegrees) < 1.0) {
                                    System.out.println("  → 文本状态: 基本水平");
                                } else if (angleDegrees > 0) {
                                    System.out.println("  → 文本状态: 向上倾斜");
                                } else {
                                    System.out.println("  → 文本状态: 向下倾斜");
                                }
                            } else {
                                System.out.println("  → 倾斜角度: 无法计算（垂直文本）");
                                System.out.println("  → 文本状态: 垂直排列");
                            }
                            
                            System.out.println(); // 空行分隔
                        }
                        super.writeString(text, textPositions);
                    }
                };

                stripper.setStartPage(1);
                stripper.setEndPage(1);
                String text = null;
                try {
                    text = stripper.getText(document);
                } catch (IOException e) {
                    e.printStackTrace();//todo
                }
                System.out.println("=== Extracted Text ===");
                System.out.println(text);
                System.out.println("=====================");
            }
        });

    }

    // 创建示例PDF的方法
//    private static void createSamplePDF() {
//        try {
//            System.out.println("Creating sample PDF...");
//
//            // Create a new document
//            PDDocument document = new PDDocument();
//
//            // Create a new page
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            // Create a content stream for the page
//            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//            // Add some text - 使用PDFBox 3.0.6正确的字体创建方式
//            contentStream.beginText();
//            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
//            contentStream.newLineAtOffset(50, 750);
//            contentStream.showText("PDFBox 3.0.6 Demo");
//            contentStream.newLineAtOffset(0, -30);
//            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
//            contentStream.showText("This is a sample PDF created with Apache PDFBox 3.0.6");
//            contentStream.newLineAtOffset(0, -20);
//            contentStream.showText("Created on: " + java.time.LocalDateTime.now());
//            contentStream.endText();
//
//            // Draw a rectangle
//            contentStream.addRect(50, 600, 200, 100);
//            contentStream.stroke();
//
//            // Close the content stream
//            contentStream.close();
//
//            // Save the document
//            File outputFile = new File("/Volumes/RamDisk/sample_output.pdf");
//            document.save(outputFile);
//            System.out.println("Sample PDF created successfully at: " + outputFile.getAbsolutePath());
//
//            // Close the document
//            document.close();
//
//        } catch (IOException e) {
//            System.err.println("Error creating sample PDF: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}