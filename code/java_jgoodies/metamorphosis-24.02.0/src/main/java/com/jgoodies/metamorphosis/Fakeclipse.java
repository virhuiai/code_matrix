/*
 * Copyright (c) 2024, JGoodies Software GmbH.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of JGoodies Software GmbH nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.metamorphosis;

import com.jgoodies.application.Application;
import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.framework.osx.OSXApplicationMenu;
import com.jgoodies.framework.util.ScreenUtils;
import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticMetroLookAndFeel;
import com.jgoodies.metamorphosis.Borders;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Fakeclipse应用程序主窗口类，模拟Eclipse风格的界面。
 * 这个类创建了一个类似Eclipse集成开发环境的用户界面，支持多种外观风格。
 */
public final class Fakeclipse extends JFrame {
    
    /**
     * 私有构造函数，初始化Fakeclipse应用程序窗口。
     * 配置UI外观并构建界面组件，设置关闭操作。
     */
    private Fakeclipse() {
        configureUI();  // 配置用户界面外观
        build();        // 构建界面组件
        setDefaultCloseOperation(2);  // 设置关闭操作为隐藏窗口（HIDE_ON_CLOSE）
    }

    /**
     * 在指定的样式和边界条件下打开Fakeclipse实例。
     * 
     * @param style 要应用的界面样式
     * @param bounds 窗口边界矩形，如果为null则使用默认大小和居中位置
     * @return 新创建的Fakeclipse实例
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Fakeclipse openOn(Style style, Rectangle bounds) {
        Style.current = style;  // 设置当前样式
        Fakeclipse instance = new Fakeclipse();  // 创建新实例
        
        // 如果没有指定边界，则设置默认大小和屏幕居中位置
        if (bounds == null) {
            instance.setSize(ScreenScaling.physicalDimension(800, 600));  // 设置窗口大小
            ScreenUtils.locateAt(instance, ScreenUtils.ScreenLocation.OPTICAL_CENTER);  // 居中显示
        } else {
            instance.setBounds(bounds);  // 使用指定的边界
        }
        
        instance.setVisible(true);  // 显示窗口
        return instance;
    }

    /**
     * 应用程序入口点。
     * 设置菜单选项并根据系统属性启动指定样式的Fakeclipse。
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 为Mac OS X设置关于菜单项名称
        OSXApplicationMenu.setAboutName("Fakeclipse");
        
        // 从系统属性获取样式名称，默认为ELEGANT
        String styleName = System.getProperty("style", Style.ELEGANT.toString());
        
        // 打开指定样式的Fakeclipse实例
        openOn(Style.valueOf(styleName), null);
    }

    /**
     * 配置用户界面外观和感觉。
     * 根据当前样式设置Look & Feel和其他UI选项。
     */
    private static void configureUI() {
        // 设置默认图标大小
        Options.setDefaultIconSize(new Dimension(18, 18));
        
        try {
            // 如果当前样式使用扩展的外观和感觉
            if (Style.getCurrent().useExtendedLookAndFeel()) {
                Options.setUseSystemFonts(true);  // 启用系统字体
                
                // 如果是Windows系统且当前样式强制使用系统外观和感觉
                if (SystemUtils.IS_OS_WINDOWS && Style.getCurrent().forceSystemLookAndFeel()) {
                    try {
                        // 尝试设置JGoodies Windows外观和感觉
                        UIManager.setLookAndFeel(Options.JGOODIES_WINDOWS_NAME);
                    } catch (Exception e) {
                        // 如果失败，则回退到Plastic Metro外观和感觉
                        UIManager.setLookAndFeel(new PlasticMetroLookAndFeel());
                    }
                } else {
                    // 设置金属主题并使用Plastic Metro外观和感觉
                    MetalLookAndFeel.setCurrentTheme(PlasticLookAndFeel.createMyDefaultTheme());
                    UIManager.setLookAndFeel(new PlasticMetroLookAndFeel());
                }
            } 
            // 如果不使用扩展的外观和感觉，但强制使用系统外观和感觉
            else if (Style.getCurrent().forceSystemLookAndFeel()) {
                // 设置系统默认的外观和感觉
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } 
            // 否则使用跨平台的外观和感觉
            else {
                // 根据样式决定是否使用更好的颜色主题
                if (Style.getCurrent().forceBetterColorTheme()) {
                    MetalLookAndFeel.setCurrentTheme(PlasticLookAndFeel.createMyDefaultTheme());
                } else {
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                }
                // 设置跨平台的外观和感觉（通常是Metal L&F）
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception e2) {
            // 如果无法更改外观和感觉，则输出错误信息
            System.out.println("Can't change L&F: " + e2);
        }
    }

    /**
     * 构建主界面组件。
     * 设置内容面板、标题和菜单栏，并配置窗口图标。
     */
    private void build() {
        setContentPane(buildContentPane());  // 设置主内容面板
        setTitle(Style.getCurrent().toString() + " - Fakeclipse");  // 设置窗口标题
        
        // 设置菜单栏
        setJMenuBar(new MenuBarView().getMenuBar());
        
        // 加载不同尺寸的应用程序图标
        Image image16 = Application.getResourceMap(null).getImage("application.icon");
        Image image48 = Application.getResourceMap(null).getImage("application.icon.48");
        Image image64 = Application.getResourceMap(null).getImage("application.icon.64");
        
        // 设置多个尺寸的窗口图标
        setIconImages(Arrays.asList(image16, image48, image64));
    }

    /**
     * 构建主内容面板。
     * 创建包含工具栏、侧边栏和中心状态区域的布局。
     * 
     * @return 构建好的主内容面板
     */
    private static JComponent buildContentPane() {
        // 创建边界布局的面板
        JPanel panel = new JPanel(new BorderLayout());
        
        // 添加工具栏到北部
        panel.add(buildToolBar(), "North");
        
        // 添加侧边栏到西部
        panel.add(buildSideBar(), "West");
        
        // 添加主状态区域到中心
        panel.add(buildMainStatus(), "Center");
        
        return panel;
    }

    /**
     * 构建工具栏组件。
     * 创建带有特定样式的工具栏，根据当前样式可能添加分隔符。
     * 
     * @return 构建好的工具栏组件
     */
    private static Component buildToolBar() {
        // 创建带图标的标签作为工具栏内容
        JLabel label = new JLabel(Utils.getIcon("topbar.gif"));
        label.setHorizontalAlignment(2);  // 设置水平居中对齐
        
        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        
        // 设置头部样式和边框样式
        toolBar.putClientProperty(HeaderStyle.KEY, HeaderStyle.BOTH);
        toolBar.putClientProperty(BorderStyle.WINDOWS_KEY, BorderStyle.SEPARATOR);
        
        // 更新UI以应用样式
        toolBar.updateUI();
        toolBar.add(label);  // 添加标签到工具栏
        
        // 如果不是FRANKEN样式，则直接返回工具栏
        if (Style.getCurrent() != Style.FRANKEN) {
            return toolBar;
        }
        
        // 对于FRANKEN样式，添加分隔符
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, "North");      // 工具栏在上方
        panel.add(new JSeparator(), "Center");  // 分隔符在中间
        return panel;
    }

