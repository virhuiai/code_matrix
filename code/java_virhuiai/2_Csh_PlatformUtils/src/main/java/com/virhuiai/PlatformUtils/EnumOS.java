package com.virhuiai.PlatformUtils;

//参考jcef-maven项目的实现
public enum EnumOS {
    MACOSX,
    LINUX,
    WINDOWS;

    private EnumOS() {
    }

    public boolean isMacOSX() {
        return this == MACOSX;
    }

    public boolean isLinux() {
        return this == LINUX;
    }

    public boolean isWindows() {
        return this == WINDOWS;
    }
}
