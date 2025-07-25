package com.virhuiai.compact7z;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

interface ICompress7z extends IListFiles{
    // 内部类MyCreateCallback的主要功能：提供压缩档案项目的元信息及加密密码
    // 实现接口：IOutCreateCallback（提供档案项目信息），ICryptoGetTextPassword（提供加密密码）
    final class MyCreateCallback
            implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
        private final String password;
        private final File baseDir;
//        private final File outputFile;

        private final ArrayList<File> fileList;

        public MyCreateCallback(Collection<File> files, HashMap<String,String> paramMap) {
            this.password = paramMap.get("password");
            this.baseDir = new File(paramMap.get("baseDir"));
            this.fileList = new ArrayList<>(files);
        }

        // 方法功能：处理每次压缩操作的结果
        // 参数说明：operationResultOk表示操作是否成功
        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
            // 中文注释：跟踪每次压缩操作的结果，通常用于记录操作状态或错误
        }

        // 方法功能：设置压缩任务的总进度
        // 参数说明：total表示总的压缩数据量
        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录压缩任务的总数据量，用于显示总体进度
        }

        // 方法功能：设置已完成的压缩进度
        // 参数说明：complete表示已完成的压缩数据量
        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            // 中文注释：记录当前已完成的压缩数据量，用于更新进度显示
        }


        /**
         * 获取文件相对于基准目录的路径
         * @param file 目标文件
         * @param baseDir 基准目录
         * @return 相对路径
         */
        private String getRelativePath(File file, File baseDir) {
            String filePath = file.getAbsolutePath();
            String basePath = baseDir.getAbsolutePath();

            // 如果文件路径以基准路径开头,则截取后面的部分作为相对路径
            if (filePath.startsWith(basePath)) {
                String relativePath = filePath.substring(basePath.length());
                // 去掉路径开头的分隔符
                return relativePath.startsWith(File.separator) ?
                        relativePath.substring(1) : relativePath;
            }
            return file.getName();
        }

        // 方法功能：获取指定索引的档案项目信息
        // 参数说明：index表示项目索引，outItemFactory用于创建档案项目对象
        // 返回值：IOutItem7z对象，包含档案项目的元信息
        public IOutItem7z getItemInformation(int index,
                                             OutItemFactory<IOutItem7z> outItemFactory) {
            // 获取当前处理的文件
            File file = fileList.get(index);
            // 获取相对于基准目录的路径
            String relativePath = getRelativePath(file, this.baseDir);

            // 创建输出项并设置属性
            // 创建新的档案项目对象
            IOutItem7z item = outItemFactory.createOutItem();
            // 中文注释：使用工厂类创建新的IOutItem7z对象，用于存储档案项目的元数据
            item.setPropertyPath(relativePath);
            item.setPropertyIsDir(file.isDirectory());

            // 如果是文件则设置文件大小
            if (!file.isDirectory()) {
                item.setDataSize(file.length());
            }

            return item;
            // 中文注释：返回配置好的档案项目对象
        }

        // 方法功能：为指定索引的项目提供输入流
        // 参数说明：i表示项目索引
        // 返回值：ISequentialInStream对象，用于读取项目内容；若为目录则返回null
        public ISequentialInStream getStream(int i) throws SevenZipException {
            File file = fileList.get(i);
            // 目录不需要输入流
            if (file.isDirectory()) {
                return null;
                // 中文注释：如果项目是目录，返回null表示无需输入流
            }

            try {
                // 创建文件随机访问流
                return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
            } catch (FileNotFoundException e) {
                throw new SevenZipException("Error opening file: " + file.getAbsolutePath(), e);
            }
        }

        // 方法功能：提供加密密码
        // 返回值：用于加密的密码字符串
        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
            // 中文注释：返回存储在类变量中的密码，用于档案加密
        }
    }

    default void compress7z(File inputDir, File outputFile, String password, int compressionLevel){
        Log LOGGER = LogFactory.getLog();

        // 参数校验
        // 功能：检查输入参数的有效性，确保输入目录、输出文件和密码不为空且符合要求
        // 执行流程：依次检查 inputDir、outputFile 和 password 是否为空或无效，若不满足条件则抛出异常
        if (null == inputDir || !inputDir.exists()) {
            // 检查输入目录是否为空或不存在
            // 注意事项：inputDir 必须为非空且实际存在的目录
            LOGGER.error("输入目录不能为空且必须存在:" + inputDir.getAbsolutePath());
            throw new IllegalArgumentException("输入目录不能为空且必须存在");
        }
        // 检查输入目录是否具有读取权限
        if (!inputDir.canRead()) {
            // 注意事项：输入目录必须具有读取权限以访问其内容
            throw new IllegalArgumentException("输入目录不可读");
        }

        if (null == outputFile) {
            // 检查输出文件是否为空
            // 注意事项：outputFile 必须为非空
            throw new IllegalArgumentException("输出文件不能为空");
        }
        // 检查输出文件所在目录是否可写
        if(!outputFile.getParentFile().exists() || !outputFile.getParentFile().canWrite()){
            // 注意事项：输出文件的父目录必须存在且具有写入权限
            // 执行流程：检查父目录是否存在，若存在则检查是否可写
            throw new IllegalArgumentException("输出文件所在目录不存在或不可写");
        }

        if (null == password || password.trim().isEmpty()) {
            // 检查密码是否为空或仅包含空白字符
            // 注意事项：password 必须为非空且不能是空字符串
            throw new IllegalArgumentException("加密密码不能为空");
        }

        // 关键变量：标记压缩操作是否成功
        boolean success = false;

        // 使用 try-with-resources 结构，自动管理 RandomAccessFile 和 IOutCreateArchive7z 的资源关闭
        // 中文注释：定义压缩操作的状态变量和资源对象
        try(// 初始化随机访问文件
            // 关键变量：随机访问文件对象
            RandomAccessFile raf = new RandomAccessFile(outputFile, "rw");// 中文注释：以读写模式打开指定的输出档案文件
            // 关键变量：7z档案创建对象
            // Open out-archive object
            // 打开档案创建对象
            IOutCreateArchive7z outArchive = SevenZip.openOutArchive7z();
        ) {


            // 中文注释：初始化7z档案创建对象

            // 设置压缩级别
            outArchive.setLevel(compressionLevel);

            // Header encryption is only available for 7z
            // 仅7z支持头部加密
            // 启用文件头加密，提供额外安全性
            outArchive.setHeaderEncryption(true);
            // 中文注释：启用档案头部加密，保护元数据

            // 获取需要压缩的所有文件和目录 // 已经排队了输入目录本身
            Collection<File> files = listFiles(inputDir.getAbsolutePath(), outputFile.getAbsolutePath());

                    // Create archive
            // 创建档案
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    files.size(), new MyCreateCallback(files,new HashMap<String,String>() {{
                        putIfAbsent("password", password);
                        putIfAbsent("baseDir", inputDir.getAbsolutePath());
                    }}));
            // 中文注释：调用创建档案方法，传入输出流、项目数量和回调对象

            // 标记压缩成功
            success = true;
            // 中文注释：如果没有异常，设置成功标志
        } catch (SevenZipException e) {
            // 处理SevenZip异常
            System.err.println("7z-Error occurs:");
            // 中文注释：捕获SevenZip相关异常并打印错误信息
            // Get more information using extended method
            e.printStackTraceExtended();
            // 中文注释：打印详细的SevenZip异常堆栈信息
        } catch (Exception e) {
            // 处理其他异常
            System.err.println("Error occurs: " + e);
            // 中文注释：捕获其他异常并打印错误信息
        } finally {
            // 中文注释：清理资源（try-with-resources 已自动处理）
            // 注意事项：由于使用 try-with-resources，raf 和 outArchive 会自动关闭，无需手动清理
            // 执行流程：finally 块确保在异常或正常完成时执行清理逻辑
            // 特殊处理：仅保留 finally 块以确保代码结构完整，无需额外关闭操作
        }
        // 输出压缩结果
        if (success) {
            System.out.println("Compression operation succeeded");
            // 中文注释：如果成功，打印压缩成功的消息
        }
    }
}
