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

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.components.JGSplitPane;
import com.jgoodies.looks.Options;
import com.jgoodies.metamorphosis.Borders;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Fakeclipse界面组件工厂类。
 * 该类提供了创建Eclipse风格界面组件的静态方法，包括面板、分割窗格和滚动窗格等。
 */
final class FakeclipseFactory {
    
    /**
     * 私有构造方法，防止实例化此类。
     * 该类仅作为工厂方法的容器使用。
     */
    private FakeclipseFactory() {
    }

    /**
     * Eclipse头部面板内部类。
     * 该面板支持渐变背景绘制，用于创建Eclipse风格的头部区域。
     */
    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/metamorphosis/FakeclipseFactory$EclipseHeaderPanel.class */
    public static class EclipseHeaderPanel extends JPanel {
        
        /**
         * 构造Eclipse头部面板。
         * 
         * @param lm 布局管理器
         */
        EclipseHeaderPanel(LayoutManager lm) {
            super(lm);
        }

        /**
         * 重写paint方法，实现渐变背景绘制。
         * 根据当前样式决定是否绘制Eclipse风格的渐变背景。
         * 
         * @param g 图形绘制上下文
         */
        public void paint(Graphics g) {
            // 检查当前样式是否启用Eclipse渐变效果
            if (Style.getCurrent().useEclipseGradient()) {
                // 根据操作系统和样式设置起始颜色
                Color start = UIManager.getColor((SystemUtils.IS_OS_WINDOWS && Style.getCurrent().forceSystemLookAndFeel()) 
                    ? "textHighlight" : "ScrollBar.thumb");
                
                // 获取结束颜色和中间颜色
                Color end = UIManager.getColor("control");
                Color middle = new Color(167, 202, 239);
                
                // 转换为Graphics2D以支持渐变绘制
                Graphics2D g2 = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                int splitX = width / 2;  // 计算渐变分界点
                
                // 绘制从起始颜色到中间颜色的渐变（左半部分）
                g2.setPaint(new GradientPaint(0.0f, 0.0f, start, splitX, 0.0f, middle));
                g2.fillRect(0, 0, splitX, height);
                
                // 绘制从中間颜色到结束颜色的渐变（右半部分）
                g2.setPaint(new GradientPaint(splitX, 0.0f, middle, width, 0.0f, end));
                g2.fillRect(splitX, 0, width, height);
            }
            // 调用父类的paint方法完成其他绘制
            super.paint(g);
        }
    }

