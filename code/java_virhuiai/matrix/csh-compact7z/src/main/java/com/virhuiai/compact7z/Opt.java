package com.virhuiai.compact7z;

import org.apache.commons.cli.Option;

public enum Opt {
    INPUT_DIR("compact7z.input_dir", Option.builder()
            .desc("设置输入目录，这是一个必填项")
            .hasArg()
            .argName("输入目录")),
    OUTPUT_FILE("compact7z.output_file", Option.builder()
            .desc("设置输出文件路径，这是一个必填项")
            .hasArg()
            .argName("输出文件路径")),
    EXTRA_ENABLED("compact7z.extra_enabled", Option.builder()
            .desc("设置是否启用额外功能")
            .hasArg()
            .argName("额外功能")),
    EXTRA_COUNT("compact7z.extra_count", Option.builder()//DEFAULT_EXTRA_COUNT = "32";
            .desc("设置额外计数值")
            .hasArg()
            .argName("额外计数值"))
    ;//use-root_path_last_resource



    /**
     * 存储与枚举常量关联的 {@link Option} 对象。
     * <p>
     * 该字段保存通过 {@link Option.Builder} 构建的命令行选项配置，
     * 包含长选项名称、描述和参数名称，用于命令行解析。
     * </p>
     */
    private final Option option;

    /**
     * 存储长选项名称。
     * <p>
     * 该字段保存选项的长名称（longOpt），用于标识命令行参数，例如 --web-lite.root_path_last。
     * 长选项名称支持字母、数字和连字符，不支持空格或特殊字符。
     * </p>
     */
    private final String optionName;

    /**
     * 构造函数，初始化枚举常量的 {@link Option} 对象和长选项名称。
     * <p>
     * 使用传入的 {@link Option.Builder} 构建 {@link Option} 对象，并设置长选项名称。
     * 长选项名称（longOpt）必须是有效的字符串，仅支持字母（a-z, A-Z）、数字（0-9）和连字符（-）。
     * 如果包含无效字符（如空格或特殊字符），可能会抛出 {@link IllegalArgumentException}。
     * </p>
     *
     * @param longOpt 长选项名称，用于命令行参数的标识
     * @param builder 预配置的 {@link Option.Builder}，包含描述和参数名称
     * @throws IllegalArgumentException 如果 longOpt 包含无效字符
     */
    Opt(String longOpt, Option.Builder builder) {
        this.optionName = longOpt;
        this.option = builder.longOpt(longOpt).build();
    }

    /**
     * 获取与枚举常量关联的 {@link Option} 对象。
     * <p>
     * 该方法返回预构建的命令行选项，用于命令行解析或配置静态 Web 服务器的根目录。
     * </p>
     *
     * @return 与枚举常量关联的 {@link Option} 对象
     */
    public Option getOption() {
        return option;
    }

    /**
     * 获取长选项名称。
     * <p>
     * 该方法返回选项的长名称（longOpt），用于标识命令行参数。
     * </p>
     *
     * @return 长选项名称字符串
     */
    public String getOptionName() {
        return optionName;
    }

}
