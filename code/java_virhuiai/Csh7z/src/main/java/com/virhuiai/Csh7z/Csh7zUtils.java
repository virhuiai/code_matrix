package com.virhuiai.Csh7z;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.Md5.MD5FileNameUtils;
import com.virhuiai.Md5.RandomMD5Utils;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collection;

public class Csh7zUtils {
    private static final Log LOGGER = CshLogUtils.createLogExtended(Csh7zUtils.class); // 日志记录器



    /**
     * 压缩指定目录为7z文件并加密
     *
     * @param inputDir   输入目录
     * @param outputFile 输出7z文件
     * @param password   加密密码
     * @throws Exception 压缩过程中的异常
     */
    public void compress(File inputDir, File outputFile, String password,int compressionLevel) throws Exception {


        try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw");
             IOutCreateArchive7z outArchive = SevenZip.openOutArchive7z();){

            // 设置压缩级别
            outArchive.setLevel(compressionLevel);

            // 启用加密
            if (outArchive instanceof IOutFeatureSetEncryptHeader) {
                ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
            }

            // 启用多线程
            if (outArchive instanceof IOutFeatureSetMultithreading) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(
                        Runtime.getRuntime().availableProcessors()
                );
            }

            // 获取要压缩的文件列表
            Collection<File> files = FileUtils.listFilesAndDirs(
                    inputDir,
                    TrueFileFilter.INSTANCE,
                    TrueFileFilter.INSTANCE
            );
            int itemCount = files.size() - 1; // 减去输入目录本身

            // 执行压缩
            outArchive.createArchive(
                    new RandomAccessFileOutStream(raf),
                    itemCount,
                    new My7zCezateCallback(inputDir, password)
            );

        }catch (Exception e){
            LOGGER.info("压缩失败！",e);
        }

    }



    // 使用示例
    public static void main(String[] args) {


        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("调试信息：命令行参数 " + String.join(", ", args));


        // 输入目录选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("i")
                .longOpt("inDir")
                .desc("要压缩的目录")
                .hasArg()
//                .required() // 设置为必需参数
                .argName("目录路径")
                .build()));

        // 密码选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
                .longOpt("password")
                .desc("设置压缩文件密码")
                .hasArg()
                .argName("密码")
                .build()));

        // 输出文件名选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("o")
                .longOpt("output")
                .desc("输出文件名(默认为: compressed.7z)")
                .hasArg()
                .argName("文件名")
                .build()));

        // 压缩等级选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("l")
                .longOpt("level")
                .desc("压缩等级," + CompressionLevel.getAvailableLevels())
                .hasArg()
                .argName("等级")
                .type(Number.class) // 指定参数类型为数字
                .build()));
        //是否插入额外字符
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("e")
                .longOpt("extra")
                .desc("是否插入额外字符")
                .hasArg()
                .argName("是否插入额外字符")
//                .type(Number.class) // 指定参数类型为数字
                .build()));

        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("extraCount")
                .desc("插入额外字符的数量")
                .hasArg()
                .argName("插入额外字符的数量")
                .type(Number.class) // 指定参数类型为数字
                .build()));

        // 是否插入额外字符
        String extra = CshCliUtils.s3GetOptionValue("e", "0");
        String extraCount = CshCliUtils.s3GetOptionValue("extraCount", "0");


        // 获取输入目录（必需参数）
        String inDir = CshCliUtils.s3GetOptionValue("i", "/Volumes/RamDisk/classes");
        LOGGER.info("输入目录: " + inDir);

        String randomMD5 = RandomMD5Utils.getRandomMD5Simple();
        String randomMD5Extra = randomMD5;
        if("1".equals(extra)){
            int extraCountNum = Integer.parseInt(extraCount);
            if(extraCountNum > 0){
                randomMD5Extra = MD5FileNameUtils.insertRandomChars(randomMD5, extraCountNum);
                // 提取MD5
                String extracted = MD5FileNameUtils.extractMD5(randomMD5Extra);
                LOGGER.info("验证ExtraPass: " + (extracted.equals(randomMD5) ? "成功" : "失败"));
                if(!extracted.equals(randomMD5)){
                    randomMD5Extra = randomMD5;
                    LOGGER.info("验证ExtraPass失败，还原");
                }
            }
        }

        // 获取密码（可选参数）
        String password = CshCliUtils.s3GetOptionValue("p", randomMD5);
        if (password != randomMD5) {
            LOGGER.info("已设置压缩密码");
        } else {
            LOGGER.info("未设置压缩密码，生成随机密码");
        }

        LOGGER.info("randomMD5:" + randomMD5);
        LOGGER.info("randomMD5Extra:" + randomMD5Extra);


        // 获取输出文件名（可选参数，默认值为compressed.zip）  "/Volumes/RamDisk/classes2.7z"
        String output = CshCliUtils.s3GetOptionValue("o" ,FileUtils7z.generateParentPath(inDir, randomMD5Extra));
        LOGGER.info("输出文件: " + output);

        // 获取压缩等级（可选参数，默认值为6）
        String levelStr = CshCliUtils.s3GetOptionValue("l", "6");
        int level;
        try {
            level = Integer.parseInt(levelStr);
            if (level < 0 || level > 9) {
                LOGGER.error("压缩等级必须在0-9之间，将使用默认值6");
                level = 6;
            }
            LOGGER.info("压缩等级: " + level);
        } catch (NumberFormatException e) {
            LOGGER.error("无效的压缩等级，将使用默认值6");
            level = 6;
        }
        CompressionLevel compressionLevel = CompressionLevel.fromLevel(level);
        LOGGER.info("选择的压缩等级: " + compressionLevel);
        if (!CompressionLevel.isValidLevel(level)) {
            LOGGER.warn("输入的压缩等级 " + level + " 已调整为最接近的可用等级: " + compressionLevel);
        }


        try {
            File inputDir = new File(inDir); // 要压缩的目录
            File outputFile = new File(output); // 输出的7z文件


            Csh7zUtils compressor = new Csh7zUtils();
            compressor.compress(inputDir, outputFile, password, compressionLevel.getLevel());

            LOGGER.info("压缩完成！");

        } catch (Exception e) {
            LOGGER.error("压缩失败：" + e.getMessage(),e);
        }
    }
}