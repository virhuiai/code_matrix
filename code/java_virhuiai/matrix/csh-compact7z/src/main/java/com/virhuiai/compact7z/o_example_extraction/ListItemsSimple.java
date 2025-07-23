package com.virhuiai.compact7z.o_example_extraction;

// 导入必要的 Java IO 包，用于文件操作
import java.io.IOException;
// 导入 RandomAccessFile 类，用于随机访问文件
import java.io.RandomAccessFile;

// 导入 SevenZipJBinding 相关接口和类，用于处理 7z 压缩文件
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 * Standard vs Simple interface
 * The 7-Zip-JBinding library offers two different interfaces: standard and simple. The standard interface directly maps all native 7-Zip library methods providing C++ like interface. The simple interface is a try to provide more Java-like easy to use interface. It was designed either for quick coding and easy start and not for feature richness nor for performance.
 *
 * The following examples will be presented in both standard and simple interface beginning with as simple interface. Please note, that the full performance can only be reached with the standard interface.
 *
 * Quering items in archive
 * Simple interface
 * This example shows how to print a list of items of archive using simple interface.
 */
// 类说明：ListItemsSimple 类用于展示如何使用 SevenZipJBinding 的简单接口列出 7z 压缩文件中的内容
// 功能：读取用户指定的压缩文件，列出其中每个文件的原始大小、压缩后大小和文件路径
// 注意事项：依赖 SevenZipJBinding 库，需确保库正确配置
public class ListItemsSimple {
    // 主方法：程序入口，接收命令行参数并执行压缩文件内容列出操作
    // 参数：args - 命令行参数，预期为压缩文件的路径
    // 返回值：无
    public static void main(String[] args) {
        // 检查命令行参数是否提供
        if (args.length == 0) {
            // 如果未提供参数，打印使用说明并退出程序
            System.out.println("Usage: java ListItemsSimple <archive-name>");
            // 中文注释：提示用户程序的正确使用方式，需提供压缩文件名作为参数
            return;
        }
        // 声明随机访问文件对象，用于读取压缩文件
        RandomAccessFile randomAccessFile = null;
        // 中文注释：randomAccessFile 用于以只读模式打开压缩文件，支持随机访问
        // 声明压缩文件输入流接口对象
        IInArchive inArchive = null;
        // 中文注释：inArchive 是 SevenZipJBinding 提供的接口，用于操作压缩文件
        // 使用 try-catch 捕获可能出现的异常
        try {
            // 初始化 RandomAccessFile 对象，以只读模式打开指定压缩文件
            randomAccessFile = new RandomAccessFile(args[0], "r");
            // 中文注释：args[0] 为用户输入的压缩文件路径，"r" 表示只读模式
            // 使用 SevenZip 库打开压缩文件，自动检测压缩类型
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));
            // 中文注释：openInArchive 方法自动检测压缩文件类型，RandomAccessFileInStream 提供文件流支持

            // 获取压缩文件的简单接口对象
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            // 中文注释：simpleInArchive 是简单接口，方便遍历压缩文件中的内容

            // 打印表头，显示文件大小、压缩后大小和文件名
            System.out.println("   Size   | Compr.Sz. | Filename");
            System.out.println("----------+-----------+---------");
            // 中文注释：输出格式化的表头，用于清晰展示压缩文件内容的列表

            // 遍历压缩文件中的每个条目
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                // 中文注释：getArchiveItems 方法返回压缩文件中所有条目的迭代器
                // 每个 item 代表压缩文件中的一个文件或目录
                // 格式化输出每个条目的大小、压缩后大小和路径
                System.out.println(String.format("%9s | %9s | %s", //
                        item.getSize(),
                        item.getPackedSize(),
                        item.getPath()));
                // 中文注释：item.getSize() 返回文件原始大小
                // item.getPackedSize() 返回文件压缩后大小
                // item.getPath() 返回文件在压缩包中的路径
                // 使用 String.format 格式化输出，确保对齐
            }
        } catch (Exception e) {
            // 捕获并打印任何异常信息
            System.err.println("Error occurs: " + e);
            // 中文注释：处理运行时可能出现的异常，例如文件不存在或格式错误
        } finally {
            // 确保资源正确关闭
            if (inArchive != null) {
                // 中文注释：检查 inArchive 是否已初始化
                try {
                    // 关闭压缩文件输入流
                    inArchive.close();
                    // 中文注释：释放 inArchive 占用的资源
                } catch (SevenZipException e) {
                    // 捕获关闭压缩文件时的异常
                    System.err.println("Error closing archive: " + e);
                    // 中文注释：处理关闭压缩文件时可能出现的 SevenZip 特定异常
                }
            }
            if (randomAccessFile != null) {
                // 中文注释：检查 randomAccessFile 是否已初始化
                try {
                    // 关闭随机访问文件
                    randomAccessFile.close();
                    // 中文注释：释放文件资源，确保文件句柄被正确关闭
                } catch (IOException e) {
                    // 捕获关闭文件时的异常
                    System.err.println("Error closing file: " + e);
                    // 中文注释：处理关闭文件时可能出现的 IO 异常
                }
            }
        }
    }
}