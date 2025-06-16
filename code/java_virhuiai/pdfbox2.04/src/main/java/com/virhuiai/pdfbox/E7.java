package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，删除指定页面并保存修改后的文档
 */
public class E7 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径，指定要加载的现有PDF文件
        File inputFile = new File("/Volumes/RamDisk/E2.pdf");

        // 定义输出PDF文件的路径，保存修改后的PDF文档
        File outputFile = new File("/Volumes/RamDisk/E7.pdf");

        // 使用try-with-resources确保PDDocument资源在使用完毕后自动关闭
        // PDDocument类属于org.apache.pdfbox.pdmodel包，是PDF文档的内存表示形式
        try (PDDocument document = PDDocument.load(inputFile)) {
            // 获取文档中的页面总数
            int noOfPages = document.getNumberOfPages();
            // 打印当前文档的页面数量
            System.out.println("文档当前页面数量: " + noOfPages);

            // 定义要删除的页面索引（基于0的索引）
            int pageIndexToRemove = 2;

            // 验证页面索引是否有效，避免索引越界
            if (pageIndexToRemove < 0 || pageIndexToRemove >= noOfPages) {
                throw new IllegalArgumentException(
                    String.format("无效的页面索引: %d，文档页面范围为0到%d", pageIndexToRemove, noOfPages - 1)
                );
            }

            // 删除指定索引的页面
            document.removePage(pageIndexToRemove);

            // 打印提示信息，确认页面已删除
            System.out.println("页面索引 " + pageIndexToRemove + " 已成功删除");

            // 将修改后的PDF文档保存到指定输出路径
            // save()方法接受文件路径作为参数，保存文档到指定位置
            document.save(outputFile);

            // 打印提示信息，确认PDF文档已成功保存
            System.out.println("PDF文档已成功保存至: " + outputFile.getAbsolutePath());

            // 无需显式调用close()方法，try-with-resources会自动关闭PDDocument对象
        } catch (IOException e) {
            // 捕获并处理可能发生的IO异常，例如文件不存在、路径错误或权限问题
            // 将IO异常包装为RuntimeException抛出，附带详细错误信息便于调试
            throw new RuntimeException("无法加载或保存PDF文档: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // 捕获并处理无效页面索引异常，打印错误信息
            System.err.println("错误: " + e.getMessage());
            throw e;
        }
    }
}