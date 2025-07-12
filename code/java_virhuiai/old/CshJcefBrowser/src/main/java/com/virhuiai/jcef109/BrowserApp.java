package com.virhuiai.jcef109;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.jcef109.jcef.CefAppBuilderV;
import com.virhuiai.jcef109.ui.*;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;
import org.cef.CefApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 应用启动类
 */
public class BrowserApp extends JFrame {
    private static final Log LOGGER = CshLogUtils.createLogExtended(BrowserApp.class); // 日志记录器
    // 序列化ID
    private static final long serialVersionUID = -5570653778104813836L;

    // CEF应用程序实例
    private final CefApp cefApp;

    // 主窗口框架
    public static JFrame mainFrame;

    // 标签页面板
    public static TabbedPane tabbedPane;

    /**
     * 构造函数
     * @param args 命令行参数
     */
    private BrowserApp(String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        // 创建CEF应用构建器
        CefAppBuilderV builder = new CefAppBuilderV();

        String passAllArgsToCef = CshCliUtils.s3GetOptionValue("passAllArgsToCef");
        if("1".equalsIgnoreCase(passAllArgsToCef)){
            // Pass all args to cef
            LOGGER.info("将全部参数传递给CEF");
            builder.getJcefArgs().addAll(Arrays.asList(args));
        }

        String proxyServer = CshCliUtils.s3GetOptionValue("proxyServer");
        if(null != proxyServer && !proxyServer.isEmpty()){
//            builder.getJcefArgs().add("--proxy-server=http://127.0.0.1:49408");// 按代理来
            LOGGER.info("使用代理:" + proxyServer);
            builder.getJcefArgs().add("--proxy-server=" + proxyServer);// 按代理来
            builder.getJcefArgs().add("--ignore-certificate-errors");// 禁用证书验证
        }

        String remoteDebuggingPort = CshCliUtils.s3GetOptionValue("remoteDebuggingPort");
        if(null != remoteDebuggingPort && !remoteDebuggingPort.isEmpty()){
            // 添加正则表达式检查，确保输入是纯数字
            if (remoteDebuggingPort.matches("^\\d+$")) {
                int port = Integer.parseInt(remoteDebuggingPort);
                // 检查端口范围是否有效（通常有效端口范围是1024-65535，但允许所有可能的端口）
                if (port > 0 && port < 65536) {
//                    builder.getCefSettings().remote_debugging_port = port;
                    builder.getJcefArgs().add("--remote-debugging-port="+port);
//                    builder.getJcefArgs().add("--remote-allow-origins=http://localhost:"+port);
//                    builder.getJcefArgs().add("--remote-allow-origins=http://127.0.0.1:"+port);
                    builder.getJcefArgs().add("--remote-allow-origins=*");

//                    builder.getCefSettings().remote_debugging_port = port;
                    LOGGER.info("启用Chrome远程调试，端口: " + port);//9222 --remoteDebuggingPort=9222
                }else {
                    LOGGER.warn("指定的远程调试端口超出有效范围(1-65535): " + port + "，远程调试将不会启用");
                }

            }else{
                LOGGER.error("启用Chrome远程调试失败，端口无效: " + remoteDebuggingPort);
            }

        }

        //设置华为镜像，加载比较快
//        builder.setMirrors(Arrays.asList("http://mirrors.huaweicloud.com/repository/maven/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar"));

        // --jcefInstallDir=/Volumes/THAWSPACE/CshProject/JCEF109/
        // 设置JCEF安装目录 jcefInstallDir
        String jcefInstallDir = CshCliUtils.s3GetOptionValue("jcefInstallDir");
        /**
         * 配置JCEF安装目录，并根据当前运行平台确定具体的bundle路径
         */
        if (null == jcefInstallDir || jcefInstallDir.isEmpty()) {
            // 如果未指定JCEF安装目录，记录错误并中断操作
            LOGGER.error("未指定JCEF安装目录【jcefInstallDir】，无法继续初始化JCEF");
            cefApp = null;
            return;
        }
        // 通过Paths工具类创建基础路径对象
        Path basePath = Paths.get(jcefInstallDir);
        // 根据当前操作系统平台动态拼接JCEF bundle目录
        // 例如：在Mac OS X上，EnumPlatform.getCurrentPlatform().getIdentifier()返回"macosx-amd64"
        // 最终会构建类似"jcef-bundle-macosx-amd64"的目录名
        Path jcefBundlePath = basePath.resolve("jcef-bundle-" + EnumPlatform.getCurrentPlatform().getIdentifier());
        // 记录JCEF配置信息，便于调试和问题排查
        LOGGER.info("JCEF安装父级目录【jcefInstallDir】：" + jcefInstallDir);
        LOGGER.info("JCEF安装实际目录【jcefBundlePath】: " +  jcefBundlePath);
        // 将解析好的JCEF安装目录设置到builder中
        // 注意：这里将Path对象转换为File对象，以兼容builder的API设计
        builder.setInstallDir(jcefBundlePath.toFile()); //"/Volumes/THAWSPACE/CshProject/JCEF109/jcef-bundle-macosx-amd64"
        // 关闭离屏渲染模式
        builder.getCefSettings().windowless_rendering_enabled = false;

        // 设置应用程序处理器
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                // 当窗口状态变为终止时退出程序(窗口关闭的时候)
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
            }
        });

        // 添加额外的JCEF参数
        if (args.length > 0) {
            builder.addJcefArgs(args);
        }

        // 构建CEF应用
        cefApp = builder.build();

        // 在Swing事件调度线程中初始化UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initAndDisplayUI();
            }
        });
        mainFrame = this;
    }

    /**
     *
     * jcefInstallDir:设置JCEF安装目录
     * defaultUrl:设置浏览器默认打开的URL
     * proxyServer:代理
     * passAllArgsToCef:
     * remoteDebuggingPort:启用Chrome远程调试并指定端口
     *
     * --jcefInstallDir=/Volumes/THAWSPACE/CshProject/JCEF109 --remoteDebuggingPort=9222
     *
     * --jcefInstallDir=/Volumes/THAWSPACE/CshProject/JCEF109 --defaultUrl=baidu.com --proxyServer=http://127.0.0.1:65201 --passAllArgsToCef=1 --remoteDebuggingPort=9222
     * 主方法
     */
    public static void main(String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        // 解析命令行参数
        CshCliUtils.s1InitializeArgs(args);

        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("jcefInstallDir")
                .desc("设置JCEF安装目录")
                .hasArg()
                .argName("JCEF安装目录")
                .build()));
        // CshCliUtils.s3GetOptionValue("jcefInstallDir");

        // 添加默认URL选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("defaultUrl")
                .desc("设置浏览器默认打开的URL")
                .hasArg()
                .argName("默认URL")
                .build()));

        // 添加是否传递全部参数到CEF的选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("passAllArgsToCef")
                .desc("是否将全部参数传递给CEF")
                .hasArg()
                .argName("true/false")
                .build()));
                // 获取该选项值: CshCliUtils.s3GetOptionValue("passAllArgsToCef");

        // 添加代理地址选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("proxyServer")
                .desc("设置浏览器代理服务器地址")
                .hasArg()
                .argName("代理地址")
                .build()));
        // 获取该选项值: CshCliUtils.s3GetOptionValue("proxyServer");

        // 添加是否打开Chrome调试端口的选项
