package com.virhuiai.jcef109.ui;

import org.cef.browser.CefBrowser;

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 开发者模式
 * 该类用于创建和管理开发者工具对话框
 * @author wanglei.
 */
public class DevToolsDialog extends JDialog {

    // 序列化ID / Serial version ID for serialization
    private static final long serialVersionUID = 6859581641415822180L;

    // 开发者工具浏览器实例 / Developer tools browser instance
    private final CefBrowser devTools_;

    /**
     * 构造函数,创建开发者工具对话框
     * @param owner 父窗口
     * @param title 对话框标题
     * @param browser 浏览器实例
     */
    public DevToolsDialog(Frame owner, String title, CefBrowser browser) {
        this(owner, title, browser, null);
    }

    /**
     * 构造函数,创建开发者工具对话框
     * @param owner 父窗口
     * @param title 对话框标题
     * @param browser 浏览器实例
     * @param inspectAt 检查位置点
     */
    public DevToolsDialog(Frame owner, String title, CefBrowser browser, Point inspectAt) {
        super(owner, title, false);

        // 设置布局为边界布局
        setLayout(new BorderLayout());
        // 设置对话框大小为800x600
        setSize(800, 600);
        // 设置对话框位置,相对于父窗口偏移20像素
        setLocation(owner.getLocation().x + 20, owner.getLocation().y + 20);

        // 获取开发者工具实例
        devTools_ = browser.getDevTools(inspectAt);
        // 将开发者工具UI组件添加到对话框
        add(devTools_.getUIComponent());

        // 添加组件监听器,处理窗口隐藏事件
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                dispose();
            }
        });
    }

    /**
     * 释放对话框资源
     * 重写dispose方法,关闭开发者工具并释放资源
     */
    @Override
    public void dispose() {
        devTools_.close(true);
        super.dispose();
    }
}