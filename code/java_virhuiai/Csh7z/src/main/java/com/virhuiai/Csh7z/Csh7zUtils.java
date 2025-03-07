package com.virhuiai.Csh7z;

import com.virhuiai.CshLogUtils.CshLogUtils;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collection;

/**
 * 7z文件压缩工具类
 * 提供创建加密7z压缩文件的功能
 *
 * @author virhuiai
 * @version 1.0
 */
public class Csh7zUtils {
    // 日志记录器，用于记录压缩过程中的信息
    private static final Log LOGGER = CshLogUtils.createLogExtended(Csh7zUtils.class);

     /**
     * 私有构造函数
     * 工具类不应被实例化，使用私有构造函数防止实例化
     *
     * @throws AssertionError 当尝试实例化该类时抛出
     */
    private Csh7zUtils() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 压缩文件夹为7z格式并进行加密
     *
     * @param inputDir         需要压缩的输入目录
     * @param outputFile       压缩后的7z文件路径
     * @param password         加密密码
     * @param compressionLevel 压缩级别（）
     * @throws Exception 在以下情况可能抛出异常:
     *                   - 输入目录不存在或无法访问
     *                   - 输出文件无法创建或写入
     *                   - 压缩过程中发生IO错误
     *                   - 7z库相关错误
     */
    public static void compress(File inputDir, File outputFile, String password, int compressionLevel) throws Exception {
// 参数校验
        if (inputDir == null || !inputDir.exists()) {
            throw new IllegalArgumentException("输入目录不能为空且必须存在");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("输出文件不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("加密密码不能为空");
        }

        try (
                // 创建随机访问文件用于写入压缩数据
                RandomAccessFile raf = new RandomAccessFile(outputFile, "rw");
                // 创建7z压缩档案
                IOutCreateArchive7z outArchive = SevenZip.openOutArchive7z();) {

            // 设置压缩级别
            outArchive.setLevel(compressionLevel);

            // 启用文件头加密，提供额外安全性
            outArchive.setHeaderEncryption(true);

            // 设置多线程压缩，使用可用的CPU核心数
            int processorCount = Runtime.getRuntime().availableProcessors();
            outArchive.setThreadCount(processorCount);
            LOGGER.debug("使用CPU核心数: " + processorCount);

            // 获取需要压缩的所有文件和目录
            Collection<File> files = FileUtils.listFilesAndDirs(
                    inputDir,
                    TrueFileFilter.INSTANCE, // 文件过滤器
                    TrueFileFilter.INSTANCE  // 目录过滤器
            );

            // 计算实际项目数（排除输入目录本身）
            int itemCount = files.size() - 1;
            LOGGER.debug("待压缩文件数量: " + itemCount);

            // 创建并执行压缩
            outArchive.createArchive(
                    new RandomAccessFileOutStream(raf), // 输出流
                    itemCount,                          // 压缩项目数量
                    new CreateCallback7z(inputDir, password) // 压缩回调处理器
            );

        } catch (Exception e) {
            LOGGER.error("压缩过程发生错误", e);
            throw new RuntimeException("压缩失败: " + e.getMessage(), e);
        }
    }

}