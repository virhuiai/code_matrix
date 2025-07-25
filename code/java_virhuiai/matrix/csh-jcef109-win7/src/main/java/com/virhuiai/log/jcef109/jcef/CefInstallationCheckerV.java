package com.virhuiai.log.jcef109.jcef;


import com.virhuiai.log.logext.LogFactory;
import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 用于检查已安装的原生包的类
 * Class used to check for already installed native bundles.
 *
 * @author Fritz Windisch
 */
public class CefInstallationCheckerV {
    // 日志记录器实例
    private static final Log LOGGER = LogFactory.getLog(CefInstallationCheckerV.class);

    /**
     * 检查CEF是否已正确安装
     *
     * @param installDir 安装目录
     * @return 如果安装正确则返回true,否则返回false
     * @throws UnsupportedPlatformException 如果当前平台不支持则抛出此异常
     */
    public static boolean checkInstallation(File installDir) throws UnsupportedPlatformException {
        // 检查安装目录参数是否为null
        Objects.requireNonNull(installDir, "installDir cannot be null");

        // 检查安装锁文件是否存在
        //   首先检查 install.lock：
        //        install.lock 是安装完成的标志文件
        //        检查这个文件成本较低(只是简单的文件存在性检查)
        //        如果这个文件不存在，就可以快速判定安装未完成，无需进行后续检查
        if (!(new File(installDir, "install.lock").exists())) return false;

        // 构建元数据文件路径
        //   然后检查 build_meta.json：
        //        build_meta.json 包含详细的构建信息
        //        只有在确认安装完成(install.lock存在)的情况下才有必要检查这个文件
        //        后续还需要读取和解析这个文件的内容，成本较高
        File buildInfo = new File(installDir, "build_meta.json");

        // 检查构建信息文件是否存在
        if (!(buildInfo.exists())) return false;

        //  这样的检查顺序遵循了"快速失败"原则：
        //        优先进行简单快速的检查
        //        如果简单检查失败就立即返回
        //        减少不必要的复杂检查操作

        // 读取已安装版本的构建信息
        CefBuildInfo installed;
        try {
            installed = CefBuildInfo.fromFile(buildInfo);
        } catch (IOException e) {
            // 解析安装信息出错时记录警告日志
            LOGGER.warn("Error while parsing existing installation. Reinstalling.", e);
            return false;
        }

        // 读取所需版本的构建信息
        CefBuildInfo required;
        try {
            required = CefBuildInfo.fromClasspath();
        } catch (IOException e) {
            // 解析所需版本信息出错时记录警告日志
            LOGGER.warn( "Error while parsing existing installation. Reinstalling.", e);
            return false;
        }

        // 比较已安装版本和所需版本的发布标签以及平台是否匹配
        // 只有当发布标签和平台都匹配时才认为安装是正确的
        return required.getReleaseTag().equals(installed.getReleaseTag())
                && installed.getPlatform().equals(EnumPlatform.getCurrentPlatform().getIdentifier());
    }
}
