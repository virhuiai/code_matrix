package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * PDF文档加载工具类，提供加载PDF文档的方法。
 */
public class PDDocumentUtils {

    /**
     * 加载指定路径的PDF文档并返回PDDocument对象。
     * PDDocumentUtils.loadPDF
     *
     * @param inputPath PDF文件的路径
     * @return 加载的PDDocument对象
     * @throws IllegalArgumentException 如果输入路径无效或为空
     * @throws RuntimeException 如果加载PDF文档失败
     */
    public static PDDocument loadPdfThenProcess(String inputPath) {
        // 验证输入路径是否有效
        FilePermissionUtils.validateReadableFile(new File(inputPath));
        try {
            return PDDocument.load(new File(inputPath));
        } catch (IOException e) {
            throw new RuntimeException("无法加载PDF文档: " + e.getMessage(), e);
        }
    }

    /**
     * 加载指定路径的PDF文档，并在加载后通过回调函数处理文档。
     * Descriptive: Clearly indicates the method’s two-step process: loading a PDF and processing it via a callback.
     * @param inputPath PDF文件的路径
     * @param documentProcessor 回调函数，用于处理加载的PDDocument对象
     * @throws IllegalArgumentException 如果输入路径无效或不可读
     * @throws RuntimeException 如果加载或处理PDF文档失败
     */
    public static void loadPdfThenProcess(String inputPath, Consumer<PDDocument> documentProcessor) {
        // 验证输入路径是否有效，确保文件存在且可读
        FilePermissionUtils.validateReadableFile(new File(inputPath));

        // 使用try-with-resources加载现有PDF文档并确保资源自动关闭
        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            // 调用回调函数处理文档，文档操作在try块内完成以避免资源关闭
            if (documentProcessor != null) {
                documentProcessor.accept(document);
            }
        } catch (IOException e) {
            // 捕获并处理IO异常，例如文件不存在或格式错误
            throw new RuntimeException("无法加载PDF文档: " + e.getMessage(), e);
        }
    }

    /**
     * 验证PDF文档中指定的页面索引是否有效。
     * PDDocumentUtils.validPage(document,);
     *
     * @param document PDF文档对象，不能为空
     * @param page 要验证的页面索引（从0开始）
     * @throws IllegalArgumentException 如果文档为空或页面索引无效
     */
    public static void validPage(PDDocument document, int page) {
        // 检查输入的PDF文档对象是否为空
        // 如果为空，抛出非法参数异常以防止后续操作导致空指针异常
        if (document == null) {
            throw new IllegalArgumentException("PDF文档不能为空");
        }

        // 获取PDF文档的总页面数
        // 使用getPages()获取页面集合，再通过getCount()得到页面总数
//        int numberOfPages = document.getPages().getCount();
        int numberOfPages = document.getNumberOfPages();

        // 验证页面索引是否有效
        // 页面索引必须大于或等于0，并且小于总页面数
        // 如果索引小于0或大于等于总页面数，则认为无效
        if (page < 0 || page >= numberOfPages) {
            // 抛出异常，并提供详细的错误信息
            // 错误信息包含无效的页面索引值以及有效的索引范围
            throw new IllegalArgumentException("无效的页面索引: " + page + "。页面索引必须在0到" + (numberOfPages - 1) + "之间");
        }
    }
    /**
     * 获取PDF文档中指定索引的页面，并在获取前验证索引有效性。
     *
     * @param document PDF文档对象，不能为空
     * @param pageIndex 要获取的页面索引（从0开始）
     * @return 指定索引的PDPage对象
     * @throws IllegalArgumentException 如果文档为空或页面索引无效
     */
    public static PDPage getValidatedPage(PDDocument document, int pageIndex) {
        // 验证输入的PDF文档对象是否为空
        if (document == null) {
            throw new IllegalArgumentException("PDF文档不能为空");
        }

        // 获取PDF文档的总页面数，使用getNumberOfPages()以提高效率
        int numberOfPages = document.getNumberOfPages();

        // 验证页面索引是否有效，确保索引在0到总页面数-1之间
        if (pageIndex < 0 || pageIndex >= numberOfPages) {
            throw new IllegalArgumentException("无效的页面索引: " + pageIndex + "。页面索引必须在0到" + (numberOfPages - 1) + "之间");
        }

        // 返回指定索引的页面对象
        return document.getPage(pageIndex);
    }



}
