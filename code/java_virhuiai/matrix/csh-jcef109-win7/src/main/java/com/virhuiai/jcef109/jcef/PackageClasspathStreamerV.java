package com.virhuiai.jcef109.jcef;

import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.impl.step.fetch.PackageClasspathStreamer;
import org.cef.CefApp;

import java.io.InputStream;

/**
 * 用于从类路径中提取原生库的类
 * Class used to extract natives from classpath.
 *
 *
 */
public class PackageClasspathStreamerV extends PackageClasspathStreamer {

    /**
     * 原生库文件在类路径中的位置模板
     * 格式: /jcef-natives-{platform}-{tag}.tar.gz
     * {platform} 会被替换为目标平台标识符
     * {tag} 会被替换为发布标签
     */
    private static final String LOCATION = "/jcef-natives-{platform}-{tag}.tar.gz";

    /**
     * 从类路径中获取原生库的输入流
     * @param info CEF构建信息对象,包含版本和发布标签等信息
     * @param platform 目标平台枚举,指定需要的平台版本
     * @return 原生库文件的输入流,如果在类路径中未找到则返回null
     */
    public static InputStream streamNatives(CefBuildInfo info, EnumPlatform platform) {
//        jcefUrl = "https://bitbucket.org/chromiumembedded/java-cef/commits/108adb8de93664ee6ad2ea0b2b88078385069df1"
//        releaseTag = "jcef-108adb8+cef-109.1.11+g6d4fdb2+chromium-109.0.5414.87"
//        releaseUrl = "https://github.com/jcefmaven/jcefbuild/releases/tag/1.0.38"
//        platform = "*"

//        MACOSX_AMD64

        // 替换路径模板中的占位符,获取实际文件路径
        // 使用platform.getIdentifier()获取平台标识符
        // 使用info.getReleaseTag()获取发布标签
        // 通过CefApp.class.getResourceAsStream获取类路径中对应文件的输入流

        //参数值是：        /jcef-natives-macosx-amd64-jcef-108adb8+cef-109.1.11+g6d4fdb2+chromium-109.0.5414.87.tar.gz
        return CefApp.class.getResourceAsStream(LOCATION
                .replace("{platform}", platform.getIdentifier())
                .replace("{tag}", info.getReleaseTag()));
    }
}