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

import com.jgoodies.animation.Animation;
import com.jgoodies.animation.swing.animations.BasicTextAnimation;
import com.jgoodies.animation.swing.animations.BasicTextAnimations;
import com.jgoodies.animation.swing.animations.GlyphAnimation;
import com.jgoodies.animation.swing.components.BasicTextLabel;
import com.jgoodies.animation.swing.components.GlyphLabel;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.layout.builder.FormBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 介绍页面视图类。
 * 提供应用程序启动时的介绍动画和页面，展示从丑陋到优雅的UI演进过程。
 */
public final class IntroPageView {
    
    /** 主应用程序引用 */
    private final Metamorphosis metamorphosis;
    
    /** 基本文本标签1 */
    private BasicTextLabel label1;
    
    /** 基本文本标签2 */
    private BasicTextLabel label2;
    
    /** 字形标签，用于显示动画效果 */
    private GlyphLabel glyphLabel;
    
    /** 跳过介绍按钮 */
    private JButton skipButton;
    
    /** 页面动画 */
    private Animation animation;

    /**
     * 构造介绍页面视图。
     * 
     * @param metamorphosis 主应用程序实例
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public IntroPageView(Metamorphosis metamorphosis) {
        this.metamorphosis = metamorphosis;
    }

    /**
     * 获取页面动画。
     * 
     * @return 页面动画实例
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public Animation animation() {
        return this.animation;
    }

    /**
     * 初始化组件。
     * 创建和配置介绍页面所需的各个组件。
     */
    private void initComponents() {
        // 创建第一个文本标签
        this.label1 = new BasicTextLabel(" ");
        this.label1.setFont(Metamorphosis.getH1Font());  // 设置字体
        this.label1.setBounds(0, 0, 400, 100);          // 设置边界
        this.label1.setOpaque(false);                    // 设置为透明
        
        // 创建第二个文本标签
        this.label2 = new BasicTextLabel(" ");
        this.label2.setFont(Metamorphosis.getH1Font());  // 设置字体
        this.label2.setBounds(0, 0, 400, 100);          // 设置边界
        this.label2.setOpaque(false);                    // 设置为透明
        
        // 创建字形标签，用于显示动画效果
        this.glyphLabel = new GlyphLabel(" ", 5000L, 100L);
        this.glyphLabel.setFont(Metamorphosis.getH1Font());  // 设置字体
        this.glyphLabel.setBounds(0, 0, 400, 100);          // 设置边界
        this.glyphLabel.setOpaque(false);                   // 设置为透明
        
        // 创建跳过介绍按钮
        this.skipButton = new JButton("Skip intro");
        this.skipButton.setFocusPainted(false);  // 不显示焦点边框
        this.skipButton.setOpaque(!SystemUtils.IS_OS_MAC);  // 在非Mac系统上设置为不透明
        this.skipButton.addActionListener(this::onSkipPerformed);  // 添加点击事件监听器
    }

    /**
     * 处理跳过介绍的事件。
     * 当用户点击跳过按钮时，切换到开始页面。
     * 
     * @param evt 事件对象
     */
    private void onSkipPerformed(ActionEvent evt) {
        goToStart();  // 切换到开始页面
    }

    /**
     * 构建介绍页面组件。
     * 创建并配置介绍页面的所有组件和布局。
     * 
     * @return 构建好的介绍页面组件
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public Component build() {
        initComponents();  // 初始化组件
        
        // 使用FormBuilder构建页面布局
        JPanel panel = new FormBuilder()
            .columns("8dlu, fill:[200dlu,pref]:grow, 8dlu", new Object[0])  // 定义列布局
            .rows("8dlu, 23dlu:grow, 3dlu, fill:[30dlu,p]:g, 3dlu, 23dlu:grow, 8dlu", new Object[0])  // 定义行布局
            .background(Color.WHITE)  // 设置背景颜色为白色
            .add(createSeparator()).xyw(1, 3, 3)  // 添加分隔线
            .add((Component) this.label1).xy(2, 4)  // 添加第一个标签
            .add((Component) this.label2).xy(2, 4)  // 添加第二个标签
            .add((Component) this.glyphLabel).xy(2, 4)  // 添加字形标签
            .add(createSeparator()).xyw(1, 5, 3)  // 添加分隔线
            .add((Component) this.skipButton).xy(2, 6, "right, bottom")  // 添加跳过按钮
            .build();  // 构建面板
        
        this.animation = createAnimation();  // 创建动画
        return panel;  // 返回构建的面板
    }

    /**
     * 创建分隔线组件。
     * 
     * @return 分隔线组件
     */
    private static Component createSeparator() {
        JPanel panel = new JPanel((LayoutManager) null);  // 创建无布局管理器的面板
        panel.setBackground(Color.GRAY);  // 设置背景颜色为灰色
        return panel;  // 返回面板
    }

