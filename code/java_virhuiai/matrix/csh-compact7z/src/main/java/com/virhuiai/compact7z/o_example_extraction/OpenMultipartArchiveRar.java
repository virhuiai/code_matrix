package com.virhuiai.compact7z.o_example_extraction;

// 导入必要的Java和SevenZipJBinding库，用于处理文件操作和RAR分卷解压
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * Open multi-part RAR archives
 * Since RAR uses its own proprietary format for splitting archives into multiple volumes, there is no need to use VolumedArchiveInStream in order to open multi-part RAR archives. To provide access to the different volumes of a RAR archive, the archive open callback class should additionally implement the IArchiveOpenVolumeCallback interface. Here is an example:
 */
// 类功能：用于打开和处理多卷RAR压缩文件的示例代码
// 实现目的：通过实现IArchiveOpenVolumeCallback接口，支持访问RAR分卷压缩文件，展示如何打开和列出压缩文件内容
// 注意事项：RAR分卷使用专有格式，无需使用VolumedArchiveInStream，需通过回调接口管理分卷访问

public class OpenMultipartArchiveRar {
    private static class ArchiveOpenVolumeCallback
            implements IArchiveOpenVolumeCallback, IArchiveOpenCallback {
        // 内部类功能：实现IArchiveOpenVolumeCallback和IArchiveOpenCallback接口，用于处理RAR分卷文件的打开和读取
        // 实现目的：提供对多卷RAR文件的访问支持，管理文件流和分卷信息

        /**
         * Cache for opened file streams
         */
        // 缓存已打开的文件流
        private Map<String, RandomAccessFile> openedRandomAccessFileList =
                new HashMap<String, RandomAccessFile>();
        // 变量用途：使用HashMap缓存已打开的RandomAccessFile对象，避免重复打开文件，提高性能
        // 数据结构：键为文件名，值为对应的RandomAccessFile对象

        /**
         * Name of the last volume returned by {@link #getStream(String)}
         */
        // 保存最近一次通过getStream方法返回的分卷文件名
        private String name ;
        // 变量用途：记录当前操作的分卷文件名，用于getProperty方法返回文件名信息

        /**
         * This method should at least provide the name of the last
         * opened volume (propID=PropID.NAME).
         *
         * @see IArchiveOpenVolumeCallback#getProperty(PropID)
         */
        // 方法功能：获取分卷文件的属性信息，至少返回最后打开的分卷文件名
        // 参数说明：propID - 属性标识符，用于指定需要获取的属性（如文件名）
        // 返回值：Object - 根据propID返回对应属性值（当前仅支持NAME属性）
        // 注意事项：仅处理PropID.NAME的情况，其他情况返回null
        public Object getProperty(PropID propID) throws SevenZipException {
            switch (propID) {
                case NAME:
                    return name ;
                    // 返回最近打开的分卷文件名
            }
            return null;
            // 未匹配的propID返回null
        }

        /**
         * The name of the required volume will be calculated out of the
         * name of the first volume and a volume index. In case of RAR file,
         * the substring ".partNN." in the name of the volume file will
         * indicate a volume with id NN. For example:
         * <ul>
         * <li>test.rar - single part archive or multi-part archive with
         * a single volume</li>
         * <li>test.part23.rar - 23-th part of a multi-part archive</li>
         * <li>test.part001.rar - first part of a multi-part archive.
         * "00" indicates, that at least 100 volumes must exist.</li>
         * </ul>
         */
        // 方法功能：根据给定的文件名获取对应的文件输入流，支持RAR分卷文件的读取
        // 参数说明：filename - 需要打开的分卷文件名（如test.part23.rar）
        // 返回值：IInStream - 文件输入流对象，若文件不存在返回null
        // 执行流程：1. 检查缓存中是否已有文件流 2. 若无则打开新文件流并缓存 3. 返回流对象
        // 特殊处理：处理文件不存在的情况，返回null；其他异常抛出RuntimeException
        public IInStream getStream(String filename) throws SevenZipException {
            try {
                // We use caching of opened streams, so check cache first
                // 检查缓存中是否已有该文件的RandomAccessFile对象
                RandomAccessFile randomAccessFile = openedRandomAccessFileList
                        .get(filename);
                if (randomAccessFile != null) { // Cache hit.
                    // 缓存命中，复用已有文件流
                    // Move the file pointer back to the beginning
                    // in order to emulating new stream
                    // 将文件指针重置到开头，模拟新建流
                    randomAccessFile.seek(0);

                    // Save current volume name in case getProperty() will be called
                    // 保存当前分卷文件名，以便getProperty方法使用
                    name = filename;

                    return new RandomAccessFileInStream(randomAccessFile);
                    // 返回基于缓存文件流的输入流对象
                }

                // Nothing useful in cache. Open required volume.
                // 缓存中无可用文件流，打开新的分卷文件
                randomAccessFile = new RandomAccessFile(filename, "r");
                // 使用只读模式打开文件

                // Put new stream in the cache
                // 将新打开的文件流加入缓存
                openedRandomAccessFileList.put(filename, randomAccessFile);

                // Save current volume name in case getProperty() will be called
                // 保存当前分卷文件名
                name = filename;
                return new RandomAccessFileInStream(randomAccessFile);
                // 返回新创建的文件输入流
            } catch (FileNotFoundException fileNotFoundException) {
                // Required volume doesn't exist. This happens if the volume:
                // 1. never exists. 7-Zip doesn't know how many volumes should
                //    exist, so it have to try each volume.
                // 2. should be there, but doesn't. This is an error case.
                // 文件不存在，可能情况：
                // 1. 分卷文件从未存在，7-Zip会尝试每个分卷
                // 2. 分卷文件应该存在但实际缺失，为错误情况

                // Since normal and error cases are possible,
                // we can't throw an error message
                // 由于存在正常和错误两种情况，不抛出异常，直接返回null
                return null; // We return always null in this case
            } catch (Exception e) {
                throw new RuntimeException(e);
                // 其他异常情况，封装为RuntimeException抛出
            }
        }

        /**
         * Close all opened streams
         */
        // 方法功能：关闭所有已打开的文件流，释放资源
        // 执行流程：遍历缓存中的所有RandomAccessFile对象，逐一关闭
        // 注意事项：可能抛出IOException，需由调用方处理
        void close() throws IOException {
            for (RandomAccessFile file : openedRandomAccessFileList.values()) {
                file.close();
                // 关闭单个文件流
            }
        }

        // 方法功能：设置解压过程中的文件和字节处理进度（空实现）
        // 参数说明：files - 已处理的文件数，bytes - 已处理的字节数
        // 注意事项：当前为空实现，未使用进度信息
        public void setCompleted(Long files, Long bytes) throws SevenZipException {
        }

        // 方法功能：设置解压过程中总的文件数和字节数（空实现）
        // 参数说明：files - 总文件数，bytes - 总字节数
        // 注意事项：当前为空实现，未使用总量信息
        public void setTotal(Long files, Long bytes) throws SevenZipException {
        }
    }

