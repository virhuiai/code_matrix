package com.virhuiai.compact7z.o_example_extraction;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * Standard interface
 * 标准接口
 * The following two examples show how to perform the same extracting task using standard interface. As in the previous example, the simple checksum will be calculated in order to consume the extracted data.
 * 以下两个示例展示了如何使用标准接口执行相同的解压任务。与之前的示例一样，将计算简单的校验和以消耗提取的数据。
 *
 * First example traverse the entire archive and uses call back method to choose on the fly whether proceed with the extracting of an item or not. The second example prepare a list of items for extracting.
 * 第一个示例遍历整个压缩包并使用回调方法动态选择是否继续提取某个项。第二个示例准备了一个要提取的项列表。
 *
 * Extracting archive using standard interface and call back method as a filter
 * 使用标准接口和回调方法作为过滤器来提取压缩包
 */
public class ExtractItemsStandardCallback {
    // MyExtractCallback 类实现了 IArchiveExtractCallback 接口，用于处理文件提取过程中的回调事件。
    public static class MyExtractCallback implements IArchiveExtractCallback {
        private int hash = 0;
        // 用于存储提取数据的哈希值。
        private int size = 0;
        // 用于存储提取数据的总大小。
        private int index;
        // 当前正在处理的压缩包项的索引。
        private boolean skipExtraction;
        // 标记是否跳过当前项的提取。
        private IInArchive inArchive;
        // 7-Zip 压缩包的输入接口，用于获取压缩包信息。

        public MyExtractCallback(IInArchive inArchive) {
            // 构造函数，初始化 MyExtractCallback 实例。
            this.inArchive = inArchive;
            // 将传入的 IInArchive 实例赋值给成员变量。
        }

