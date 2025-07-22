package com.virhuiai.compact7z.o_example_update;

import java.io.Closeable;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * # Modifying existing archives
 *
 * 7-Zip-JBinding provides API for archive modification. Especially by small changes the modification of an archive is much faster compared to the extraction and the consequent compression. The archive modification API (like the compression API) offers archive format specific and archive format independent variants. The process of the modification of an existing archive contains following steps:
 *
 * -
 *
 * - Open existing archive obtaining an instance of the [IInArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IInArchive.html)
 * - Call [IInArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IInArchive.html).getConnectedOutArchive() to get an instance of the [IOutUpdateArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchive.html)
 * - Optionally cast it to an archive format specific API interface like [IOutUpdateArchiveZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchiveZip.html)
 * - Call [IOutUpdateArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutUpdateArchive.html).updateItems() passing the new count of items and a callback implementing the modification
 *
 * The following snippets show the modification process in details using archive format independent API. The archive to be modified is one of the Zip, 7z or Tar archives created by the corresponding compression snippets on this page. The structure of those archives is specified in the [CompressArchiveStructure](https://sevenzipjbind.sourceforge.net/compression_snippets.html#archive-test-structure) class.
 *
 * ## Altering existing archive items
 *
 * The first snippet modifies one existing item with index 2 (info.txt, 16 bytes):It changes the path (name of the item) to the "info2.txt"It changes the content to the "More Info!" (10 bytes)
 */
// 类说明：UpdateAlterItems类用于修改现有压缩档案中的指定项，主要功能是更改档案中索引为2的项（info.txt）的路径和内容
public class UpdateAlterItems {
    /**
     * The callback defines the modification to be made.
     */
    // 内部类说明：MyCreateCallback实现了IOutCreateCallback接口，用于定义档案修改的具体逻辑
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemAllFormats> {

        // 方法说明：setOperationResult用于跟踪每次操作的结果
        // 参数：operationResultOk - 操作是否成功
        // 异常：可能抛出SevenZipException，表示操作过程中出现错误
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：记录每次操作的结果，通常用于确认操作是否成功
        }

        // 方法说明：setTotal设置操作的总进度
        // 参数：total - 操作的总大小（字节）
        // 异常：可能抛出SevenZipException，表示设置进度时出错
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录操作的总进度，通常用于进度条或日志显示
        }

        // 方法说明：setCompleted设置已完成的进度
        // 参数：complete - 已完成的大小（字节）
        // 异常：可能抛出SevenZipException，表示设置进度时出错
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录操作的当前完成进度，用于实时更新进度信息
        }

        // 方法说明：getItemInformation定义档案中每个项的修改信息
        // 参数：index - 档案项的索引；outItemFactory - 用于创建输出项的工厂
        // 返回值：IOutItemAllFormats - 修改后的档案项信息
        // 异常：可能抛出SevenZipException，表示获取项信息时出错
        public IOutItemAllFormats getItemInformation(int index,
                                                     OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            // 中文注释：检查是否为需要修改的项（索引为2）
            if (index != 2) {
                // Keep properties and content
                // 中文注释：对于非索引2的项，保留原始属性和内容
                return outItemFactory.createOutItem(index);
            }

            // 中文注释：创建并克隆索引为2的项的属性
            IOutItemAllFormats item;
            item = outItemFactory.createOutItemAndCloneProperties(index);

            // Change property PATH
            // 中文注释：设置标志表示需要更新属性
            item.setUpdateIsNewProperties(true);
            // 中文注释：将项的路径（文件名）更改为"info2.txt"
            item.setPropertyPath("info2.txt");

            // Change content
            // 中文注释：设置标志表示需要更新数据内容
            item.setUpdateIsNewData(true);
            // 中文注释：设置新的数据大小（NEW_CONTENT的字节长度）
            item.setDataSize((long) NEW_CONTENT.length);

            // 中文注释：返回修改后的项信息
            return item;
        }

        // 方法说明：getStream提供修改项的数据流
        // 参数：i - 档案项的索引
        // 返回值：ISequentialInStream - 项的新数据流，若无需修改则返回null
        // 异常：可能抛出SevenZipException，表示获取数据流时出错
        public ISequentialInStream getStream(int i) throws SevenZipException {
            // 中文注释：检查是否为需要修改的项（索引为2）
            if (i != 2) {
                // 中文注释：非索引2的项不提供数据流
                return null;
            }
            // 中文注释：为索引2的项提供新的数据流，包含"More Info!"内容
            return new ByteArrayStream(NEW_CONTENT, true);
        }
    }

    // 变量说明：NEW_CONTENT存储要替换的项的新内容，内容为"More Info!"的字节数组
    static final byte[] NEW_CONTENT = "More Info!".getBytes();

    // 方法说明：main方法是程序入口，检查命令行参数并调用压缩方法
    // 参数：args - 命令行参数，预期包含输入和输出档案路径
    public static void main(String[] args) {
        // 中文注释：检查命令行参数是否包含输入和输出档案路径
        if (args.length == 2) {
            // 中文注释：创建UpdateAlterItems实例并调用compress方法进行档案修改
            new UpdateAlterItems().compress(args[0], args[1]);
            return;
        }
        // 中文注释：若参数不正确，打印使用说明
        System.out.println("Usage: java UpdateAlterItems <in-arc> <out-arc>");
    }

    // 方法说明：compress方法执行档案修改的核心逻辑
    // 参数：in - 输入档案路径；out - 输出档案路径
    private void compress(String in, String out) {
        // 中文注释：定义操作成功标志
        boolean success = false;
        // 中文注释：声明输入和输出文件的随机访问文件对象
        RandomAccessFile inRaf = null;
        RandomAccessFile outRaf = null;
        // 中文注释：声明输入档案接口
        IInArchive inArchive;
        // 中文注释：声明输出档案接口
        IOutUpdateArchive<IOutItemAllFormats> outArchive = null;
        // 中文注释：存储需要关闭的资源列表
        List<Closeable> closeables = new ArrayList<Closeable>();
        try {
            // Open input file
            // 中文注释：打开输入档案以只读模式
            inRaf = new RandomAccessFile(in, "r");
            // 中文注释：将输入文件资源添加到关闭列表
            closeables.add(inRaf);
            // 中文注释：创建输入文件流
            IInStream inStream = new RandomAccessFileInStream(inRaf);

            // Open in-archive
            // 中文注释：打开输入档案，获取IInArchive实例
            inArchive = SevenZip.openInArchive(null, inStream);
            // 中文注释：将输入档案资源添加到关闭列表
            closeables.add(inArchive);

            // 中文注释：打开输出档案以读写模式
            outRaf = new RandomAccessFile(out, "rw");
            // 中文注释：将输出文件资源添加到关闭列表
            closeables.add(outRaf);

            // Open out-archive object
            // 中文注释：获取与输入档案关联的输出档案接口
            outArchive = inArchive.getConnectedOutArchive();

            // Modify archive
            // 中文注释：调用updateItems方法执行档案修改，传入输出文件流、档案项数量和回调对象
            outArchive.updateItems(new RandomAccessFileOutStream(outRaf),
                    inArchive.getNumberOfItems(), new MyCreateCallback());

            // 中文注释：修改成功，设置成功标志
            success = true;
        } catch (SevenZipException e) {
            // 中文注释：捕获7-Zip相关异常，打印详细错误信息
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
        } catch (Exception e) {
            // 中文注释：捕获其他异常，打印错误信息
            System.err.println("Error occurs: " + e);
        } finally {
            // 中文注释：确保所有资源正确关闭
            for (int i = closeables.size() - 1; i >= 0; i--) {
                try {
                    // 中文注释：关闭资源
                    closeables.get(i).close();
                } catch (Throwable e) {
                    // 中文注释：捕获关闭资源时的异常，打印错误信息并设置失败标志
                    System.err.println("Error closing resource: " + e);
                    success = false;
                }
            }
        }
        // 中文注释：如果操作成功，打印成功信息
        if (success) {
            System.out.println("Update successful");
        }
    }
}