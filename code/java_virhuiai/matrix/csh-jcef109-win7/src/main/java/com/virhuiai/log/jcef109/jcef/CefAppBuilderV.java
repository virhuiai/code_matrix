package com.virhuiai.log.jcef109.jcef;

// 导入必要的依赖包

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.EnumProgress;
import me.friwi.jcefmaven.IProgressHandler;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import me.friwi.jcefmaven.impl.step.extract.TarGzExtractor;
import me.friwi.jcefmaven.impl.step.init.CefInitializer;
import me.friwi.jcefmaven.impl.util.FileUtils;
import me.friwi.jcefmaven.impl.util.macos.UnquarantineUtil;
import org.cef.CefApp;
import org.cef.CefSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

/**
 * CefAppBuilder的扩展类,用于构建和管理CEF应用程序实例
 */
public class CefAppBuilderV extends CefAppBuilder {
    private static final Log LOGGER = LogFactory.getLog();

    // 默认的安装目录
    private static final File DEFAULT_INSTALL_DIR = new File("jcef-bundle");
    // 默认的进度处理器
    private static final IProgressHandler DEFAULT_PROGRESS_HANDLER = new ConsoleProgressHandler();
    // 默认的JCEF参数列表
    private static final List<String> DEFAULT_JCEF_ARGS = new LinkedList<>();
    // 默认的CEF设置
    private static final CefSettings DEFAULT_CEF_SETTINGS = new CefSettings();

    // 用于线程同步的锁对象
    private final Object lock = new Object();
    // JCEF命令行参数列表
    private final List<String> jcefArgs;
    // CEF设置对象
    private final CefSettings cefSettings;
    // 安装目录
    private File installDir;
    // 进度处理器
    private IProgressHandler progressHandler;
    // CEF应用程序实例
    private CefApp instance = null;
    // 是否正在构建标志
    private boolean building = false;
    // 是否已安装标志
    private boolean installed = false;
    // 下载镜像源集合
    private final Set<String> mirrors;

    /**
     * 构造函数 - 创建一个新的CefAppBuilder实例
     * Constructs a new CefAppBuilder instance.
     */
    public CefAppBuilderV() {
        // 初始化默认值
        installDir = DEFAULT_INSTALL_DIR;
        progressHandler = DEFAULT_PROGRESS_HANDLER;
        jcefArgs = new LinkedList<>();
        jcefArgs.addAll(DEFAULT_JCEF_ARGS);
        cefSettings = DEFAULT_CEF_SETTINGS.clone();
        mirrors = new HashSet<>();
        // 添加默认的下载镜像源
//        mirrors.add("https://github.com/jcefmaven/jcefmaven/releases/download/{mvn_version}/jcef-natives-{platform}-{tag}.jar");
//        mirrors.add("https://repo.maven.apache.org/maven2/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar");
        mirrors.add("http://mirrors.huaweicloud.com/repository/maven/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar");

    }

    /**
     * 设置安装目录。默认为"./jcef-bundle"
     * Sets the install directory to use. Defaults to "./jcef-bundle".
     *
     * @param installDir 要安装到的目录 / the directory to install to
     */
    public void setInstallDir(File installDir) {
        Objects.requireNonNull(installDir, "installDir cannot be null");
        this.installDir = installDir;
    }

    /**
     * 指定进度处理器以接收安装进度更新。默认为"new ConsoleProgressHandler()"
     * Specify a progress handler to receive install progress updates.
     * Defaults to "new ConsoleProgressHandler()".
     *
     * @param progressHandler 要使用的进度处理器 / a progress handler to use
     */
    public void setProgressHandler(IProgressHandler progressHandler) {
        Objects.requireNonNull(installDir, "progressHandler cannot be null");
        this.progressHandler = progressHandler;
    }

    /**
     * 获取要传递给JCef库的可变参数列表。参数可以包含空格。
     * 由于使用maven安装,某些参数可能会根据平台再次被覆盖。
     * 确保不要指定破坏安装过程的参数(例如子进程路径、资源路径等)！
     *
     * Retrieves a mutable list of arguments to pass to the JCef library.
     * Arguments may contain spaces.
     * Due to installation using maven some arguments may be overwritten
     * again depending on your platform. Make sure to not specify arguments
     * that break the installation process (e.g. subprocess path, resources path...)!
     *
     * @return 传递给JCef库的可变参数列表 / A mutable list of arguments to pass to the JCef library
     */
    public List<String> getJcefArgs() {
        return jcefArgs;
    }

