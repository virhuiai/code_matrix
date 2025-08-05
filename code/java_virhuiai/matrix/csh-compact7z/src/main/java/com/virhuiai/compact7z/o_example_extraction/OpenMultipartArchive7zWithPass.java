package com.virhuiai.compact7z.o_example_extraction;

import com.virhuiai.codec.CharsetConverter;
import com.virhuiai.log.CommonRuntimeException;
import com.virhuiai.log.logext.LogFactory;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.VolumedArchiveInStream;
import org.apache.commons.logging.Log;

import java.io.*;
import java.util.*;

/**
 * It's important to provide corrent names in IArchiveOpenVolumeCallback.getProperty() method and to VolumedArchiveInStream class. Also you will probably need to parse filenames passed to IArchiveOpenVolumeCallback.getStream(String filename) in order to get a number of the required volume.
 * 在IArchiveOpenVolumeCallback.getProperty()方法和VolumedArchiveInStream类中提供正确的名称非常重要。此外，您可能需要解析传递给IArchiveOpenVolumeCallback.getStream(String filename)的文件名以获取所需卷的编号。
 *
 * Here is an example of how to open a multi-part archive directly from the file system. In this example all the volumes are named correctly, so it's no need to parse requred volume name to extract the index. The volumes already exists under the passed names und can be directly opened.
 * 这是一个直接从文件系统打开多分卷压缩文件的示例。在此示例中，所有卷都已正确命名，因此无需解析所需的卷名来提取索引。这些卷已经存在于传递的名称下，可以直接打开。
 */
public class OpenMultipartArchive7zWithPass {

    static final Log LOGGER = LogFactory.getLog();

    /**
     * In this example we use VolumedArchiveInStream class only.
     * 在这个例子中，我们只使用了VolumedArchiveInStream类。
     * It means, we doesn't pass instances of this class directly
     * 这意味着我们不直接传递这个类的实例
     * to 7-Zip, so not complete implementation
     * 给7-Zip，所以不需要完整的实现
     * of {@link IArchiveOpenVolumeCallback} is required.
     * {@link IArchiveOpenVolumeCallback}接口。
     * See VolumedArchiveInStream JavaDoc for more information
     * 有关更多信息，请参阅VolumedArchiveInStream的JavaDoc。
     */
    private static class ArchiveOpenVolumeCallback
            implements IArchiveOpenVolumeCallback {
        // 定义一个内部静态类ArchiveOpenVolumeCallback，实现IArchiveOpenVolumeCallback接口，用于处理多卷压缩文件的打开回调。

        /**
         * Cache for opened file streams
         * 已打开文件流的缓存
         */
        private Map<String, RandomAccessFile> openedRandomAccessFileList =
                new HashMap<String, RandomAccessFile>();
        // 定义一个Map，用于缓存已经打开的RandomAccessFile对象，键为文件名，值为RandomAccessFile实例。

        /**
         * This method doesn't needed, if using with VolumedArchiveInStream
         * 如果与VolumedArchiveInStream一起使用，则不需要此方法
         * and pass the name of the first archive in constructor.
         * 并在构造函数中传递第一个存档的名称。
         * (Use two argument constructor)
         * （使用两个参数的构造函数）
         *
         * @see IArchiveOpenVolumeCallback#getProperty(PropID)
         */
        public Object getProperty(PropID propID) throws SevenZipException {
            // 实现IArchiveOpenVolumeCallback接口的getProperty方法。
            // 当使用VolumedArchiveInStream时，这个方法通常不需要实现，可以直接返回null。
            return null;
        }

        /**
         *
         * The name of the required volume will be calculated out of the
         * 所需卷的名称将根据以下内容计算
         * name of the first volume and volume index. If you need
         * 第一个卷的名称和卷索引。如果您需要
         * need volume index (integer) you will have to parse filename
         * 卷索引（整数），您将不得不解析文件名
         * and extract index.
         * 并提取索引。
         *
         * <pre>
         * int index = filename.substring(filename.length() - 3,
         *         filename.length());
         * </pre>
         *
         */
        public IInStream getStream(String filename) throws SevenZipException {
            // 实现IArchiveOpenVolumeCallback接口的getStream方法，根据文件名获取对应的输入流。
            // filename参数是7-Zip库请求的卷文件名。
            try {
                // We use caching of opened streams, so check cache first
                // 我们使用已打开流的缓存，所以首先检查缓存
                RandomAccessFile randomAccessFile = openedRandomAccessFileList
                        .get(filename);
                // 尝试从缓存中获取与请求文件名对应的RandomAccessFile对象。
                if (randomAccessFile != null) { // Cache hit.
                    // 缓存命中。
                    // Move the file pointer back to the beginning
                    // 将文件指针移回开头
                    // in order to emulating new stream
                    // 以模拟新的流
                    randomAccessFile.seek(0);
                    // 如果缓存命中，将文件指针重置到文件开头，以便模拟一个新的流。
                    return new RandomAccessFileInStream(randomAccessFile);
                    // 返回一个新的RandomAccessFileInStream实例，它包装了缓存中的RandomAccessFile。
                }

                // Nothing useful in cache. Open required volume.
                // 缓存中没有有用的东西。打开所需卷。
                randomAccessFile = new RandomAccessFile(filename, "r");
                // 如果缓存中没有，则根据请求的文件名创建一个新的RandomAccessFile对象，以只读模式打开。

                // Put new stream in the cache
                // 将新流放入缓存
                openedRandomAccessFileList.put(filename, randomAccessFile);
                // 将新创建的RandomAccessFile对象放入缓存中，以便后续重复使用。

                return new RandomAccessFileInStream(randomAccessFile);
                // 返回一个新的RandomAccessFileInStream实例，它包装了新打开的RandomAccessFile。
            } catch (FileNotFoundException fileNotFoundException) {
                // Required volume doesn't exist. This happens if the volume:
                // 所需的卷不存在。如果出现以下情况，则会发生这种情况：
                // 1. never exists. 7-Zip doesn't know how many volumes should
                //    从不存在。7-Zip不知道应该有多少卷
                //    exist, so it have to try each volume.
                //    存在，所以它必须尝试每个卷。
                // 2. should be there, but doesn't. This is an error case.
                //    应该在那里，但没有。这是一个错误情况。

                // Since normal and error cases are possible,
                // 由于正常和错误情况都可能发生，
                // we can't throw an error message
                // 我们不能抛出错误消息
                return null; // We return always null in this case
                // 返回null表示请求的卷不存在。
            } catch (Exception e) {
                // 捕获其他所有异常。
                throw new RuntimeException(e);
                // 将其他异常包装成RuntimeException并重新抛出。
            }
        }

        /**
         * Close all opened streams
         * 关闭所有已打开的流
         */
        void close() throws IOException {
            // 关闭所有在缓存中打开的文件流。
            for (RandomAccessFile file : openedRandomAccessFileList.values()) {
                // 遍历缓存中所有的RandomAccessFile对象。
                file.close();
                // 关闭每一个RandomAccessFile。
            }
        }
    }

