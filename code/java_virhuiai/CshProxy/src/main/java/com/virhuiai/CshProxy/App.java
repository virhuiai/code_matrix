package com.virhuiai.Csh7z;

// 工具类导入

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.Md5.MD5FileNameUtils;
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
    private static final Log LOGGER = CshLogUtils.createLogExtended(App.class);


    // 使用示例
    public static void main(String[] args) {
        System.out.println("ab");
    }
}
