package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * ## Creating archives with the generic API
 *
 * The one of the great features of the 7-Zip (and though of the 7-Zip-JBinding) is the ability to write archive format independent code supporting most or even all of the archive formats, supported by 7-Zip. The following code snippet accepts the required archive format as the first parameter compressing the test data in the specified archive format.The key steps to write a generic compression code are
 *
 * -
 *
 * - Use [IOutItemAllFormats](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemAllFormats.html) interface instead of the one of the archive specific interfaces, like [IOutItem7z](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItem7z.html)
 * - Create out-archive object using generic create method [SevenZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/SevenZip.html).openOutArchive([ArchiveFormat](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/ArchiveFormat.html))
 *
 * 使用通用API创建压缩档案
 * 7-Zip（以及7-Zip-JBinding）的强大特性之一是能够编写与档案格式无关的代码，支持7-Zip支持的大多数或全部档案格式。
 * 以下代码片段接受指定的档案格式作为第一个参数，将测试数据压缩为指定的档案格式。
 * 编写通用压缩代码的关键步骤包括：
 * - 使用IOutItemAllFormats接口，而不是特定于某种档案格式的接口，如IOutItem7z
 * - 使用SevenZip.openOutArchive(ArchiveFormat)方法创建输出档案对象
 */
public class CompressGeneric {
    /**
     * The callback provides information about archive items.
     *
     * 回调接口，用于提供档案条目相关信息
     * 主要功能：定义了在创建压缩档案过程中所需的信息获取和操作结果的处理
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemAllFormats> {

        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 跟踪每次操作的结果
            // 功能：记录压缩操作的成功或失败状态
            // 参数 operationResultOk：表示操作是否成功的布尔值
            // 注意事项：可能抛出SevenZipException异常，需要妥善处理
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 跟踪操作的总进度
            // 功能：设置压缩操作的总数据量
            // 参数 total：表示需要压缩的总字节数
            // 注意事项：用于进度监控，可能抛出SevenZipException异常
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 跟踪已完成的进度
            // 功能：记录当前已完成的压缩数据量
            // 参数 complete：表示已压缩的字节数
            // 注意事项：用于更新压缩进度，可能抛出SevenZipException异常
        }

        public IOutItemAllFormats getItemInformation(int index,
                                                     OutItemFactory<IOutItemAllFormats> outItemFactory) {
            // 获取指定索引的档案条目信息
            // 功能：为压缩档案中的每个条目（文件或目录）提供元数据
            // 参数 index：当前处理的条目索引
            // 参数 outItemFactory：用于创建IOutItemAllFormats对象的工厂
            // 返回值：配置好的IOutItemAllFormats对象，包含条目信息
            IOutItemAllFormats item = outItemFactory.createOutItem();

            if (items[index].getContent() == null) {
                // Directory
                // 如果条目内容为空，则表示这是一个目录
                item.setPropertyIsDir(true);
                // 设置条目为目录
            } else {
                // File
                // 如果条目内容不为空，则表示这是一个文件
                item.setDataSize((long) items[index].getContent().length);
                // 设置文件数据大小
            }

            item.setPropertyPath(items[index].getPath());
            // 设置条目的路径信息

            return item;
            // 返回配置好的条目信息对象
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            // 获取指定索引条目的输入流
            // 功能：为压缩提供文件内容的输入流
            // 参数 i：当前处理的条目索引
            // 返回值：ISequentialInStream对象，表示文件内容的输入流；若为目录则返回null
            // 注意事项：可能抛出SevenZipException异常
            if (items[i].getContent() == null) {
                return null;
                // 如果是目录，返回null
            }
            return new ByteArrayStream(items[i].getContent(), true);
            // 创建并返回基于文件内容的字节数组输入流
        }
    }

    private Item[] items;
    // 定义一个Item数组，用于存储待压缩的条目（文件或目录）
    // 用途：保存压缩档案中所有条目的信息，包括路径和内容

    public static void main(String[] args) {
        // 主函数，程序入口
        // 功能：解析命令行参数并启动压缩过程
        // 参数 args：命令行参数，包含档案格式、输出文件名和文件数量
        if (args.length != 3) {
            // 检查命令行参数数量是否正确
            System.out.println("Usage: java CompressGeneric "
                    + "<archive-format> <archive> <count-of-files>");
            // 提示正确的使用方法
            for (ArchiveFormat af : ArchiveFormat.values()) {
                // 遍历所有支持的档案格式
                if (af.isOutArchiveSupported()) {
                    // 检查格式是否支持输出（压缩）
                    System.out.println("Supported formats: " + af.name());
                    // 打印支持的档案格式名称
                }
            }
            return;
            // 参数错误时退出程序
        }

        int itemsCount = Integer.valueOf(args[2]);
        // 将第三个参数转换为整数，表示待压缩的文件数量
        new CompressGeneric().compress(args[0], args[1], itemsCount);
        // 创建CompressGeneric实例并调用compress方法开始压缩
        // 参数 args[0]：档案格式
        // 参数 args[1]：输出档案文件名
        // 参数 itemsCount：待压缩的条目数量
    }


    private void compress(String filename, String fmtName, int count) {
        // 压缩方法，执行具体的压缩操作
        // 功能：根据指定的档案格式和文件名，创建压缩档案
        // 参数 filename：输出档案的文件名
        // 参数 fmtName：档案格式名称（如ZIP、7z等）
        // 参数 count：待压缩的条目数量
        items = CompressArchiveStructure.create();
        // 初始化items数组，创建待压缩的条目结构
        // 注意事项：CompressArchiveStructure.create()的具体实现未提供，假设返回Item数组

        boolean success = false;
        // 定义标志变量，跟踪压缩操作是否成功
        RandomAccessFile raf = null;
        // 定义RandomAccessFile对象，用于读写输出档案文件
        IOutCreateArchive<IOutItemAllFormats> outArchive = null;
        // 定义输出档案对象，支持通用档案格式
        ArchiveFormat archiveFormat = ArchiveFormat.valueOf(fmtName);
        // 根据格式名称获取对应的ArchiveFormat枚举值
        try {
            raf = new RandomAccessFile(filename, "rw");
            // 创建RandomAccessFile对象，用于读写输出档案
            // 参数 filename：输出文件名
            // 参数 "rw"：读写模式

            // Open out-archive object
            // 打开输出档案对象
            outArchive = SevenZip.openOutArchive(archiveFormat);
            // 使用指定的档案格式创建输出档案对象
            // 注意事项：SevenZip.openOutArchive是7-Zip-JBinding提供的通用API

            // Configure archive
            // 配置档案参数
            if (outArchive instanceof IOutFeatureSetLevel) {
                // 检查是否支持设置压缩级别
                ((IOutFeatureSetLevel) outArchive).setLevel(5);
                // 设置压缩级别为5（中等压缩率）
                // 重要配置参数：压缩级别影响压缩速度和压缩比，范围通常为1-9
            }
            if (outArchive instanceof IOutFeatureSetMultithreading) {
                // 检查是否支持多线程压缩
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(2);
                // 设置压缩线程数为2
                // 重要配置参数：线程数影响压缩性能，需根据硬件性能调整
            }

            // Create archive
            // 创建压缩档案
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    count, new MyCreateCallback());
            // 调用createArchive方法执行压缩
            // 参数 RandomAccessFileOutStream(raf)：输出流，写入压缩数据
            // 参数 count：待压缩的条目数量
            // 参数 MyCreateCallback：回调对象，提供条目信息和输入流

            success = true;
            // 压缩成功，设置标志为true
        } catch (SevenZipException e) {
            // 捕获7-Zip相关异常
            System.err.println("7z-Error occurs:");
            // 打印错误提示
            // Get more information using extended method
            e.printStackTraceExtended();
            // 使用扩展方法打印详细的错误堆栈信息
        } catch (Exception e) {
            // 捕获其他异常
            System.err.println("Error occurs: " + e);
            // 打印通用错误信息
        } finally {
            // 清理资源
            if (outArchive != null) {
                // 检查输出档案对象是否已创建
                try {
                    outArchive.close();
                    // 关闭输出档案对象
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    // 打印关闭档案时的错误信息
                    success = false;
                    // 设置压缩失败标志
                }
            }
            if (raf != null) {
                // 检查RandomAccessFile对象是否已创建
                try {
                    raf.close();
                    // 关闭文件
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 打印关闭文件时的错误信息
                    success = false;
                    // 设置压缩失败标志
                }
            }
        }
        if (success) {
            // 检查压缩是否成功
            System.out.println(archiveFormat.getMethodName()
                    + " archive with " + count + " item(s) created");
            // 打印压缩成功的提示信息，包含档案格式和条目数量
        }
    }
}