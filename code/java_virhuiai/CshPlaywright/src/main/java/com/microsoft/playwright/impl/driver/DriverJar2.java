package com.microsoft.playwright.impl.driver;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * DriverJar2 类 - 负责从JAR包中提取和管理Playwright驱动程序
 * 该类继承自Driver2，专门处理JAR包内嵌的驱动程序资源
 */
public class DriverJar2 extends Driver2 {
    // 跳过浏览器下载的环境变量名
    private static final String PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD";
    // Selenium远程URL环境变量名
    private static final String SELENIUM_REMOTE_URL = "SELENIUM_REMOTE_URL";
    // Playwright Node.js路径环境变量名
    static final String PLAYWRIGHT_NODEJS_PATH = "PLAYWRIGHT_NODEJS_PATH";
    // 驱动程序临时目录
    private final Path driverTempDir;
    // 预安装的Node.js路径
    private Path preinstalledNodePath;


    /**
     * 构造函数 - 初始化驱动程序JAR管理器
     * 创建临时目录并检查预设的Node.js路径
     * @throws IOException 如果创建临时目录失败
     */
    public DriverJar2() throws IOException {
        // 获取可选的临时目录路径
        String alternativeTmpdir = System.getProperty("playwright.driver.tmpdir");
        // 临时目录前缀
        String prefix = "playwright-java-";
        // 创建临时目录，如果指定了替代路径则使用替代路径，否则使用系统默认临时目录
        this.driverTempDir = alternativeTmpdir == null ? Files.createTempDirectory(prefix) : Files.createTempDirectory(Paths.get(alternativeTmpdir), prefix);
        // 标记临时目录在程序退出时删除
        this.driverTempDir.toFile().deleteOnExit();
        // 获取系统属性中指定的Node.js路径
        String nodePath = System.getProperty("playwright.nodejs.path");
        if (nodePath != null) {
            // 设置预安装的Node.js路径
            this.preinstalledNodePath = Paths.get(nodePath);
            // 验证Node.js路径是否存在
            if (!Files.exists(this.preinstalledNodePath, new LinkOption[0])) {
                throw new RuntimeException("Invalid Node.js path specified: " + nodePath);
            }
        }


        // 记录日志：已创建DriverJar实例
        logMessage("created DriverJar: " + this.driverTempDir);
    }


    /**
     * 初始化方法 - 设置驱动程序和可选安装浏览器
     * @param installBrowsers 是否安装浏览器
     * @throws Exception 如果初始化过程失败
     */
    protected void initialize(Boolean installBrowsers) throws Exception {
        // 如果未通过系统属性设置Node.js路径，则尝试从环境变量获取
        if (this.preinstalledNodePath == null && this.env.containsKey("PLAYWRIGHT_NODEJS_PATH")) {
            // 从环境变量获取Node.js路径
            this.preinstalledNodePath = Paths.get((String)this.env.get("PLAYWRIGHT_NODEJS_PATH"));
            // 验证路径有效性
            if (!Files.exists(this.preinstalledNodePath, new LinkOption[0])) {
                throw new RuntimeException("Invalid Node.js path specified: " + this.preinstalledNodePath);
            }
        } else if (this.preinstalledNodePath != null) {
            // 如果已设置Node.js路径，将其添加到环境变量中
            this.env.put("PLAYWRIGHT_NODEJS_PATH", this.preinstalledNodePath.toString());
        }


        // 从JAR包中提取驱动程序到临时目录
        this.extractDriverToTempDir();
        // 记录日志：驱动程序已提取
        logMessage("extracted driver from jar to " + this.driverPath());
        // 如果需要，安装浏览器
        if (installBrowsers) {
            this.installBrowsers(this.env);
        }

    }


