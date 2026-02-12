package com.example.pdfboxdemo;

import com.virhuiai.pdfbox_3_0_6_utils.PrepareUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

public class PdfBoxDemo {
    public static void main(String[] args) {
        PrepareUtil.document("/Volumes/RamDisk/test.pdf", document->{
            try {
                // 执行表格分析
                List<PdfTableAnalyzerO.Table> tables = PdfTableAnalyzerO.analyzeTables(document);
                PdfTableAnalyzerO.printTableAnalysis(tables);
                
                // 同时执行原有的文本分析功能
                // demonstrateOriginalFeatures(document);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 保留原有的文本分析功能
    private static void demonstrateOriginalFeatures(PDDocument document) throws IOException {
        // Check if document has pages
        if (document.getNumberOfPages() > 0) {
            // Get the first page
            PDPage page = document.getPage(0);
            System.out.println("\n=== 原始文本分析功能 ===");
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
                        
                        // TODO 3: 使用 getTextMatrix() 计算倾斜角度
                        /*
                         * 倾斜角度计算原理：
                         * - getTextMatrix() 返回6个值的数组 [a,b,c,d,e,f]
                         * - 其中 a 和 d 控制缩放，b 和 c 控制旋转/倾斜
                         * - 对于纯旋转矩阵，b = -sin(θ), c = sin(θ), a = d = cos(θ)
                         * - 通过矩阵元素计算实际的旋转角度
                         */
                        org.apache.pdfbox.util.Matrix textMatrix = textPosition.getTextMatrix();
                        
                        // 获取矩阵的具体值用于分析
                        System.out.println("  → 完整文本矩阵: " + textMatrix);
                        
                        // 从矩阵中提取变换信息
                        // 矩阵形式: [a b 0]
                        //          [c d 0] 
                        //          [tx ty 1]
                        float a = textMatrix.getValue(0, 0); // scaleX * cos(θ)
                        float b = textMatrix.getValue(0, 1); // scaleX * sin(θ)  
                        float c = textMatrix.getValue(1, 0); // -scaleY * sin(θ)
                        float d = textMatrix.getValue(1, 1); // scaleY * cos(θ)
                        float tx = textMatrix.getValue(2, 0); // x translation
                        float ty = textMatrix.getValue(2, 1); // y translation
                        
                        System.out.println("  → 矩阵分解值: ");
                        System.out.println("    a(scale*cosθ): " + String.format("%.3f", a));
                        System.out.println("    b(scale*sinθ): " + String.format("%.3f", b));
                        System.out.println("    c(-scale*sinθ): " + String.format("%.3f", c));
                        System.out.println("    d(scale*cosθ): " + String.format("%.3f", d));
                        System.out.println("    tx(平移x): " + String.format("%.3f", tx));
                        System.out.println("    ty(平移y): " + String.format("%.3f", ty));
                        
                        // 计算旋转角度（弧度）
                        // θ = atan2(b, a) 或 θ = atan2(-c, d)
                        double angleFromAB = Math.atan2(b, a);
                        double angleFromCD = Math.atan2(-c, d);
                        
                        // 使用平均值提高精度
                        double angleRadians = (angleFromAB + angleFromCD) / 2.0;
                        double angleDegrees = Math.toDegrees(angleRadians);
                        
                        System.out.println("  → 从(a,b)计算角度: " + String.format("%.2f", Math.toDegrees(angleFromAB)) + "°");
                        System.out.println("  → 从(c,d)计算角度: " + String.format("%.2f", Math.toDegrees(angleFromCD)) + "°");
                        System.out.println("  → 最终倾斜角度: " + String.format("%.2f", angleDegrees) + "°");
                        System.out.println("  → 倾斜弧度: " + String.format("%.4f", angleRadians) + " rad");
                        
                        // 判断倾斜方向和状态
                        double absAngle = Math.abs(angleDegrees);
                        if (absAngle < 1.0) {
                            System.out.println("  → 文本状态: 基本水平");
                        } else if (absAngle > 89.0 && absAngle < 91.0) {
                            System.out.println("  → 文本状态: 基本垂直");
                            if (angleDegrees > 0) {
                                System.out.println("  → 垂直方向: 从下往上");
                            } else {
                                System.out.println("  → 垂直方向: 从上往下");
                            }
                        } else if (angleDegrees > 0) {
                            System.out.println("  → 文本状态: 向上倾斜");
                        } else {
                            System.out.println("  → 文本状态: 向下倾斜");
                        }
                        
                        // 额外信息：计算缩放因子
                        double scaleX = Math.sqrt(a * a + b * b);
                        double scaleY = Math.sqrt(c * c + d * d);
                        System.out.println("  → X轴缩放因子: " + String.format("%.2f", scaleX));
                        System.out.println("  → Y轴缩放因子: " + String.format("%.2f", scaleY));
                        
                        System.out.println(); // 空行分隔
                    }
                    super.writeString(text, textPositions);
                }
            };

            stripper.setStartPage(1);
            stripper.setEndPage(1);
            String text = stripper.getText(document);
            System.out.println("=== Extracted Text ===");
            System.out.println(text);
            System.out.println("=====================");
        }
    }
}