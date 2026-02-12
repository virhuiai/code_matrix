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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * 定义各种边框样式的工具类。
 * 包含多种边框实现，用于不同的UI外观风格，如ThinStatus、Flat、Eclipse等。
 */
final class Borders {
    
    /**
     * 私有构造方法，防止实例化此类。
     * 此类仅作为边框类型的容器使用。
     */
    private Borders() {
    }

    /**
     * 薄状态边框实现。
     * 绘制一个简单的细边框，使用浅色和深色线条形成轻微的立体效果。
     * 顶部和左侧使用阴影颜色，右侧和底部使用高亮颜色。
     */
    static final class ThinStatusBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距，上下左右均为1像素 */
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        /**
         * 绘制边框的方法
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            // 使用控制面板阴影颜色绘制顶部和左侧边线
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawLine(0, 0, w - 2, 0);  // 顶部边线
            g.drawLine(0, 0, 0, h - 2);  // 左侧边线
            // 使用控制面板高亮颜色绘制右侧和底部边线
            g.setColor(UIManager.getColor("controlLtHighlight"));
            g.drawLine(w - 1, 0, w - 1, h - 1);  // 右侧边线
            g.drawLine(0, h - 1, w - 1, h - 1);  // 底部边线
            g.translate(-x, -y);
        }

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /**
     * 平面头部边框实现。
     * 这种边框只在顶部、左侧和右侧绘制边线，底部无边线，通常用于表头等组件。
     */
    static final class FlatHeaderBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距：上1像素，左1像素，下0像素，右1像素 */
        private static final Insets INSETS = new Insets(1, 1, 0, 1);

        /**
         * 绘制平面头部边框
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            // 使用控制面板阴影颜色绘制顶部和左侧边线
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawLine(0, 0, w - 1, 0);  // 顶部边线
            g.drawLine(0, 0, 0, h - 1);  // 左侧边线
            // 使用控制面板高亮颜色绘制右侧边线
            g.setColor(UIManager.getColor("controlLtHighlight"));
            g.drawLine(w - 1, 0, w - 1, h - 1);  // 右侧边线
            g.translate(-x, -y);
        }

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /**
     * Eclipse风格的头部边框实现。
     * 与平面头部边框类似，但在颜色顺序上有所区别，适应Eclipse界面风格。
     */
    static final class EclipseHeaderBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距：上1像素，左1像素，下1像素，右0像素 */
        private static final Insets INSETS = new Insets(1, 1, 1, 0);

        /**
         * 绘制Eclipse风格头部边框
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            // 使用控制面板高亮颜色绘制顶部和左侧边线
            g.setColor(UIManager.getColor("controlLtHighlight"));
            g.drawLine(0, 0, w - 1, 0);  // 顶部边线
            g.drawLine(0, 0, 0, h - 2);  // 左侧边线
            // 使用控制面板阴影颜色绘制底部边线
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawLine(0, h - 1, w - 1, h - 1);  // 底部边线
            g.translate(-x, -y);
        }

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /**
     * Eclipse风格的标签页头部边框实现。
     * 只在底部绘制一条线，用于分隔标签页头部和内容区域。
     */
    static final class EclipseTabHeaderBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距：上0像素，左0像素，下1像素，右0像素 */
        private static final Insets INSETS = new Insets(0, 0, 1, 0);

        /**
         * 绘制Eclipse风格标签页头部边框
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            // 使用控制面板阴影颜色绘制底部边线
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawLine(0, h - 1, w - 1, h - 1);  // 底部边线
            g.translate(-x, -y);
        }

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /**
     * Eclipse风格的标签页边框实现。
     * 用于选中的标签页，提供三维视觉效果，使标签页看起来突出于其他元素。
     */
    static final class EclipseTabBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距：上0像素，左0像素，下0像素，右2像素 */
        private static final Insets INSETS = new Insets(0, 0, 0, 2);

        /**
         * 绘制Eclipse风格标签页边框
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            // 使用控制面板阴影颜色绘制右边线的暗部
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawLine(w - 2, 0, w - 2, 1);      // 右边线暗部顶部
            g.drawLine(w - 1, 1, w - 1, h - 1); // 右边线暗部主体
            // 使用控制面板颜色填充最右边的像素点
            g.setColor(UIManager.getColor("control"));
            g.drawLine(w - 1, 0, w - 1, 0);     // 右边线最顶点
            // 使用控制面板高亮颜色绘制右边线的亮部
            g.setColor(UIManager.getColor("controlLtHighlight"));
            g.drawLine(w - 2, 1, w - 2, h - 1); // 右边线亮部
            g.translate(-x, -y);
        }

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /**
     * Eclipse风格的面板边框实现。
     * 提供更复杂的三维边框效果，使用多层不同透明度的颜色创建深度感。
     */
    static final class EclipsePanelBorder extends AbstractBorder implements UIResource {
        
        /** 定义边框的内外间距：上1像素，左1像素，下3像素，右3像素 */
        private static final Insets INSETS = new Insets(1, 1, 3, 3);

        /**
         * 获取边框的内外间距
         * @param c 组件对象
         * @return 返回固定的间距值
         */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        /**
         * 绘制Eclipse风格面板边框
         * @param c 组件对象
         * @param g 图形绘制上下文
         * @param x 边框左上角X坐标
         * @param y 边框左上角Y坐标
         * @param w 边框宽度
         * @param h 边框高度
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            // 获取控制面板阴影颜色
            Color shadow = UIManager.getColor("controlShadow");
            // 创建半透明的浅阴影颜色
            Color lightShadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 170);
            // 创建更透明的浅阴影颜色
            Color lighterShadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 70);
            
            g.translate(x, y);
            
            // 绘制主要的阴影边框
            g.setColor(shadow);
            g.fillRect(0, 0, w - 3, 1);    // 顶部边框
            g.fillRect(0, 0, 1, h - 3);    // 左侧边框
            g.fillRect(w - 3, 1, 1, h - 3); // 右侧边框
            g.fillRect(1, h - 3, w - 3, 1); // 底部边框
            
            // 绘制较浅的阴影边框，增加立体感
            g.setColor(lightShadow);
            g.fillRect(w - 3, 0, 1, 1);      // 右上角像素
            g.fillRect(0, h - 3, 1, 1);      // 左下角像素
            g.fillRect(w - 2, 1, 1, h - 3);  // 右侧浅边框
            g.fillRect(1, h - 2, w - 3, 1);  // 底部浅边框
            
            // 绘制最浅的阴影边框，完成立体效果
            g.setColor(lighterShadow);
            g.fillRect(w - 2, 0, 1, 1);      // 右上角额外像素
            g.fillRect(0, h - 2, 1, 1);      // 左下角额外像素
            g.fillRect(w - 2, h - 2, 1, 1);  // 右下角像素
            g.fillRect(w - 1, 1, 1, h - 2);  // 最右侧边框
            g.fillRect(1, h - 1, w - 2, 1);  // 最底部边框
            
            g.translate(-x, -y);
        }
    }
}