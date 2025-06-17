package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 用于创建新PDF文档，添加空白页面，设置文档属性并保存到指定路径。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E8 {
    // 定义输出PDF文件的路径常量


    public static void main(String[] args) {
        String OUTPUT_PATH = "/Volumes/RamDisk/E8.pdf";
        // 检查输出路径是否有效
        File outputFile = new File(OUTPUT_PATH);
        validateOutputPath(outputFile);

        // 使用try-with-resources确保PDDocument资源在使用完毕后自动关闭
        try (PDDocument document = new PDDocument()) {
            // 添加空白页面到文档
            addBlankPage(document);

            // 设置文档属性
            setDocumentProperties(document);

            // 保存PDF文档到指定路径
            document.save(outputFile);

            // 打印成功保存的信息，包含文件的绝对路径
            System.out.println("PDF文档已成功保存至: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            // 捕获并处理IO异常，例如文件路径错误、权限问题或磁盘空间不足
            throw new RuntimeException("无法保存PDF文档: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // 捕获并处理无效参数异常，例如日期格式错误
            System.err.println("参数错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 检查输出文件路径是否有效，确保父目录存在且可写。
     *
     * @param outputFile 输出文件对象
     * @throws IllegalArgumentException 如果路径无效或不可写
     */
    private static void validateOutputPath(File outputFile) {
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            throw new IllegalArgumentException("输出路径的父目录不存在: " + parentDir.getAbsolutePath());
        }
        if (parentDir != null && !parentDir.canWrite()) {
            throw new IllegalArgumentException("输出路径的父目录不可写: " + parentDir.getAbsolutePath());
        }
    }

    /**
     * 向PDF文档添加空白页面。
     *
     * @param document PDF文档对象
     */
    private static void addBlankPage(PDDocument document) {
        // 创建一个新的空白页面
        PDPage blankPage = new PDPage();
        // 将空白页面添加到文档
        document.addPage(blankPage);
    }

    /**
     * 设置PDF文档的属性，包括作者、标题、创建者、主题、创建日期、修改日期和关键字。
     *
     * @param document PDF文档对象
     */
    private static void setDocumentProperties(PDDocument document) {
        // 获取文档的属性对象
        PDDocumentInformation pdd = document.getDocumentInformation();

        // 设置文档作者
        pdd.setAuthor("Tutorialspoint");

        // 设置文档标题
        pdd.setTitle("Sample document");

        // 设置文档创建者
        pdd.setCreator("PDF Examples");

        // 设置文档主题
        pdd.setSubject("Example document");

        // 设置文档创建日期
        Calendar creationDate = new GregorianCalendar();
        creationDate.set(2015, Calendar.NOVEMBER, 5); // 使用Calendar常量提高可读性
        pdd.setCreationDate(creationDate);

        // 设置文档修改日期
        Calendar modificationDate = new GregorianCalendar();
        modificationDate.set(2016, Calendar.JUNE, 5); // 使用Calendar常量提高可读性
        pdd.setModificationDate(modificationDate);

        // 设置文档关键字，多个关键字以逗号分隔
        pdd.setKeywords("sample, first example, my pdf");
    }
}