    // 方法功能：程序主入口，用于打开并列出RAR分卷压缩文件中的内容
    // 参数说明：args - 命令行参数，需提供第一个分卷文件名（如test.part001.rar）
    // 执行流程：
    // 1. 检查命令行参数是否提供
    // 2. 创建ArchiveOpenVolumeCallback实例并打开第一个分卷
    // 3. 使用SevenZip库打开RAR压缩文件
    // 4. 遍历并打印压缩文件中每个项目的属性（大小、压缩大小、路径）
    // 5. 确保资源正确关闭
    // 注意事项：需妥善处理异常和资源释放
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Usage: java OpenMultipartArchiveRar <first-volume>");
            // 检查命令行参数，若无参数则打印使用说明并退出
            return;
        }
        ArchiveOpenVolumeCallback archiveOpenVolumeCallback = null;
        // 定义回调对象，用于管理分卷文件访问
        IInArchive inArchive = null;
        // 定义压缩文件操作接口对象
        try {
            // 创建回调实例
            archiveOpenVolumeCallback = new ArchiveOpenVolumeCallback();
            // 获取第一个分卷的文件输入流
            IInStream inStream = archiveOpenVolumeCallback.getStream(args[0]);
            // 使用SevenZip库打开RAR压缩文件
            inArchive = SevenZip.openInArchive(ArchiveFormat.RAR, inStream,
                    archiveOpenVolumeCallback);

            // 打印表头，显示文件大小、压缩大小和文件路径
            System.out.println("   Size   | Compr.Sz. | Filename");
            System.out.println("----------+-----------+---------");
            // 获取压缩文件中项目的总数
            int itemCount = inArchive.getNumberOfItems();
            // 遍历每个项目，打印其属性
            for (int i = 0; i < itemCount; i++) {
                System.out.println(String.format("%9s | %9s | %s",
                        inArchive.getProperty(i, PropID.SIZE),
                        inArchive.getProperty(i, PropID.PACKED_SIZE),
                        inArchive.getProperty(i, PropID.PATH)));
                // 格式化输出每个项目的原始大小、压缩后大小和路径
            }
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            // 捕获并打印任何异常信息
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                    // 关闭压缩文件对象，释放资源
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                    // 捕获并打印关闭压缩文件时的异常
                }
            }
            if (archiveOpenVolumeCallback != null) {
                try {
                    archiveOpenVolumeCallback.close();
                    // 关闭所有打开的文件流
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    // 捕获并打印关闭文件流时的异常
                }
            }
        }
    }
}