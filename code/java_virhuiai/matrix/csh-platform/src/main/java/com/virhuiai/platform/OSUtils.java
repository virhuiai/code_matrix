package com.virhuiai.platform;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;


/**
 * 操作系统工具类
 * 提供了判断当前操作系统类型的方法
 * 新加
 */
public class OSUtils {
    // 日志对象
    private static final Log LOGGER = LogFactory.getLog(OSUtils.class);

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();
    private static final String OS_VERSION = System.getProperty("os.version").toLowerCase();

    private static final boolean IS_WINDOWS = OS_NAME.startsWith("windows");
    private static final boolean IS_MAC = OS_NAME.startsWith("mac");
    private static final boolean IS_LINUX = OS_NAME.startsWith("linux");

    private static final boolean IS_X86 = OS_ARCH.equals("x86") || OS_ARCH.equals("i386");
    private static final boolean IS_X86_64 = OS_ARCH.equals("amd64") || OS_ARCH.equals("x86_64");
    private static final boolean IS_ARM = OS_ARCH.startsWith("arm");
    private static final boolean IS_ARM64 = OS_ARCH.startsWith("aarch64");

    /**
     * 判断当前操作系统是否为Windows
     *
     * @return 如果是Windows系统返回true，否则返回false
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    /**
     * 判断当前操作系统是否为Mac
     *
     * @return 如果是Mac系统返回true，否则返回false
     */
    public static boolean isMac() {
        return IS_MAC;
    }

    /**
     * 判断当前操作系统是否为Linux
     *
     * @return 如果是Linux系统返回true，否则返回false
     */
    public static boolean isLinux() {
        return IS_LINUX;
    }

    /**
     * 判断当前系统架构是否为x86
     *
     * @return 如果是x86架构返回true，否则返回false
     */
    public static boolean isX86() {
        return IS_X86;
    }

    /**
     * 判断当前系统架构是否为x86_64
     *
     * @return 如果是x86_64架构返回true，否则返回false
     */
    public static boolean isX86_64() {
        return IS_X86_64;
    }

    /**
     * 判断当前系统架构是否为ARM
     *
     * @return 如果是ARM架构返回true，否则返回false
     */
    public static boolean isArm() {
        return IS_ARM;
    }

    /**
     * 判断当前系统架构是否为ARM64
     *
     * @return 如果是ARM64架构返回true，否则返回false
     */
    public static boolean isArm64() {
        return IS_ARM64;
    }

    /**
     * 获取操作系统名称
     *
     * @return 操作系统名称
     */
    public static String getOsName() {
        return OS_NAME;
    }

    /**
     * 获取操作系统架构
     *
     * @return 操作系统架构
     */
    public static String getOsArch() {
        return OS_ARCH;
    }

    /**
     * 获取操作系统版本
     *
     * @return 操作系统版本
     */
    public static String getOsVersion() {
        return OS_VERSION;
    }

    /**
     * 输出所有系统信息的组合
     *
     * @return 包含所有系统信息的字符串
     */
    public static String getAllSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("操作系统信息：\n");
        info.append("操作系统名称：").append(OS_NAME).append("\n");
        info.append("操作系统架构：").append(OS_ARCH).append("\n");
        info.append("操作系统版本：").append(OS_VERSION).append("\n");
        info.append("是否Windows：").append(IS_WINDOWS).append("\n");
        info.append("是否Mac：").append(IS_MAC).append("\n");
        info.append("是否Linux：").append(IS_LINUX).append("\n");
        info.append("是否x86架构：").append(IS_X86).append("\n");
        info.append("是否x86_64架构：").append(IS_X86_64).append("\n");
        info.append("是否ARM架构：").append(IS_ARM).append("\n");
        info.append("是否ARM64架构：").append(IS_ARM64).append("\n");
        return info.toString();
    }

    public static void main(String[] args) {
        LOGGER.info(getAllSystemInfo());
    }
}