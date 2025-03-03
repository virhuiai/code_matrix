// 包声明
package com.virhuiai.Csh7z;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
// Apache Commons IO工具类
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
// Java标准库导入
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义7z压缩回调处理类
 * 实现了IOutCreateCallback接口用于创建7z文件
 * 实现了ICryptoGetTextPassword接口用于处理加密密码
 */
public class My7zCezateCallback implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {

    // 存储要压缩的文件列表
    private final List<File> fileList;
    // 压缩的基准目录
    private final File baseDir;
    // 7z文件的加密密码
    private String password;

    /**
     * 构造函数
     * @param baseDir 要压缩的根目录
     * @param password 压缩密码
     */
    public My7zCezateCallback(File baseDir, String password) {
        this.password = password;
        this.baseDir = baseDir;
        // 递归获取目录下所有文件和子目录
        Collection<File> files = FileUtils.listFilesAndDirs(
                baseDir,
                TrueFileFilter.INSTANCE, // 文件过滤器
                TrueFileFilter.INSTANCE  // 目录过滤器
        );
        this.fileList = new ArrayList<>(files);
        this.fileList.remove(baseDir); // 从列表中移除根目录本身
    }

    // 以下是实现IOutCreateCallback接口的方法

    // 设置总大小的回调
    @Override
    public void setTotal(long total) {
    }

    // 设置完成进度的回调
    @Override
    public void setCompleted(long complete){
    }

// 设置操作结果的回调
    @Override
    public void setOperationResult(boolean operationResultOk) {
    }

    /**
     * 获取要压缩的文件信息
     * @param index 文件索引
     * @param outItemFactory 输出项工厂
     * @return 压缩项信息
     */
    @Override
    public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
            throws SevenZipException {
        // 获取当前处理的文件
        File file = fileList.get(index);
        // 获取相对于基准目录的路径
        String relativePath = getRelativePath(file, baseDir);

        // 创建输出项并设置属性
        IOutItem7z item = outItemFactory.createOutItem();
        item.setPropertyPath(relativePath);
        item.setPropertyIsDir(file.isDirectory());

        // 如果是文件则设置文件大小
        if (!file.isDirectory()) {
            item.setDataSize(file.length());
        }

        return item;
    }

    /**
     * 获取文件输入流
     * @param index 文件索引
     * @return 文件输入流
     */
    @Override
    public ISequentialInStream getStream(int index) throws SevenZipException {
        File file = fileList.get(index);
        // 目录不需要输入流
        if (file.isDirectory()) {
            return null;
        }
        try {
            // 创建文件随机访问流
            return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
        } catch (FileNotFoundException e) {
            throw new SevenZipException("Error opening file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 实现ICryptoGetTextPassword接口的方法
     * 返回加密密码
     */
    @Override
    public String cryptoGetTextPassword() {
        return password;
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
}
