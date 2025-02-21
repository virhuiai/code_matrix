// 版权信息
/*
 * 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
 * TeamDev 专有和保密。
 * 使用须遵守许可条款。
 */

package com.teamdev.jxbrowser.chromium.demo;

// 导入所需的类

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.EditorCommand;
import com.teamdev.jxbrowser.chromium.SavePageType;
import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * 工具栏类,继承自JPanel
 */
public class ToolBar extends JPanel {
    // 常量定义
    private static final String RUN_JAVASCRIPT = "运行JavaScript...";
    private static final String CLOSE_JAVASCRIPT = "关闭JavaScript控制台";
    private static final String 默认URL = "about:blank";

    // UI组件
    private JButton 后退按钮;    // 后退按钮
    private JButton 前进按钮;     // 前进按钮
    private JButton 刷新按钮;     // 刷新按钮
    private JButton 停止按钮;        // 停止按钮
    private JMenuItem 控制台菜单项; // 控制台菜单项

    private final JTextField 地址栏; // 地址栏
    private final BrowserView 浏览器视图; // 浏览器视图

    /**
     * 工具栏构造方法
     *
     * @param browserView 浏览器视图组件
     *                    <p>
     *                    主要特点:
     *                    <p>
     *                    1. 使用GridBagLayout进行布局,这是Swing中最灵活的布局管理器
     *                    2. 工具栏包含三个主要部分:
     *                    - 导航按钮区域(左)
     *                    - 地址栏(中,可扩展)
     *                    - 菜单按钮(右)
     *                    3. 通过GridBagConstraints精确控制每个组件的:
     *                    - 位置和跨度
     *                    - 对齐方式
     *                    - 填充方式
     *                    - 扩展行为
     *                    - 边距
     *                    4. 地址栏获得水平方向的扩展权重,会自动填充可用空间
     *                    <p>
     *                    这样的布局可以实现一个标准的浏览器工具栏外观,并能够随窗口大小变化自适应调整。
     */
    public ToolBar(BrowserView browserView) {
        // 保存浏览器视图引用
        this.浏览器视图 = browserView;

        // 创建地址栏
        地址栏 = createAddressBar();

        // 设置网格包布局管理器
        setLayout(new GridBagLayout());

        // 添加导航按钮区域(后退、前进、刷新、停止)
        add(createActionsPane(), new GridBagConstraints(
                0, 0,                    // 网格位置 x,y
                1, 1,                    // 网格跨度 width,height
                0.0, 0.0,               // 权重 weightx,weighty
                GridBagConstraints.WEST, // 组件对齐方式
                GridBagConstraints.NONE, // 填充方式
                new Insets(0, 0, 0, 0),  // 内边距
                0, 0                     // 扩展尺寸 ipadx,ipady
        ));

        // 添加地址栏
        add(地址栏, new GridBagConstraints(
                1, 0,                     // 位置在导航按钮右侧
                1, 1,                     // 占据1x1网格
                1.0, 0.0,                // 水平方向可扩展
                GridBagConstraints.WEST,  // 靠左对齐
                GridBagConstraints.BOTH,  // 水平垂直填充
                new Insets(4, 0, 4, 5),   // 上下留间距
                0, 0
        ));

        // 添加菜单按钮
        add(createMenuButton(), new GridBagConstraints(
                2, 0,                        // 位置在地址栏右侧
                1, 1,                        // 占据1x1网格
                0.0, 0.0,                    // 不可扩展
                GridBagConstraints.LINE_END,  // 靠右对齐
                GridBagConstraints.HORIZONTAL,// 水平填充
                new Insets(0, 0, 0, 5),      // 右侧留间距
                0, 0
        ));
    }

    // 控制台关闭时的处理方法
    public void didJSConsoleClose() {
        // 将菜单项文本设置为"运行JavaScript"
        控制台菜单项.setText(RUN_JAVASCRIPT);
    }

