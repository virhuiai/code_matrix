
package com.virhuiai.jcef109.ui;

// 导入必要的包 Imports required packages
import com.virhuiai.jcef109.BrowserApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * 工具栏类，实现浏览器的基本导航功能和控制面板
 */
public class ToolBar extends JPanel {
    // 常量定义 Constants definition
    private static final String RUN_JAVASCRIPT = "Run JavaScript..."; // 运行JavaScript
    private static final String CLOSE_JAVASCRIPT = "Close JavaScript Console"; // 关闭JavaScript控制台
    private static final String DEFAULT_URL = "about:blank"; // 默认URL

    // 导航按钮 Navigation buttons
    private JButton backwardButton; // 后退按钮
    private JButton forwardButton;  // 前进按钮
    private JButton refreshButton;  // 刷新按钮
    private JButton stopButton;     // 停止按钮
    private JMenuItem consoleMenuItem; // 控制台菜单项

    // 地址栏 Address bar
    private final JTextField addressBar;

    // CEF组件 CEF components
    private final CefClient client;
    private final CefBrowser browser;

    // 浏览器焦点状态 Browser focus state
    private boolean browserFocus = true;

    /**
     * 构造函数：初始化工具栏
     * @param client CEF客户端
     * @param browser CEF浏览器实例
     */
    public ToolBar(CefClient client, CefBrowser browser) {
        this.client = client;
        this.browser = browser;
        addressBar = createAddressBar(); // 创建地址栏

        // 设置布局为网格包布局
        setLayout(new GridBagLayout());

        // 添加组件到工具栏 Add components to toolbar
        add(createActionsPane(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(addressBar, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 0, 4, 5), 0, 0));
        add(createMenuButton(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    }

    /**
     * 设置URL地址
     * @param url 要设置的URL
     */
    public void setUrl(String url) {
        addressBar.setText(url);
    }

    /**
     * 处理JavaScript控制台关闭事件
     */
    public void didJSConsoleClose() {
        consoleMenuItem.setText(RUN_JAVASCRIPT);
    }

    /**
     * 创建导航按钮面板
     * @return 包含导航按钮的面板
     */
    private JPanel createActionsPane() {
        // 创建各个导航按钮
        backwardButton = createBackwardButton();
        forwardButton = createForwardButton();
        refreshButton = createRefreshButton();
        stopButton = createStopButton();

        // 创建面板并添加按钮
        JPanel actionsPanel = new JPanel();
        actionsPanel.add(backwardButton);
        actionsPanel.add(forwardButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(stopButton);
        return actionsPanel;
    }

    /**
     * 创建地址栏
     * @return 配置完成的地址栏文本框
     */
    private JTextField createAddressBar() {
        final JTextField address = new JTextField(DEFAULT_URL);

        // 添加焦点监听器，处理地址栏获得焦点的情况
        address.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!browserFocus) return;
                browserFocus = false;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                address.requestFocus();
            }
        });

        // 添加CEF焦点处理器
        client.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public void onGotFocus(CefBrowser browser) {
                if (browserFocus) return;
                browserFocus = true;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browser.setFocus(true);
            }

