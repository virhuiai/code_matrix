package com.virhuiai.compact7z;

import org.apache.commons.cli.Option;

public enum Opt {

    MODE("compact7z.mode", Option.builder()
            .desc("指定操作模式：" +
                    "input_str.extract_md5=去除非MD5字符" +
                    "，compress=压缩文件" +
                    ",input_file.extract_items_simple_withpass=解压" +
                    ",quering_items_in_archive=列出文档" +
                    ",extract_items_simple=解压"+
                    ",extract_items_stand=解压"
            )

            .hasArg()
            .argName("操作模式")),
    INPUT_STR("compact7z.input_str", Option.builder()
            .desc("输入字符串")
            .hasArg()
            .argName("输入字符串")),
    COMPRESSION_LEVEL("compact7z.compression_level", Option.builder()
            .desc("压缩等级")
            .hasArg()
            .argName("压缩等级")),

    PASSWORD_VALUE("compact7z.password_value", Option.builder()
            .desc("用户指定的密码")
            .hasArg()
            .argName("密码")),
    PASSWORD_PREFIX("compact7z.password_prefix", Option.builder()
            .desc("密码前缀")
            .hasArg()
            .argName("密码前缀")),
    PASSWORD_SUFFIX("compact7z.password_suffix", Option.builder()
            .desc("密码后缀")
            .hasArg()
            .argName("密码后缀")),
    PASSWORD_SHOW("compact7z.password_show", Option.builder()
            .desc("密码显示")
            .hasArg()
            .argName("密码显示")),



    INPUT_DIR("compact7z.input_dir", Option.builder()
            .desc("设置输入目录，这是一个必填项")
            .hasArg()
            .argName("输入目录")),
    INPUT_7z("compact7z.input_7z", Option.builder()
            .desc("设置输入的7z文件")
            .hasArg()
            .argName("输入目录")),

    INPUT_FILE("compact7z.input_file", Option.builder()
            .desc("设置输入的文件")
            .hasArg()
            .argName("输入文件")),
    OUTPUT_FILE_PATH("compact7z.output_file_path", Option.builder()
            .desc("设置输出文件路径")
            .hasArg()
            .argName("输出文件路径")),
    EXTRA_ENABLED("compact7z.extra_enabled", Option.builder()
            .desc("是否在文件名中插入额外字符 (0:否, 1:是)")
            .hasArg()
            .argName("是否启用额外字符")
            .type(Number.class) // 指定参数类型为数字
    ),
    EXTRA_COUNT("compact7z.extra_count", Option.builder()//DEFAULT_EXTRA_COUNT = "32";
            .desc("设置额外计数值")
            .hasArg()
            .argName("额外字符数量")),

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
