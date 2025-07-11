package com.virhuiai.web_lite;

import com.virhuiai.web_lite.helper.ConfigWeb;
import org.apache.commons.cli.Option;

/**
 * 枚举类型，定义静态 Web 服务器的命令行配置选项。
 * <p>
 * 该枚举用于管理命令行参数选项，每个枚举常量对应一个 Apache Commons CLI 的 {@link Option} 对象。
 * 选项通过 {@link Option.Builder} 构建，包含长选项名称、描述和参数名称，用于配置静态 Web 服务器的根目录。
 * 每个选项都需要一个参数（通过 {@link Option.Builder#hasArg()} 设置），用于指定根目录路径。
 * </p>
 *
 * @author [Your Name]
 * @since 1.0
 */
public enum Opt {
    /**
     * 根目录使用 root_path_last 的选项。
     * <p>
     * 该选项指定静态 Web 服务器的根目录使用配置文件中的 {@code root_path_last} 值。
     * 通常用于指定文件系统中的本地目录路径。
     * </p>
     */
    ROOT_PATH_LAST(ConfigWeb.Keys.ROOT_PATH_LAST, Option.builder()
            .desc("根目录使用root_path_last")
            .hasArg()
            .argName("根目录使用root_path_last")
            ),

    /**
     * 根目录使用 JAR 包资源的选项。
     * <p>
     * 该选项指定静态 Web 服务器的根目录使用 JAR 包中的资源路径。
     * 适用于从 JAR 文件中加载静态内容的场景。
     * </p>
     */
    ROOT_PATH_LAST_RESOURCE(ConfigWeb.Keys.ROOT_PATH_LAST_RESOURCE, Option.builder()
            .desc("根目录使用jar包的resource")
            .hasArg()
            .argName("根目录使用jar包的resource"))
    ;

    /**
     * 存储与枚举常量关联的 {@link Option} 对象。
     * <p>
     * 该字段保存通过 {@link Option.Builder} 构建的命令行选项配置，包含长选项名称、描述和参数名称。
     * </p>
     */
    private final Option option;

    /**
     * 存储长选项名称。
     * <p>
     * 该字段保存选项的长名称（longOpt），用于标识命令行参数，例如 "--root-path-last"。
     * </p>
     */
    private final String optionName;

    /**
     * 构造函数，初始化枚举常量的 {@link Option} 对象和长选项名称。
     * <p>
     * 使用传入的 {@link Option.Builder} 构建 {@link Option} 对象，并设置长选项名称。
     * 每个枚举常量通过此构造函数初始化其特定的命令行选项配置。
     * </p>
     *
     * @param longOpt 长选项名称，用于命令行参数的标识
     * @param builder 预配置的 {@link Option.Builder}，包含描述和参数名称
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
