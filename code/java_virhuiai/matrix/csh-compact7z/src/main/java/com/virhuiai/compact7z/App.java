package com.virhuiai.compact7z;

// 工具类导入

import com.virhuiai.cli.CliUtils;
import com.virhuiai.file.PathUtils;
import com.virhuiai.log.CommonRuntimeException;
import com.virhuiai.log.logext.LogFactory;
import com.virhuiai.md5.MD5FileNameUtils;
import com.virhuiai.md5.RandomMD5Utils;
import org.apache.commons.logging.Log;

import java.io.File;

/**
 * 7z压缩程序主类
 * 提供命令行界面进行文件压缩操作
 * 支持的命令行参数：
 * -h: 显示帮助信息
 * -e: 是否在文件名中插入额外字符(1:是, 0:否)
 * --extraCount: 插入额外字符的数量
 *
 * @author virhuiai
 * @version 1.0
 */
public class App {
    /**
     * 日志记录器
     * 用于记录程序运行过程中的各种信息
     */
    private static final Log LOGGER = LogFactory.getLog(App.class);

    /**
     * 解析参数并返回模式
     * @param args
     * @return
     */
    public static String parseAndGetMode(String[] args) {
        // 解析命令行参数
        // 解析传入的命令行参数数组，初始化相关配置
        CliUtils.s1InitArgs(args);
        // 配置所有选项
        // 遍历所有选项枚举，添加每个选项到配置中
        for (Opt value : Opt.values()) {
            CliUtils.s2AddOption(options -> options.addOption(value.getOption()));
        }

        // 获取模式选项的值
        String mode = CliUtils.s3GetOptionValue(Opt.MODE.getOptionName());
        // 检查模式是否为空
        if (null == mode || mode.isEmpty()) {
            // 记录日志，提示未指定模式
            LOGGER.info("未指定模式！");
            // 抛出运行时异常，提示未指定模式
            throw new CommonRuntimeException("compact7z.App", "未指定模式");
        }
        // 返回解析得到的模式值
        return mode;
    }
    // 使用示例

