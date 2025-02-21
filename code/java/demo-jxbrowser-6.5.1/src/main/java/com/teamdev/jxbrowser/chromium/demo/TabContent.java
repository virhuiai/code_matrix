/*
 * 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
 * TeamDev 专有和机密。
 * 使用需遵循许可条款。
 */

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author TeamDev Ltd.
 */
public class TabContent extends JPanel {

    private final BrowserView browserView;           // 浏览器视图组件
    private final ToolBar toolBar;                  // 工具栏
    private final JComponent jsConsole;             // JS控制台组件
    private final JComponent container;             // 主容器
    private final JComponent browserContainer;      // 浏览器容器

    public TabContent(final BrowserView browserView) {
        this.browserView = browserView;
        // 添加页面加载监听器
        this.browserView.getBrowser().addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    // 页面标题改变时触发事件
                    firePropertyChange("PageTitleChanged", null, TabContent.this.browserView.getBrowser().getTitle());
                }
            }
        });

        browserContainer = createBrowserContainer();
        jsConsole = createConsole();
        toolBar = createToolBar(browserView);

        // 创建主容器
        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        // 设置布局
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    // 创建工具栏
    private ToolBar createToolBar(BrowserView browserView) {
        ToolBar toolBar = new ToolBar(browserView);
        // 标签页关闭事件监听
        toolBar.addPropertyChangeListener("TabClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("TabClosed", false, true);
            }
        });
        // JS控制台显示事件监听
        toolBar.addPropertyChangeListener("JSConsoleDisplayed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                showConsole();
            }
        });
        // JS控制台关闭事件监听
        toolBar.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
            }
        });
        return toolBar;
    }

    // 隐藏控制台
    private void hideConsole() {
        showComponent(browserContainer);
    }

    // 显示指定组件
    private void showComponent(JComponent component) {
        container.removeAll();
        container.add(component, BorderLayout.CENTER);
        validate();
    }

    // 显示控制台
    private void showConsole() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(browserContainer, JSplitPane.TOP);
        splitPane.add(jsConsole, JSplitPane.BOTTOM);
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        showComponent(splitPane);
    }

    // 创建控制台
    private JComponent createConsole() {
        JS控制台 result = new JS控制台(browserView.getBrowser());
        result.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
                toolBar.didJSConsoleClose();
            }
        });
        return result;
    }

    // 创建浏览器容器
    private JComponent createBrowserContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(browserView, BorderLayout.CENTER);
        return container;
    }

    // 释放资源
    public void dispose() {
        browserView.getBrowser().dispose();
    }
}
