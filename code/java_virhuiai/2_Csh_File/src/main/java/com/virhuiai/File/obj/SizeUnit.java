package com.virhuiai.File.obj;

// 定义文件大小单位的枚举
public enum SizeUnit {
    BYTE("B", 1L),
    KB("KB", 1024L),
    MB("MB", 1024L * 1024L),
    GB("GB", 1024L * 1024L * 1024L),
    TB("TB", 1024L * 1024L * 1024L * 1024L);

    private final String unit;
    private final long factor;

    SizeUnit(String unit, long factor) {
        this.unit = unit;
        this.factor = factor;
    }

    public String getUnit() {
        return unit;
    }

    public long getFactor() {
        return factor;
    }
}