    /**
     * 添加一个或多个要传递给JCef库的参数。参数可以包含空格。
     * 由于使用maven安装,某些参数可能会根据平台再次被覆盖。
     * 确保不要指定破坏安装过程的参数(例如子进程路径、资源路径等)！
     *
     * Add one or multiple arguments to pass to the JCef library.
     * Arguments may contain spaces.
     * Due to installation using maven some arguments may be overwritten
     * again depending on your platform. Make sure to not specify arguments
     * that break the installation process (e.g. subprocess path, resources path...)!
     *
     * @param args 要添加的参数 / the arguments to add
     */
    public void addJcefArgs(String... args) {
        Objects.requireNonNull(args, "args cannot be null");
        jcefArgs.addAll(Arrays.asList(args));
    }

    /**
     * 获取嵌入的CefSettings实例以更改配置参数。
     * 由于使用maven安装,某些设置可能会根据平台再次被覆盖。
     *
     * Retrieve the embedded {@link org.cef.CefSettings} instance to change
     * configuration parameters.
     * Due to installation using maven some settings may be overwritten
     * again depending on your platform.
     *
     * @return 嵌入的CefSettings实例 / the embedded {@link org.cef.CefSettings} instance
     */
    public CefSettings getCefSettings() {
        return cefSettings;
    }

    /**
     * 附加自定义适配器以处理CEF中的某些事件
     * Attach your own adapter to handle certain events in CEF.
     *
     * @param handlerAdapter 要附加的适配器 / the adapter to attach
     */
    public void setAppHandler(MavenCefAppHandlerAdapter handlerAdapter) {
        CefApp.addAppHandler(handlerAdapter);
    }

    /**
     * 获取当前使用的所有镜像的副本。要添加另一个镜像，请使用setter。
     * 镜像URL可以包含在获取尝试时被替换的占位符：
     * {mvn_version}: jcefmaven的版本(例如100.0.14.3)
     * {platform}: 下载所需的平台(例如linux-amd64)
     * {tag}: 下载所需的版本标签(例如jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     *
     * Get a copy of all mirrors that are currently in use. To add another mirror, use the setter.
     * Mirror urls can contain placeholders that are replaced when a fetch is attempted:
     * {mvn_version}: The version of jcefmaven (e.g. 100.0.14.3)
     * {platform}: The desired platform for the download (e.g. linux-amd64)
     * {tag}: The desired version tag for the download (e.g. jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     *
     * @return 当前使用的所有镜像的副本。第一个元素将首先尝试 / A copy of all mirrors that are currently in use. First element will be attempted first.
     */
    public Collection<String> getMirrors() {
        return new HashSet<>(mirrors);
    }

    /**
     * 设置下载jcef时应使用的镜像URL。第一个元素将首先尝试。
     * 镜像URL可以包含在获取尝试时被替换的占位符：
     * {mvn_version}: jcefmaven的版本(例如100.0.14.3)
     * {platform}: 下载所需的平台(例如linux-amd64)
     * {tag}: 下载所需的版本标签(例如jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     *
     * Set mirror urls that should be used when downloading jcef. First element will be attempted first.
     * Mirror urls can contain placeholders that are replaced when a fetch is attempted:
     * {mvn_version}: The version of jcefmaven (e.g. 100.0.14.3)
     * {platform}: The desired platform for the download (e.g. linux-amd64)
     * {tag}: The desired version tag for the download (e.g. jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     */
    public void setMirrors(Collection<String> mirrors) {
        Objects.requireNonNull(mirrors, "mirrors can not be null");
        this.mirrors.clear();
        this.mirrors.addAll(mirrors);
    }

