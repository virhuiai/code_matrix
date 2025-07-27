package com.virhuiai.compact7z;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import com.virhuiai.log.md5.MD5FileNameUtils;
import org.apache.commons.logging.Log;

public enum AppMode {

    EXTRACT_MD5("input_str.extract_md5") {
        @Override
        void execute() {//--compact7z.mode=input_str.extract_md5 --compact7z.input_str=123
            // 获取输入字符串参数
            String inputStr = CliUtils.s3GetOptionValue(Opt.INPUT_STR.getOptionName());
            // 提取 MD5 值
            String extracted = MD5FileNameUtils.extractMD5(inputStr);
            // 记录提取结果
            LOGGER.info("extracted:" + extracted);
        }
    },

    QUERYING_ITEMS("quering_items_in_archive") {
        @Override
        void execute() {
            // 查询压缩包中的项目（标准方式）
            Csh7zUtils.queringItemsInArchiveStand();
        }
    },
    EXTRACT_ITEMS("extract_items_simple") {
        @Override
        void execute() {
            // 提取压缩包中的项目（标准方式）
            Csh7zUtils.extractItemsStand();
        }
    };

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
