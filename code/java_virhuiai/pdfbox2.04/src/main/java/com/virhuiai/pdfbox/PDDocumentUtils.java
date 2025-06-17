package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

/**
 * PDF文档加载工具类，提供加载PDF文档的方法。
 */
public class PDDocumentUtils {

    /**
     * 加载指定路径的PDF文档并返回PDDocument对象。
     *
     * @param inputPath PDF文件的路径
     * @return 加载的PDDocument对象
     * @throws IllegalArgumentException 如果输入路径无效或为空
     * @throws RuntimeException 如果加载PDF文档失败
     */
    public static PDDocument loadPDF(String inputPath) {
        // 验证输入路径是否有效
        FilePermissionUtils.validateReadableFile(new File(inputPath));
        // 使用try-with-resources加载现有PDF文档并确保资源自动关闭
        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            // 返回文档对象，try-with-resources会确保资源在离开作用域时关闭
            // 为防止自动关闭，需将文档对象转移到调用者管理
            return document;
        } catch (IOException e) {
            // 捕获并处理IO异常，例如文件不存在或格式错误
            throw new RuntimeException("无法加载PDF文档: " + e.getMessage(), e);
        }
    }

}
