package com.virhuiai.log.jcef109.v2;

import org.cef.browser.CefBrowser;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * CEF浏览器开发者工具对话框
 *
 * 用途说明：
 * 该类继承自JFrame，用于创建和显示Chrome开发者工具（DevTools）窗口。
 * 开发者工具提供了网页调试功能，包括查看DOM结构、调试JavaScript、
 * 查看网络请求、分析性能等功能，类似于Chrome浏览器中按F12打开的开发者工具。
 *
 * 主要功能：
 * 1. 创建独立的开发者工具窗口
 * 2. 支持在指定位置检查元素
 * 3. 管理开发者工具的生命周期
 * 4. 自动处理窗口关闭事件
 */
public class CefVDebugToolDialog extends JFrame {
    // 开发者工具的浏览器实例，用于显示和管理开发者工具界面
    private final CefBrowser devTools_;

    /**
     * 构造函数（不指定检查位置）
     *
     * 创建一个开发者工具对话框，显示整个页面的开发者工具
     *
     * @param owner 父窗口，用于设置对话框的所有者
     * @param title 对话框标题，显示在窗口标题栏
     * @param browser 要调试的目标浏览器实例
     *
     * TODO: 考虑添加默认标题，如"开发者工具 - " + browser.getURL()
     */
    public CefVDebugToolDialog(Frame owner, String title, CefBrowser browser) {
        // 调用重载的构造函数，inspectAt参数传null表示不指定检查位置
        this(owner, title, browser, null);
    }

    /**
     * 构造函数（指定检查位置）
     *
     * 创建一个开发者工具对话框，可以在指定坐标位置检查元素
     *
     * @param owner 父窗口，用于设置对话框的所有者
     * @param title 对话框标题
     * @param browser 要调试的目标浏览器实例
     * @param inspectAt 要检查的元素位置坐标，如果为null则显示整个页面的开发者工具
     *
     * TODO: 考虑添加参数验证，确保browser不为null
     * TODO: 考虑设置最小窗口大小，避免开发者工具显示不完整
     */
    public CefVDebugToolDialog(Frame owner, String title, CefBrowser browser, Point inspectAt) {
        // 调用父类构造函数，设置窗口标题
        super(title);

        // 设置布局管理器为BorderLayout，这是最适合单一组件填充的布局
        setLayout(new BorderLayout());

        // 设置窗口默认大小为800x600像素
        // TODO: 考虑根据屏幕大小自适应或记住用户上次的窗口大小
        setSize(800, 600);

        // 设置窗口始终在最前面，确保开发者工具不会被其他窗口遮挡
        // TODO: 考虑让用户可以选择是否置顶
        setAlwaysOnTop(true);

        // 获取开发者工具的浏览器实例
        // 如果inspectAt不为null，将在指定位置检查元素
        this.devTools_ = browser.getDevTools(inspectAt);

        // 将开发者工具的UI组件添加到对话框中，填充整个窗口
        add(this.devTools_.getUIComponent());

        // 显示对话框
        // TODO: 考虑设置窗口位置，如居中显示或相对于父窗口定位
        setVisible(true);

        // 添加组件监听器，监听窗口隐藏事件
        addComponentListener(new ComponentAdapter() {
            /**
             * 当窗口被隐藏时触发
             *
             * 确保在窗口隐藏时正确释放资源
             *
             * @param e 组件事件对象
             */
            public void componentHidden(ComponentEvent e) {
                // 当窗口隐藏时，调用dispose方法释放资源
                CefVDebugToolDialog.this.dispose();
            }
        });
    }

    /**
     * 释放资源并关闭对话框
     *
     * 重写JFrame的dispose方法，确保在关闭对话框时
     * 同时关闭开发者工具并释放相关资源
     *
     * TODO: 考虑添加try-catch处理可能的异常
     * TODO: 考虑在关闭前保存开发者工具的状态（如断点、设置等）
     */
    @Override
    public void dispose() {
        // 先关闭开发者工具浏览器实例，释放CEF相关资源
        this.devTools_.close(true);// todo 后面看是不是弄个继承的。。。

        // 调用父类的dispose方法，释放窗口资源
        super.dispose();
    }
}