    /**
     * 创建Eclipse风格的面板。
     * 
     * @param leftIcon 左侧图标
     * @param label 面板标签文本
     * @param rightIcon 右侧图标
     * @param selected 是否为选中状态
     * @param edit 是否为编辑模式
     * @return 创建的Eclipse风格面板
     */
    static JPanel createEclipsePanel(Icon leftIcon, String label, Icon rightIcon, boolean selected, boolean edit) {
        // 创建头部面板，使用EclipseHeaderPanel以支持渐变背景
        JPanel topLeft = new EclipseHeaderPanel(new BorderLayout());
        
        // 创建标签，包含文本和图标
        JLabel left = new JLabel(label);
        
        // 如果是选中状态且启用Eclipse渐变，则设置前景色
        if (selected && Style.getCurrent().useEclipseGradient()) {
            left.setForeground((SystemUtils.IS_OS_WINDOWS && Style.getCurrent().forceSystemLookAndFeel()) 
                ? UIManager.getColor("textHighlightText") : Color.WHITE);
        }
        
        left.setIcon(leftIcon);      // 设置左侧图标
        left.setOpaque(false);       // 设置标签为非不透明
        
        // 创建右侧标签（通常用于工具按钮）
        JLabel right = new JLabel(rightIcon);
        right.setOpaque(!selected);  // 选中时设置为不透明
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));  // 设置右侧边距
        
        topLeft.add(left, "West");   // 将左侧标签添加到西部
        topLeft.setOpaque(edit || !selected);  // 根据编辑状态或未选中状态设置不透明
        
        // 创建内边距边框
        Border pad = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        
        // 根据编辑模式设置边框
        if (edit) {
            // 编辑模式下使用Eclipse标签边框和高亮背景
            topLeft.setBorder(BorderFactory.createCompoundBorder(new Borders.EclipseTabBorder(), pad));
            topLeft.setBackground(UIManager.getColor("controlLtHighlight"));
        } else {
            // 非编辑模式下只使用内边距
            topLeft.setBorder(pad);
        }
        
        // 创建顶层面板
        JPanel top = new JPanel(new BorderLayout());
        // 根据编辑模式确定左侧面板的位置
        top.add(topLeft, edit ? "West" : "Center");
        top.add(right, "East");  // 右侧图标始终在东部
        
        // 根据编辑模式和样式设置顶部边框
        if (edit) {
            if (Style.getCurrent().useEclipseBorder()) {
                // 编辑模式下使用Eclipse标签头部边框
                top.setBorder(new Borders.EclipseTabHeaderBorder());
            } else {
                // 否则使用平面头部边框
                top.setBorder(new Borders.FlatHeaderBorder());
            }
        } else if (Style.getCurrent().useEclipseBorder()) {
            // 非编辑模式下使用Eclipse头部边框
            top.setBorder(new Borders.EclipseHeaderBorder());
        } else {
            // 否则使用平面头部边框
            top.setBorder(new Borders.FlatHeaderBorder());
        }
        
        top.setOpaque(!selected);  // 选中时不设置为不透明
        
        // 创建最终的面板容器
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, "North");  // 将顶部面板添加到北部
        
        // 根据样式设置面板边框
        if (Style.getCurrent().useEclipseBorder()) {
            panel.setBorder(new Borders.EclipsePanelBorder());
        }
        
        return panel;
    }

    /**
     * 创建Eclipse风格的面板（简版）。
     * 
     * @param leftIcon 左侧图标
     * @param label 面板标签文本
     * @param rightIcon 右侧图标
     * @param selected 是否为选中状态
     * @return 创建的Eclipse风格面板
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static JPanel createEclipsePanel(Icon leftIcon, String label, Icon rightIcon, boolean selected) {
        // 调用完整版方法，编辑模式设为false
        return createEclipsePanel(leftIcon, label, rightIcon, selected, false);
    }

    /**
     * 创建编辑器面板。
     * 
     * @param leftIcon 左侧图标
     * @param label 面板标签文本
     * @return 创建的编辑器面板
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static JPanel createEditorPanel(Icon leftIcon, String label) {
        // 调用完整版方法，选中状态为false，编辑模式为true
        return createEclipsePanel(leftIcon, label, null, false, true);
    }

    /**
     * 创建分割窗格。
     * 
     * @param orientation 分割方向（JSplitPane.HORIZONTAL_SPLIT 或 VERTICAL_SPLIT）
     * @param comp1 第一个组件
     * @param comp2 第二个组件
     * @param resizeWeight 调整权重
     * @return 创建的分割窗格
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSplitPane createSplitPane(int orientation, Component comp1, Component comp2, double resizeWeight) {
        // 根据样式决定是否减少边框，选择合适的分割窗格类型
        JSplitPane split = Style.getCurrent().reduceBorders() 
            ? new JGSplitPane(orientation, comp1, comp2) 
            : new JSplitPane(orientation, comp1, comp2);
        
        split.setResizeWeight(resizeWeight);  // 设置调整权重
        
        // 如果减少边框，则设置空边框
        if (Style.getCurrent().reduceBorders()) {
            split.setBorder(BorderFactory.createEmptyBorder());
        }
        
        return split;
    }

    /**
     * 创建滚动窗格。
     * 
     * @param component 要包装的组件
     * @return 创建的滚动窗格
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static JScrollPane createScrollPane(Component component) {
        // 创建滚动窗格
        JScrollPane scrollPane = new JScrollPane(component);
        
        // 设置蚀刻边框属性
        scrollPane.putClientProperty(Options.IS_ETCHED_KEY, Boolean.TRUE);
        scrollPane.updateUI();  // 更新UI以应用设置
        
        // 如果使用Eclipse边框，则设置空边框
        if (Style.getCurrent().useEclipseBorder()) {
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
        }
        
        return scrollPane;
    }
}