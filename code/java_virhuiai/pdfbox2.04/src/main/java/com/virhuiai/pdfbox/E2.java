package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

/**
 * Creating an Empty PDF Document
 */
public class E2 {
    public static void main(String[] args) {
        //PDDocument类属于org.apache.pdfbox.pdmodel包，是PDF文档的内存表示。
        //通过try-with-resources方式创建PDDocument实例，自动管理资源关闭
        try (PDDocument document = new PDDocument()) {
            for (int i=0; i<10; i++) {
                //Creating a blank page
                PDPage blankPage = new PDPage();

                //Adding the blank page to the document
                document.addPage( blankPage );
            }

            //创建文档后，使用save()方法将文档保存到指定路径
            document.save("/Volumes/RamDisk/E2.pdf");
            System.out.println("PDF created");
            //不需要显式调用close()，try-with-resources会自动关闭资源
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //try-with-resources会自动处理关闭文档
    }
}
