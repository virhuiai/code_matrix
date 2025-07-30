package com.virhuiai.jcef109;

import org.apache.commons.cli.Option;

public enum Opt {

    INSTALL_DIR("jcef.install_dir", Option.builder()
            .desc("设置JCEF安装目录")
            .hasArg()
            .argName("JCEF安装目录")
    ),

    DEFAULT_URL("jcef.default_url", Option.builder()
            .desc("设置浏览器默认打开的URL")
            .hasArg()
            .argName("默认URL")
    ),

    PASS_ALL_ARGS_TO_CEF("jcef.pass_all_args_to_cef", Option.builder()
            .desc("是否将全部参数传递给CEF,1为是")
            .hasArg()
            .argName("将全部参数传递给CEF")
    )

    ,PROXY_SERVER("jcef.proxy_server", Option.builder()
            .desc("设置浏览器代理服务器地址")
            .hasArg()
            .argName("代理地址")
    )

    ,REMOTE_DEBUGGING_PORT("jcef.remote_debugging_port", Option.builder()
            .desc("设置Chrome远程调试端口号")
            .hasArg()
            .argName("端口号")
    )
    ;


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
