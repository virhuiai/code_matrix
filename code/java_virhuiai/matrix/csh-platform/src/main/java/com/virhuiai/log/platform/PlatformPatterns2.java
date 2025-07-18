package com.virhuiai.log.platform;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 平台模式类
 * 用于定义不同操作系统和架构的识别模式 (参考jcef-maven项目的实现
 */
public final class PlatformPatterns2 {

    // 操作系统识别模式

    /**
     * macOS 操作系统的识别关键字
     * 包括 "mac" 和 "darwin"
     */
    public static final List<String> OS_MACOSX = Collections.unmodifiableList(
        Arrays.asList("mac", "darwin")
    );

    /**
     * Linux 操作系统的识别关键字
     * 包括 "nux"
     * 注: 可以考虑添加更多的 Linux 发行版关键字,如 "ubuntu", "fedora" 等
     */
    public static final List<String> OS_LINUX = Collections.unmodifiableList(
        Arrays.asList("linux", "ubuntu", "debian", "fedora", "redhat", "centos", "nux")
    );

    /**
     * Windows 操作系统的识别关键字
     * 包括 "win"
     */
    public static final List<String> OS_WINDOWS = Collections.unmodifiableList(
        Arrays.asList("win")
    );

    // 架构识别模式

    /**
     * AMD64 架构的识别关键字
     * 包括 "amd64" 和 "x86_64"
     */
    public static final List<String> ARCH_AMD64 = Collections.unmodifiableList(
        Arrays.asList("amd64", "x86_64")
    );

    /**
     * Intel x86 架构的识别关键字
     * 包括 "x86", "i386", "i486", "i586", "i686", "i786"
     */
    public static final List<String> ARCH_I386 = Collections.unmodifiableList(
        Arrays.asList("x86", "i386", "i486", "i586", "i686", "i786")
    );

    /**
     * ARM64 架构的识别关键字
     * 包括 "arm64" 和 "aarch64"
     */
    public static final List<String> ARCH_ARM64 = Collections.unmodifiableList(
        Arrays.asList("arm64", "aarch64")
    );

    /**
     * ARM 架构的识别关键字
     * 包括 "arm"
     * 注: 可以考虑添加更具体的 ARM 版本,如 "armv7", "armv8" 等
     */
    public static final List<String> ARCH_ARM = Collections.unmodifiableList(
        Arrays.asList("arm", "armv6", "armv7", "armv7l")
    );

    /**
     私有构造函数，防止实例化
     */
    private PlatformPatterns2() {
        throw new AssertionError("工具类不应该被实例化");
    }

    /**
     * 判断给定的操作系统名称是否匹配指定的操作系统类型
     * @param osName 操作系统名称
     * @param osPattern 操作系统模式列表
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean matchesOS(String osName, List<String> osPattern) {
        if (osName == null || osPattern == null) {
            return false;
        }
        String lowerOsName = osName.toLowerCase();
        return osPattern.stream()
                .anyMatch(pattern -> lowerOsName.contains(pattern.toLowerCase()));
    }

    /**
     * 判断给定的架构名称是否匹配指定的架构类型
     * @param archName 架构名称
     * @param archPattern 架构模式列表
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean matchesArch(String archName, List<String> archPattern) {
        if (archName == null || archPattern == null) {
            return false;
        }
        String lowerArchName = archName.toLowerCase();
        return archPattern.stream()
                .anyMatch(pattern -> lowerArchName.equals(pattern.toLowerCase()));
    }

    /**
     * 获取当前系统的详细信息
     * @return 包含系统信息的字符串
     */
    public static String getCurrentPlatformInfo() {
        String osName = System.getProperty("os.name", "unknown");
        String osArch = System.getProperty("os.arch", "unknown");
        String osVersion = System.getProperty("os.version", "unknown");

        StringBuilder info = new StringBuilder()
                .append("Current Platform Information:\n")
                .append("OS Name: ").append(osName).append("\n")
                .append("OS Architecture: ").append(osArch).append("\n")
                .append("OS Version: ").append(osVersion);

        return info.toString();
    }
}