    // 创建导航按钮面板
    private JPanel createActionsPane() {
        // 创建浏览器导航按钮
        后退按钮 = createBackwardButton(浏览器视图.getBrowser());  // 后退按钮
        前进按钮 = createForwardButton(浏览器视图.getBrowser());    // 前进按钮
        刷新按钮 = createRefreshButton(浏览器视图.getBrowser());    // 刷新按钮
        停止按钮 = createStopButton(浏览器视图.getBrowser());         // 停止按钮

// 创建面板并添加按钮
        JPanel actionsPanel = new JPanel();
        actionsPanel.add(后退按钮);  // 添加后退按钮
        actionsPanel.add(前进按钮);   // 添加前进按钮
        actionsPanel.add(刷新按钮);   // 添加刷新按钮
        actionsPanel.add(停止按钮);      // 添加停止按钮
        return actionsPanel; // 返回包含所有导航按钮的面板
    }

    // 创建地址栏

    /**
     * 1. 这是一个创建浏览器地址栏的方法，返回一个JTextField组件。
     * 2. 主要功能包括：
     * - 创建地址栏并设置默认URL
     * - 监听用户在地址栏的输入操作
     * - 监听浏览器的加载状态变化
     * 3. 具体实现了三个主要事件处理：
     * - 页面开始加载时：禁用刷新按钮，启用停止按钮
     * - 临时加载状态：更新地址栏显示的URL
     * - 页面加载完成时：启用刷新按钮，禁用停止按钮，更新前进/后退按钮状态
     * 4. 使用SwingUtilities.invokeLater确保UI更新操作在事件调度线程中执行，避免线程安全问题。
     * 5. 所有UI状态更新都是根据浏览器的实际状态来进行的，比如前进/后退按钮的启用状态取决于浏览历史记录。
     * <p>
     * 这段代码是典型的Swing应用程序中浏览器控件的实现，采用了事件驱动的编程模式。
     *
     * @return
     */
    private JTextField createAddressBar() {
        // 创建文本输入框并设置默认URL
        final JTextField 地址栏 = new JTextField(默认URL);

        // 添加地址栏输入事件监听器
        地址栏.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL(地址栏.getText());
            }
        });

        // 添加浏览器加载事件监听器
        浏览器视图.getBrowser().addLoadListener(new LoadAdapter() {
            // 开始加载页面时触发
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            刷新按钮.setEnabled(false);
                            停止按钮.setEnabled(true);
                        }
                    });
                }
            }

            // 临时加载状态时触发
            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                if (event.isMainFrame()) {
                    地址栏.setText(event.getURL());
                    地址栏.setCaretPosition(地址栏.getText().length());
                }
            }

            // 页面加载完成时触发
            @Override
            public void onFinishLoadingFrame(final FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            刷新按钮.setEnabled(true);
                            停止按钮.setEnabled(false);

                            Browser 浏览器 = event.getBrowser();
                            final boolean 可前进 = 浏览器.canGoForward();
                            final boolean 可后退 = 浏览器.canGoBack();

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    前进按钮.setEnabled(可前进);
                                    后退按钮.setEnabled(可后退);
                                }
                            });
                        }
                    });
                }
            }
        });
        return 地址栏;
    }


    /**
     * 1. 这段代码实现了浏览器导航栏的四个基本按钮：
     * - 后退按钮（Back）
     * - 前进按钮（Forward）
     * - 刷新按钮（Refresh）
     * - 停止按钮（Stop）
     * 2. 每个按钮的创建方法：
     * - 都使用了共同的 `createButton` 方法
     * - 接收一个 Browser 对象作为参数
     * - 使用 AbstractAction 来定义按钮的点击行为
     * 3. `createButton` 方法的功能：
     * - 创建一个 ActionButton 实例
     * - 设置按钮的基本图标
     * - 设置鼠标悬停时的图标
     * - 图标文件命名规则：
     * - 普通状态：`[按钮名].png`
     * - 选中状态：`[按钮名]-selected.png`
     * 4. 按钮功能说明：
     * - 后退按钮：调用 `browser.goBack()` 实现页面后退
     * - 前进按钮：调用 `browser.goForward()` 实现页面前进
     * - 刷新按钮：调用 `browser.reload()` 重新加载当前页面
     * - 停止按钮：调用 `browser.stop()` 停止页面加载
     * 5. 按钮外观特点：
     * - 每个按钮都有文字说明
     * - 包含默认图标和悬停状态图标
     * - 使用 ActionButton 自定义按钮类实现
     * <p>
     * 这是一个典型的浏览器导航控制界面实现，采用了面向对象的设计方法，将共同的按钮创建逻辑抽象到单独的方法中，使代码更加简洁和易于维护。
     *
     * @param browser
     * @return
     */
    private static JButton createBackwardButton(final Browser browser) {
        return createButton("Back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goBack();
            }
        });
    }

    private static JButton createForwardButton(final Browser browser) {
        return createButton("Forward", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goForward();
            }
        });
    }

    private static JButton createRefreshButton(final Browser browser) {
        return createButton("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.reload();
            }
        });
    }

    private static JButton createStopButton(final Browser browser) {
        return createButton("Stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.stop();
            }
        });
    }

    private static JButton createButton(String caption, Action action) {
        动作按钮 button = new 动作按钮(caption, action);
        String imageName = caption.toLowerCase();
        button.setIcon(Resources.getIcon(imageName + ".png"));
        button.setRolloverIcon(Resources.getIcon(imageName + "-selected.png"));
        return button;
    }

    /**
     * 码解释：
     * <p>
     * 1. `createMenuButton()` 方法：
     * - 创建一个弹出式菜单（JPopupMenu）
     * - 向菜单中添加各种功能选项
     * - 创建一个带有齿轮图标的"首选项"按钮
     * - 为按钮添加鼠标事件监听器，处理左键点击显示菜单的逻辑
     * 2. 按钮的鼠标事件处理：
     * - 左键点击时（BUTTON1_MASK），在按钮下方显示弹出菜单
     * - 点击其他按钮时，隐藏弹出菜单
     *
     * @return
     */
    private JComponent createMenuButton() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createConsoleMenuItem());            // 添加控制台菜单项
        popupMenu.add(创建获取HTML菜单项());           // 添加获取HTML菜单项
        popupMenu.add(createPopupsMenuItem());            // 添加弹出窗口菜单项
        popupMenu.add(createUploadFileMenuItem());        // 添加上传文件菜单项
        popupMenu.add(创建文件下载菜单项());      // 添加下载文件菜单项
        popupMenu.add(创建JavaScript对话框菜单项()); // 添加JavaScript对话框菜单项
        popupMenu.add(createPDFViewerMenuItem());         // 添加PDF查看器菜单项
        popupMenu.add(创建Flash菜单项());             // 添加Flash菜单项
        popupMenu.add(创建Google地图菜单项());        // 添加谷歌地图菜单项
        popupMenu.add(创建HTML5视频菜单项());        // 添加HTML5视频菜单项
        popupMenu.add(创建放大菜单项());            // 添加放大菜单项
        popupMenu.add(创建缩小菜单项());           // 添加缩小菜单项
        popupMenu.add(创建实际大小菜单项());        // 添加实际大小菜单项
        popupMenu.add(创建保存网页菜单项());       // 添加保存网页菜单项
        popupMenu.add(创建清除缓存菜单项());        // 添加清除缓存菜单项
        popupMenu.add(createPreferencesSubMenu());        // 添加首选项子菜单
        popupMenu.add(createExecuteCommandSubMenu());     // 添加执行命令子菜单
        popupMenu.add(createPrintMenuItem());             // 添加打印菜单项
        popupMenu.addSeparator();                         // 添加分隔线
        popupMenu.add(创建更多功能菜单项());              // 添加更多菜单项
        popupMenu.addSeparator();                         // 添加分隔线
        popupMenu.add(创建关于菜单项());             // 添加关于菜单项

        // 创建设置按钮
        final 动作按钮 button = new 动作按钮("首选项", null);
        button.setIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/gear.png"));

        // 添加鼠标事件监听器
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    // 左键点击时显示弹出菜单
                    popupMenu.show(e.getComponent(), 0, button.getHeight());
                } else {
                    // 其他键点击时隐藏弹出菜单
                    popupMenu.setVisible(false);
                }
            }
        });
        return button;
    }

    // 创建打印菜单项
    private Component createPrintMenuItem() {
        JMenuItem menuItem = new JMenuItem("打印...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().print();  // 执行浏览器打印功能
            }
        });
        return menuItem;
    }

    /**
     * 创建首选项子菜单
     *
     * @return 包含各种浏览器设置选项的菜单组件
     * <p>
     * 1. 这个方法创建了一个浏览器首选项的下拉菜单，包含多个可配置的选项。
     * 2. 每个选项都是一个复选框菜单项，使用`createCheckBoxMenuItem`方法创建，包含：
     * - 选项名称（显示文本）
     * - 当前状态（是否选中）
     * - 回调函数（当选项状态改变时执行）
     * 3. 包含的设置选项：
     * - JavaScript启用/禁用
     * - 图片加载启用/禁用
     * - 插件启用/禁用
     * - JavaScript访问剪贴板权限
     * - JavaScript自动打开窗口权限
     * - Web音频启用/禁用
     * 4. 每个选项的回调函数都遵循相同的模式：
     * - 获取当前浏览器首选项
     * - 更新相应的设置
     * - 将更新后的首选项应用到浏览器
     * - 强制刷新浏览器以使设置生效
     * 5. `browserView`是类的成员变量，代表浏览器视图实例。
     * 6. 所有更改都会立即生效，因为每次修改后都会调用`reloadIgnoringCache()`方法重新加载页面。
     * <p>
     * 这是一个典型的设置菜单实现，使用了Java Swing界面组件，属于JxBrowser框架的一部分，用于控制浏览器的各种行为和功能。
     */
    private Component createPreferencesSubMenu() {
        JMenu menu = new JMenu("首选项");
        BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();

        // 添加JavaScript启用选项
        menu.add(创建复选框菜单项("启用JavaScript",
                preferences.isJavaScriptEnabled(),
                new 复选菜单回调() {
                    public void 调用(boolean selected) {
                        BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                        preferences.setJavaScriptEnabled(selected);
                        浏览器视图.getBrowser().setPreferences(preferences);
                        浏览器视图.getBrowser().reloadIgnoringCache();
                    }
                }));
        // 添加图片启用选项
        menu.add(创建复选框菜单项("启用图片", preferences.isImagesEnabled(), new 复选菜单回调() {
            public void 调用(boolean selected) {
                BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                preferences.setImagesEnabled(selected);
                浏览器视图.getBrowser().setPreferences(preferences);
                浏览器视图.getBrowser().reloadIgnoringCache();
            }
        }));
        menu.add(创建复选框菜单项("启用插件", preferences.isPluginsEnabled(), new 复选菜单回调() {
            public void 调用(boolean selected) {
                BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                preferences.setPluginsEnabled(selected);
                浏览器视图.getBrowser().setPreferences(preferences);
                浏览器视图.getBrowser().reloadIgnoringCache();
            }
        }));
        // 添加JavaScript访问剪贴板权限选项
        menu.add(创建复选框菜单项("允许JavaScript访问剪贴板", preferences.isJavaScriptCanAccessClipboard(), new 复选菜单回调() {
            public void 调用(boolean selected) {
                BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                preferences.setJavaScriptCanAccessClipboard(selected);
                浏览器视图.getBrowser().setPreferences(preferences);
                浏览器视图.getBrowser().reloadIgnoringCache();
            }
        }));
        // 添加JavaScript打开窗口权限选项
        menu.add(创建复选框菜单项("允许JavaScript自动打开窗口", preferences.isJavaScriptCanOpenWindowsAutomatically(), new 复选菜单回调() {
            public void 调用(boolean selected) {
                BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                preferences.setJavaScriptCanOpenWindowsAutomatically(selected);
                浏览器视图.getBrowser().setPreferences(preferences);
                浏览器视图.getBrowser().reloadIgnoringCache();
            }
        }));
        // 添加Web音频启用选项
        menu.add(创建复选框菜单项("启用Web音频", preferences.isWebAudioEnabled(), new 复选菜单回调() {
            public void 调用(boolean selected) {
                BrowserPreferences preferences = 浏览器视图.getBrowser().getPreferences();
                preferences.setWebAudioEnabled(selected);
                浏览器视图.getBrowser().setPreferences(preferences);
                浏览器视图.getBrowser().reloadIgnoringCache();
            }
        }));
        return menu;
    }

    /**
     * 创建复选框菜单项方法（createCheckBoxMenuItem）：
     * <p>
     * - 参数：
     * - 标题：菜单项显示的文本
     * - 是否选中：菜单项的初始选中状态
     * - 动作：当选中状态改变时要执行的回调函数
     * - 功能：
     * - 创建一个带复选框的菜单项
     * - 设置点击监听器
     * - 当用户点击时触发回调函数
     * - 返回创建好的菜单项
     *
     * @param title
     * @param selected
     * @param action
     * @return
     */
    private static JCheckBoxMenuItem 创建复选框菜单项(String title, boolean selected, final 复选菜单回调 action) {
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title, selected);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action.调用(menuItem.isSelected());
            }
        });
        return menuItem;
    }

    /**
     * 创建清除缓存菜单项方法（createClearCacheMenuItem）：
     * <p>
     * - 功能：
     * - 创建一个普通菜单项，用于清除浏览器缓存
     * - 设置点击监听器
     * - 点击时调用浏览器的清除缓存功能
     * - 清除完成后显示成功提示对话框
     * - 使用了：
     * - JMenuItem：菜单项组件
     * - ActionListener：动作监听器
     * - JOptionPane：消息对话框
     * - Callback：异步操作回调
     * <p>
     * 这两个方法都是用于创建菜单项的工具方法，通常用在浏览器应用程序的菜单栏中。第一个方法创建可切换的选项，第二个方法创建执行特定功能（清除缓存）的命令项。
     *
     * @return
     */
    private Component 创建清除缓存菜单项() {
        JMenuItem menuItem = new JMenuItem("清除缓存");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().getCacheStorage().clearCache(new Callback() {
                    public void invoke() {
                        JOptionPane.showMessageDialog(浏览器视图, "缓存已成功清除。",
                                "清除缓存", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        });
        return menuItem;
    }

    /**
     * 创建执行命令子菜单
     *
     * @return 返回创建好的菜单组件
     */
    private Component createExecuteCommandSubMenu() {
        // 创建一个名为"执行命令"的菜单
        final JMenu menu = new JMenu("执行命令");

        // 为菜单添加监听器
        menu.addMenuListener(new MenuListener() {
            // 当菜单被选中时触发
            public void menuSelected(MenuEvent e) {
                // 获取菜单中的所有子项
                Component[] menuItems = menu.getMenuComponents();
                // 遍历所有菜单项
                for (Component menuItem : menuItems) {
                    // 根据浏览器当前状态设置菜单项的启用/禁用状态
                    menuItem.setEnabled(浏览器视图.getBrowser().isCommandEnabled(
                            ((CommandMenuItem) menuItem).getCommand()));
                }
            }

            // 当菜单取消选中时触发
            public void menuDeselected(MenuEvent e) {
            }

            // 当菜单被取消时触发
            public void menuCanceled(MenuEvent e) {
            }
        });

        // 添加各种编辑命令的菜单项
        menu.add(创建执行命令子菜单项("剪切", EditorCommand.CUT));
        menu.add(创建执行命令子菜单项("复制", EditorCommand.COPY));
        menu.add(创建执行命令子菜单项("粘贴", EditorCommand.PASTE));
        menu.add(创建执行命令子菜单项("全选", EditorCommand.SELECT_ALL));
        menu.add(创建执行命令子菜单项("取消选择", EditorCommand.UNSELECT));
        menu.add(创建执行命令子菜单项("撤销", EditorCommand.UNDO));
        menu.add(创建执行命令子菜单项("重做", EditorCommand.REDO));
        menu.add(创建执行命令子菜单项("插入文本...", "插入文本", EditorCommand.INSERT_TEXT));
        menu.add(创建执行命令子菜单项("查找文本...", "查找文本", EditorCommand.FIND_STRING));
        return menu;
    }

    /**
     * 创建执行命令的子菜单项
     *
     * @param commandName 命令名称
     * @param command     编辑器命令
     * @return 返回创建好的菜单项组件
     */
    private Component 创建执行命令子菜单项(final String commandName, final EditorCommand command) {
        // 创建一个命令菜单项
        final CommandMenuItem menuItem = new CommandMenuItem(commandName, command);
        // 为菜单项添加动作监听器
        menuItem.addActionListener(new ActionListener() {
            // 当菜单项被点击时执行对应的命令
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().executeCommand(command);
            }
        });
        return menuItem;
    }

    /**
     * `创建执行命令子菜单项`：
     * <p>
     * - 创建一个可执行特定命令的菜单项
     * - 点击后弹出输入对话框，让用户输入命令值
     * - 执行浏览器命令并传入用户输入的值
     *
     * @param 命令名称
     * @param 对话框标题
     * @param 命令
     * @return
     */
    private Component 创建执行命令子菜单项(final String 命令名称, final String 对话框标题, final EditorCommand 命令) {
        final CommandMenuItem 菜单项 = new CommandMenuItem(命令名称, 命令);
        菜单项.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String 值 = JOptionPane.showInputDialog(浏览器视图, "命令值:", 对话框标题, JOptionPane.PLAIN_MESSAGE);
                浏览器视图.getBrowser().executeCommand(命令, 值);
            }
        });
        return 菜单项;
    }

    /**
     * `创建更多功能菜单项`：
     * <p>
     * - 创建一个"更多功能"菜单项
     * - 点击后在浏览器中打开指定的支持文档URL
     *
     * @return
     */
    private Component 创建更多功能菜单项() {
        JMenuItem 菜单项 = new JMenuItem("更多功能...");
        菜单项.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("https://jxbrowser.support.teamdev.com/support/solutions/9000049010");
            }
        });
        return 菜单项;
    }

    /**
     * `创建保存网页菜单项`：
     * <p>
     * - 创建一个"保存网页"菜单项
     * - 点击后打开文件保存对话框
     * - 默认文件名为"我的网页.html"
     * - 用户选择保存位置后，将完整的HTML页面保存到指定位置
     * - 同时会在相同目录下创建资源文件夹保存页面相关资源
     *
     * @return
     */
    private Component 创建保存网页菜单项() {
        JMenuItem 菜单项 = new JMenuItem("保存网页...");
        菜单项.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser 文件选择器 = new JFileChooser();
                文件选择器.setSelectedFile(new File("我的网页.html"));
                int 结果 = 文件选择器.showSaveDialog(浏览器视图);
                if (结果 == JFileChooser.APPROVE_OPTION) {
                    File 选中文件 = 文件选择器.getSelectedFile();
                    String 目录路径 = new File(选中文件.getParent(), "com/teamdev/jxbrowser/chromium/demo/resources").getAbsolutePath();
                    浏览器视图.getBrowser().saveWebPage(选中文件.getAbsolutePath(), 目录路径, SavePageType.COMPLETE_HTML);
                }
            }
        });
        return 菜单项;
    }

    /**
     * 创建实际大小菜单项
     * @return 返回实际大小菜单组件
     */
    private Component 创建实际大小菜单项() {
        JMenuItem menuItem = new JMenuItem("实际大小");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().zoomReset();
            }
        });
        return menuItem;
    }



    /**
     * 创建缩小菜单项
     * @return 返回缩小菜单组件
     */
    private Component 创建缩小菜单项() {
        JMenuItem menuItem = new JMenuItem("缩小");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().zoomOut();
            }
        });
        return menuItem;
    }

    /**
     * 创建放大菜单项
     * @return 返回放大菜单组件
     */
    private Component 创建放大菜单项() {
        JMenuItem menuItem = new JMenuItem("放大");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().zoomIn();
            }
        });
        return menuItem;
    }