//        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
//                .longOpt("enableRemoteDebugging")
//                .desc("是否启用Chrome远程调试功能")
//                .hasArg()
//                .argName("0/1")
//                .build()));

//// 添加Chrome调试端口设置选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("remoteDebuggingPort")
                .desc("设置Chrome远程调试端口号")
                .hasArg()
                .argName("端口号")
                .build()));

        new BrowserApp(args);
    }

    /**
     * 初始化并显示用户界面
     */
    private void initAndDisplayUI() {
        // 创建标签页面板
        tabbedPane = new TabbedPane();

        String defaultUrl = CshCliUtils.s3GetOptionValue("defaultUrl");
        if(null !=defaultUrl && !defaultUrl.isEmpty()){
            // 创建初始标签页并打开默认网页
            Tab tab = TabFactory.createTab(defaultUrl, cefApp);
            insertTab(tab);
        }


        // 插入新建标签页按钮
        insertNewTabButton();

        // 设置窗口标题
        this.setTitle("自定义浏览器");
        // 设置关闭操作
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // 添加窗口监听器
        this.addWindowListener(new WindowAdapter() {
            @SuppressWarnings("CallToSystemExit")
            @Override
            public void windowClosing(WindowEvent e) {
                // 关闭所有标签页
                tabbedPane.disposeAllTabs();
                System.exit(0);
            }
        });

        // 将标签页面板添加到窗口
        this.add(tabbedPane, BorderLayout.CENTER);
        this.pack();
        // 设置窗口大小
        this.setSize(1024, 700);
        // 设置窗口居中
        this.setLocationRelativeTo(null);
        // 设置窗口图标
        this.setIconImage(Resources.getIcon("browser16x16.png").getImage());
        // 显示窗口
        this.setVisible(true);

        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 释放CEF资源
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }

    /**
     * 插入新建标签页按钮
     */
    private void insertNewTabButton() {
        // 创建新建标签页按钮
        TabButton button = new TabButton(Resources.getIcon("new-tab.png"), "New tab");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // 点击时创建新标签页
                insertTab(TabFactory.createTab(cefApp));
            }
        });
        tabbedPane.addTabButton(button);
    }

    /**
     * 插入新标签页
     * @param tab 要插入的标签页
     */
    public static void insertTab(Tab tab) {
        // 添加标签页并选中它
        tabbedPane.addTab(tab);
        tabbedPane.selectTab(tab);
    }
}
