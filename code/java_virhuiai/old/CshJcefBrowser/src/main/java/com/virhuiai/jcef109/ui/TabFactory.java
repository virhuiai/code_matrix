

package com.virhuiai.jcef109.ui;

import com.virhuiai.jcef109.BrowserApp;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author wanglei
 * 标签页工厂类,用于创建新的浏览器标签页
 */
public final class TabFactory {

    /**
     * 创建一个空白标签页
     * @param cefApp CEF应用程序实例
     * @return 新创建的标签页
     */
    public static Tab createTab(CefApp cefApp) {
        return createTab("about:blank",cefApp);
    }

    /**
     * 创建一个指定URL的标签页
     * @param url 要打开的URL
     * @param cefApp CEF应用程序实例
     * @return 新创建的标签页
     */
    public static Tab createTab(String url,CefApp cefApp) {
        // 创建CEF客户端
        CefClient client = cefApp.createClient();

        //尝试处理打开新窗口问题 Try to handle new window opening
        client.addLifeSpanHandler(new LifeSpanHandler(cefApp));

        // (3) Create a simple message router to receive messages from CEF.
        // 创建一个简单的消息路由器来接收来自CEF的消息
        CefMessageRouter msgRouter = CefMessageRouter.create();
        client.addMessageRouter(msgRouter);

        // 创建标签页内容
        TabContent tabContent = new TabContent(cefApp,url);

        // 创建标签页标题
        final TabCaption tabCaption = new TabCaption();
        tabCaption.setTitle("about:blank");

        // 添加页面标题变更监听器
        tabContent.addPropertyChangeListener("PageTitleChanged", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                tabCaption.setTitle((String) evt.getNewValue());
            }
        });

        // 设置工具栏URL
        tabContent.getToolBar().setUrl(url);

        // 返回新创建的标签页
        return new Tab(tabCaption, tabContent);
    }
}

/**
 * 打开新窗口处理 Handle new window opening
 */
class LifeSpanHandler extends CefLifeSpanHandlerAdapter {

    // CEF应用程序实例
    private CefApp cefApp;

    /**
     * 构造函数
     * @param cefApp CEF应用程序实例
     */
    LifeSpanHandler(CefApp cefApp){
        this.cefApp = cefApp;
    }

    /**
     * 在弹出新窗口前调用
     * @param browser 当前浏览器实例
     * @param frame 当前框架
     * @param target_url 目标URL
     * @param target_frame_name 目标框架名称
     * @return true表示取消弹出窗口，false表示允许弹出窗口
     */
    @Override
    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
        //返回true表示取消弹出窗口 Return true to cancel popup window
        BrowserApp.insertTab(TabFactory.createTab(target_url, cefApp));
        return true;
    }
}