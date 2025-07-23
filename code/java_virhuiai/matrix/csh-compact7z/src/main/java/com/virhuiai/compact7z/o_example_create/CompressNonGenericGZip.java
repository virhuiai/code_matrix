package com.virhuiai.compact7z.o_example_create;

// 导入处理文件操作的类
import java.io.IOException;
// 导入用于随机访问文件的类
import java.io.RandomAccessFile;

// 导入7z压缩库的相关接口和类
import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

// 类功能描述：使用7z压缩库的GZip格式特定API创建GZip压缩文件
// GZip是一种流式压缩格式，仅能压缩单个文件，简化了编程逻辑
// 支持为压缩项设置可选的路径和最后修改时间属性
/**
 * Creating GZip archive using archive format specific API
 * GZip format is a stream archive format meaning, that it can only compress a single file. This simplifies the programming quite a bit. In the following snippet a single message passed through the second command line parameter get compressed. Note, that like non-stream archive formats GZip also supports optional Path and LastModificationTime properties for the single archive item.
 */
// 中文注释：
// 该类实现通过命令行参数指定的文件名，创建GZip压缩文件
// 主要功能：
// 1. 接收命令行参数指定的输出文件名
// 2. 使用7z压缩库创建GZip格式的压缩文件
// 3. 设置压缩级别并处理单个文件的压缩
// 4. 通过回调接口提供压缩项信息和数据流
// 注意事项：
// - GZip格式限制只能压缩单个文件
// - 压缩过程中需妥善处理文件和压缩对象的关闭

public class CompressNonGenericGZip {
    // 类功能描述：定义GZip压缩的回调接口实现，用于提供压缩项信息和数据流
    /**
     * The callback provides information about archive items.
     */
    // 中文注释：
    // 内部类 MyCreateCallback 实现 IOutCreateCallback 接口
    // 主要功能：
    // 1. 提供压缩项的信息（如数据大小）
    // 2. 提供压缩数据的数据流
    // 3. 跟踪压缩操作的进度和结果
    private final class MyCreateCallback
            implements IOutCreateCallback<IOutItemGZip> {

        // 方法功能描述：处理每次压缩操作的结果
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
        }
        // 中文注释：
        // 方法：setOperationResult
        // 参数：
        // - operationResultOk: 布尔值，表示操作是否成功
        // 功能：用于跟踪每次压缩操作的结果
        // 注意事项：当前方法为空实现，可根据需要记录操作结果
        // 异常处理：可能抛出SevenZipException，表示压缩操作中的错误

