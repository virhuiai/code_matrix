/*
 * 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
 * TeamDev 专有和机密。
 * 使用需遵循许可条款。
 */

package com.teamdev.jxbrowser.chromium.demo;

// 导入必要的包
import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author TeamDev Ltd.
 */
public class JxBrowserDemo {
    // 初始化环境设置
    private static void initEnvironment() throws Exception {
        // 设置Mac系统菜单栏
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        // 设置Mac应用程序名称
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Chrome49Demo");
        // 设置系统外观和感觉
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // 禁用轻量级弹出菜单
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    }


    // 初始化并显示用户界面
    private static void initAndDisplayUI() {
        // 创建标签页面板
        final TabbedPane tabbedPane = new TabbedPane();
        // 插入第一个标签页
        insertTab(tabbedPane, TabFactory.createFirstTab());
        // 插入新建标签页按钮
        insertNewTabButton(tabbedPane);

        // 创建主窗口
        JFrame frame = new JFrame("我的Chrome49");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // 添加窗口关闭监听器
        frame.addWindowListener(new WindowAdapter() {
            @SuppressWarnings("CallToSystemExit")
            @Override
            public void windowClosing(WindowEvent e) {
                tabbedPane.disposeAllTabs();
                // 如果是Mac系统，则退出程序
                if (Environment.isMac()) {
                    System.exit(0);
                }
            }
        });
        // 设置窗口属性
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(1024, 700);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/jxbrowser16x16.png").getImage());
        frame.setVisible(true);
    }

    // 插入新建标签页按钮
    private static void insertNewTabButton(final TabbedPane tabbedPane) {
        TabButton button = new TabButton(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/new-tab.png"), "新建标签页");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                insertTab(tabbedPane, TabFactory.createTab());
            }
        });
        tabbedPane.addTabButton(button);
    }

    // 插入标签页
    private static void insertTab(TabbedPane tabbedPane, Tab tab) {
        tabbedPane.addTab(tab);
        tabbedPane.selectTab(tab);
    }


    // 主函数
    public static void main(String[] args) throws Exception {
        initEnvironment();
        // 在Swing事件调度线程中执行UI初始化
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initAndDisplayUI();
            }
        });
    }
}