    public static void main(String[] args) {
        String outputDir = "/Volumes/RamDisk/test/";
        String pass = "123456";
        // 主方法，程序的入口。
//        if (args.length == 0) {
//            // 检查命令行参数的数量。
//            System.out.println(
//                    "Usage: java OpenMultipartArchive7z <first-volume>");
//            // 如果没有提供参数，则打印使用说明。
//            return;
//            // 退出程序。
//        }
        ArchiveOpenVolumeCallback archiveOpenVolumeCallback = null;
        // 声明一个ArchiveOpenVolumeCallback对象，并初始化为null。
        IInArchive inArchive = null;
        // 声明一个IInArchive对象，并初始化为null。IInArchive表示一个打开的压缩文件。
        try {

            archiveOpenVolumeCallback = new ArchiveOpenVolumeCallback();
            // 创建ArchiveOpenVolumeCallback的实例。
            inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP,
                    new VolumedArchiveInStream("/Volumes/RamDisk/pass123456.7z.001",
                            archiveOpenVolumeCallback),pass);
            // 使用SevenZip库打开一个7z格式的压缩文件。
            // 第一个参数ArchiveFormat.SEVEN_ZIP指定了压缩格式。
            // 第二个参数是一个VolumedArchiveInStream实例，它需要第一个卷的文件名(args[0])和自定义的ArchiveOpenVolumeCallback来处理多卷文件。

            System.out.println("   Size   | Compr.Sz. | Filename");
            // 打印表格头部，显示文件大小、压缩大小和文件名。
            System.out.println("----------+-----------+---------");
            // 打印分隔线。
//            int itemCount = inArchive.getNumberOfItems();

            int count = inArchive.getNumberOfItems();
            List<Integer> itemsToExtract = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (!((Boolean) inArchive.getProperty(i, PropID.IS_FOLDER)).booleanValue()) {
                    itemsToExtract.add(i);
                }
            }
            int[] items = new int[itemsToExtract.size()];
            int i = 0;
            for (Integer integer : itemsToExtract) {
                items[i++] = integer;
            }