    /**
     * 创建介绍页面动画。
     * 创建一系列动画序列，展示UI演进的过程。
     * 
     * @return 动画序列
     */
    private Animation createAnimation() {
        // 创建第一个淡入淡出动画
        Animation a0 = BasicTextAnimations.defaultFade(
            this.label1, this.label2, 3000L, 100L, 
            "Karsten Lentzsch|presents|The Swing Metamorphosis|A short course in", 
            Color.DARK_GRAY
        );
        
        // 创建字形动画
        Animation a1 = new GlyphAnimation(this.glyphLabel, 5000L, 1L, "Swing visual design");
        
        // 创建第二个淡入淡出动画
        Animation a2 = BasicTextAnimations.defaultFade(
            this.label1, this.label2, 2000L, -400L, 
            "Five steps|from ugly|to elegant.", 
            Color.DARK_GRAY
        );
        
        // 创建步骤1动画
        Animation a3 = BasicTextAnimation.defaultScale(this.label1, 2500L, "Step 1:", Color.DARK_GRAY);
        BasicTextAnimation a4 = BasicTextAnimation.defaultFade(this.label1, 4000L, "METAL L&F FRANKEN UI", Color.red.darker());
        a4.setOffsetEnabled(true);  // 启用偏移效果
        
        // 创建步骤2动画
        Animation a5 = BasicTextAnimation.defaultScale(this.label1, 2500L, "Step 2:", Color.DARK_GRAY);
        BasicTextAnimation a6 = BasicTextAnimation.defaultFade(this.label1, 4000L, "A ROOKIE UI", Color.DARK_GRAY);
        
        // 创建步骤3动画
        Animation a7 = BasicTextAnimation.defaultScale(this.label1, 2500L, "Step 3:", Color.DARK_GRAY);
        BasicTextAnimation a8 = BasicTextAnimation.defaultFade(this.label1, 4000L, "A STANDARD UI", Color.DARK_GRAY);
        
        // 创建步骤4动画
        Animation a9 = BasicTextAnimation.defaultScale(this.label1, 2500L, "Step 4:", Color.DARK_GRAY);
        Animation a10 = new GlyphAnimation(this.glyphLabel, 5000L, 1L, "AN ADVANCED UI");
        
        // 创建步骤5动画
        Animation a11 = BasicTextAnimation.defaultScale(this.label1, 2500L, "Step 5:", Color.DARK_GRAY);
        Animation a12 = BasicTextAnimation.defaultSpace(this.label1, 6500L, "AN ELEGANT SWING UI", Color.DARK_GRAY);
        
        // 将所有动画按顺序组合成一个序列
        Animation all = Animation.sequential(
            Animation.pause(1000L),  // 开始前暂停1秒
            a0, a1, 
            Animation.pause(1000L),  // 中间暂停1秒
            a2, 
            Animation.pause(1000L),  // 中间暂停1秒
            a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, 
            Animation.pause(2000L)  // 结束前暂停2秒
        );
        
        // 设置动画结束时的回调函数，切换到开始页面
        all.onStop(this::goToStart);
        
        return all;  // 返回动画序列
    }

    /**
     * 切换到开始页面。
     * 调用主应用程序的goToStart方法。
     */
    private void goToStart() {
        this.metamorphosis.goToStart();  // 切换到开始页面
    }
}