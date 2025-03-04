package com.virhuiai.PlatformUtils;

//参考jcef-maven项目的实现
public class UnsupportedPlatformException extends Exception {
    private final String osName;
    private final String osArch;

    public UnsupportedPlatformException(String osName, String osArch) {
        super("Could not determine platform for os.name=" + osName + " and " + "os.arch" + "=" + osArch);
        this.osName = osName;
        this.osArch = osArch;
    }

    public String getOsName() {
        return this.osName;
    }

    public String getOsArch() {
        return this.osArch;
    }
}
