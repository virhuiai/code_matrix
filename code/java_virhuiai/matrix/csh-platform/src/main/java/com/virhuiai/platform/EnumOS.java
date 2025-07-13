package com.virhuiai.platform;

/**
 * 操作系统枚举类
 * 用于定义支持的操作系统类型
 * 参考jcef-maven项目的实现
 */
public enum EnumOS {
    /**
     * macOS 操作系统
     */
    MACOSX,

    /**
     * Linux 操作系统
     */
    LINUX,

    /**
     * Windows 操作系统
     */
    WINDOWS;

    /**
     * 判断是否为 macOS 系统
     * @return 如果是 macOS 系统返回 true，否则返回 false
     */
    public boolean isMacOSX() {
        return this == MACOSX;
    }

    /**
     * 判断是否为 Linux 系统
     * @return 如果是 Linux 系统返回 true，否则返回 false
     */
    public boolean isLinux() {
        return this == LINUX;
    }

    /**
     * 判断是否为 Windows 系统
     * @return 如果是 Windows 系统返回 true，否则返回 false
     */
    public boolean isWindows() {
        return this == WINDOWS;
    }

//    /**
//     * 获取当前系统的操作系统类型
//     * @return 当前系统的操作系统类型
//     */
//    public static EnumOS getCurrentOS() {
//        String osName = System.getProperty("os.name").toLowerCase();
//        if (osName.contains("mac") || osName.contains("darwin")) {
//            return MACOSX;
//        } else if (osName.contains("linux")) {
//            return LINUX;
//        } else if (osName.contains("windows")) {
//            return WINDOWS;
//        }
//        throw new IllegalStateException("Unsupported operating system: " + osName);
//    }
}
