package com.virhuiai.platform;

/**
 * 平台模式类
 * 用于定义不同操作系统和架构的识别模式 参考jcef-maven项目的实现
 */
public class PlatformPatterns {
    // 操作系统识别模式

    /**
     * macOS 操作系统的识别关键字
     * 包括 "mac" 和 "darwin"
     */
    public static String[] OS_MACOSX = new String[]{"mac", "darwin"};

    /**
     * Linux 操作系统的识别关键字
     * 包括 "nux"
     * 注: 可以考虑添加更多的 Linux 发行版关键字,如 "ubuntu", "fedora" 等
     */
    public static String[] OS_LINUX = new String[]{"nux"};

    /**
     * Windows 操作系统的识别关键字
     * 包括 "win"
     */
    public static String[] OS_WINDOWS = new String[]{"win"};

    // 架构识别模式

    /**
     * AMD64 架构的识别关键字
     * 包括 "amd64" 和 "x86_64"
     */
    public static String[] ARCH_AMD64 = new String[]{"amd64", "x86_64"};

    /**
     * Intel x86 架构的识别关键字
     * 包括 "x86", "i386", "i486", "i586", "i686", "i786"
     */
    public static String[] ARCH_I386 = new String[]{"x86", "i386", "i486", "i586", "i686", "i786"};

    /**
     * ARM64 架构的识别关键字
     * 包括 "arm64" 和 "aarch64"
     */
    public static String[] ARCH_ARM64 = new String[]{"arm64", "aarch64"};

    /**
     * ARM 架构的识别关键字
     * 包括 "arm"
     * 注: 可以考虑添加更具体的 ARM 版本,如 "armv7", "armv8" 等
     */
    public static String[] ARCH_ARM = new String[]{"arm"};

    /**
     * 构造函数
     * 注: 当前为空构造函数,可以考虑是否需要初始化操作或者改为私有构造函数
     */
    public PlatformPatterns() {
        // 可以在此添加初始化代码,如果需要的话
    }

    // 优化建议:
    // 1. 考虑使用枚举类型来替代字符串数组,可以提高类型安全性
    // 2. 可以添加方法来判断当前系统的操作系统和架构
    // 3. 考虑使用不可变集合(如 List.of())来替代数组,以增加安全性
    // 4. 可以添加更多的操作系统和架构类型,以增加全面性
    // 5. 考虑添加版本号识别的功能,可以更精确地识别系统
}