    /**
     * 安装浏览器方法 - 下载并安装Playwright支持的浏览器
     * @param env 环境变量映射
     * @throws IOException 如果IO操作失败
     * @throws InterruptedException 如果进程被中断
     */
    private void installBrowsers(Map<String, String> env) throws IOException, InterruptedException {
        // 检查是否设置了跳过浏览器下载的环境变量
        String skip = (String)env.get("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD");
        if (skip == null) {
            // 如果映射中没有，尝试从系统环境变量获取
            skip = System.getenv("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD");
        }


        // 判断是否需要跳过浏览器下载
        if (skip != null && !"0".equals(skip) && !"false".equals(skip)) {
            // 跳过浏览器下载，因为设置了环境变量
            System.out.println("Skipping browsers download because `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD` env variable is set");
        } else if (env.get("SELENIUM_REMOTE_URL") == null && System.getenv("SELENIUM_REMOTE_URL") == null) {
            // 如果没有设置Selenium远程URL，则需要本地安装浏览器
            Path driver = this.driverPath();
            // 验证驱动程序文件是否存在
            if (!Files.exists(driver, new LinkOption[0])) {
                throw new RuntimeException("Failed to find driver: " + driver);
            } else {
                // 创建进程构建器来执行安装命令
                ProcessBuilder pb = this.createProcessBuilder();
                // 添加install命令参数
                pb.command().add("install");
                // 将错误输出重定向到当前进程
                pb.redirectError(Redirect.INHERIT);
                // 将标准输出重定向到当前进程
                pb.redirectOutput(Redirect.INHERIT);
                // 启动安装进程
                Process p = pb.start();
                // 等待进程完成，最多10分钟
                boolean result = p.waitFor(10L, TimeUnit.MINUTES);
                if (!result) {
                    // 超时，销毁进程
                    p.destroy();
                    throw new RuntimeException("Timed out waiting for browsers to install");
                } else if (p.exitValue() != 0) {
                    // 进程返回非零退出码，表示失败
                    throw new RuntimeException("Failed to install browsers, exit code: " + p.exitValue());
                }
            }
        } else {
            // 使用远程Selenium，跳过本地浏览器安装
            logMessage("Skipping browsers download because `SELENIUM_REMOTE_URL` env variable is set");
        }
    }


    /**
     * 判断文件是否为可执行文件
     * @param filePath 文件路径
     * @return 如果是可执行文件返回true
     */
    private static boolean isExecutable(Path filePath) {
        // 获取文件名
        String name = filePath.getFileName().toString();
        // 判断是否为shell脚本、exe文件或无扩展名文件（Unix可执行文件）
        return name.endsWith(".sh") || name.endsWith(".exe") || !name.contains(".");
    }


    /**
     * 初始化文件系统 - 用于访问JAR内部资源
     * @param uri JAR文件的URI
     * @return 文件系统实例，如果已存在则返回null
     * @throws IOException 如果创建文件系统失败
     */
    private FileSystem initFileSystem(URI uri) throws IOException {
        try {
            // 创建新的文件系统来访问JAR内容
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException var3) {
            // 文件系统已存在，返回null
            return null;
        }
    }


    /**
     * 获取驱动程序资源URI - 定位JAR内的驱动程序资源
     * @return 驱动程序资源的URI
     * @throws URISyntaxException 如果URI语法错误
     */
    public static URI getDriverResourceURI() throws URISyntaxException {
        // 获取当前线程的类加载器
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        // 根据平台获取对应的驱动程序资源路径
        return classloader.getResource("driver/" + platformDir()).toURI();
    }


    /**
     * 提取驱动程序到临时目录 - 从JAR包中复制所有驱动程序文件
     * @throws URISyntaxException 如果URI语法错误
     * @throws IOException 如果IO操作失败
     */
    void extractDriverToTempDir() throws URISyntaxException, IOException {
        // 获取原始URI
        URI originalUri = getDriverResourceURI();
        // 处理可能的嵌套JAR情况
        URI uri = this.maybeExtractNestedJar(originalUri);
        // 如果是JAR协议，初始化文件系统
        FileSystem fileSystem = "jar".equals(uri.getScheme()) ? this.initFileSystem(uri) : null;

        try {
            // 获取源根目录路径
            Path srcRoot = Paths.get(uri);
            // 转换为默认文件系统路径（用于计算相对路径）
            Path srcRootDefaultFs = Paths.get(srcRoot.toString());
            // 遍历源目录中的所有文件和目录
            Files.walk(srcRoot).forEach((fromPath) -> {
                // 如果已指定预安装的Node.js，跳过内嵌的node可执行文件
                if (this.preinstalledNodePath != null) {
                    String fileName = fromPath.getFileName().toString();
                    if ("node.exe".equals(fileName) || "node".equals(fileName)) {
                        return;
                    }
                }


                // 计算相对路径
                Path relative = srcRootDefaultFs.relativize(Paths.get(fromPath.toString()));
                // 计算目标路径
                Path toPath = this.driverTempDir.resolve(relative.toString());

                try {
                    // 如果是目录，创建对应的目录结构
                    if (Files.isDirectory(fromPath, new LinkOption[0])) {
                        Files.createDirectories(toPath);
                    } else {
                        // 如果是文件，复制文件内容
                        Files.copy(fromPath, toPath);
                        // 如果是可执行文件，设置执行权限
                        if (isExecutable(toPath)) {
                            toPath.toFile().setExecutable(true, true);
                        }
                    }


                    // 标记文件在程序退出时删除
                    toPath.toFile().deleteOnExit();
                } catch (IOException var8) {
                    // 提取失败时抛出运行时异常
                    throw new RuntimeException("Failed to extract driver from " + uri + ", full uri: " + originalUri, var8);
                }
            });
        } catch (Throwable var7) {
            // 确保文件系统被正确关闭
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }


        // 关闭文件系统
        if (fileSystem != null) {
            fileSystem.close();
        }

    }


