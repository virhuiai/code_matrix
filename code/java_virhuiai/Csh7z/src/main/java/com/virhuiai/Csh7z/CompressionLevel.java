package com.virhuiai.Csh7z;

// 压缩等级枚举
public enum CompressionLevel {
    COPY(0, "Copy mode (no compression)"),
    FASTEST(1, "Fastest"),
    FAST(3, "Fast"),
    NORMAL(5, "Normal"),
    MAXIMUM(7, "Maximum"),
    ULTRA(9, "Ultra");

    private final int level;
    private final String description;

    CompressionLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public static CompressionLevel fromLevel(int level) {
        if (level < 0) return COPY;
        if (level > 9) return ULTRA;

        CompressionLevel closest = NORMAL;
        int minDiff = Math.abs(level - NORMAL.level);

        for (CompressionLevel value : values()) {
            int diff = Math.abs(level - value.level);
            if (diff < minDiff) {
                minDiff = diff;
                closest = value;
            }
        }
        return closest;
    }

    public static String getAvailableLevels() {
        StringBuilder sb = new StringBuilder("可用的压缩等级:\n");
        for (CompressionLevel level : values()) {
            sb.append(String.format("  %d - %s%n", level.getLevel(), level.getDescription()));
        }
        return sb.toString();
    }

    public static boolean isValidLevel(int level) {
        for (CompressionLevel value : values()) {
            if (value.level == level) return true;
        }
        return false;
    }

    public CompressionLevel next() {
        int index = ordinal();
        if (index < values().length - 1) {
            return values()[index + 1];
        }
        return this;
    }

    public CompressionLevel previous() {
        int index = ordinal();
        if (index > 0) {
            return values()[index - 1];
        }
        return this;
    }

    @Override
    public String toString() {
        return String.format("%d (%s)", level, description);
    }
}