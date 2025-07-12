package com.virhuiai.web_lite;

import org.apache.commons.cli.Option;

/**
 * 枚举类型，定义静态 Web 服务器的命令行配置选项。
 * <p>
 * 该枚举用于管理命令行参数选项，每个枚举常量对应一个 Apache Commons CLI 的 {@link Option} 对象。
 * 选项通过 {@link Option.Builder} 构建，包含长选项名称（longOpt）、描述（desc）和参数名称（argName），
 * 用于在 {@code createContext} 方法中配置静态 Web 服务器的根目录路径。
 * 每个选项都需要一个参数（通过 {@link Option.Builder#hasArg()} 设置），用于指定根目录路径或启用资源模式。
 * 长选项名称支持字母（a-z, A-Z）、数字（0-9）和连字符（-），例如 --web-lite.root_path_last，
 * 但不支持空格或特殊字符（如 !、@），否则可能抛出 {@link IllegalArgumentException}。
 * </p>
 *
 * @author [virhuiai]
 * @since 1.0
 */
public enum Opt {

    BIND_PATH("web-lite.bind_path", Option.builder()
            .desc("指定目录，多个选项，按逗号分割，每个项以冒号分隔，左边是url路径，右边是映射到的文件路径")
            .hasArg()
            .argName("指定的目录")
            ),

    /**
     * 根目录使用指定目录路径的选项。
     * <p>
     * 该选项用于在 {@code createContext} 方法中配置静态 Web 服务器的根目录，
     * 使用指定的文件系统目录路径，例如 --web-lite.root_path_last=/path/to/dir。
     * 通常适用于从本地文件系统加载静态内容的场景。
     * </p>
     */
    ROOT_PATH_LAST("web-lite.root_path_last", Option.builder()
            .desc("指定一个目录作为根目录")
            .hasArg()
            .argName("指定的根目录")
            ),

    /**
     * 根目录使用 JAR 包资源的选项。
     * <p>
     * 该选项用于在 {@code createContext} 方法中配置静态 Web 服务器的根目录，
     * 使用 JAR 包中的资源路径，例如 --web-lite.use-root_path_last_resource=1。
     * 当参数值设置为 "1" 时，启用从 JAR 文件加载静态内容的模式，适用于打包部署的场景。
     * </p>
     *
     * <p>
     *     先判断ROOT_PATH_LAST，没有值则判断 ROOT_PATH_LAST_RESOURCE
     * </p>
     */
    ROOT_PATH_LAST_RESOURCE("web-lite.use-root_path_last_resource", Option.builder()
            .desc("如果设置为1，则根目录使用jar包的resource")
            .hasArg()
            .argName("是否设置：根目录使用jar包的resource"))
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
