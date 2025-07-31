package com.virhuiai.compact7z;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import com.virhuiai.md5.MD5FileNameUtils;
import com.virhuiai.md5.MD5Utils;
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
