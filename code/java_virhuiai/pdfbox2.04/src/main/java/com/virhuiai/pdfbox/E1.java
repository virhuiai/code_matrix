package com.virhuiai.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

/**
 * Creating an Empty PDF Document
 */
public class E1 {
    public static void main(String[] args) {
        //PDDocument类属于org.apache.pdfbox.pdmodel包，是PDF文档的内存表示。因此，通过实例化此类，您可以创建一个空的PDF文档，如以下代码块所示。
        PDDocument document = new PDDocument();
        //创建文档后，您需要将此文档保存到所需的路径，您可以使用PDDocument类的Save()方法来实现。此方法接受一个字符串值，表示您希望存储文档的路径，作为参数。以下是PDDocument类save()方法的原型。
        try {
            document.save("/Volumes/RamDisk/E1.pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                System.out.println("PDF created");
                document.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //步骤 3：关闭文档
        //当您的任务完成后，最后，您需要使用close()方法关闭PDDocument对象。以下是PDDocument类close()方法的原型。

    }
}
