package com.virhuiai.log.platform;

import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;


import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

// 定义平台枚举类 参考jcef-maven项目的实现
public enum EnumPlatform {
    // 枚举实例：不同的操作系统和架构组合
    MACOSX_AMD64(PlatformPatterns.OS_MACOSX, PlatformPatterns.ARCH_AMD64, EnumOS.MACOSX),
    MACOSX_ARM64(PlatformPatterns.OS_MACOSX, PlatformPatterns.ARCH_ARM64, EnumOS.MACOSX),
    LINUX_AMD64(PlatformPatterns.OS_LINUX, PlatformPatterns.ARCH_AMD64, EnumOS.LINUX),
    LINUX_ARM64(PlatformPatterns.OS_LINUX, PlatformPatterns.ARCH_ARM64, EnumOS.LINUX),
    LINUX_ARM(PlatformPatterns.OS_LINUX, PlatformPatterns.ARCH_ARM, EnumOS.LINUX),
    WINDOWS_AMD64(PlatformPatterns.OS_WINDOWS, PlatformPatterns.ARCH_AMD64, EnumOS.WINDOWS),
    WINDOWS_I386(PlatformPatterns.OS_WINDOWS, PlatformPatterns.ARCH_I386, EnumOS.WINDOWS),
    WINDOWS_ARM64(PlatformPatterns.OS_WINDOWS, PlatformPatterns.ARCH_ARM64, EnumOS.WINDOWS);

    // 系统属性常量
    public static final String PROPERTY_OS_NAME = "os.name";
    public static final String PROPERTY_OS_ARCH = "os.arch";
//    private static final Logger LOGGER = Logger.getLogger(EnumPlatform.class.getName());
private static final Log LOGGER = LogFactory.getLog();
    private static EnumPlatform DETECTED_PLATFORM = null;
    private final String[] osMatch; // 操作系统匹配、操作系统的识别关键字
    private final String[] archMatch; // 架构匹配、架构的识别关键字
    private final String identifier; // 标识符
    private final EnumOS os; // 操作系统枚举

    // 构造函数
    private EnumPlatform(String[] osMatch, String[] archMatch, EnumOS os) {
        Objects.requireNonNull(osMatch, "操作系统匹配(osMatch)不能为 null");
        Objects.requireNonNull(archMatch, "架构匹配(archMatch)不能为 null");
        Objects.requireNonNull(os, "os 不能为 null");
        this.osMatch = osMatch;
        this.archMatch = archMatch;
        this.identifier = this.name().toLowerCase(Locale.ENGLISH).replace("_", "-");
        this.os = os;
    }

    /**
     * 支持的平台信息
     *
     *
     * MACOSX_AMD64(os.name: [mac, darwin], os.arch: [amd64, x86_64])
     * MACOSX_ARM64(os.name: [mac, darwin], os.arch: [arm64, aarch64])
     * LINUX_AMD64(os.name: [nux], os.arch: [amd64, x86_64])
     * LINUX_ARM64(os.name: [nux], os.arch: [arm64, aarch64])
     * LINUX_ARM(os.name: [nux], os.arch: [arm])
     * WINDOWS_AMD64(os.name: [win], os.arch: [amd64, x86_64])
     * WINDOWS_I386(os.name: [win], os.arch: [x86, i386, i486, i586, i686, i786])
     * WINDOWS_ARM64(os.name: [win], os.arch: [arm64, aarch64])
     * @return
     */
    public static String getSupported(){
        EnumPlatform[] platforms = values(); // 获取所有枚举实例
        // 构造支持的平台信息
        StringBuilder supported = new StringBuilder();
        for (EnumPlatform platform : platforms) {
            supported.append(platform.name())
                    .append("(os.name: ")
                    .append(Arrays.toString(platform.osMatch))
                    .append(", os.arch: ")
                    .append(Arrays.toString(platform.archMatch))
                    .append(")\n");
        }
        return supported.toString();
    }