            @Override
            public void onTakeFocus(CefBrowser browser, boolean next) {
                browserFocus = false;
            }
        });

        // 添加地址栏动作监听器，处理URL输入
        address.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadURL(address.getText());
            }
        });

        // 添加页面加载处理器
        client.addLoadHandler(new CefLoadHandlerAdapter() {
            // 页面开始加载时的处理
            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                refreshButton.setEnabled(false);
                stopButton.setEnabled(true);
            }

            // 页面加载完成时的处理
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                refreshButton.setEnabled(true);
                stopButton.setEnabled(false);
                address.setText(browser.getURL());
                backwardButton.setEnabled(browser.canGoBack());
                forwardButton.setEnabled(browser.canGoForward());
            }

            // 页面加载出错时的处理
            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                stopButton.setEnabled(false);
                refreshButton.setEnabled(true);
            }
        });
        return address;
    }

    /**
     * 创建后退按钮
     * @return 后退按钮实例
     */
    private JButton createBackwardButton() {
        return createButton("Back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goBack();
            }
        });
    }

    /**
     * 创建前进按钮
     * @return 前进按钮实例
     */
    private JButton createForwardButton() {
        return createButton("Forward", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goForward();
            }
        });
    }

    /**
     * 创建刷新按钮
     * @return 刷新按钮实例
     */
    private JButton createRefreshButton() {
        return createButton("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.reload();
            }
        });
    }

    /**
     * 创建停止按钮
     * @return 停止按钮实例
     */
    private JButton createStopButton() {
        return createButton("Stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.stopLoad();
            }
        });
    }

    /**
     * 创建通用按钮
     * @param caption 按钮标题
     * @param action 按钮动作
     * @return 配置完成的按钮实例
     */
    private JButton createButton(String caption, Action action) {
        ActionButton button = new ActionButton(caption, action);
        String imageName = caption.toLowerCase();
        // 设置按钮图标和悬停图标
        button.setIcon(Resources.getIcon(imageName + ".png"));
        button.setRolloverIcon(Resources.getIcon(imageName + "-selected.png"));
        return button;
    }

    /**
     * 创建菜单按钮及其下拉菜单
     * @return 菜单按钮组件
     */
    private JComponent createMenuButton() {
        // 创建弹出菜单
        final JPopupMenu popupMenu = new JPopupMenu();

        // 添加各种菜单项
        popupMenu.add(createGetHTMLMenuItem());      // 获取HTML源码
        popupMenu.add(createDevToolsMenuItem());     // 开发者工具
        popupMenu.add(createUploadFileMenuItem());   // 文件上传
        popupMenu.add(createDownloadFileMenuItem()); // 文件下载
        popupMenu.add(createJavaScriptDialogsMenuItem()); // JavaScript对话框
        popupMenu.add(createPDFViewerMenuItem());    // PDF查看器
        popupMenu.add(createGoogleMapsMenuItem());   // Echarts示例
        popupMenu.add(createHTML5VideoMenuItem());   // HTML5视频
        popupMenu.add(createZoomInMenuItem());       // 放大
        popupMenu.add(createZoomOutMenuItem());      // 缩小
        popupMenu.add(createActualSizeMenuItem());   // 实际大小
        popupMenu.add(createPrintMenuItem());        // 打印
        popupMenu.addSeparator();                    // 分隔线
        popupMenu.addSeparator();                    // 分隔线
        popupMenu.add(createAboutMenuItem());        // 关于

        // 创建并配置菜单按钮
        final ActionButton button = new ActionButton("Preferences", null);
        button.setIcon(Resources.getIcon("gear.png"));

        // 添加鼠标事件监听器，处理菜单的显示和隐藏
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    // 左键点击显示菜单
                    popupMenu.show(e.getComponent(), 0, button.getHeight());
                } else {
                    // 其他情况隐藏菜单
                    popupMenu.setVisible(false);
                }
            }
        });
        return button;
    }

    /**
     * 创建打印菜单项
     * @return 打印菜单项组件
     */
    private Component createPrintMenuItem() {
        JMenuItem menuItem = new JMenuItem("打印");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.print();
            }
        });
        return menuItem;
    }

    /**
     * 创建复选框菜单项
     * @param title 菜单项标题
     * @param selected 初始选中状态
     * @param action 状态改变时的回调
     * @return 复选框菜单项
     */
    private static JCheckBoxMenuItem createCheckBoxMenuItem(String title, boolean selected, final CheckBoxMenuItemCallback action) {
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title, selected);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action.call(menuItem.isSelected());
            }
        });
        return menuItem;
    }

    /**
     * 创建还原大小菜单项
     * @return 还原大小菜单项组件
     */
    private Component createActualSizeMenuItem() {
        JMenuItem menuItem = new JMenuItem("还原");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.setZoomLevel(0.00); // 重置缩放级别为默认值
            }
        });
        return menuItem;
    }

    /**
     * 创建缩小菜单项
     * @return 缩小菜单项组件
     */
    private Component createZoomOutMenuItem() {
        JMenuItem menuItem = new JMenuItem("缩小");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double zoom = browser.getZoomLevel();
                if (zoom > -1) {
                    browser.setZoomLevel(zoom - 0.1); // 减小缩放级别
                }
            }
        });
        return menuItem;
    }

    /**
     * 创建放大菜单项
     * @return 放大菜单项组件
     */
    private Component createZoomInMenuItem() {
        JMenuItem menuItem = new JMenuItem("放大");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double zoom = browser.getZoomLevel();
                if (zoom < 1) {
                    browser.setZoomLevel(zoom + 0.1); // 增加缩放级别
                }
            }
        });
        return menuItem;
    }

    /**
     * 创建HTML5视频测试菜单项
     * @return 视频测试菜单项组件
     */
    private Component createHTML5VideoMenuItem() {
        JMenuItem menuItem = new JMenuItem("直播测试");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadURL("https://live.bilibili.com/"); // 加载B站直播页面
            }
        });
        return menuItem;
    }

    /**
     * 创建Echarts示例菜单项
     * @return Echarts示例菜单项组件
     */
    private Component createGoogleMapsMenuItem() {
        JMenuItem menuItem = new JMenuItem("Echarts演示");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadURL("https://echarts.apache.org/examples/zh/index.html"); // 加载Echarts示例页面
            }
        });
        return menuItem;
    }

    /**
     * 创建Vue支持测试菜单项
     * @return Vue支持菜单项组件
     */
    private Component createJavaScriptDialogsMenuItem() {
        JMenuItem menuItem = new JMenuItem("VUE支持");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadURL("https://element.eleme.cn/#/zh-CN/component/switch"); // 加载Element UI示例
            }
        });
        return menuItem;
    }

    /**
     * 创建文件下载测试菜单项
     * @return 文件下载菜单项组件
     */
    private Component createDownloadFileMenuItem() {
        JMenuItem menuItem = new JMenuItem("下载文件测试");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadURL("http://www.51xiazai.cn/soft/85579.htm"); // 加载下载测试页面
            }
        });
        return menuItem;
    }

    /**
     * 创建查看HTML源码菜单项
     * @return HTML源码菜单项组件
     */
    private Component createGetHTMLMenuItem() {
        JMenuItem menuItem = new JMenuItem("获取HTML");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.viewSource();// 查看页面源代码
            }
        });
        return menuItem;
    }

