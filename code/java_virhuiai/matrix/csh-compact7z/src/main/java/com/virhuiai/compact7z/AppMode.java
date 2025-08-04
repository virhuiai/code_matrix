package com.virhuiai.compact7z;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.file.PathUtils;
import com.virhuiai.log.logext.LogFactory;
import com.virhuiai.md5.MD5FileNameUtils;
import com.virhuiai.md5.MD5Utils;
import com.virhuiai.md5.RandomMD5Utils;
import org.apache.commons.logging.Log;

import java.io.File;
import java.util.HashMap;

public enum AppMode {


    /**
     * --compact7z.mode=input_str.gen_md5, --compact7z.input_str=123
     * md5:202cb962ac59075b964b07152d234b70
     */
    STR_GEN_MD5("input_str.gen_md5") {
        @Override
        void execute() {//--compact7z.mode=input_str.extract_md5 --compact7z.input_str=123
            // 获取输入字符串参数
            String inputStr = CliUtils.s3GetOptionValue(Opt.INPUT_STR.getOptionName());
            // 提取 MD5 值
            String md5 = MD5Utils.getMD5(inputStr);
            LOGGER.info("md5:" + md5);
        }
    },
    // --compact7z.mode=input_file.gen_md5 --compact7z.input_file=/Volumes/RamDisk/4b9a4a887e3b49ea2fc25e52b52fd823vTDqA.7z
    FILE_GEN_MD5("input_file.gen_md5") {
        @Override
        void execute() {//--compact7z.mode=input_file.gen_md5 --compact7z.input_file=/Volumes/RamDisk/4b9a4a887e3b49ea2fc25e52b52fd823vTDqA.7z
            // 获取输入字符串参数
            String inputFile = CliUtils.s3GetOptionValue(Opt.INPUT_FILE.getOptionName());
            // 提取 MD5 值
            String md5 = MD5Utils.getMD5(new File(inputFile));
            LOGGER.info("md5:" + md5);
        }
    },

    EXTRACT_MD5("input_str.extract_md5") {
        @Override
        void execute() {//--compact7z.mode=input_str.extract_md5 --compact7z.input_str=123
            // 获取输入字符串参数
            String inputStr = CliUtils.s3GetOptionValue(Opt.INPUT_STR.getOptionName());
            // 提取 MD5 值
            String extracted = MD5FileNameUtils.extractMD5(inputStr,new HashMap<String,String>(){{
//                put("needCheckMd5", "yes");
                put("needCheckMd5", "no");
            }});
            // 记录提取结果
            LOGGER.info("extracted:" + extracted);
        }
    },

    EXTRACT_ITEMS_SIMPLE("input_file.extract_items_simple") {
        @Override
        void execute() {
            // --compact7z.mode=input_file.extract_items_simple --compact7z.input_file=/Volumes/RamDisk/abc.zip --compact7z.output_file_path=/Volumes/RamDisk/abcd
            // --compact7z.mode=input_file.extract_items_simple --compact7z.input_file=/Volumes/RamDisk/FSViewer77nopass.7z  --compact7z.output_file_path=/Volumes/RamDisk/abcd
            // 获取输入字符串参数
            String inputFile = CliUtils.s3GetOptionValue(Opt.INPUT_FILE.getOptionName());
            String outputDir = CliUtils.s3GetOptionValue(Opt.OUTPUT_FILE_PATH.getOptionName());
            String pass = CliUtils.s3GetOptionValue(Opt.PASSWORD_VALUE.getOptionName());
            new IExtractItemsSimple() {}
                    .extractWithPass(new HashMap<String, String>() {{
                        putIfAbsent("inputFile", inputFile);
                        putIfAbsent("outputDir", outputDir);
                        putIfAbsent("pass", pass);
            }});
        }
    },


    EXTRACT_ITEMS_STANDARD("input_file.extract_items_standard") {
        @Override
        void execute() {
            // --compact7z.mode=input_file.extract_items_simple --compact7z.input_file=/Volumes/RamDisk/abc.zip --compact7z.output_file_path=/Volumes/RamDisk/abcd
            // 获取输入字符串参数
            String inputFile = CliUtils.s3GetOptionValue(Opt.INPUT_FILE.getOptionName());
            String outputDir = CliUtils.s3GetOptionValue(Opt.OUTPUT_FILE_PATH.getOptionName());
            String pass = CliUtils.s3GetOptionValue(Opt.PASSWORD_VALUE.getOptionName());
            new IExtractItemsStandard() {}
                    .extractWithPass(new HashMap<String, String>() {{
                        putIfAbsent("inputFile", inputFile);
                        putIfAbsent("outputDir", outputDir);
                        putIfAbsent("pass", pass);
                    }});
        }
    },

    // --compact7z.mode=input_dir.compact7z --compact7z.input_dir=/Volumes/RamDisk/FSViewer77  --compact7z.output_file_path=/Volumes/RamDisk --compact7z.extra_enabled=1 --compact7z.extra_count=5 --compact7z.password_value=123456 --compact7z.password_prefix=pre --compact7z.password_suffix=after --compact7z.password_show=1 --compact7z.compression_level=3
    // 压缩为7z
    COMPACT7z("input_dir.compact7z") {
        @Override
        void execute() {
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


                Compress7z.Utils.compress7z(inputDir, outputFile
                        , password
                        , level);

                LOGGER.info("压缩完成！");

            } catch (Exception e) {
                LOGGER.error("压缩失败：" + e.getMessage(), e);
            }
        }
    },



    QUERYING_ITEMS("quering_items_in_archive") {
        @Override
        void execute() {
            // 查询压缩包中的项目（标准方式）
            Csh7zUtils.queringItemsInArchiveStand();
        }
    }
    ;

    /**
     * 日志记录器
     * 用于记录程序运行过程中的各种信息
     */
    private static final Log LOGGER = LogFactory.getLog(AppMode.class);

    private final String modeName;

    AppMode(String modeName) {
        this.modeName = modeName;
    }

    // 根据字符串查找对应的枚举值
    static AppMode fromString(String mode) {
        for (AppMode m : AppMode.values()) {
            if (m.modeName.equalsIgnoreCase(mode)) {
                return m;
            }
        }
        // 如果模式无效，抛出异常
        throw new IllegalArgumentException("无效的模式: " + mode);
    }

    // 抽象方法，定义每种模式的执行逻辑
    abstract void execute();
}
