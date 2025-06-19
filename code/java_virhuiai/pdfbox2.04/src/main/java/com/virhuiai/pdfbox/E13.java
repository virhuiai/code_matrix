package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;

/**
 * 用于加载现有PDF文档，在第一页插入图片，加密文档并保存的示例程序。
 * 该类使用Apache PDFBox库操作PDF文档。
 */
public class E13 {
    public static void main(String[] args) {
        // 定义输入PDF文件的路径
        String inputPath = "/Volumes/RamDisk/E12.pdf";
        String outPath = "/Volumes/RamDisk/E13.pdf";
        FilePermissionUtils.validateReadWriteFile(new File(outPath));

        // 使用loadPDF2方法加载PDF并通过回调处理文档
        PDDocumentUtils.loadPdfThenProcess(inputPath, document -> {
            try {
                // 创建访问权限对象
                AccessPermission ap = new AccessPermission();


                // 创建标准保护策略对象，设置所有者和用户密码
                StandardProtectionPolicy spp = new StandardProtectionPolicy("1234", "1234", ap);

                // 设置加密密钥长度
                spp.setEncryptionKeyLength(128);

                // 设置访问权限
                spp.setPermissions(ap);

                // 保护文档
                document.protect(spp);


                // 保存修改后的文档
                document.save(outPath);

                // 关闭文档
                document.close();
            } catch (IOException e) {
                // 捕获并处理IO异常，例如图片插入、加密或保存失败
                throw new RuntimeException("处理PDF文档时出错: " + e.getMessage(), e);
            }
        });
    }

}