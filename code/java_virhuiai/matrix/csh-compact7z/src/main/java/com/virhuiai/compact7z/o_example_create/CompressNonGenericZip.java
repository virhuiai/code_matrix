package com.virhuiai.compact7z.o_example_create;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * ## Creating archives with the archive format specific API
 * ## 使用特定归档格式API创建归档文件
 *
 *
 *
 * The archive format specific API provides easy access to the archive configuration methods (e.g. for setting the compression level). Also it uses archive format specific item description interfaces (like [IOutItemZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemZip.html) for Zip). Different archive formats support different archive item properties. Those interfaces provide access to the properties supported by the corresponding archive format, whether the unsupported properties remain hidden.
 * 特定归档格式API提供了方便的归档配置方法（例如设置压缩级别）。它还使用特定归档格式的项目描述接口（例如 Zip 的 [IOutItemZip](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutItemZip.html)）。不同的归档格式支持不同的归档项目属性。这些接口提供了对相应归档格式支持的属性的访问，而未受支持的属性则保持隐藏。
 *
 * Lets see how different archives can be created using archive format specific API
 * 让我们看看如何使用特定归档格式API创建不同的归档文件
 *
 * ### Creating Zip archive using archive format specific API
 * ### 使用特定归档格式API创建Zip归档文件
 *
 * Creating Zip archive using archive format specific API was already presented in the "first steps". The key parts of the code are:
 * 使用特定归档格式API创建Zip归档文件已在“first steps”中介绍。代码的关键部分是：
 *
 * -
 *
 * - Implementation of the [IOutCreateCallback](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutCreateCallback.html)<IOutItemCallbackZip> interface specifying the structure of the new archive. Also the progress of the compression/update operation get reported here.
 * - 实现 [IOutCreateCallback](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutCreateCallback.html)<IOutItemCallbackZip> 接口，用于指定新归档的结构。压缩/更新操作的进度也会在此处报告。
 * - Creating an instance of the [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html) interface and calling createArchive() method.
 * - 创建 [IOutArchive](https://sevenzipjbind.sourceforge.net/javadoc/net/sf/sevenzipjbinding/IOutArchive.html) 接口的实例并调用 createArchive() 方法。
 */
public class CompressNonGenericZip {
    /**
     * The callback provides information about archive items.
     * 此回调提供了有关归档项目的信息。
     */
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemZip> {

        /**
         * 设置操作结果。
         *
         * @param operationResultOk 如果操作成功，则为 true；否则为 false。
         * @throws SevenZipException 如果发生 SevenZip 相关的错误。
         */
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 在此处跟踪每个操作结果
        }

        /**
         * 设置总进度。
         *
         * @param total 操作的总字节数。
         * @throws SevenZipException 如果发生 SevenZip 相关的错误。
         */
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 在此处跟踪操作进度
        }

        /**
         * 设置已完成的进度。
         *
         * @param complete 已完成的字节数。
         * @throws SevenZipException 如果发生 SevenZip 相关的错误。
         */
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 在此处跟踪操作进度
        }

        /**
         * 获取归档项的信息。
         * 此方法在 SevenZipJBinding 库创建归档文件时回调，用于获取每个待压缩文件/目录的详细信息。
         *
         * @param index 归档项的索引。
         * @param outItemFactory 用于创建 IOutItemZip 实例的工厂。
         * @return 包含归档项信息的 IOutItemZip 实例。
         */
        public IOutItemZip getItemInformation(int index,
                                              OutItemFactory<IOutItemZip> outItemFactory) {
            // 定义文件属性，初始化为 Unix 扩展属性的位掩码
            int attr = PropID.AttributesBitMask.FILE_ATTRIBUTE_UNIX_EXTENSION;

            // 创建一个新的 Zip 归档项
            IOutItemZip item = outItemFactory.createOutItem();

            // 判断当前项是目录还是文件
            if (items[index].getContent() == null) {
                // Directory
                // 目录
                // 设置该项为目录
                item.setPropertyIsDir(true);
                // 添加目录属性位掩码
                attr |= PropID.AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY;
                // 添加 Unix 权限：drwxr-xr-x (目录，所有者读写执行，组读执行，其他读执行)
                attr |= 0x81ED << 16; // permissions: drwxr-xr-x
            } else {
                // File
                // 文件
                // 设置文件大小
                item.setDataSize((long) items[index].getContent().length);
                // 添加 Unix 权限：-rw-r--r-- (文件，所有者读写，组读，其他读)
                attr |= 0x81a4 << 16; // permissions: -rw-r--r--
            }
            // 设置归档项的路径
            item.setPropertyPath(items[index].getPath());

            // 设置归档项的属性
            item.setPropertyAttributes(attr);

            // 返回配置好的归档项
            return item;
        }

        /**
         * 获取指定索引的输入流。
         * 此方法在 SevenZipJBinding 库需要读取文件内容进行压缩时回调。
         *
         * @param i 归档项的索引。
         * @return 文件的顺序输入流，如果项是目录则返回 null。
         * @throws SevenZipException 如果发生 SevenZip 相关的错误。
         */
        public ISequentialInStream getStream(int i) throws SevenZipException {
            // 如果当前项的内容为 null，说明是目录，不需要提供输入流
            if (items[i].getContent() == null) {
                return null;
            }
            // 返回一个字节数组流，用于读取文件内容
            return new ByteArrayStream(items[i].getContent(), true);
        }
    }

    // 存储要压缩的文件的数组
    private Item[] items;

    /**
     * 主方法，程序的入口点。
     *
     * @param args 命令行参数，期望第一个参数是输出的压缩文件名。
     */
    public static void main(String[] args) {
        // 检查命令行参数的数量
        if (args.length == 1) {
            // 如果参数数量为1，则创建 CompressNonGenericZip 实例并执行压缩操作
            new CompressNonGenericZip().compress(args[0]);
            return;
        }
        // 如果参数数量不正确，则打印使用说明
        System.out.println("Usage: java CompressNonGenericZip <archive>");
    }


    /**
     * 执行压缩操作。
     *
     * @param filename 输出的压缩文件名。
     */
    private void compress(String filename) {
        // 创建要压缩的项的结构（文件和目录列表）
        items = CompressArchiveStructure.create();

        // 标记压缩操作是否成功
        boolean success = false;
        // RandomAccessFile 用于文件读写
        RandomAccessFile raf = null;
        // IOutCreateArchiveZip 接口用于创建 Zip 归档文件
        IOutCreateArchiveZip outArchive = null;
        try {
            // 以读写模式打开或创建一个 RandomAccessFile
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            // 打开输出归档对象
            // 获取 SevenZip 库的 Zip 归档创建器实例
            outArchive = SevenZip.openOutArchiveZip();

            // Configure archive
            // 配置归档文件
            // 设置压缩级别，5 表示中等压缩
            outArchive.setLevel(5);

            // Create archive
            // 创建归档文件
            // 调用 createArchive 方法创建 Zip 归档文件
            // RandomAccessFileOutStream 是用于 SevenZipJBinding 的输出流适配器
            // items.length 是要添加到归档的项的数量
            // new MyCreateCallback() 是实现 IOutCreateCallback 接口的回调，用于提供文件信息
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());

            // 压缩操作成功
            success = true;
        } catch (SevenZipException e) {
            // 捕获 SevenZip 库相关的错误
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            // 使用扩展方法获取更多错误信息
            e.printStackTraceExtended();
        } catch (Exception e) {
            // 捕获其他类型的异常
            System.err.println("Error occurs: " + e);
        } finally {
            // 确保归档对象被关闭
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    // 关闭归档时发生错误
                    System.err.println("Error closing archive: " + e);
                    success = false;
                }
            }
            // 确保 RandomAccessFile 对象被关闭
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    // 关闭文件时发生错误
                    System.err.println("Error closing file: " + e);
                    success = false;
                }
            }
        }
        // 根据 success 标志打印压缩结果
        if (success) {
            System.out.println("Compression operation succeeded");
        }
    }
}