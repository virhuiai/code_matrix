package com.virhuiai.pdfbox_3_0_6_utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * PDF文档处理工具类
 * 提供便捷的PDF文档操作方法封装
 * 
 * @author VirHuiai
 * @version 1.0
 * @since 2026-01-31
 */
public class PrepareUtil {

    /**
     * 使用指定路径加载PDF文档并执行指定操作
     * 该方法会自动管理文档资源的关闭，无需手动调用close()
     * 
     * 功能特点：
     * 1. 自动资源管理：使用try-with-resources确保文档正确关闭
     * 2. 参数验证：检查文件路径和消费者函数的有效性
     * 3. 异常处理：提供详细的错误信息和堆栈跟踪
     * 4. 灵活性：支持任意PDF文件路径
     * 
     * 使用示例：
     * <pre>
     * {@code
     * PrepareUtil.document("/path/to/your/file.pdf", doc -> {
     *     // 执行PDF操作
     *     PDFTextStripper stripper = new PDFTextStripper();
     *     String text = stripper.getText(doc);
     *     System.out.println("提取的文本: " + text);
     * });
     * }
     * </pre>
     * 
     * @param filePath PDF文件的完整路径
     * @param consumer 接收PDDocument对象的消费者函数
     * @throws RuntimeException 当PDF文件不存在、无法读取或处理过程中发生错误时抛出
     * @throws IllegalArgumentException 当filePath为空或consumer为null时抛出
     */
    public static void document(String filePath, Consumer<PDDocument> consumer) {
        // 参数验证
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("PDF文件路径不能为空");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("消费者函数不能为null");
        }

        // 检查文件是否存在
        File pdfFile = new File(filePath);
        if (!pdfFile.exists()) {
            throw new RuntimeException("PDF文件不存在: " + filePath);
        }
        if (!pdfFile.isFile()) {
            throw new RuntimeException("指定路径不是有效的文件: " + filePath);
        }

        try (
            // 加载PDF文件 - 使用PDFBox 3.0.6的正确方式
            PDDocument document = Loader.loadPDF(pdfFile)
        ) {
            // 验证文档是否成功加载
            if (document == null) {
                throw new RuntimeException("无法加载PDF文档: " + filePath);
            }

            // 执行用户定义的操作
            consumer.accept(document);
            
        } catch (IOException e) {
            // 包装IO异常，提供更清晰的错误信息
            throw new RuntimeException(
                String.format("读取PDF文件时发生错误 [%s]: %s", filePath, e.getMessage()), 
                e
            );
        } catch (Exception e) {
            // 处理用户操作过程中可能发生的其他异常
            throw new RuntimeException(
                String.format("处理PDF文档时发生错误 [%s]: %s", filePath, e.getMessage()), 
                e
            );
        }
    }

    /**
     * 检查指定路径的PDF文件是否存在且可读
     * 
     * @param filePath PDF文件路径
     * @return 如果文件存在且可读返回true，否则返回false
     */
    public static boolean isPdfFileValid(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }

}