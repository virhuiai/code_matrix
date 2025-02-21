// 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
// TeamDev 专有和保密。
// 使用须遵守许可条款。

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import com.teamdev.jxbrowser.chromium.swing.DefaultDownloadHandler;
import com.teamdev.jxbrowser.chromium.swing.DefaultPopupHandler;
import com.teamdev.jxbrowser.chromium.Browser;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author TeamDev Ltd.
 */
public final class TabFactory {

    // 创建第一个标签页,默认打开百度
    public static Tab createFirstTab() {
//        return createTab("https://www.teamdev.com/jxbrowser");
        return createTab("https://www.baidu.com");
    }

    // 创建空白标签页
    public static Tab createTab() {
        return createTab("about:blank");
    }

    // 根据URL创建标签页
    public static Tab createTab(String url) {
        // 创建浏览器实例
        Browser browser = new Browser();
        // 创建浏览器视图
        BrowserView browserView = new BrowserView(browser);
        // 创建标签页内容
        TabContent tabContent = new TabContent(browserView);

        // 设置下载处理器
        browser.setDownloadHandler(new DefaultDownloadHandler(browserView));
        // 设置对话框处理器
        browser.setDialogHandler(new DefaultDialogHandler(browserView));
        // 设置弹窗处理器
        browser.setPopupHandler(new DefaultPopupHandler());

        // 创建标签页标题
        final TabCaption tabCaption = new TabCaption();
        tabCaption.setTitle("about:blank");

        // 添加标题改变的监听器
        tabContent.addPropertyChangeListener("PageTitleChanged", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                tabCaption.setTitle((String) evt.getNewValue());
            }
        });

        // 加载URL
        browser.loadURL(url);
        // 返回新创建的标签页
        return new Tab(tabCaption, tabContent);
    }
}