    /**
     * 处理嵌套JAR的情况 - 某些情况下驱动程序可能在JAR中的JAR里
     * @param uri 原始URI
     * @return 处理后的URI，如果不是嵌套JAR则返回原URI
     * @throws URISyntaxException 如果URI语法错误
     */
    private URI maybeExtractNestedJar(URI uri) throws URISyntaxException {
        // 如果不是JAR协议，直接返回
        if (!"jar".equals(uri.getScheme())) {
            return uri;
        } else {
            // JAR URL分隔符
            String JAR_URL_SEPARATOR = "!/";
            // 按分隔符拆分URI
            String[] parts = uri.toString().split("!/");
            // 如果不是嵌套JAR格式（3部分），返回原URI
            if (parts.length != 3) {
                return uri;
            } else {
                // 构建内部JAR的URI
                String innerJar = String.join("!/", parts[0], parts[1]);
                URI jarUri = new URI(innerJar);

                try {
                    // 为内部JAR创建文件系统
                    FileSystem fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap());


                    URI var9;
                    try {
                        // 获取内部JAR的路径
                        Path fromPath = Paths.get(jarUri);
                        // 计算提取目标路径
                        Path toPath = this.driverTempDir.resolve(fromPath.getFileName().toString());
                        // 复制内部JAR到临时目录
                        Files.copy(fromPath, toPath);
                        // 标记临时文件在退出时删除
                        toPath.toFile().deleteOnExit();
                        // 构建新的JAR URI，指向提取后的文件
                        var9 = new URI("jar:" + toPath.toUri() + "!/" + parts[2]);
                    } catch (Throwable var11) {
                        // 确保文件系统被关闭
                        if (fs != null) {
                            try {
                                fs.close();
                            } catch (Throwable var10) {
                                var11.addSuppressed(var10);
                            }
                        }


                        throw var11;
                    }


                    // 关闭文件系统
                    if (fs != null) {
                        fs.close();
                    }

                    return var9;
                } catch (IOException var12) {
                    // 提取嵌套JAR失败
                    throw new RuntimeException("Failed to extract driver's nested .jar from " + jarUri + "; full uri: " + uri, var12);
                }
            }
        }
    }


    /**
     * 获取平台目录名 - 根据操作系统和架构返回对应的目录名
     * @return 平台特定的目录名
     */
    private static String platformDir() {
        // 获取操作系统名称（小写）
        String name = System.getProperty("os.name").toLowerCase();
        // 获取系统架构（小写）
        String arch = System.getProperty("os.arch").toLowerCase();
        // 根据操作系统返回对应的目录名
        if (name.contains("windows")) {
            // Windows平台
            return "win32_x64";
        } else if (name.contains("linux")) {
            // Linux平台，区分ARM64和其他架构
            return arch.equals("aarch64") ? "linux-arm64" : "linux";
        } else if (name.contains("mac os x")) {
            // macOS平台
            return "mac";
        } else {
            // 不支持的操作系统
            throw new RuntimeException("Unexpected os.name value: " + name);
        }
    }


    /**
     * 获取驱动程序目录 - 返回驱动程序的临时目录路径
     * @return 驱动程序目录路径
     */
    protected Path driverDir() {
        return this.driverTempDir;
    }
}

