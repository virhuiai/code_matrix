
package com.virhuiai.jcef109.ui;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * 标签页内容类 - 用于管理浏览器标签页的内容显示
 */
public class TabContent extends JPanel {

    // 工具栏组件
    private final ToolBar toolBar;
    // 主容器组件
    private final JComponent container;
    // 浏览器容器组件
    private final JComponent browserContainer;

    // CEF客户端实例
    private final CefClient client;

    // CEF浏览器实例
    private final CefBrowser browser;

    /**
     * 构造函数
     * @param cefApp CEF应用程序实例
     * @param url 初始加载的URL
     */
    public TabContent(final CefApp cefApp, String url) {
        // 创建CEF客户端
        client = cefApp.createClient();
        // 创建浏览器实例
        browser = client.createBrowser(url, false, false);
        // 创建消息路由器
        CefMessageRouter msgRouter = CefMessageRouter.create();
        client.addMessageRouter(msgRouter);
        // 初始化浏览器容器
        browserContainer = createBrowserContainer();
        // 初始化工具栏
        toolBar = createToolBar();

        // 添加显示处理器，用于处理地址栏和标题变化
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                // 更新工具栏URL
                toolBar.setUrl(url);
            }

            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                // 触发页面标题改变事件
                firePropertyChange("PageTitleChanged", null, title);
            }
        });

        // 初始化主容器
        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        // 设置面板布局
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    /**
     * 创建工具栏
     * @return 工具栏实例
     */
    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar(client, browser);
        // 添加标签页关闭事件监听器
        toolBar.addPropertyChangeListener("TabClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("TabClosed", false, true);
            }
        });

        // 添加JS控制台关闭事件监听器
        toolBar.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
            }
        });
        return toolBar;
    }

    /**
     * 获取工具栏实例
     */
    public ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * 隐藏控制台
     */
    private void hideConsole() {
        showComponent(browserContainer);
    }

    /**
     * 显示指定组件
     * @param component 要显示的组件
     */
    private void showComponent(JComponent component) {
        container.removeAll();
        container.add(component, BorderLayout.CENTER);
        validate();
    }

    /**
     * 显示控制台
     */
    private void showConsole() {
        // 创建垂直分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(browserContainer, JSplitPane.TOP);
        // 设置分割比例
        splitPane.setResizeWeight(0.8);
        // 设置边框
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        showComponent(splitPane);
    }

    /**
     * 创建浏览器容器
     * @return 浏览器容器组件
     */
    private JComponent createBrowserContainer() {
        JPanel container = new JPanel(new BorderLayout());
        // 禁用浏览器组件的焦点
        browser.getUIComponent().setFocusable(false);
        container.add(browser.getUIComponent(), BorderLayout.CENTER);
        return container;
    }

    /**
     * 释放资源
     */
    public void dispose() {
        // 关闭浏览器
        browser.doClose();
        // 释放客户端资源
        client.dispose();
    }
}