            inArchive.extract(items, false, new MyExtractCallback(inArchive, outputDir));

/**
 *
            // 获取压缩文件中包含的条目（文件或目录）数量。
            for (int i = 0; i < itemCount; i++) {


                String rawPath = String.valueOf(inArchive.getProperty(i, PropID.PATH));
                String path;
                try {
                    path = CharsetConverter.convertToOriginal(rawPath);
                } catch (Exception e) {
                    path = CharsetConverter.bytesToHex(rawPath.getBytes());
                }
                File outputFile = new File(outputDir, path);
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }



                // 遍历压缩文件中的每一个条目。
                System.out.println(String.format("%9s | %9s | %s", //
                        inArchive.getProperty(i, PropID.SIZE),
                        // 获取当前条目的原始大小。
                        inArchive.getProperty(i, PropID.PACKED_SIZE),
                        // 获取当前条目的压缩后大小。
                        inArchive.getProperty(i, PropID.PATH)));
                // 获取当前条目在压缩文件中的路径/名称。
                // 使用String.format格式化输出，确保对齐。
            }

 */

        } catch (Exception e) {
            // 捕获可能发生的任何异常。
            System.err.println("Error occurs: " + e);
            // 打印错误信息到标准错误输出。
        } finally {
            // finally块确保资源被关闭，无论是否发生异常。
            if (inArchive != null) {
                // 如果inArchive对象不为null，说明压缩文件被成功打开。
                try {
                    inArchive.close();
                    // 关闭打开的压缩文件。
                } catch (SevenZipException e) {
                    // 捕获关闭压缩文件时可能发生的SevenZipException。
                    System.err.println("Error closing archive: " + e);
                    // 打印关闭存档时的错误信息。
                }
            }
            if (archiveOpenVolumeCallback != null) {
                // 如果archiveOpenVolumeCallback对象不为null。
                try {
                    archiveOpenVolumeCallback.close();
                    // 关闭所有通过回调打开的文件流。
                } catch (IOException e) {
                    // 捕获关闭文件时可能发生的IOException。
                    System.err.println("Error closing file: " + e);
                    // 打印关闭文件时的错误信息。
                }
            }
        }
    }


    /**
     * 自定义解压回调类，用于处理解压过程中的数据流和结果
     */
    static class MyExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {
        private final String outputDir;
        private int hash = 0;
        private int size = 0;
        private int index;
        private IInArchive inArchive;

        public MyExtractCallback(IInArchive inArchive, String outputDir) {
            this.inArchive = inArchive;
            this.outputDir = outputDir;
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
        }

        @Override
        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = index;
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }

            String rawPath = String.valueOf(inArchive.getProperty(index, PropID.PATH));
            String path;
            try {
                path = CharsetConverter.convertToOriginal(rawPath);
            } catch (Exception e) {
                path = CharsetConverter.bytesToHex(rawPath.getBytes());
            }

            File outputFile = new File(outputDir, path);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            return new ISequentialOutStream() {
                @Override
                public int write(byte[] data) throws SevenZipException {
                    hash ^= Arrays.hashCode(data);
                    size += data.length;

                    try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                        fos.write(data);
                    } catch (IOException e) {
                        LOGGER.error("写入文件失败:" + outputFile.getAbsoluteFile());
                        throw new CommonRuntimeException("compact7z.IExtractItemsStandard", "写入文件失败: " + outputFile.getAbsoluteFile());
                    }

                    return data.length;
                }
            };
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error");
            } else {
                String succPath = (String) inArchive.getProperty(index, PropID.PATH);
                succPath = CharsetConverter.convertToOriginal(succPath);

                System.out.println(String.format("%9X | %10s | %s", hash, size, succPath));
                hash = 0;
                size = 0;
            }
        }

        @Override
        public void setCompleted(long completeValue) throws SevenZipException {
        }

        @Override
        public void setTotal(long total) throws SevenZipException {
        }

        @Override
        public String cryptoGetTextPassword() throws SevenZipException {
            return "123456";//todo
//            return null;
        }
    }
}