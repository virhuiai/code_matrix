package com.virhuiai.compact7z.o_example_extraction;

import com.virhuiai.log.codec.CharsetConverter;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 使用标准接口和预先准备的解压项列表来解压压缩文件
/**
 * Extracting archive using standard interface and prepared list of items to extract
 * zip可以
 */
// 提供从压缩文件中解压指定项的功能，支持标准接口
public class ExtractItemsStandard {
    // 自定义解压回调类，用于处理解压过程中的数据流和结果
    public static class MyExtractCallback implements IArchiveExtractCallback {
        private final String outputDir;
        private int hash = 0;
        // 用于存储当前解压文件的哈希值
        private int size = 0;
        // 用于存储当前解压文件的大小
        private int index;
        // 当前处理的压缩文件项的索引
        private IInArchive inArchive;
        // 压缩文件对象，用于获取文件属性

        // 构造函数，初始化压缩文件对象
        public MyExtractCallback(IInArchive inArchive, String outputDir) {
            this.inArchive = inArchive;
            // 将传入的压缩文件对象赋值给类成员变量

            this.outputDir = outputDir;
            // 确保输出目录存在
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs(); // 创建目录（包括父目录）
            }
        }

        // 获取解压数据流的方法
        public ISequentialOutStream getStream(int index,
                                              ExtractAskMode extractAskMode) throws SevenZipException {
            // 方法功能：根据索引和解压模式返回对应的数据流
            // 参数 index：当前处理的压缩文件项的索引
            // 参数 extractAskMode：解压模式（如提取或测试）
            // 返回值：ISequentialOutStream 数据流对象，用于写入解压数据
            this.index = index;
            // 保存当前处理的索引
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                // 如果不是提取模式，返回空
                return null;
            }

//            String.valueOf(inArchive.getProperty(index, PropID.PATH));
            // 获取路径并处理编码
            String rawPath = String.valueOf(inArchive.getProperty(index, PropID.PATH));
            String path;
            try {
                path = CharsetConverter.convertToOriginal(rawPath);
            } catch (Exception e) {
//                throw new SevenZipException("路径解码失败: " + rawPath, e);
                path = CharsetConverter.bytesToHex(rawPath.getBytes());
            }