//    可以通过英文词义来理解：
//    In = 向内/增加 → 放大
//    Out = 向外/减少 → 缩小


    /**
     * 创建HTML5视频菜单项
     * @return 返回HTML5视频菜单组件
     */
    private Component 创建HTML5视频菜单项() {
        JMenuItem menuItem = new JMenuItem("HTML5视频");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://www.w3.org/2010/05/video/mediaevents.html");
            }
        });
        return menuItem;
    }


    // 创建Google地图菜单项
    private Component 创建Google地图菜单项() {
        JMenuItem menuItem = new JMenuItem("谷歌地图");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("https://maps.google.com/");
            }
        });
        return menuItem;
    }



    // 创建JavaScript对话框菜单项
    private Component 创建JavaScript对话框菜单项() {
        JMenuItem menuItem = new JMenuItem("JavaScript对话框");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://www.javascripter.net/faq/alert.htm");
            }
        });
        return menuItem;
    }



    // 创建文件下载菜单项
    private Component 创建文件下载菜单项() {
        JMenuItem menuItem = new JMenuItem("下载文件");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("https://s3.amazonaws.com/cloud.teamdev.com/downloads/demo/jxbrowserdemo.jnlp");
            }
        });
        return menuItem;
    }


    // 创建获取HTML菜单项
    private Component 创建获取HTML菜单项() {
        JMenuItem menuItem = new JMenuItem("获取HTML");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String html = 浏览器视图.getBrowser().getHTML();
                Window window = SwingUtilities.getWindowAncestor(浏览器视图);
                JDialog dialog = new JDialog(window);
                dialog.setModal(true);
                dialog.setContentPane(new JScrollPane(new JTextArea(html)));
                dialog.setSize(700, 500);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
        return menuItem;
    }

    private JMenuItem createConsoleMenuItem() {
        控制台菜单项 = new JMenuItem(RUN_JAVASCRIPT);
        控制台菜单项.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (RUN_JAVASCRIPT.equals(控制台菜单项.getText())) {
                    控制台菜单项.setText(CLOSE_JAVASCRIPT);
                    firePropertyChange("JSConsoleDisplayed", false, true);
                } else {
                    控制台菜单项.setText(RUN_JAVASCRIPT);
                    firePropertyChange("JSConsoleClosed", false, true);
                }
            }
        });
        return 控制台菜单项;
    }

    private JMenuItem createUploadFileMenuItem() {
        JMenuItem menuItem = new JMenuItem("上传文件");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://www.cs.tut.fi/~jkorpela/forms/file.html#example");
            }
        });
        return menuItem;
    }

    private JMenuItem createPopupsMenuItem() {
        JMenuItem menuItem = new JMenuItem("弹出窗口");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://www.popuptest.com");
            }
        });
        return menuItem;
    }

    private JMenuItem createPDFViewerMenuItem() {
        JMenuItem menuItem = new JMenuItem("PDF查看器");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://www.orimi.com/pdf-test.pdf");
            }
        });
        return menuItem;
    }

    // 创建Flash菜单项
    private JMenuItem 创建Flash菜单项() {
        JMenuItem menuItem = new JMenuItem("Adobe Flash播放器");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                浏览器视图.getBrowser().loadURL("http://helpx.adobe.com/flash-player.html");
            }
        });
        return menuItem;
    }


    // 创建"关于"菜单项
    // 创建关于菜单项
    private JMenuItem 创建关于菜单项() {
        JMenuItem menuItem = new JMenuItem("关于");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ToolBar.this);
                AboutDialog aboutDialog = new AboutDialog(parentFrame);
                aboutDialog.setVisible(true);
            }
        });
        return menuItem;
    }



    // 检查是否需要焦点
    private boolean 检查是否需要焦点() {
        String url = 地址栏.getText();
        return url.isEmpty() || url.equals(默认URL);
    }

    // 组件添加通知方法
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (检查是否需要焦点()) {
                    地址栏.requestFocus();
                    地址栏.selectAll();
                }
            }
        });
    }


/**
 * 自定义动作按钮类
 * 继承自JButton，用于创建特殊样式的工具栏按钮
 */
private static class 动作按钮 extends JButton {
    /**
     * 构造函数
     * @param 提示文本 按钮的悬停提示文本
     * @param 动作 按钮绑定的动作监听器
     */
    private 动作按钮(String 提示文本, Action 动作) {
        super(动作);
        setContentAreaFilled(false);     // 不填充按钮内容区域
        setBorder(BorderFactory.createEmptyBorder());  // 设置空边框
        setBorderPainted(false);         // 不绘制边框
        setRolloverEnabled(true);        // 启用鼠标悬停效果
        setToolTipText(提示文本);         // 设置提示文本
        setText(null);                   // 不显示文本
        setFocusable(false);            // 禁用焦点
        setDefaultCapable(false);        // 禁用默认按钮功能
    }
}

/**
 * 复选菜单项回调接口
 * 用于处理复选菜单项的选中状态改变事件
 */
private interface 复选菜单回调 {
    /**
     * 回调方法
     * @param 是否选中 复选菜单项的选中状态
     */
    void 调用(boolean 是否选中);
}
}
