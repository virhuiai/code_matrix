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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

/**
 * 渲染弹出菜单和下拉菜单中的分隔符。
 * 与其超类不同，我们使用内边距设置，
 * 并使用共享的UI委托。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.8 $
 */
public final class ExtBasicPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {

	/** 分隔符高度 */
	private static final int SEPARATOR_HEIGHT = 2;

	/** 内边距 */
	private Insets insets;

    /** 共享的UI对象 */
    private static ComponentUI popupMenuSeparatorUI;

	/**
	 * 创建UI实例
	 * 
	 * @param b 组件
	 * @return 组件UI
	 */
	public static ComponentUI createUI(JComponent b) {
        if (popupMenuSeparatorUI == null) {
            popupMenuSeparatorUI = new ExtBasicPopupMenuSeparatorUI();
        }
		return popupMenuSeparatorUI;
	}


	/**
	 * 安装默认设置
	 * 
	 * @param s 分隔符组件
	 */
	@Override
    protected void installDefaults(JSeparator s) {
		super.installDefaults(s);
        insets = UIManager.getInsets("PopupMenuSeparator.margin");
	}


    /**
     * 绘制组件
     * 
     * @param g 图形上下文
     * @param c 组件
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        Dimension s = c.getSize();

        int topInset   = insets.top;
        int leftInset  = insets.left;
        int rightInset = insets.right;

        // 绘制背景
        g.setColor(UIManager.getColor("MenuItem.background"));
        g.fillRect(0, 0, s.width, s.height);

		// 绘制边框
		/*
		g.setColor(UIManager.getColor("controlHighlight"));
		g.drawLine(0, 0, 0, s.height -1);
		g.drawLine(s.width-1, 0, s.width-1, s.height-1);
		*/

		g.translate(0, topInset);
		g.setColor(c.getForeground());
		g.drawLine(leftInset, 0, s.width - rightInset, 0);

		g.setColor(c.getBackground());
		g.drawLine(leftInset, 1, s.width - rightInset, 1);
		g.translate(0, -topInset);
    }


    /**
     * 获取首选尺寸
     * 
     * @param c 组件
     * @return 首选尺寸
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
    	return new Dimension(0, insets.top + SEPARATOR_HEIGHT + insets.bottom);
    }

}