    // 获取当前平台
    public static EnumPlatform getCurrentPlatform() throws UnsupportedPlatformException {
        if (null != DETECTED_PLATFORM) {
            return DETECTED_PLATFORM;
        } else {
            String osName = System.getProperty(PROPERTY_OS_NAME);
            String osArch = System.getProperty(PROPERTY_OS_ARCH);


            // 检查匹配的枚举
            for (EnumPlatform platform : values()) {
                if (platform.matches(osName, osArch)) {
                    DETECTED_PLATFORM = platform;
                    return platform;//如何符合的，就返回了
                }
            }


            // 日志记录与异常抛出
            LOGGER.error( "无法检测到当前的平台。是否被支持？\n" +
                    "如果您认为这是一个错误，请提供您的 os.name 和 os.arch 来报告问题！\n\n" +
                    "您的平台规格：\nos.name: \"" + osName + "\"\n" +
                    "os.arch: \"" + osArch + "\"\n\n支持的平台：\n" + getSupported());
            throw new UnsupportedPlatformException(osName, osArch);
        }
    }

    // 检查平台是否匹配
    private boolean matches(String osName, String osArch) {
        Objects.requireNonNull(osName, "osName 不能为 null");
        Objects.requireNonNull(osArch, "osArch 不能为 null");
        boolean osMatches = false;

        // 检查操作系统名称匹配
        for (String os : this.osMatch) {
            if (osName.toLowerCase(Locale.ENGLISH).contains(os)) {
                osMatches = true;
                break;
            }
        }

        if (!osMatches) {
            return false;
        } else {
            // 检查架构匹配
            for (String arch : this.archMatch) {
                if (osArch.toLowerCase(Locale.ENGLISH).equals(arch)) {
                    return true;
                }
            }
            return false;
        }
    }

    // 获取标识符
    public String getIdentifier() {
        return this.identifier;
    }

    // 获取操作系统枚举
    public EnumOS getOs() {
        return this.os;
    }

    // 判断是否是 MacOS
    public boolean isMac() {
        return this.os == EnumOS.MACOSX;
    }

    // 判断是否是 Windows
    public boolean isWindows() {
        return this.os == EnumOS.WINDOWS;
    }

    // 判断是否是 Linux
    public boolean isLinux() {
        return this.os == EnumOS.LINUX;
    }

    /**
     * 判断当前平台是否为64位架构
     *
     * @return 如果是64位架构返回true，否则返回false
     */
    public boolean is64Arch() {
        // 判断当前平台是否为以下64位架构之一：
        return
                // Windows ARM64 架构
                WINDOWS_ARM64.equals(this) ||
                        // Windows AMD64 架构
                        WINDOWS_AMD64.equals(this) ||
                        // Linux ARM64 架构
                        LINUX_ARM64.equals(this) ||
                        // Linux AMD64 架构
                        LINUX_AMD64.equals(this) ||
                        // macOS ARM64 架构
                        MACOSX_ARM64.equals(this) ||
                        // macOS AMD64 架构
                        MACOSX_AMD64.equals(this);

        // 优化建议：
        // 1. 考虑使用 EnumSet 来存储所有64位架构的枚举值，可以提高查找效率
        //    例如：private static final EnumSet<EnumPlatform> ARCH_64BIT = EnumSet.of(WINDOWS_ARM64, WINDOWS_AMD64, ...);
        //    然后使用：return ARCH_64BIT.contains(this);

        // 2. 如果未来添加新的64位架构，记得在这里更新条件

        // 3. 可以考虑添加一个静态初始化块，在类加载时就确定每个枚举值是否为64位架构，
        //    这样可以避免每次调用方法时都进行判断

        // 4. 如果这个方法经常被调用，可以考虑缓存结果，例如：
        //    private final boolean is64Bit;
        //    在构造函数中初始化：this.is64Bit = ...
        //    然后在此方法中直接返回：return this.is64Bit;

        // 5. 可以添加单元测试，确保所有的64位架构都被正确识别
    }

    /////////
    // 判断是否是 AMD64 架构
    public boolean isAMD64() {
        return Arrays.equals(this.archMatch, PlatformPatterns.ARCH_AMD64);
    }

    // 判断是否是 ARM64 架构
    public boolean isARM64() {
        return Arrays.equals(this.archMatch, PlatformPatterns.ARCH_ARM64);
    }

    // 判断是否是 ARM 架构
    public boolean isARM() {
        return Arrays.equals(this.archMatch, PlatformPatterns.ARCH_ARM);
    }

    // 判断是否是 I386 架构
    public boolean isI386() {
        return Arrays.equals(this.archMatch, PlatformPatterns.ARCH_I386);
    }
    // 以上方法可以再优化，修改下

}