package com.virhuiai.compact7z.o_example_create;

// 导入必要的Java IO包，用于文件操作
import java.io.IOException;
// 导入RandomAccessFile类，用于随机访问文件
import java.io.RandomAccessFile;

// 导入SevenZipJBinding相关接口和类，用于TAR档案操作
import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
// 导入回调接口，用于创建档案时的信息提供
import net.sf.sevenzipjbinding.IOutCreateCallback;
// 导入TAR档案项接口
import net.sf.sevenzipjbinding.IOutItemTar;
// 导入输入流接口，用于读取数据
import net.sf.sevenzipjbinding.ISequentialInStream;
// 导入SevenZip核心类
import net.sf.sevenzipjbinding.SevenZip;
// 导入SevenZip异常类
import net.sf.sevenzipjbinding.SevenZipException;
// 导入输出项工厂类，用于创建TAR档案项
import net.sf.sevenzipjbinding.impl.OutItemFactory;
// 导入随机访问文件输出流类
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
// 导入字节数组流工具类
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Creating Tar archive using archive format specific API
 * Creating tar archives is pretty much the same, as creating 7z archives, since the default values for most properties are good enough in most cases. Note, that the tar archive format do have attribute property. But due to the Unix-nature of the tar, it was renamed to PosixAttributes. Also the meaning of the bits is different.
 */
// 使用特定于档案格式的API创建TAR档案
// 创建TAR档案与创建7z档案类似，大多数属性的默认值在大多数情况下都适用
// 注意：TAR档案格式具有属性，但由于其Unix特性，被重命名为PosixAttributes，且位含义不同
public class CompressNonGenericTar {
    /**
     * The callback provides information about archive items.
     */
    // 回调接口，用于提供档案项相关信息
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemTar> {

        // 设置操作结果，处理每次操作的成功或失败状态
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 在此跟踪每个操作的结果
        }

        // 设置总进度，用于跟踪整个压缩过程的总数据量
        // 参数 total: 压缩的总数据量
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 在此跟踪操作进度
        }

        // 设置已完成进度，用于更新当前压缩的进度
        // 参数 complete: 已完成的数据量
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 在此跟踪操作进度
        }

        // 获取档案项信息，根据索引创建对应的档案项
        // 参数 index: 档案项的索引
        // 参数 outItemFactory: 用于创建TAR档案项的工厂
        // 返回值: 配置好的TAR档案项
        public IOutItemTar getItemInformation(int index,
                                              OutItemFactory<IOutItemTar> outItemFactory) {
            // 创建一个新的TAR档案项
            IOutItemTar item = outItemFactory.createOutItem();

            // 判断是否为目录
            if (items[index].getContent() == null) {
                // Directory
                // 如果内容为空，则设置为目录
                item.setPropertyIsDir(true);
            } else {
                // File
                // 如果是文件，设置文件数据大小
                item.setDataSize((long) items[index].getContent().length);
            }

            // 设置档案项的路径
            item.setPropertyPath(items[index].getPath());

            // 返回配置好的档案项
            return item;
        }

        // 获取指定索引的输入流，用于读取文件内容
        // 参数 i: 档案项的索引
        // 返回值: 输入流对象，若为目录则返回null
        public ISequentialInStream getStream(int i) throws SevenZipException {
            // 如果是目录，返回null
            if (items[i].getContent() == null) {
                return null;
            }
            // 创建字节数组流，包含文件内容
            return new ByteArrayStream(items[i].getContent(), true);
        }
    }

    // 存储待压缩的档案项数组
    private Item[] items;

    // 主函数，程序入口
    // 参数 args: 命令行参数，预期包含一个档案文件名
    public static void main(String[] args) {
        // 检查命令行参数是否为一个
        if (args.length == 1) {
            // 创建CompressNonGenericTar实例并调用压缩方法
            new CompressNonGenericTar().compress(args[0]);
            return;
        }
        // 如果参数不正确，打印使用说明
        System.out.println("Usage: java CompressNonGenericTar <archive>");
    }

    // 执行TAR档案压缩的私有方法
    // 参数 filename: 输出档案的文件名
    private void compress(String filename) {
        // 初始化待压缩的档案项结构
        items = CompressArchiveStructure.create();

        // 标记压缩操作是否成功
        boolean success = false;
        // 随机访问文件对象
        RandomAccessFile raf = null;
        // TAR档案输出对象
        IOutCreateArchiveTar outArchive = null;
        try {
            // 以读写模式打开目标文件
            raf = new RandomAccessFile(filename, "rw");

            // 打开TAR档案输出对象
            outArchive = SevenZip.openOutArchiveTar();

            // No configuration methods for Tar
            // TAR档案无需额外配置方法

            // 创建档案，使用随机访问文件输出流和自定义回调
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    items.length, new MyCreateCallback());

            // 标记压缩成功
            success = true;
        } catch (SevenZipException e) {
            // 捕获SevenZip相关异常
            System.err.println("Tar-Error occurs:");
            // 打印详细的错误堆栈信息
            e.printStackTraceExtended();
        } catch (Exception e) {
            // 捕获其他异常
            System.err.println("Error occurs: " + e);
        } finally {
            // 确保资源正确关闭
            if (outArchive != null) {
                try {
                    // 关闭档案输出对象
                    outArchive.close();
                } catch (IOException e) {
                    // 捕获关闭档案时的IO异常
                    System.err.println("Error closing archive: " + e);
                    // 标记压缩失败
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    // 关闭随机访问文件
                    raf.close();
                } catch (IOException e) {
                    // 捕获关闭文件时的IO异常
                    System.err.println("Error closing file: " + e);
                    // 标记压缩失败
                    success = false;
                }
            }
        }
        // 如果压缩成功，打印成功信息
        if (success) {
            System.out.println("Compression operation succeeded");
        }
    }
}