    /**
     * 辅助方法,用于安装本地库/资源。
     * 对于在实际需要创建CEF应用程序实例之前触发安装很有用。
     * 此方法不是线程安全的,调用者必须确保一次只有一个线程调用此方法。
     *
     * Helper method to install the native libraries/resources. Useful for triggering an install ahead of actually
     * needing to create a CEF app instance.  This method is NOT thread safe and the caller must ensure only one thread
     * will call this method at a time.
     *
     * @return 此构建器实例 / This builder instance
     * @throws IOException 如果无法获取工件或磁盘上的IO操作失败 / if an artifact could not be fetched or IO-actions on disk failed
     * @throws UnsupportedPlatformException 如果平台不受支持 / if the platform is not supported
     */
    public CefAppBuilder install() throws IOException, UnsupportedPlatformException {
        // 检查是否已安装
        if (this.installed) {
            return this;
        }
        this.progressHandler.handleProgress(EnumProgress.LOCATING, EnumProgress.NO_ESTIMATION);
        boolean installOk = CefInstallationCheckerV.checkInstallation(this.installDir);
        if (!installOk) {
            //执行安装
            //清除安装目录
            FileUtils.deleteDir(this.installDir);
            if (!this.installDir.mkdirs()) throw new IOException("Could not create installation directory");
            //获取原生输入流
            InputStream nativesIn = PackageClasspathStreamerV.streamNatives(
                    CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform());
            try {
                boolean downloading = false;
                if (nativesIn == null) {
                    this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, EnumProgress.NO_ESTIMATION);
                    downloading = true;
                    File download = new File(this.installDir, "download.zip.temp");
                    PackageDownloaderV.downloadNatives(
                            CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform(),
                            download, f -> {
                                this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, f);
                            }, mirrors);
                    nativesIn = new ZipInputStream(new FileInputStream(download));
                    ZipEntry entry;
                    boolean found = false;
                    while ((entry = ((ZipInputStream) nativesIn).getNextEntry()) != null) {
                        if (entry.getName().endsWith(".tar.gz")) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IOException("Downloaded artifact did not contain a .tar.gz archive");
                    }
                }
                //提取原生包
                this.progressHandler.handleProgress(EnumProgress.EXTRACTING, EnumProgress.NO_ESTIMATION);
                TarGzExtractor.extractTarGZ(this.installDir, nativesIn);
//                if (downloading) {// 不再删除
//                    if (!new File(this.installDir, "download.zip.temp").delete()) {
//                        throw new IOException("Could not remove downloaded temp file");
//                    }
//                }
            } finally {
                // 确保在上述任何操作失败时关闭nativesIn
                if (nativesIn != null) {
                    nativesIn.close();
                }
            }
            //安装原生包
            this.progressHandler.handleProgress(EnumProgress.INSTALL, EnumProgress.NO_ESTIMATION);
            //在macOS上移除隔离
            if (EnumPlatform.getCurrentPlatform().getOs().isMacOSX()) {
                UnquarantineUtil.unquarantine(this.installDir);
            }
            //锁定安装
            if (!(new File(installDir, "install.lock").createNewFile())) {
                throw new IOException("Could not create install.lock to complete installation");
            }
        }
        this.installed = true;
        return this;
    }

    /**
     * 构建CefApp实例。
     * 多次调用时将返回先前构建的实例。
     * 此方法是线程安全的。
     *
     * Builds a {@link org.cef.CefApp} instance. When called multiple times,
     * will return the previously built instance. This method is thread-safe.
     *
     * @return 已构建的CefApp实例 / a built {@link org.cef.CefApp} instance
     * @throws IOException 如果无法获取工件或磁盘上的IO操作失败 / if an artifact could not be fetched or IO-actions on disk failed
     * @throws UnsupportedPlatformException 如果平台不受支持 / if the platform is not supported
     * @throws InterruptedException 如果安装过程被中断 / if the installation process got interrupted
     * @throws CefInitializationException 如果JCef的初始化失败 / if the initialization of JCef failed
     */
    public CefApp build() throws IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        //检查是否已经构建了实例
        if (this.instance != null) {
            return this.instance;
        }
        //检查是否正在构建实例
        synchronized (lock) {
            if (building) {
                //检查实例是否在此期间未创建
                //以防止竞态条件
                if (this.instance == null) {
                    //等待另一个线程完成构建
                    lock.wait();
                }
                return this.instance;
            }
            this.building = true;
        }
        this.install();
        this.progressHandler.handleProgress(EnumProgress.INITIALIZING, EnumProgress.NO_ESTIMATION);
        synchronized (lock) {
            //设置实例必须在同步块中进行
            //以防止竞态条件
            this.instance = CefInitializer.initialize(this.installDir, this.jcefArgs, this.cefSettings);
            //添加关闭钩子,在jvm退出时尝试释放我们的实例
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.instance.dispose()));
            //通知进度处理器
            this.progressHandler.handleProgress(EnumProgress.INITIALIZED, EnumProgress.NO_ESTIMATION);
            //恢复等待的线程
            lock.notifyAll();
        }
        return this.instance;
    }

    /**
     * 获取下载所需的URL
     * @param platform
     * @return
     * @throws IOException
     * @throws UnsupportedPlatformException
     */
    public String fetchInstallationUrl(EnumPlatform platform) throws IOException {
        return PackageDownloaderV.buildDownloadUrl(
                CefBuildInfo.fromClasspath(), platform, mirrors);
    }

    public static void main(String[] args) throws IOException, UnsupportedPlatformException {
// 创建CEF应用构建器
        CefAppBuilderV builder = new CefAppBuilderV();
        //设置华为镜像，加载比较快
        builder.setMirrors(Arrays.asList("http://mirrors.huaweicloud.com/repository/maven/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar"));

        String baseDir = "/Volumes/RamDisk/jcef109/";
        for (EnumPlatform platform : EnumPlatform.values()) {

            String identifier = platform.getIdentifier();
            File installDir = new File("/Volumes/RamDisk/jcef109/" + identifier);
            boolean installOk = CefInstallationCheckerV.checkInstallation(installDir);
            if (!installOk) {
                String mirror =  "http://mirrors.huaweicloud.com/repository/maven/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar";
                String url = builder.fetchInstallationUrl(platform);
                IProgressHandler progressHandler = new ConsoleProgressHandler();
                progressHandler.handleProgress(EnumProgress.LOCATING, EnumProgress.NO_ESTIMATION);
                //执行安装
                //清除安装目录
                FileUtils.deleteDir(installDir);

                if (!installDir.mkdirs()){
                    LOGGER.error("因为无法建立文件夹，安装失败,identifier:" + identifier);
                    throw new IOException("Could not create installation directory");
                }

                LOGGER.info("准备获取原生输入流,identifier:" + identifier);
                //获取原生输入流
                InputStream nativesIn = PackageClasspathStreamerV.streamNatives(
                        CefBuildInfo.fromClasspath(), platform);
                try {
                    boolean downloading = false;
                    if (nativesIn == null) {
                        progressHandler.handleProgress(EnumProgress.DOWNLOADING, EnumProgress.NO_ESTIMATION);
                        downloading = true;
                        File download = new File(installDir, "download.zip.temp");
                        PackageDownloaderV.downloadNatives(
                                CefBuildInfo.fromClasspath(), platform,
                                download, f -> {
                                    progressHandler.handleProgress(EnumProgress.DOWNLOADING, f);
                                }, builder.mirrors);
                        nativesIn = new ZipInputStream(new FileInputStream(download));
                        ZipEntry entry;
                        boolean found = false;
                        while ((entry = ((ZipInputStream) nativesIn).getNextEntry()) != null) {
                            if (entry.getName().endsWith(".tar.gz")) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new IOException("Downloaded artifact did not contain a .tar.gz archive");
                        }
                    }
                    //提取原生包
                    progressHandler.handleProgress(EnumProgress.EXTRACTING, EnumProgress.NO_ESTIMATION);
                    TarGzExtractor.extractTarGZ(installDir, nativesIn);
//                if (downloading) {// 不再删除
//                    if (!new File(this.installDir, "download.zip.temp").delete()) {
//                        throw new IOException("Could not remove downloaded temp file");
//                    }
//                }

                } finally {
                    // 确保在上述任何操作失败时关闭nativesIn
                    if (nativesIn != null) {
                        nativesIn.close();
                    }
                }

                //安装原生包
                progressHandler.handleProgress(EnumProgress.INSTALL, EnumProgress.NO_ESTIMATION);
                //在macOS上移除隔离
                if (EnumPlatform.getCurrentPlatform().getOs().isMacOSX()) {
                    UnquarantineUtil.unquarantine(installDir);
                }
                //锁定安装
                if (!(new File(installDir, "install.lock").createNewFile())) {
                    throw new IOException("Could not create install.lock to complete installation");
                }
                LOGGER.info("安装成功,identifier:" + identifier);
            }else{
                LOGGER.info("已经安装过,identifier:" + identifier);
            }




//            System.out.println(url);


            // 替换镜像URL中的变量
//                String m = mirror
//                        .replace("{platform}", platform.getIdentifier())
//                        .replace("{tag}", info.getReleaseTag())
//                        .replace("{mvn_version}", mvn_version);

//            System.out.println(identifier);
//            macosx-amd64
//            macosx-arm64
//            linux-amd64
//            linux-arm64
//            linux-arm
//            windows-amd64
//            windows-i386
//            windows-arm64

        }
    }

}
