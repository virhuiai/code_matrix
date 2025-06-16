package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载一个PDF文档并向其中添加空白页面
 */
public class E6 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径，指定要加载的现有PDF文件
        File file = new File("/Volumes/RamDisk/E2.pdf");

        // 使用try-with-resources确保PDDocument资源在使用完毕后自动关闭
        // PDDocument类属于org.apache.pdfbox.pdmodel包，是PDF文档的内存表示形式
        try (PDDocument document = PDDocument.load(file)) {
            // 创建一个新的空白页面，PDPage类表示PDF文档中的单个页面
            PDPage blankPage = new PDPage();

            // 将新创建的空白页面添加到PDF文档中
            // addPage()方法用于向PDDocument对象添加页面
            document.addPage(blankPage);

            // 将修改后的PDF文档保存到指定路径
            // save()方法接受文件路径作为参数，保存文档到指定位置
            document.save("/Volumes/RamDisk/E6.pdf");

            // 打印提示信息，确认PDF文档已成功创建
            System.out.println("PDF创建成功");

            // 无需显式调用close()方法，try-with-resources会自动关闭PDDocument对象
        } catch (IOException e) {
            // 捕获并处理可能发生的IO异常，例如文件路径错误或权限问题
            // 将IO异常包装为RuntimeException抛出，便于调试
            throw new RuntimeException("无法加载或保存PDF文档: " + e.getMessage(), e);
        }
    }
}