            // 构造输出文件路径
            File outputFile = new File(outputDir, path);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // 创建文件的父目录
            }

            return new ISequentialOutStream() {
                // 创建匿名内部类实现数据流写入

                // 写入解压数据的核心方法
                public int write(byte[] data) throws SevenZipException {
                    // 方法功能：处理解压数据，计算哈希值并累加大小
                    // 参数 data：当前解压的数据块
                    // 返回值：写入的数据长度
                    hash ^= Arrays.hashCode(data);
                    // 更新哈希值，基于数据块的哈希值进行异或运算
                    size += data.length;

                    // 将数据写入到指定文件
                            try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                                fos.write(data);
                            } catch (IOException e) {
                                System.out.printf("写入文件失败:" + e);
//                                LOGGER.error("写入文件失败:" + outputFile.getAbsoluteFile());
//                                throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "写入文件失败: " + outputFile.getAbsoluteFile());
                            }


                    // 累加当前数据块的长度
                    return data.length; // Return amount of proceed data
                    // 返回处理的数据长度
                }
            };
        }

        // 准备解压操作的方法
        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {
            // 方法功能：在解压操作开始前进行准备
            // 参数 extractAskMode：解压模式
            // 注意事项：当前方法为空实现，仅为接口要求
        }

        // 设置解压操作结果的方法
        public void setOperationResult(ExtractOperationResult
                                               extractOperationResult) throws SevenZipException {
            // 方法功能：处理解压操作的结果，输出文件信息或错误信息
            // 参数 extractOperationResult：解压操作的结果状态
            if (extractOperationResult != ExtractOperationResult.OK) {
                // 如果解压失败
                System.err.println("Extraction error");
                // 输出解压错误信息
            } else {
                // 如果解压成功
                String succPath = (String) inArchive.getProperty(index, PropID.PATH);
                succPath = CharsetConverter.convertToOriginal(succPath);
//                System.out.println("解压成功:" + succPath);
//                System.out.println("解压成功:" + CharsetConverter.convertToOriginal(succPath));

                System.out.println(String.format("%9X | %10s | %s", hash, size,
                                succPath)
//                        CharsetConverter.convertToOriginal((String) inArchive.getProperty(index, PropID.PATH)))
                        );
                // 打印文件的哈希值、大小和路径
                hash = 0;
                // 重置哈希值
                size = 0;
                // 重置大小
            }
        }

        // 设置解压完成的进度
        public void setCompleted(long completeValue) throws SevenZipException {
            // 方法功能：设置当前解压的完成字节数
            // 参数 completeValue：已完成的字节数
            // 注意事项：当前方法为空实现，仅为接口要求
        }

        // 设置解压总字节数
        public void setTotal(long total) throws SevenZipException {
            // 方法功能：设置解压的总字节数
            // 参数 total：总字节数
            // 注意事项：当前方法为空实现，仅为接口要求
        }

    }

    // 程序主入口
    public static void main(String[] args) {
        // 方法功能：程序入口，处理命令行参数并执行解压操作
        // 参数 args：命令行参数，包含压缩文件路径
//        if (args.length == 0) {
//            // 如果没有提供命令行参数
//            System.out.println("Usage: java ExtractItemsStandard <arch-name>");
//            // 输出使用说明
//            return;
//            // 退出程序
//        }
        RandomAccessFile randomAccessFile = null;
        // 随机访问文件对象，用于读取压缩文件
        IInArchive inArchive = null;
        // 压缩文件对象，用于操作压缩内容
        try {
            // 尝试打开并处理压缩文件
            randomAccessFile = new RandomAccessFile("/Volumes/RamDisk/abc.zip", "r");
            // 打开指定的压缩文件为只读模式
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));
            // 自动检测压缩文件类型并打开

            System.out.println("   Hash   |    Size    | Filename");
            // 打印表头：哈希值、大小、文件名
            System.out.println("----------+------------+---------");
            // 打印分隔线

            int count = inArchive.getNumberOfItems();
            // 获取压缩文件中的项总数
            List<Integer> itemsToExtract = new ArrayList<Integer>();
            // 创建列表存储需要解压的项索引
            for (int i = 0; i < count; i++) {
                // 遍历压缩文件中的所有项
                if (!((Boolean) inArchive.getProperty(i, PropID.IS_FOLDER))
                        .booleanValue()) {
                    // 如果当前项不是文件夹
                    itemsToExtract.add(Integer.valueOf(i));
                    // 将该项的索引添加到解压列表
                }
            }
            int[] items = new int[itemsToExtract.size()];
            // 创建数组存储解压项索引
            int i = 0;
            // 数组索引计数器
            for (Integer integer : itemsToExtract) {
                // 遍历解压项列表
                items[i++] = integer.intValue();
                // 将列表中的索引转换为数组
            }
            inArchive.extract(items, false, // Non-test mode
                    new MyExtractCallback(inArchive, "/Volumes/RamDisk/abcOut"));
            // 执行解压操作，使用自定义回调处理解压过程
        } catch (Exception e) {
            // 捕获所有异常
            System.err.println("Error occurs: " + e);
            // 输出错误信息
        } finally {
            // 确保资源正确关闭
            if (inArchive != null) {
                // 如果压缩文件对象不为空
                try {
                    inArchive.close();
                    // 关闭压缩文件
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                    // 输出关闭压缩文件时的错误
                }
            }
            if (randomAccessFile != null) {
                // 如果文件对象不为空
                try {
                    randomAccessFile.close();
                    // 关闭文件
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 输出关闭文件时的错误
                }
            }
        }
    }
}