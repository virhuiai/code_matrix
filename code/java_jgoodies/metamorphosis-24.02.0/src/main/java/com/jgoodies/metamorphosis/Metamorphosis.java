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

import com.jgoodies.animation.Animator;
import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.components.JGCardPanel;
import com.jgoodies.framework.application.JGApplication;
import com.jgoodies.framework.osx.OSXApplicationMenu;
import com.jgoodies.framework.setup.AbstractUISetup;
import com.jgoodies.framework.util.FrameBuilder;
import com.jgoodies.framework.util.ScreenUtils;
import com.jgoodies.framework.util.ThreadUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Metamorphosis应用程序主类。
 * 这是一个演示不同界面样式和外观的Swing应用程序，继承自JGApplication。
 */
public final class Metamorphosis extends JGApplication {
    
    /** 介绍页面卡片标识 */
    private static final String INTRO_CARD = "intro";
    
    /** 开始页面卡片标识 */
    private static final String START_CARD = "start";
    
    /** H1标题字体 */
    private static Font h1Font;
    
    /** H2标题字体 */
    private static Font h2Font;
    
    /** 介绍页面视图组件 */
    private IntroPageView intro;
    
    /** 开始页面视图组件 */
    private StartPageView start;
    
    /** 卡片面板，用于切换介绍页面和开始页面 */
    private JGCardPanel cardPanel;
    
    /** Fakeclipse实例，模拟Eclipse界面 */
    private Fakeclipse fakeclipse;
    
    /** 主窗口框架 */
    private JFrame frame;

    /**
     * 应用程序入口点。
     * 设置Mac OS X菜单栏选项并启动应用程序。
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 为Mac OS X设置关于菜单项名称
        OSXApplicationMenu.setAboutName("Metamorphosis");
        
        // 设置使用屏幕菜单栏
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        // 启动应用程序
        Application.launch(Metamorphosis.class, args);
    }

    /**
     * 配置用户界面。
     * 调用父类的配置方法并设置基本UI设置。
     */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.framework.application.JGApplication
    protected void configureUI() {
        super.configureUI();  // 调用父类的UI配置
        new AbstractUISetup.BasicUISetup().setup();  // 设置基本UI配置
    }

    /**
     * 创建并显示图形用户界面。
     * 初始化主窗口框架，设置其属性和外观。
     */
    @Override // com.jgoodies.framework.application.JGApplication
    protected void createAndShowGUI() {
        // 加载不同尺寸的应用程序图标
        Image image16 = getResourceMap().getImage("application.icon");
        Image image48 = getResourceMap().getImage("application.icon.48");
        Image image64 = getResourceMap().getImage("application.icon.64");
        
        // 使用FrameBuilder构建主窗口
        this.frame = new FrameBuilder()
            .initialSize(500, 330)  // 初始尺寸
            .initialLocation(ScreenUtils.ScreenLocation.OPTICAL_CENTER)  // 居中位置
            .minimumSize(500, 330)  // 最小尺寸
            .iconImages(image16, image48, image64)  // 设置多个尺寸的图标
            .background(Color.WHITE)  // 背景颜色
            .splashTitleText("Metamorphosis", new Object[0])  // 启动画面标题
            .splashTitleFont(48)  // 标题字体大小
            .splashSubtitleText("© 2002 – 2024 JGoodies", new Object[0])  // 副标题
            .asRootFrame()  // 设置为主框架
            .show();  // 显示框架
    }

    /**
     * 应用程序准备就绪时调用。
     * 初始化组件，延时后设置内容面板并启动动画。
     */
    @Override // com.jgoodies.application.Application
    protected void ready() {
        // 注册Mac OS X的关于菜单项
        OSXApplicationMenu.register(this::onAboutPerformed, null);
        
        initComponents();  // 初始化组件
        
        ThreadUtils.sleep(500L);  // 延时500毫秒
        
        // 设置内容面板
        this.frame.setContentPane(buildContentPane());
        this.frame.revalidate();  // 重新验证
        this.frame.repaint();     // 重绘界面
        
        // 启动介绍页面的动画
        new Animator(this.intro.animation(), 25).start();
    }

    /**
     * 初始化界面组件。
     * 创建介绍页面、开始页面和卡片面板。
     */
    private void initComponents() {
        this.intro = new IntroPageView(this);      // 创建介绍页面
        this.start = new StartPageView(this);      // 创建开始页面
        this.cardPanel = new JGCardPanel();        // 创建卡片面板
    }

    /**
     * 构建内容面板。
     * 将介绍页面和开始页面添加到卡片面板中。
     * 
     * @return 构建好的内容面板
     */
    private JComponent buildContentPane() {
        // 将介绍页面添加到卡片面板，标识为INTRO_CARD
        this.cardPanel.add(this.intro.build(), INTRO_CARD);
        
        // 将开始页面添加到卡片面板，标识为START_CARD
        this.cardPanel.add(this.start.build(), START_CARD);
        
        return this.cardPanel;  // 返回卡片面板
    }

    /**
     * 切换到开始页面。
     * 显示开始页面卡片并启动开始页面功能。
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public void goToStart() {
        this.cardPanel.showCard(START_CARD);  // 显示开始页面卡片
        this.start.start();                   // 启动开始页面功能
    }

    /**
     * 启动Fakeclipse实例。
     * 关闭现有的Fakeclipse实例（如果存在），然后使用指定样式打开新的实例。
     * 
     * @param style 要使用的样式
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLaunch(Style style) {
        Rectangle bounds = null;  // 保存当前Fakeclipse窗口的位置和大小
        
        // 如果已有Fakeclipse实例，则保存其边界并释放
        if (this.fakeclipse != null) {
            bounds = this.fakeclipse.getBounds();
            this.fakeclipse.dispose();
        }
        
        // 使用指定样式打开新的Fakeclipse实例
        this.fakeclipse = Fakeclipse.openOn(style, bounds);
    }

    /**
     * 获取H1标题字体。
     * 
     * @return H1标题字体
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Font getH1Font() {
        if (h1Font == null) {
            h1Font = createFont(0, 24);  // 创建24号普通字体作为H1字体
        }
        return h1Font;
    }

    /**
     * 获取H2标题字体。
     * 
     * @return H2标题字体
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Font getH2Font() {
        if (h2Font == null) {
            h2Font = createFont(0, 16);  // 创建16号普通字体作为H2字体
        }
        return h2Font;
    }

    /**
     * 创建指定样式和大小的字体。
     * 
     * @param style 字体样式（如Font.BOLD, Font.ITALIC等）
     * @param size 字体大小
     * @return 创建的字体对象
     */
    private static Font createFont(int style, int size) {
        // 使用屏幕缩放创建物理字体，并应用指定样式
        return ScreenScaling.physicalFont(new JLabel(), size).deriveFont(style);
    }

    /**
     * 处理关于菜单项被点击的事件。
     * 显示包含应用程序信息的对话框。
     * 
     * @param evt 事件对象
     */
    private void onAboutPerformed(ActionEvent evt) {
        // 获取资源映射
        ResourceMap resources = Application.getResourceMap(null);
        
        // 显示关于对话框，显示应用程序名称、版权和版本信息
        JOptionPane.showMessageDialog(
            JOptionPane.getRootFrame(),
            Strings.get("%1$s\n%2$s\n\nVersion %3$s",
                resources.getString("application.name", new Object[0]),
                resources.getString("application.copyright", new Object[0]),
                resources.getString("application.buildNo", new Object[0])
            )
        );
    }
}