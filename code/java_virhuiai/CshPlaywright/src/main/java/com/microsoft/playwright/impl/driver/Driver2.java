package com.microsoft.playwright.impl.driver;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Driver2抽象类 - Playwright驱动程序的基类
 * 用于管理和初始化Playwright命令行工具
 */
public abstract class Driver2 {
    /**
     * 环境变量映射表
     * 用于存储驱动程序运行时所需的环境变量
     */
    protected final Map<String, String> env = new LinkedHashMap<>();

    /**
     * 单例实例
     * 确保整个应用程序中只有一个Driver2实例
     */
    private static Driver2 instance;

    /**
     * 默认构造函数
     */
    public Driver2() {
    }

    /**
     * 确保驱动程序已安装的静态同步方法
     * 使用单例模式确保只创建一个驱动实例
     *
     * @param env 环境变量映射
     * @param installBrowsers 是否安装浏览器的标志
     * @return 返回Driver2的单例实例
     */
    public static synchronized Driver2 ensureDriverInstalled(Map<String, String> env, Boolean installBrowsers) {
        // 如果实例不存在，创建并安装新实例
        if (instance == null) {
            instance = createAndInstall(env, installBrowsers);
        }

        return instance;
    }

    /**
     * 初始化驱动程序
     * 设置环境变量并调用子类的初始化方法
     *
     * @param env 要添加的环境变量
     * @param installBrowsers 是否安装浏览器
     * @throws Exception 初始化过程中可能抛出的异常
     */
    private void initialize(Map<String, String> env, Boolean installBrowsers) throws Exception {
        // 将传入的环境变量添加到实例的环境变量中
        this.env.putAll(env);
        // 调用子类实现的初始化方法
        this.initialize(installBrowsers);
    }

    /**
     * 抽象方法：子类需要实现的初始化逻辑
     *
     * @param installBrowsers 是否安装浏览器
     * @throws Exception 初始化过程中可能抛出的异常
     */
    protected abstract void initialize(Boolean installBrowsers) throws Exception;

    /**
     * 获取驱动程序可执行文件的路径
     * 根据操作系统选择合适的命令文件
     *
     * @return 驱动程序可执行文件的完整路径
     */
    public Path driverPath() {
        // 根据操作系统判断使用的命令文件名
        // Windows系统使用.cmd文件，其他系统使用.sh文件
        String cliFileName = System.getProperty("os.name").toLowerCase().contains("windows") ? "playwright.cmd" : "playwright.sh";
        // 返回驱动目录下的命令文件路径
        return this.driverDir().resolve(cliFileName);
    }

    /**
     * 创建用于启动Playwright进程的ProcessBuilder
     * 配置所有必要的环境变量
     *
     * @return 配置好的ProcessBuilder实例
     */
    public ProcessBuilder createProcessBuilder() {
        // 创建ProcessBuilder，使用驱动程序路径作为命令
        ProcessBuilder pb = new ProcessBuilder(this.driverPath().toString());
        // 添加所有环境变量
        pb.environment().putAll(this.env);
        // 设置语言标识为Java
        pb.environment().put("PW_LANG_NAME", "java");
        // 设置Java主版本号
        pb.environment().put("PW_LANG_NAME_VERSION", getMajorJavaVersion());
        // 获取当前包的实现版本
        String version = Driver2.class.getPackage().getImplementationVersion();
        if (version != null) {
            // 如果版本不为空，设置CLI显示版本
            pb.environment().put("PW_CLI_DISPLAY_VERSION", version);
        }

        return pb;
    }

    /**
     * 获取Java的主版本号
     * 处理不同格式的Java版本字符串
     *
     * @return Java主版本号字符串
     */
    private static String getMajorJavaVersion() {
        // 获取Java版本属性
        String version = System.getProperty("java.version");
        // 处理旧版本格式（1.x.x）
        if (version.startsWith("1.")) {
            // 返回第三个字符（例如：1.8.0 -> 8）
            return version.substring(2, 3);
        } else {
            // 处理新版本格式（9.x.x, 10.x.x等）
            int dot = version.indexOf(".");
            // 返回第一个点之前的部分，如果没有点则返回整个版本号
            return dot != -1 ? version.substring(0, dot) : version;
        }
    }

    /**
     * 创建并安装Driver2实例
     * 工厂方法，负责创建合适的驱动实例并进行初始化
     *
     * @param env 环境变量映射
     * @param installBrowsers 是否安装浏览器
     * @return 初始化完成的Driver2实例
     * @throws RuntimeException 如果创建或初始化失败
     */
    public static Driver2 createAndInstall(Map<String, String> env, Boolean installBrowsers) {
        try {
            // 创建新的Driver2实例
            Driver2 instance = newInstance();
            // 记录初始化开始日志
            logMessage("initializing Driver2");
            // 执行初始化
            instance.initialize(env, installBrowsers);
            // 记录初始化完成日志
            logMessage("Driver2 initialized.");
            return instance;
        } catch (Exception e) {
            // 如果出现异常，包装成运行时异常抛出
            throw new RuntimeException("Failed to create Driver2", e);
        }
    }

    /**
     * 创建新的Driver2实例
     * 根据系统属性决定使用预安装驱动还是JAR包驱动
     *
     * @return 新创建的Driver2实例
     * @throws Exception 创建过程中可能抛出的异常
     */
    private static Driver2 newInstance() throws Exception {
        // 检查是否通过系统属性指定了CLI目录
        String pathFromProperty = System.getProperty("playwright.cli.dir");
        if (pathFromProperty != null) {
            // 如果指定了路径，使用预安装的驱动
            return new PreinstalledDriver(Paths.get(pathFromProperty));
        } else {
            // 否则使用JAR包中的驱动
            return new DriverJar2();
        }
    }

    /**
     * 抽象方法：获取驱动程序所在目录
     * 子类必须实现此方法以提供驱动程序的安装位置
     *
     * @return 驱动程序目录的路径
     */
    protected abstract Path driverDir();

    /**
     * 记录带时间戳的日志消息
     * 使用统一的日志前缀"pw:install"
     *
     * @param message 要记录的消息内容
     */
    protected static void logMessage(String message) {
        DriverLogging.logWithTimestamp("pw:install " + message);
    }

    /**
     * 预安装驱动实现类
     * 用于处理已经安装在系统中的Playwright CLI
     */
    private static class PreinstalledDriver extends Driver2 {
        /**
         * 驱动程序所在目录
         */
        private final Path driverDir;

        /**
         * 构造函数
         *
         * @param driverDir 预安装的驱动程序目录路径
         */
        PreinstalledDriver(Path driverDir) {
            // 记录创建预安装驱动的日志
            logMessage("created PreinstalledDriver: " + driverDir);
            this.driverDir = driverDir;
        }

        /**
         * 预安装驱动的初始化方法
         * 因为驱动已经安装，所以这里不需要做任何操作
         *
         * @param installBrowsers 是否安装浏览器（对预安装驱动无效）
         */
        protected void initialize(Boolean installBrowsers) {
            // 预安装驱动无需初始化操作
        }

        /**
         * 返回驱动程序目录
         *
         * @return 预安装的驱动程序目录路径
         */
        protected Path driverDir() {
            return this.driverDir;
        }
    }
}
