package com.virhuiai.compact7z.o_example_create;

// 导入必要的Java和SevenZipJBinding库，用于处理文件操作和7z压缩功能
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

// 实现7z格式的压缩功能示例
// 使用SevenZipJBinding库创建7z压缩文件，相较于Zip格式，7z格式的实现较为简单，主要是MyCreateCallback.getItemInformation()方法的实现不同，7z提供了更简洁的默认行为
/**
 * Creating 7-Zip archive using archive format specific API
 * Creating 7z archive is a little bit easer that creating Zip archives. The main difference is the implementation of the MyCreateCallback.getItemInformation() method. 7z doesn't need relative complex calculation of the attribute property providing a nice default behavior.
 */
// 类功能：提供创建7z压缩文件的功能，包含处理文件和目录的逻辑
public class CompressNonGeneric7z {
    /**
     * The callback provides information about archive items.
     */
    // 内部类MyCreateCallback：实现IOutCreateCallback接口，用于提供压缩过程中所需的文件或目录信息
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItem7z> {

        // 处理每次压缩操作的结果
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：记录每次压缩操作的结果，operationResultOk表示操作是否成功
        }

        // 设置压缩任务的总数据量
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录压缩任务的总数据量，用于跟踪压缩进度
            // 参数total：表示压缩任务的总字节数
        }

        // 设置当前已完成的压缩数据量
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录当前已完成的压缩数据量，用于更新压缩进度
            // 参数complete：表示已完成的字节数
        }

        // 获取指定索引的压缩项信息
        public IOutItem7z getItemInformation(int index,
                                             OutItemFactory<IOutItem7z> outItemFactory) {
            // 创建一个新的压缩项对象
            IOutItem7z item = outItemFactory.createOutItem();
            // 中文注释：通过OutItemFactory创建新的IOutItem7z对象，用于定义压缩项的属性

            if (items[index].getContent() == null) {
                // Directory
                // 如果内容为空，表示这是一个目录
                item.setPropertyIsDir(true);
                // 中文注释：设置压缩项为目录类型
            } else {
                // File
                // 如果内容不为空，表示这是一个文件
                item.setDataSize((long) items[index].getContent().length);
                // 中文注释：设置压缩项为文件，并指定文件内容的字节长度
            }

            // 设置压缩项的路径
            item.setPropertyPath(items[index].getPath());
            // 中文注释：设置压缩项的相对路径，基于items数组中存储的路径信息

            // 返回配置好的压缩项
            return item;
            // 中文注释：返回配置完成的IOutItem7z对象，供压缩过程使用
        }

        // 获取指定索引的输入流
        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (items[i].getContent() == null) {
                // 如果是目录，返回null
                return null;
                // 中文注释：对于目录项，返回null，因为目录没有实际内容需要压缩
            }
            // 返回文件的字节流
            return new ByteArrayStream(items[i].getContent(), true);
            // 中文注释：对于文件项，返回基于文件内容的ByteArrayStream对象，用于提供压缩数据
            // 参数i：表示items数组中的索引
        }
    }

    // 存储待压缩的文件或目录的数组
    private Item[] items;
    // 中文注释：items数组存储待压缩的文件或目录信息，每个Item对象包含路径和内容等信息

    // 程序主入口
    public static void main(String[] args) {
        // 检查命令行参数
        if (args.length == 1) {
            // 如果提供了一个参数（压缩文件名），执行压缩操作
            new CompressNonGeneric7z().compress(args[0]);
            // 中文注释：创建CompressNonGeneric7z实例并调用compress方法，传入压缩文件名
            return;
        }
        // 如果参数数量不正确，打印使用说明
        System.out.println("Usage: java CompressNonGeneric7z <archive>");
        // 中文注释：提示用户正确的程序使用方法，要求提供一个压缩文件名参数
    }

    // 执行压缩操作的核心方法
    private void compress(String filename) {
        // 初始化待压缩的文件或目录结构
        items = CompressArchiveStructure.create();
        // 中文注释：调用CompressArchiveStructure.create()生成待压缩的文件和目录列表，存储在items数组中

        // 标记压缩操作是否成功
        boolean success = false;
        // 中文注释：success变量用于记录压缩操作的最终状态
        RandomAccessFile raf = null;
        // 中文注释：RandomAccessFile对象，用于读写压缩文件
        IOutCreateArchive7z outArchive = null;
        // 中文注释：IOutCreateArchive7z对象，用于操作7z压缩文件
        try {
            // 创建随机访问文件对象
            raf = new RandomAccessFile(filename, "rw");
            // 中文注释：以读写模式打开指定的压缩文件

            // Open out-archive object
            // 打开7z压缩对象
            outArchive = SevenZip.openOutArchive7z();
            // 中文注释：通过SevenZip库打开一个新的7z压缩对象，用于后续压缩操作

            // Configure archive
            // 设置压缩级别为5
            outArchive.setLevel(5);
            // 中文注释：设置压缩级别为5（中等压缩率），范围通常为0-9，值越高压缩率越高
            // 设置为固实压缩
            outArchive.setSolid(true);
            // 中文注释：启用固实压缩模式，将多个文件作为一个整体压缩以提高压缩效率

            // Create archive
            // 创建压缩文件
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());
            // 中文注释：调用createArchive方法创建7z压缩文件
            // 参数RandomAccessFileOutStream：基于RandomAccessFile的输出流
            // 参数items.length：待压缩的项数量
            // 参数MyCreateCallback：提供压缩项信息的回调对象

            // 压缩成功
            success = true;
            // 中文注释：如果没有抛出异常，标记压缩操作为成功
        } catch (SevenZipException e) {
            // 处理7z相关异常
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
            // 中文注释：捕获SevenZipException异常，打印详细错误信息
        } catch (Exception e) {
            // 处理其他异常
            System.err.println("Error occurs: " + e);
            // 中文注释：捕获其他类型的异常并打印错误信息
        } finally {
            // 关闭压缩对象
            if (outArchive != null) {
                try {
                    outArchive.close();
                    // 中文注释：关闭压缩对象，释放资源
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    // 中文注释：处理关闭压缩对象时的IO异常
                    success = false;
                    // 中文注释：如果关闭失败，标记压缩操作为失败
                }
            }
            // 关闭文件
            if (raf != null) {
                try {
                    raf.close();
                    // 中文注释：关闭RandomAccessFile，释放文件资源
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 中文注释：处理关闭文件时的IO异常
                    success = false;
                    // 中文注释：如果关闭失败，标记压缩操作为失败
                }
            }
        }
        // 输出压缩结果
        if (success) {
            System.out.println("Compression operation succeeded");
            // 中文注释：如果压缩成功，打印成功信息
        }
    }
}