/**
     * 创建文件上传菜单项
     * Creates a menu item for file upload functionality
     * @return 返回配置好的文件上传菜单项 / Returns the configured upload file menu item
     */
    private JMenuItem createUploadFileMenuItem() {
        JMenuItem menuItem = new JMenuItem("Upload File"); // 文件上传菜单项的标签文本
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 加载文件上传示例页面 / Load the file upload example page
                browser.loadURL("http://www.cs.tut.fi/~jkorpela/forms/file.html#example");
            }
        });
        return menuItem;
    }

    /**
     * 创建开发者工具菜单项
     * Creates a menu item for developer tools
     * @return 返回配置好的开发者工具菜单项 / Returns the configured developer tools menu item
     */
    private JMenuItem createDevToolsMenuItem() {
        JMenuItem menuItem = new JMenuItem("开发者模式"); // 开发者工具菜单项的标签文本
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 创建新的开发者工具对话框实例 / Create new developer tools dialog instance
                DevToolsDialog devToolsDlg = new DevToolsDialog(
                    BrowserApp.mainFrame, // 父窗口 / Parent window
                    "开发者选项", // 对话框标题 / Dialog title
                    browser // 浏览器实例 / Browser instance
                );
                devToolsDlg.setVisible(true); // 显示开发者工具对话框 / Show the developer tools dialog
            }
        });
        return menuItem;
    }

    /**
     * 创建PDF查看器菜单项
     * Creates a menu item for PDF viewer
     * @return 返回配置好的PDF查看器菜单项 / Returns the configured PDF viewer menu item
     */
    private JMenuItem createPDFViewerMenuItem() {
        JMenuItem menuItem = new JMenuItem("PDF预览"); // PDF查看器菜单项的标签文本
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 加载PDF测试文件到浏览器中 / Load the PDF test file in the browser
                browser.loadURL("http://www.orimi.com/pdf-test.pdf");
            }
        });
        return menuItem;
    }

    /**
     * 创建关于对话框菜单项
     * Creates a menu item for about dialog
     * @return 返回配置好的关于对话框菜单项 / Returns the configured about dialog menu item
     */
    private JMenuItem createAboutMenuItem() {
        JMenuItem menuItem = new JMenuItem("关于我们"); // 关于菜单项的标签文本
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 获取工具栏所在的顶层窗口 / Get the top-level window containing the toolbar
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ToolBar.this);
                // 创建并显示关于对话框 / Create and show the about dialog
                AboutDialog aboutDialog = new AboutDialog(parentFrame);
                aboutDialog.setVisible(true);
            }
        });
        return menuItem;
    }

    /**
     * 判断是否需要设置地址栏焦点
     * Determines if the address bar needs focus
     * @return 如果地址栏为空或包含默认URL则返回true / Returns true if address bar is empty or contains default URL
     */
    private boolean isFocusRequired() {
        String url = addressBar.getText(); // 获取地址栏当前文本
        return url.isEmpty() || url.equals(DEFAULT_URL); // 检查URL是否为空或为默认值
    }

    /**
     * 重写组件添加通知方法
     * Override component addition notification method
     * 当组件被添加到容器时调用此方法 / This method is called when the component is added to a container
     */
    @Override
    public void addNotify() {
        super.addNotify(); // 调用父类的通知方法 / Call parent's notification method
        // 使用SwingUtilities确保在EDT线程中执行 / Use SwingUtilities to ensure execution in EDT
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isFocusRequired()) {
                    addressBar.requestFocus(); // 请求地址栏获取焦点
                    addressBar.selectAll(); // 选中地址栏所有文本
                }
            }
        });
    }

    /**
     * 自定义按钮类，用于创建特殊样式的工具栏按钮
     * Custom button class for creating specially styled toolbar buttons
     */
    private static class ActionButton extends JButton {
        /**
         * 构造函数
         * Constructor
         * @param hint 按钮提示文本 / Button tooltip text
         * @param action 按钮动作 / Button action
         */
        private ActionButton(String hint, Action action) {
            super(action);
            // 配置按钮的视觉外观 / Configure button's visual appearance
            setContentAreaFilled(false);     // 不填充按钮内容区域 / Don't fill button content area
            setBorder(BorderFactory.createEmptyBorder()); // 设置空边框 / Set empty border
            setBorderPainted(false);         // 不绘制边框 / Don't paint border
            setRolloverEnabled(true);        // 启用鼠标悬停效果 / Enable rollover effect
            setToolTipText(hint);            // 设置提示文本 / Set tooltip text
            setText(null);                   // 不显示文本 / Don't show text
            setFocusable(false);            // 设置不可获得焦点 / Set non-focusable
            setDefaultCapable(false);       // 设置不能作为默认按钮 / Set non-default capable
        }
    }

    /**
     * 复选框菜单项的回调接口
     * Callback interface for checkbox menu items
     */
    private interface CheckBoxMenuItemCallback {
        /**
         * 当复选框状态改变时调用此方法
         * This method is called when checkbox state changes
         * @param selected 复选框的选中状态 / Checkbox selected state
         */
        void call(boolean selected);
    }
}
