/*
 * Copyright (c) 2001-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.looks.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.UIManager;


/**
 * 用于编辑器和上一个/下一个按钮的简单布局管理器。
 * 有关组件确切排列方式的更多信息，请参见BasicSpinnerUI的javadoc。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.7 $
 */
public final class ExtBasicSpinnerLayout implements LayoutManager {

    /**
     * 供默认LayoutManager类- SpinnerLayout使用，
     * 用于缺失（null）的editor/nextButton/previousButton子组件。
     */
    private static final Dimension ZERO_SIZE = new Dimension(0, 0);


    /** 下一个按钮组件 */
    private Component nextButton     = null;
    
    /** 上一个按钮组件 */
    private Component previousButton = null;
    
    /** 编辑器组件 */
    private Component editor         = null;


    /**
     * 添加布局组件
     * 
     * @param name 组件名称
     * @param c 组件
     */
    @Override
	public void addLayoutComponent(String name, Component c) {
        if ("Next".equals(name)) {
            nextButton = c;
        } else if ("Previous".equals(name)) {
            previousButton = c;
        } else if ("Editor".equals(name)) {
            editor = c;
        }
    }


    /**
     * 移除布局组件
     * 
     * @param c 要移除的组件
     */
    @Override
	public void removeLayoutComponent(Component c) {
        if (c == nextButton) {
            c = null;
        } else if (c == previousButton) {
            previousButton = null;
        } else if (c == editor) {
            editor = null;
        }
    }


    /**
     * 获取组件的首选尺寸
     * 
     * @param c 组件
     * @return 组件的首选尺寸
     */
    private static Dimension preferredSize(Component c) {
        return c == null ? ZERO_SIZE : c.getPreferredSize();
    }


    /**
     * 获取布局的首选尺寸
     * 
     * @param parent 父容器
     * @return 布局的首选尺寸
     */
    @Override
	public Dimension preferredLayoutSize(Container parent) {
        Dimension nextD = preferredSize(nextButton);
        Dimension previousD = preferredSize(previousButton);
        Dimension editorD = preferredSize(editor);

        Dimension size = new Dimension(editorD.width, editorD.height);
        size.width += Math.max(nextD.width, previousD.width);
        Insets insets = parent.getInsets();
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        return size;
    }


    /**
     * 获取布局的最小尺寸
     * 
     * @param parent 父容器
     * @return 布局的最小尺寸
     */
    @Override
	public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }


    /**
     * 设置组件边界
     * 
     * @param c 组件
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     */
    private static void setBounds(Component c, int x, int y, int width, int height) {
        if (c != null) {
            c.setBounds(x, y, width, height);
        }
    }


    /**
     * 布局容器
     * 
     * @param parent 父容器
     */
    @Override
	public void layoutContainer(Container parent) {
        int width = parent.getWidth();
        int height = parent.getHeight();

        Insets insets = parent.getInsets();
        Dimension nextD = preferredSize(nextButton);
        Dimension previousD = preferredSize(previousButton);
        int buttonsWidth = Math.max(nextD.width, previousD.width);
        int editorHeight = height - (insets.top + insets.bottom);

        // 如果不为null，则使用arrowButtonInsets值代替JSpinner的
        // 内边距。将其定义为(0, 0, 0, 0)会使按钮与spinner边框的
        // 外边缘对齐，而将其保留为"null"则将按钮完全放置在
        // spinner边框内部。
        Insets buttonInsets = UIManager
                .getInsets("Spinner.arrowButtonInsets");
        if (buttonInsets == null) {
            buttonInsets = insets;
        }

        /*
         * 处理spinner的componentOrientation属性。
         */
        int editorX, editorWidth, buttonsX;
        if (parent.getComponentOrientation().isLeftToRight()) {
            editorX = insets.left;
            editorWidth = width - insets.left - buttonsWidth
                    - buttonInsets.right;
            buttonsX = width - buttonsWidth - buttonInsets.right;
        } else {
            buttonsX = buttonInsets.left;
            editorX = buttonsX + buttonsWidth;
            editorWidth = width - buttonInsets.left - buttonsWidth
                    - insets.right;
        }

        int nextY = buttonInsets.top;
        int nextHeight = height / 2 + height % 2 - nextY;
        int previousY = buttonInsets.top + nextHeight;
        int previousHeight = height - previousY - buttonInsets.bottom;

        setBounds(editor, editorX, insets.top, editorWidth, editorHeight);
        setBounds(nextButton, buttonsX, nextY, buttonsWidth, nextHeight);
        setBounds(previousButton, buttonsX, previousY, buttonsWidth,
                previousHeight);
    }

}