    /**
     * --compact7z.mode=compress --compact7z.input_dir=/Volumes/RamDisk/abc --compact7z.output_file_path=/Volumes/RamDisk --compact7z.extra_enabled=1 --compact7z.extra_count=5 --compact7z.password_value=123456 --compact7z.password_prefix=pre --compact7z.password_suffix=after --compact7z.password_show=1 --compact7z.compression_level=3
     * --compact7z.mode=quering_items_in_archive --compact7z.input_7z=/Volumes/RamDisk/out.7z --compact7z.password_value=pre[062eejW56a8f7fd8dcz4@5b5902b]123456[09b55855j]after
     * --compact7z.mode=extract_items_simple --compact7z.input_7z=/Volumes/RamDisk/fc88d885b2269af7f05eebdW045655e5Zzj@1.7z --compact7z.password_value=pre[062eejW56a8f7fd8dcz4@5b5902b]123456[09b55855j]after
     *
     * --compact7z.mode=input_str.extract_md5 --compact7z.input_str=123
     * --compact7z.mode=input_str.gen_md5 --compact7z.input_str=123
     *
     * @param args
     */
    public static void main(String[] args) {
        String mode = parseAndGetMode(args);//解析参数并返回模式
        AppMode action = AppMode.fromString(mode);
        if(null != action){
            // 执行对应的模式逻辑
            action.execute();
            return;
        }


        if ("quering_items_in_archive".equalsIgnoreCase(mode)) {
//            Csh7zUtils.queringItemsInArchive();
            Csh7zUtils.queringItemsInArchiveStand();
            return;
        }


        String inDir = CliUtils.s3GetOptionValue(Opt.INPUT_DIR.getOptionName());
        if (null == inDir || inDir.isEmpty()) {
//            LOGGER.error("指定要压缩的源目录路径要填写");
            return;
        }

        String outputPath = CliUtils.s3GetOptionValue(Opt.OUTPUT_FILE_PATH.getOptionName());

        String randomMD5 = RandomMD5Utils.getRandomMD5Simple();
        String randomOutName = randomMD5;
        String extraEnabled = CliUtils.s3GetOptionValue(Opt.EXTRA_ENABLED.getOptionName());
        String extraCount = CliUtils.s3GetOptionValue(Opt.EXTRA_COUNT.getOptionName());
        if ("1".equals(extraEnabled)) {
            try {
                int extraCountNum = Integer.parseInt(extraCount);
                if (extraCountNum > 0) {
                    randomOutName = MD5FileNameUtils.insertRandomChars(randomMD5, extraCountNum);
                    String extracted = MD5FileNameUtils.extractMD5(randomOutName);

                    if (!extracted.equals(randomMD5)) {
                        LOGGER.warn("额外字符验证失败，使用原始MD5");
                        randomOutName = randomMD5;
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.error("额外字符数量格式无效", e);
            }
        }

        // 第一步：使用FileUtils7z工具类的wrapStr方法处理密码
        // 该方法可能是对原始密码进行包装或加密处理
        // 从配置中获取以下参数：
        //   - PASSWORD_VALUE: 原始密码值（如果未配置则使用空字符串）
        //   - RANDOM_CHAR_B: 可能用于 混淆的随机字符B（如果未配置则使用空字符串）
        //   - RANDOM_CHAR_A: 可能用于 混淆的随机字符A（如果未配置则使用空字符串）
        final String RANDOM_CHAR_B = FileUtils7z.getRandomChars(randomOutName);
        final String RANDOM_CHAR_A = FileUtils7z.getRandomChars(randomOutName);
        String password = FileUtils7z.wrapStr(
                CliUtils.s3GetOptionValue(Opt.PASSWORD_VALUE.getOptionName(), ""),
                RANDOM_CHAR_B,
                RANDOM_CHAR_A
        );
        // 第二步：为处理后的密码添加前缀和后缀
        // 从配置中获取密码前缀和后缀，如果未配置则使用空字符串
        // 最终密码格式为：前缀 + 处理后的密码 + 后缀
        password = CliUtils.s3GetOptionValue(Opt.PASSWORD_PREFIX.getOptionName())
                + password
                + CliUtils.s3GetOptionValue(Opt.PASSWORD_SUFFIX.getOptionName());

        String showPassword = CliUtils.s3GetOptionValue(Opt.PASSWORD_SHOW.getOptionName());
        if ("1".equalsIgnoreCase(showPassword)) {
            LOGGER.info("PASSWORD_VALUE: " + CliUtils.s3GetOptionValue(Opt.PASSWORD_VALUE.getOptionName()));
            LOGGER.info("RANDOM_CHAR_B: " + RANDOM_CHAR_B);
            LOGGER.info("RANDOM_CHAR_A: " + RANDOM_CHAR_A);
            LOGGER.info("PASSWORD_PREFIX: " + CliUtils.s3GetOptionValue(Opt.PASSWORD_PREFIX.getOptionName()));
            LOGGER.info("PASSWORD_SUFFIX: " + CliUtils.s3GetOptionValue(Opt.PASSWORD_SUFFIX.getOptionName()));
            LOGGER.info("password: " + password);
        }

        // validateCompressionLevel  验证压缩等级值
        int level = Integer.parseInt(CliUtils.s3GetOptionValue(Opt.COMPRESSION_LEVEL.getOptionName()));
        if (level < 0 || level > 9) {
            LOGGER.warn("压缩等级0 1 3 5 7 9，默认值:0");
            level = 0;
            LOGGER.warn("1压缩等级已自动调整为0");
        }
        if (!Level7z.isValidLevel(level)) {
            level = 0;
            LOGGER.warn("2压缩等级已自动调整为0");
        }


        try {
            File inputDir = new File(inDir); // 要压缩的目录
            File outputFile = new File(PathUtils.combinePath(outputPath, randomOutName) + ".7z"); // 输出的7z文件


//            Csh7zUtils.compress(inputDir, outputFile
//                    , password
//                    , level);
            Compress7z.Utils.compress7z(inputDir, outputFile
                    , password
                    , level);

            LOGGER.info("压缩完成！");

        } catch (Exception e) {
            LOGGER.error("压缩失败：" + e.getMessage(), e);
        }
    }
}