    /**
     * 构建侧边栏组件。
     * 创建带有特定样式的侧边栏，包括图标和垂直分隔符。
     * 
     * @return 构建好的侧边栏组件
     */
    private static Component buildSideBar() {
        // 创建带图标的标签作为侧边栏内容
        JLabel label = new JLabel(Utils.getIcon("sidebar.gif"));
        label.setVerticalAlignment(1);  // 设置垂直居中对齐
        
        // 创建布局面板
        JPanel panel = new JPanel(new BorderLayout(2, 0));
        
        // 将标签放在西部（左边）
        panel.add(label, "West");
        
        // 添加垂直分隔符到中心
        panel.add(new JSeparator(1), "Center");  // 1表示垂直方向
        
        return panel;
    }

    /**
     * 构建编辑面板。
     * 创建包含Java源文件图标、描述文本区域的编辑器面板。
     * 
     * @return 构建好的编辑面板组件
     */
    private static Component buildEditPanel() {
        // 使用工厂方法创建编辑器面板
        JPanel panel = FakeclipseFactory.createEditorPanel(Utils.getIcon("javasrc.gif"), "description.txt");
        
        // 创建文本区域来显示样式描述
        JTextArea area = new JTextArea();
        // 设置首选大小
        area.setPreferredSize(new Dimension(80, DelayedPropertyChangeHandler.DEFAULT_DELAY));
        area.setLineWrap(true);      // 启用自动换行
        area.setWrapStyleWord(true); // 按单词换行
        // 设置当前样式的描述文本
        area.setText(Style.getCurrent().getDescription());
        
        // 创建包含边距的面板
        JPanel barAndArea = new JPanel(new BorderLayout());
        barAndArea.add(Box.createHorizontalStrut(12), "West");  // 左侧留白
        barAndArea.add(area, "Center");                         // 文本区域在中心
        
        // 使用工厂方法创建滚动面板
        JScrollPane scrollPane = FakeclipseFactory.createScrollPane(barAndArea);
        panel.add(scrollPane, "Center");  // 将滚动面板添加到编辑器面板的中心
        
        return panel;
    }

