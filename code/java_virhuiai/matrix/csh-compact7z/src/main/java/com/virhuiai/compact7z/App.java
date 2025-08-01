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
     *
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

    }
}
