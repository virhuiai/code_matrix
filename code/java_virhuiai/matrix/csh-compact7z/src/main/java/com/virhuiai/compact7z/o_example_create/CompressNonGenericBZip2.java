package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveBZip2;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Creating BZip2 archive using archive format specific API
 * 通过归档格式特定的API创建BZip2归档
 * BZip2 is like GZip a stream archive format. It compresses single archive item supporting no additional archive item properties at all.
 * BZip2像GZip一样是一种流归档格式。它压缩单个归档项，完全不支持额外的归档项属性。
 */
public class CompressNonGenericBZip2 {
    /**
     * The callback provides information about archive items.
     * 回调接口，提供关于归档项的信息。
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemBZip2> {

        /**
         * Invoked to inform about the result of the operation for the given item.
         * 调用此方法来通知给定项的操作结果。
         *
         * @param operationResultOk indicates whether the operation was successful.
         * 指示操作是否成功。
         * @throws SevenZipException if an error occurs during the operation.
         * 如果在操作期间发生错误。
         */
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 在此处跟踪每个操作结果
        }

        /**
         * Invoked to inform about the total size of the archive being created.
         * 调用此方法来通知正在创建的归档的总大小。
         *
         * @param total total size of the archive.
         * 归档的总大小。
         * @throws SevenZipException if an error occurs during the operation.
         * 如果在操作期间发生错误。
         */
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 在此处跟踪操作进度
        }

        /**
         * Invoked to inform about the number of bytes that have been processed.
         * 调用此方法来通知已处理的字节数。
         *
         * @param complete number of bytes that have been processed.
         * 已处理的字节数。
         * @throws SevenZipException if an error occurs during the operation.
         * 如果在操作期间发生错误。
         */
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 在此处跟踪操作进度
        }

        /**
         * Invoked to get information about the archive item.
         * 调用此方法来获取归档项的信息。
         *
         * @param index        the index of the archive item.
         * 归档项的索引。
         * @param outItemFactory factory to create the output item.
         * 创建输出项的工厂。
         * @return an instance of IOutItemBZip2 containing information about the item.
         * 包含项信息的IOutItemBZip2实例。
         */
        public IOutItemBZip2 getItemInformation(int index,
                                                OutItemFactory<IOutItemBZip2> outItemFactory) {
            IOutItemBZip2 item = outItemFactory.createOutItem();
            // 创建一个新的BZip2输出项

            item.setDataSize((long) content.length);
            // 设置要压缩的数据的大小

            return item;
            // 返回配置好的归档项
        }

        /**
         * Invoked to get the input stream for the archive item.
         * 调用此方法来获取归档项的输入流。
         *
         * @param i the index of the archive item.
         * 归档项的索引。
         * @return an instance of ISequentialInStream providing the data to be compressed.
         * 提供要压缩数据的ISequentialInStream实例。
         * @throws SevenZipException if an error occurs.
         * 如果发生错误。
         */
        public ISequentialInStream getStream(int i) throws SevenZipException {
            return new ByteArrayStream(content, true);
            // 返回一个字节数组流，其中包含要压缩的内容。
            // 第二个参数 'true' 表示流是可重置的。
        }
    }

    byte[] content;
    // 存储要压缩的数据内容

    /**
     * Main method to run the compression example.
     * 运行压缩示例的主方法。
     *
     * @param args command line arguments. Expects one argument: the output filename.
     * 命令行参数。期望一个参数：输出文件名。
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            new CompressNonGenericBZip2().compress(args[0]);
            // 如果提供了文件名参数，则创建实例并调用压缩方法
            return;
        }
        System.out.println("Usage: java CompressNonGenericBZip2 <archive>");
        // 如果没有提供文件名参数，则打印使用说明
    }

    /**
     * Compresses data into a BZip2 archive.
     * 将数据压缩成BZip2归档。
     *
     * @param filename The name of the output BZip2 file.
     * 输出BZip2文件的名称。
     */
    private void compress(String filename) {
        boolean success = false;
        // 标记压缩操作是否成功
        RandomAccessFile raf = null;
        // 用于读写文件，随机访问文件流
        IOutCreateArchiveBZip2 outArchive = null;
        // BZip2输出归档对象接口
        content = CompressArchiveStructure.create()[0].getContent();
        // 获取要压缩的内容，这里假设是从某个结构中获取第一个内容
        try {
            raf = new RandomAccessFile(filename, "rw");
            // 以读写模式打开或创建文件，用于写入BZip2归档

            // Open out-archive object
            // 打开输出归档对象
            outArchive = SevenZip.openOutArchiveBZip2();
            // 通过SevenZip库获取一个BZip2的输出归档实例

            // Configure archive
            // 配置归档参数
            outArchive.setLevel(5);
            // 设置压缩级别，5表示中等压缩

            // Create archive
            // 创建归档
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    1, new MyCreateCallback());
            // 调用createArchive方法开始创建BZip2归档。
            // 参数1: 用于写入归档数据的输出流，这里是RandomAccessFileOutStream封装的raf。
            // 参数2: 要压缩的项目数量，这里是1。
            // 参数3: 回调对象，提供关于要压缩项的信息和数据流。

            success = true;
            // 如果没有抛出异常，则认为操作成功
        } catch (SevenZipException e) {
            System.err.println("BZip2-Error occurs:");
            // 捕获SevenZip库特有的异常
            // Get more information using extended method
            // 使用扩展方法获取更多错误信息
            e.printStackTraceExtended();
            // 打印扩展的堆栈跟踪信息，包含SevenZip相关的详细错误
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            // 捕获其他所有异常
        } finally {
            if (outArchive != null) {
                try {
                    outArchive.close();
                    // 关闭输出归档对象，释放资源
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    // 关闭归档时发生IO错误
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                    // 关闭文件随机访问流
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 关闭文件时发生IO错误
                    success = false;
                }
            }
        }
        if (success) {
            System.out.println("Compression operation succeeded");
            // 如果操作成功，则打印成功信息
        }
    }
}