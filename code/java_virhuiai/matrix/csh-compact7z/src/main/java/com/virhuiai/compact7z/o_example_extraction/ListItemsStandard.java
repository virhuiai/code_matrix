package com.virhuiai.compact7z.o_example_extraction;

// 包声明：定义当前类所属的包路径，用于组织代码
// 中文注释：指定类所在的包结构，便于代码管理和调用

import java.io.IOException;
import java.io.RandomAccessFile;

// 导入语句：引入处理文件输入输出的相关类
// 中文注释：IOException 用于处理输入输出异常，RandomAccessFile 用于随机访问文件

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

// 导入语句：引入 SevenZipJBinding 库的相关类
// 中文注释：这些类用于处理压缩文件的操作，包括打开、读取和关闭压缩文件

/**
 * Standard interface
 * This example shows how to print a list of items of archive using standard interface.
 */
// 类注释：标准接口示例，展示如何使用标准接口列出压缩文件中的项目
// 中文注释：此类实现了一个简单的程序，用于读取并打印压缩文件中的文件列表

public class ListItemsStandard {
    // 类定义：ListItemsStandard 类，包含主程序逻辑
    // 中文注释：此类为程序的入口，处理压缩文件的读取和信息打印

    public static void main(String[] args) {
        // 方法定义：main 方法，程序的入口点
        // 中文注释：接收命令行参数并执行压缩文件内容列表的打印操作
        // 参数说明：args 为命令行传入的参数，预期为压缩文件的路径

        if (args.length == 0) {
            // 条件检查：判断是否提供了命令行参数
            // 中文注释：如果未提供压缩文件路径，则打印使用说明并退出程序
            System.out.println("Usage: java ListItemsStandard <archive-name>");
            // 打印使用说明
            // 中文注释：提示用户需要提供压缩文件的路径作为参数
            return;
            // 返回语句：退出程序
            // 中文注释：当参数缺失时，提前终止程序运行
        }
        RandomAccessFile randomAccessFile = null;
        // 变量声明：randomAccessFile 用于访问压缩文件
        // 中文注释：定义 RandomAccessFile 对象，初始为 null，用于后续文件操作
        IInArchive inArchive = null;
        // 变量声明：inArchive 用于操作压缩文件
        // 中文注释：定义 IInArchive 对象，初始为 null，用于处理压缩文件内容

        try {
            // try 块：开始异常处理
            // 中文実行流程：尝试打开和读取压缩文件，捕获可能出现的异常

            randomAccessFile = new RandomAccessFile(args[0], "r");
            // 文件操作：创建 RandomAccessFile 对象以只读模式打开文件
            // 中文注释：使用命令行参数提供的文件路径，打开压缩文件以供读取

            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));
            // 文件操作：打开压缩文件
            // 中文注释：调用 SevenZip 库的 openInArchive 方法，自动检测压缩文件类型并创建输入流

            System.out.println("   Size   | Compr.Sz. | Filename");
            // 打印表头：输出表格标题
            // 中文注释：打印文件大小、压缩后大小和文件名的表头，格式化输出

            System.out.println("----------+-----------+---------");
            // 打印分隔线：输出表格分隔线
            // 中文注释：打印分隔线以增强输出可读性，区分表头和内容

            int itemCount = inArchive.getNumberOfItems();
            // 变量赋值：获取压缩文件中的项目数量
            // 中文注释：通过 getNumberOfItems 方法获取压缩文件中的文件总数

            for (int i = 0; i < itemCount; i++) {
                // 循环：遍历压缩文件中的每个项目
                // 中文注释：通过循环逐一读取并打印每个文件的信息

                System.out.println(String.format("%9s | %9s | %s", //
                        inArchive.getProperty(i, PropID.SIZE),
                        inArchive.getProperty(i, PropID.PACKED_SIZE),
                        inArchive.getProperty(i, PropID.PATH)));
                // 打印文件信息：格式化输出文件大小、压缩大小和路径
                // 中文注释：使用 String.format 格式化输出每个文件的详细信息
                // 参数说明：getProperty 方法获取文件的特定属性（如大小、压缩后大小、路径）
            }
        } catch (Exception e) {
            // 异常处理：捕获所有异常
            // 中文注释：捕获在文件操作过程中可能出现的任何异常

            System.err.println("Error occurs: " + e);
            // 打印错误信息
            // 中文注释：将异常信息输出到错误流，提示用户操作失败的原因
        } finally {
            // finally 块：确保资源被正确释放
            // 中文注释：无论是否发生异常，都执行资源清理操作

            if (inArchive != null) {
                // 条件检查：确保 inArchive 不为 null
                // 中文注释：检查压缩文件对象是否已初始化

                try {
                    // try 块：尝试关闭压缩文件
                    // 中文注释：调用 close 方法释放压缩文件资源

                    inArchive.close();
                    // 关闭压缩文件
                    // 中文注释：释放压缩文件相关的资源
                } catch (SevenZipException e) {
                    // 异常处理：捕获关闭压缩文件时的异常
                    // 中文注释：处理关闭压缩文件时可能出现的 SevenZip 异常

                    System.err.println("Error closing archive: " + e);
                    // 打印错误信息
                    // 中文注释：将关闭压缩文件时的错误信息输出到错误流
                }
            }
            if (randomAccessFile != null) {
                // 条件检查：确保 randomAccessFile 不为 null
                // 中文注释：检查文件对象是否已初始化

                try {
                    // try 块：尝试关闭文件
                    // 中文注释：调用 close 方法释放文件资源

                    randomAccessFile.close();
                    // 关闭文件
                    // 中文注释：释放文件句柄，防止资源泄漏
                } catch (IOException e) {
                    // 异常处理：捕获关闭文件时的异常
                    // 中文注释：处理关闭文件时可能出现的 IO 异常

                    System.err.println("Error closing file: " + e);
                    // 打印错误信息
                    // 中文注释：将关闭文件时的错误信息输出到错误流
                }
            }
        }
    }
}