        // 方法功能描述：设置压缩操作的总进度
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
        }
        // 中文注释：
        // 方法：setTotal
        // 参数：
        // - total: 长整型，表示压缩操作的总数据量
        // 功能：跟踪压缩操作的总进度
        // 注意事项：当前方法为空实现，可用于更新进度条等
        // 异常处理：可能抛出SevenZipException，表示压缩操作中的错误

        // 方法功能描述：设置已完成的压缩进度
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
        }
        // 中文注释：
        // 方法：setCompleted
        // 参数：
        // - complete: 长整型，表示已完成的压缩数据量
        // 功能：跟踪压缩操作的完成进度
        // 注意事项：当前方法为空实现，可用于更新进度条等
        // 异常处理：可能抛出SevenZipException，表示压缩操作中的错误

        // 方法功能描述：提供压缩项的信息
        public IOutItemGZip getItemInformation(int index,
                                               OutItemFactory<IOutItemGZip> outItemFactory) {
            IOutItemGZip item = outItemFactory.createOutItem();

            item.setDataSize((long) content.length);

            return item;
        }
        // 中文注释：
        // 方法：getItemInformation
        // 参数：
        // - index: 整型，压缩项的索引（GZip只支持单个文件，索引通常为0）
        // - outItemFactory: OutItemFactory对象，用于创建压缩项
        // 返回值：IOutItemGZip，压缩项的配置信息
        // 功能：
        // 1. 通过工厂方法创建压缩项对象
        // 2. 设置压缩项的数据大小（content字节数组的长度）
        // 注意事项：
        // - GZip只处理单个文件，index参数通常为0
        // - content为类成员变量，存储待压缩的数据

        // 方法功能描述：提供压缩数据的输入流
        public ISequentialInStream getStream(int i) throws SevenZipException {
            return new ByteArrayStream(content, true);
        }
        // 中文注释：
        // 方法：getStream
        // 参数：
        // - i: 整型，压缩项的索引（GZip只支持单个文件，索引通常为0）
        // 返回值：ISequentialInStream，压缩数据的输入流
        // 功能：将content字节数组包装为ByteArrayStream，提供压缩数据流
        // 注意事项：
        // - content为类成员变量，存储待压缩的数据
        // - ByteArrayStream的第二个参数为true，表示流可读
        // 异常处理：可能抛出SevenZipException，表示流创建中的错误
    }

    // 关键变量：存储待压缩的数据
    byte[] content;
    // 中文注释：
    // 变量：content
    // 类型：byte数组
    // 用途：存储需要压缩的文件内容
    // 注意事项：由CompressArchiveStructure.create()方法初始化

    // 方法功能描述：程序入口，处理命令行参数并启动压缩
    public static void main(String[] args) {
        if (args.length == 1) {
            new CompressNonGenericGZip().compress(args[0]);
            return;
        }
        System.out.println("Usage: java CompressNonGenericGZip <archive>");
    }
    // 中文注释：
    // 方法：main
    // 参数：
    // - args: 字符串数组，命令行参数
    // 功能：
    // 1. 检查命令行参数是否提供输出文件名
    // 2. 如果提供一个参数，调用compress方法进行压缩
    // 3. 如果参数数量不正确，打印使用说明
    // 执行流程：
    // - 验证args长度是否为1
    // - 创建CompressNonGenericGZip实例并调用compress方法
    // - 输出使用说明（如果参数错误）
    // 注意事项：需要提供一个输出文件名作为参数

    // 方法功能描述：执行GZip压缩操作
    private void compress(String filename) {
        // 关键变量：标记压缩操作是否成功
        boolean success = false;
        // 中文注释：
        // 变量：success
        // 类型：布尔值
        // 用途：记录压缩操作是否成功
        // 初始化：默认false，成功时置为true

        // 关键变量：随机访问文件对象
        RandomAccessFile raf = null;
        // 中文注释：
        // 变量：raf
        // 类型：RandomAccessFile
        // 用途：用于读写输出压缩文件
        // 初始化：初始为null，在try块中创建

        // 关键变量：GZip压缩存档对象
        IOutCreateArchiveGZip outArchive = null;
        // 中文注释：
        // 变量：outArchive
        // 类型：IOutCreateArchiveGZip
        // 用途：7z库提供的GZip压缩存档操作接口
        // 初始化：初始为null，在try块中创建

        // 初始化待压缩数据
        content = CompressArchiveStructure.create()[0].getContent();
        // 中文注释：
        // 代码功能：初始化content变量
        // 执行流程：
        // - 调用CompressArchiveStructure.create()获取压缩结构数组
        // - 取第一个元素并获取其内容，赋值给content
        // 注意事项：假定create()返回至少一个元素

        try {
            // 创建随机访问文件对象
            raf = new RandomAccessFile(filename, "rw");
            // 中文注释：
            // 代码功能：创建RandomAccessFile对象
            // 参数：
            // - filename: 输出压缩文件的路径
            // - "rw": 读写模式
            // 注意事项：文件路径必须有效，否则抛出IOException

            // 打开GZip压缩存档对象
            // Open out-archive object
            outArchive = SevenZip.openOutArchiveGZip();
            // 中文注释：
            // 代码功能：初始化GZip压缩存档对象
            // 返回值：IOutCreateArchiveGZip，压缩存档操作接口
            // 注意事项：依赖7z库的正确配置

            // 配置压缩级别
            // Configure archive
            outArchive.setLevel(5);
            // 中文注释：
            // 代码功能：设置压缩级别
            // 参数：
            // - 5: 压缩级别（0-9，5为中等压缩）
            // 配置参数说明：
            // - 压缩级别影响压缩比和速度，0为无压缩，9为最大压缩
            // 注意事项：级别过高可能增加压缩时间

            // 创建压缩存档
            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    1, new MyCreateCallback());
            // 中文注释：
            // 代码功能：执行压缩操作
            // 参数：
            // - RandomAccessFileOutStream(raf): 输出流，基于随机访问文件
            // - 1: 压缩项数量（GZip只支持单个文件）
            // - MyCreateCallback: 回调对象，提供压缩项信息和数据流
            // 执行流程：
            // 1. 将raf包装为输出流
            // 2. 指定压缩项数量为1
            // 3. 使用MyCreateCallback提供压缩数据和信息
            // 注意事项：可能抛出SevenZipException

            // 标记压缩成功
            success = true;
            // 中文注释：
            // 代码功能：设置success标志为true，表示压缩成功
        } catch (SevenZipException e) {
            System.err.println("GZip-Error occurs:");
            // Get more information using extended method
            e.printStackTraceExtended();
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            // 关闭压缩存档对象
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                    success = false;
                }
            }
            // 中文注释：
            // 代码功能：关闭GZip压缩存档对象
            // 执行流程：
            // - 检查outArchive是否为null
            // - 调用close()方法释放资源
            // 注意事项：
            // - 可能抛出IOException
            // - 关闭失败会将success置为false

            // 关闭文件对象
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    success = false;
                }
            }
            // 中文注释：
            // 代码功能：关闭随机访问文件对象
            // 执行流程：
            // - 检查raf是否为null
            // - 调用close()方法释放文件资源
            // 注意事项：
            // - 可能抛出IOException
            // - 关闭失败会将success置为false
        }
        // 输出压缩结果
        if (success) {
            System.out.println("Compression operation succeeded");
        }
        // 中文注释：
        // 代码功能：根据success标志输出压缩结果
        // 执行流程：
        // - 如果success为true，打印成功信息
        // 注意事项：success标志由压缩和关闭操作的结果决定
    }
}