        public ISequentialOutStream getStream(int index,
                                              ExtractAskMode extractAskMode) throws SevenZipException {
            // 在提取操作开始时被调用，用于获取一个输出流以写入提取的数据。
            // 参数:
            //   index: 当前要提取的项的索引。
            //   extractAskMode: 提取模式（例如，EXTRACT 表示实际提取，SKIP 表示跳过）。
            // 返回值:
            //   一个 ISequentialOutStream 实例，数据将写入其中；如果跳过提取，则返回 null。
            this.index = index;
            // 存储当前项的索引。
            skipExtraction = (Boolean) inArchive
                    .getProperty(index, PropID.IS_FOLDER);
            // 检查当前项是否是文件夹。如果是文件夹，则跳过提取。
            if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
                // 如果是文件夹或者提取模式不是 EXTRACT，则跳过。
                return null;
                // 返回 null 表示不提供输出流，从而跳过提取。
            }
            return new ISequentialOutStream() {
                // 返回一个新的匿名 ISequentialOutStream 实例，用于接收提取的数据。
                public int write(byte[] data) throws SevenZipException {
                    // 当有数据块被提取时，该方法会被调用。
                    // 参数:
                    //   data: 包含提取数据块的字节数组。
                    // 返回值:
                    //   实际处理的字节数。
                    hash ^= Arrays.hashCode(data);
                    // 计算数据的哈希值（这里使用异或操作来累加哈希）。
                    size += data.length;
                    // 累加已处理的数据大小。
                    return data.length; // Return amount of proceed data
                    // 返回已处理的数据长度，表示所有数据都已被接收。
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {
            // 在每次提取操作（例如，一个文件或目录的提取）开始之前被调用。
            // 通常用于准备资源或执行预处理操作，但在此示例中未实现具体逻辑。
            // 参数:
            //   extractAskMode: 提取模式。
        }

        public void setOperationResult(ExtractOperationResult
                                               extractOperationResult) throws SevenZipException {
            // 在每个项的提取操作完成后被调用，报告操作结果。
            // 参数:
            //   extractOperationResult: 提取操作的结果，例如成功（OK）、错误等。
            if (skipExtraction) {
                // 如果当前项被标记为跳过提取（例如，它是文件夹），则直接返回。
                return;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                // 如果提取结果不是 OK（即出现错误），则打印错误信息。
                System.err.println("Extraction error");
                // 打印提取错误信息到标准错误流。
            } else {
                System.out.println(String.format("%9X | %10s | %s", hash, size,//
                        inArchive.getProperty(index, PropID.PATH)));
                // 如果提取成功，则格式化并打印哈希值、大小和文件路径。
                // %9X: 哈希值，以大写十六进制表示，至少9个字符宽。
                // %10s: 大小，以字符串形式表示，至少10个字符宽。
                // %s: 文件路径。
                hash = 0;
                // 重置哈希值，为下一个文件的提取做准备。
                size = 0;
                // 重置大小，为下一个文件的提取做准备。
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
            // 在整个提取过程完成后被调用。
            // 参数:
            //   completeValue: 已完成的总字节数。
            // 通常用于清理资源或执行收尾工作，但在此示例中未实现具体逻辑。
        }

        public void setTotal(long total) throws SevenZipException {
            // 在提取开始时被调用，提供要提取的总字节数。
            // 参数:
            //   total: 要提取的总字节数。
            // 通常用于更新进度条等，但在此示例中未实现具体逻辑。
        }
    }

    public static void main(String[] args) {
        // 主方法，程序的入口点。
        // 参数:
        //   args: 命令行参数，期望第一个参数是压缩文件的路径。
        if (args.length == 0) {
            // 检查是否提供了压缩文件路径。
            System.out.println("Usage: java ExtractItemsStandard <arch-name>");
            // 如果没有提供，打印使用说明。
            return;
            // 退出程序。
        }
        RandomAccessFile randomAccessFile = null;
        // 用于随机访问文件的对象，这里用于打开压缩文件。
        IInArchive inArchive = null;
        // 7-Zip 压缩包的输入接口对象。
        try {
            randomAccessFile = new RandomAccessFile(args[0], "r");
            // 以只读模式打开指定的压缩文件。
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    // 尝试自动检测压缩文件类型并打开压缩包。
                    new RandomAccessFileInStream(randomAccessFile));
            // 将 RandomAccessFile 封装成 SevenZip 库所需的输入流。

            System.out.println("   Hash   |    Size    | Filename");
            // 打印表头。
            System.out.println("----------+------------+---------");
            // 打印分隔线。

            int[] in = new int[inArchive.getNumberOfItems()];
            // 创建一个整数数组，用于存储所有待提取项的索引。数组大小等于压缩包中的项数。
            for (int i = 0; i < in.length; i++) {
                // 遍历所有项。
                in[i] = i;
                // 将每个项的索引添加到数组中，表示要提取所有项。
            }
            inArchive.extract(in, false, // Non-test mode
                    // 执行文件提取操作。
                    // 第一个参数 'in' 是要提取的项的索引数组。
                    // 第二个参数 'false' 表示非测试模式，即实际进行提取而不是只进行完整性检查。
                    new MyExtractCallback(inArchive));
            // 传入自定义的 MyExtractCallback 实例作为回调处理器。
        } catch (Exception e) {
            // 捕获可能发生的任何异常。
            System.err.println("Error occurs: " + e);
            // 打印错误信息到标准错误流。
        } finally {
            // 无论是否发生异常，都会执行 finally 块中的代码，用于资源清理。
            if (inArchive != null) {
                // 检查 inArchive 是否已成功打开。
                try {
                    inArchive.close();
                    // 关闭 7-Zip 压缩包。
                } catch (SevenZipException e) {
                    // 捕获关闭压缩包时可能发生的异常。
                    System.err.println("Error closing archive: " + e);
                    // 打印关闭压缩包时的错误信息。
                }
            }
            if (randomAccessFile != null) {
                // 检查 randomAccessFile 是否已成功打开。
                try {
                    randomAccessFile.close();
                    // 关闭文件输入流。
                } catch (IOException e) {
                    // 捕获关闭文件时可能发生的 IO 异常。
                    System.err.println("Error closing file: " + e);
                    // 打印关闭文件时的错误信息。
                }
            }
        }
    }
}