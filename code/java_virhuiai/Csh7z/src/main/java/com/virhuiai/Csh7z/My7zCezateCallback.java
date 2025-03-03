package com.virhuiai.Csh7z;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class My7zCezateCallback implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
    private final List<File> fileList;
    private final File baseDir;

    private String password;

    public My7zCezateCallback(File baseDir, String password) {
        this.password = password;
        this.baseDir = baseDir;
        Collection<File> files = FileUtils.listFilesAndDirs(
                baseDir,
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE
        );
        this.fileList = new ArrayList<>(files);
        this.fileList.remove(baseDir); // 移除根目录
    }

    @Override
    public void setTotal(long total) throws SevenZipException {
    }

    @Override
    public void setCompleted(long complete) throws SevenZipException {
    }

    @Override
    public void setOperationResult(boolean operationResultOk) throws SevenZipException {
    }

    @Override
    public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
            throws SevenZipException {

        File file = fileList.get(index);
        String relativePath = getRelativePath(file, baseDir);

        IOutItem7z item = outItemFactory.createOutItem();
        item.setPropertyPath(relativePath);
        item.setPropertyIsDir(file.isDirectory());

        if (!file.isDirectory()) {
            item.setDataSize(file.length());
        }

        return item;
    }

    @Override
    public ISequentialInStream getStream(int index) throws SevenZipException {
        File file = fileList.get(index);
        if (file.isDirectory()) {
            return null;
        }
        try {
            return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
        } catch (FileNotFoundException e) {
            throw new SevenZipException("Error opening file: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public String cryptoGetTextPassword() throws SevenZipException {
        return password;
    }



    private String getRelativePath(File file, File baseDir) {
        String filePath = file.getAbsolutePath();
        String basePath = baseDir.getAbsolutePath();

        if (filePath.startsWith(basePath)) {
            String relativePath = filePath.substring(basePath.length());
            return relativePath.startsWith(File.separator) ?
                    relativePath.substring(1) : relativePath;
        }
        return file.getName();
    }
}
