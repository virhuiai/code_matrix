package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * # Troubleshoot problems using tracing
 *
 * One of the weak sides of the 7-zip compression engine is a rather simple error reporting. If some provided data doesn't satisfy the compressor it fails without any descriptive error message. One way to get an clue is to use 7-Zip-JBinding tracing feature. Here is the code passing invalid data size for the item 1 and though failing.
 */
// 使用追踪功能解决7-zip压缩引擎的问题
// 7-zip压缩引擎的错误报告较为简单，当提供的数据不满足要求时，会直接失败且无详细错误信息
// 通过7-Zip-JBinding的追踪功能可以获取更多线索
// 本代码示例故意为item 1设置无效数据大小，导致压缩失败，以展示追踪功能的使用

public class CompressWithError {
    /**
     * The callback provides information about archive items.
     */
    // 定义回调接口，用于提供归档项目的相关信息
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemTar> {
        // 实现IOutCreateCallback接口，处理TAR格式归档的创建回调

        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 跟踪每个操作的结果
            // 参数operationResultOk表示操作是否成功
            // 用于在归档过程中记录每个操作的状态
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 设置归档操作的总体进度
            // 参数total表示归档的总数据量
            // 用于监控归档操作的整体进度
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 设置当前已完成的归档数据量
            // 参数complete表示已完成的数据量
            // 用于实时更新归档操作的进度
        }

        public IOutItemTar getItemInformation(int index,
                                              OutItemFactory<IOutItemTar> outItemFactory) {
            // 获取指定索引的归档项目信息
            // 参数index表示当前处理的归档项目索引
            // 参数outItemFactory用于创建归档项目对象
            // 返回IOutItemTar对象，包含归档项目的元数据

            IOutItemTar item = outItemFactory.createOutItem();
            // 创建一个新的归档项目对象

            if (items[index].getContent() == null) {
                // Directory
                // 如果当前项目的内容为空，则表示这是一个目录
                item.setPropertyIsDir(true);
                // 设置项目属性为目录
            } else {
                // File
                // 如果当前项目有内容，则表示这是一个文件
                item.setDataSize((long) items[index].getContent().length);
                // 设置文件的数据大小为内容的字节长度
            }

            item.setPropertyPath(items[index].getPath());
            // 设置归档项目的路径

            if (index == 1) {
                // 特殊处理：为索引为1的项目设置无效数据大小
                item.setDataSize(null); // Provide invalid data for item 1
                // 故意将数据大小设置为null，导致压缩失败
                // 用于测试7-Zip-JBinding的错误追踪功能
            }

            return item;
            // 返回配置好的归档项目对象
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            // 获取指定索引项目的输入流
            // 参数i表示当前处理的归档项目索引
            // 返回ISequentialInStream对象，用于提供项目的数据流

            if (items[i].getContent() == null) {
                // 如果项目内容为空（目录），返回null
                return null;
            }
            return new ByteArrayStream(items[i].getContent(), true);
            // 为文件项目创建字节数组输入流
            // 参数true表示流是只读的
        }
    }

    private Item[] items;
    // 定义归档项目数组，存储待压缩的文件或目录信息

    public static void main(String[] args) {
        // 程序主入口
        // 参数args为命令行参数，预期接收一个归档文件名
        if (args.length == 1) {
            // 如果提供了一个参数（归档文件名）
            new CompressWithError().compress(args[0]);
            // 创建CompressWithError实例并调用compress方法进行压缩
            return;
        }
        System.out.println("Usage: java CompressNonGenericTar <archive>");
        // 如果参数数量不正确，打印使用说明
    }


    private void compress(String filename) {
        // 执行压缩操作
        // 参数filename为目标归档文件的路径
        // 方法创建TAR格式归档，并处理可能的错误

        items = CompressArchiveStructure.create();
        // 初始化归档项目数组，调用外部方法创建项目结构

        boolean success = false;
        // 标志变量，记录压缩操作是否成功
        RandomAccessFile raf = null;
        // RandomAccessFile对象，用于读写归档文件
        IOutCreateArchiveTar outArchive = null;
        // TAR格式归档对象，用于创建和管理归档
        try {
            raf = new RandomAccessFile(filename, "rw");
            // 以读写模式打开目标归档文件

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveTar();
            // 创建TAR格式的归档对象

            // Activate tracing
            outArchive.setTrace(true);
            // 启用追踪功能，以便在发生错误时获取详细信息

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());
            // 创建归档
            // 参数1：RandomAccessFileOutStream包装文件输出流
            // 参数2：items.length指定归档项目数量
            // 参数3：MyCreateCallback提供归档项目的回调处理

            success = true;
            // 如果没有抛出异常，标记压缩成功
        } catch (SevenZipException e) {
            // 捕获7-Zip相关异常
            System.err.println("Tar-Error occurs:");
            // 打印TAR归档错误提示
            // Get more information using extended method
            e.printStackTraceExtended();
            // 使用扩展方法打印详细错误信息，包括追踪信息
        } catch (Exception e) {
            // 捕获其他异常
            System.err.println("Error occurs: " + e);
            // 打印错误信息
        } finally {
            // 清理资源，确保归档对象和文件正确关闭
            if (outArchive != null) {
                try {
                    outArchive.close();
                    // 关闭归档对象
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    // 打印关闭归档时的错误信息
                    success = false;
                    // 标记操作失败
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                    // 关闭文件流
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 打印关闭文件时的错误信息
                    success = false;
                    // 标记操作失败
                }
            }
        }
        if (success) {
            System.out.println("Compression operation succeeded");
            // 如果操作成功，打印成功信息
        }
    }
}