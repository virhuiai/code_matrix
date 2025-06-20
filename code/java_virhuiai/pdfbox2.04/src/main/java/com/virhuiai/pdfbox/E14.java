package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，嵌入JavaScript动作，加密文档并保存的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E14 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E12.pdf";
        String outPath = "/Volumes/RamDisk/E14.pdf";
        FilePermissionUtils.validateReadWriteFile(new File(outPath));

        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // 定义JavaScript代码
                String javaScript = "app.alert( {cMsg: 'this is an example', nIcon: 3,"
                        + " nType: 0, cTitle: 'PDFBox Javascript example' } );";
                // 创建PDActionJavaScript对象
                PDActionJavaScript PDAjavascript = new PDActionJavaScript(javaScript);
                // 嵌入JavaScript动作
                document.getDocumentCatalog().setOpenAction(PDAjavascript);
                // 保存修改后的文档
                document.save(outPath);
                // 打印提示信息，确认数据已添加
                System.out.println("JavaScript added and document encrypted");
                // 关闭文档
                document.close();
            } catch (IOException e) {
                // 捕获并处理IO异常，例如JavaScript嵌入、加密或保存失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }
}