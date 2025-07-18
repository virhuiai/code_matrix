package com.virhuiai.Csh7z;

// 压缩等级枚举
public enum Level7z {
    COPY(0, "Copy mode (no compression)"),
    FASTEST(1, "Fastest"),
    FAST(3, "Fast"),
    NORMAL(5, "Normal"),
    MAXIMUM(7, "Maximum"),
    ULTRA(9, "Ultra");

    private final int level;
    private final String description;

    Level7z(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据输入的数值获取最接近的压缩等级枚举值
     *
     * @param level 输入的压缩等级数值
     * @return 最接近的 CompressionLevel 枚举值
     */
    public static Level7z fromLevel(int level) {
        // 处理边界情况：如果输入值小于0，返回最低等级 COPY
        if (level < 0) return COPY;
        // 处理边界情况：如果输入值大于9，返回最高等级 ULTRA
        if (level > 9) return ULTRA;

        // 初始化：假设 NORMAL(5) 是最接近的值
        // closest 用于存储最接近的压缩等级枚举值
        Level7z closest = NORMAL;
        // minDiff 用于存储最小差值
        // 计算输入值与 NORMAL 等级的差的绝对值作为初始最小差值
        int minDiff = Math.abs(level - NORMAL.level);

        // 遍历所有压缩等级枚举值
        for (Level7z value : values()) {
            // 计算当前枚举值与输入值的差的绝对值
            int diff = Math.abs(level - value.level);
            // 如果找到更小的差值
            if (diff < minDiff) {
                // 更新最小差值
                minDiff = diff;
                // 更新最接近的枚举值
                closest = value;
            }
        }
        // 返回找到的最接近的压缩等级
        return closest;
    }

    public static String getAvailableLevels() {
        StringBuilder sb = new StringBuilder("可用的压缩等级:\n");
        for (Level7z level : values()) {
            sb.append(String.format("  %d - %s%n", level.getLevel(), level.getDescription()));
        }
        return sb.toString();
    }

    public static boolean isValidLevel(int level) {
        for (Level7z value : values()) {
            if (value.level == level) return true;
        }
        return false;
    }

    public Level7z next() {
        int index = ordinal();
        if (index < values().length - 1) {
            return values()[index + 1];
        }
        return this;
    }

    public Level7z previous() {
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