    /**
     * 构建主状态区域。
     * 创建包含分割窗格（包面板和编辑/大纲/任务面板）以及状态栏的主区域。
     * 
     * @return 构建好的主状态组件
     */
    private static Component buildMainStatus() {
        // 创建边界布局面板
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建水平分割窗格，左侧是包面板，右侧是编辑/大纲/任务面板
        JSplitPane split = FakeclipseFactory.createSplitPane(
            1,                           // 1表示水平分割
            PackagesPanel.build(),       // 左侧：包面板
            buildEditOutlineTasks(),     // 右侧：编辑/大纲/任务面板
            0.3d                         // 初始分隔比例：左侧占30%
        );
        
        // 如果当前样式减少边框，则添加空边框
        if (Style.getCurrent().reduceBorders()) {
            split.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 1));
        }
        
        panel.add(split, "Center");     // 将分割窗格添加到中心
        
        // 添加状态栏到底部
        panel.add(buildStatusBar(), "South");
        
        return panel;
    }

    /**
     * 构建编辑/大纲/任务区域。
     * 创建垂直分割窗格，上方是编辑/大纲组合，下方是任务面板。
     * 
     * @return 构建好的编辑/大纲/任务组件
     */
    private static Component buildEditOutlineTasks() {
        // 创建垂直分割窗格（0表示垂直分割）
        // 上方是编辑和大纲的组合，下方是任务面板
        // 编辑/大纲组合占100%，任务面板占剩余空间
        return FakeclipseFactory.createSplitPane(0, buildEditOutline(), TasksPanel.build(), 1.0d);
    }

    /**
     * 构建编辑/大纲区域。
     * 创建水平分割窗格，左侧是编辑面板，右侧是大纲面板。
     * 
     * @return 构建好的编辑/大纲组件
     */
    private static Component buildEditOutline() {
        // 创建水平分割窗格（1表示水平分割）
        // 左侧是编辑面板，右侧是大纲面板
        // 编辑面板占70%，大纲面板占30%
        return FakeclipseFactory.createSplitPane(1, buildEditPanel(), buildOutlinePanel(), 0.7d);
    }

    /**
     * 构建大纲面板。
     * 创建模拟Eclipse风格的大纲视图面板，但目前仅显示占位文本。
     * 
     * @return 构建好的大纲面板组件
     */
    private static Component buildOutlinePanel() {
        // 使用工厂方法创建Eclipse风格的面板
        JPanel panel = FakeclipseFactory.createEclipsePanel(
            Utils.getIcon("outline.gif"),        // 大纲图标
            "Outline",                           // 面板标题
            Utils.getIcon("outline_tools.gif"),  // 工具图标
            false                                // 不可折叠
        );
        
        // 创建内容面板
        JPanel content = new JPanel(new BorderLayout());
        // 添加提示文本
        content.add(new JLabel("An outline is not availiable."), "North");
        // 设置边距
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 将内容添加到滚动面板中
        panel.add(FakeclipseFactory.createScrollPane(content), "Center");
        
        // 设置首选大小
        panel.setPreferredSize(new Dimension(80, 300));
        
        return panel;
    }

    /**
     * 构建状态栏。
     * 创建位于窗口底部的状态栏，显示当前文件路径。
     * 
     * @return 构建好的状态栏组件
     */
    private static Component buildStatusBar() {
        // 创建显示文件路径的标签
        JLabel label = new JLabel(" com/jgoodies/metamorphosis/description.txt");
        
        // 设置复合边框：外部是薄状态边框，内部是2像素的空白边框
        label.setBorder(BorderFactory.createCompoundBorder(
            new Borders.ThinStatusBorder(),  // 使用自定义的薄状态边框
            BorderFactory.createEmptyBorder(2, 2, 2, 2)  // 内部空白边框
        ));
        
        return label;
    }
}