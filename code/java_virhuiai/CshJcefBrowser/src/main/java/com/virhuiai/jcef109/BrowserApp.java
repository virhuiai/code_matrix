package com.virhuiai.jcef109;

import com.virhuiai.jcef109.jcef.CefAppBuilderV;
import com.virhuiai.jcef109.ui.Tab;
import com.virhuiai.jcef109.ui.TabButton;
import com.virhuiai.jcef109.ui.TabFactory;
import com.virhuiai.jcef109.ui.TabbedPane;
import com.virhuiai.jcef109.ui.Resources;
import me.friwi.jcefmaven.*;
import org.cef.CefApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 应用启动类
 */
public class BrowserApp extends JFrame {
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
        //设置华为镜像，加载比较快
        builder.setMirrors(Arrays.asList("http://mirrors.huaweicloud.com/repository/maven/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar"));

        // 设置JCEF安装目录
        builder.setInstallDir(new File("/Volumes/THAWSPACE/CshProject/JCEF109/jcef-bundle-macosx-amd64"));
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
     * 主方法
     */
    public static void main(String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        new BrowserApp(args);
    }

    /**
     * 初始化并显示用户界面
     */
    private void initAndDisplayUI() {
        // 创建标签页面板
        tabbedPane = new TabbedPane();
        // 创建初始标签页并打开默认网页
//        Tab tab = TabFactory.createTab("http://www.baidu.com/", cefApp);
//        insertTab(tab);
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
