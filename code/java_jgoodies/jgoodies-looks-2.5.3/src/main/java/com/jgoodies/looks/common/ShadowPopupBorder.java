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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

/**
 * 带有投影效果的边框，旨在用作弹出窗口的外边框。
 * 如果与重量级弹出窗口一起使用，可以绘制屏幕背景。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.9 $
 *
 * @see ShadowPopup
 * @see ShadowPopupFactory
 */
final class ShadowPopupBorder extends AbstractBorder {

    /**
     * 投影效果在底部和右侧需要5个像素。
     */
    private static final int SHADOW_SIZE = 5;

	/**
	 * 用于绘制所有边框的单例实例。
	 */
	private static ShadowPopupBorder instance = new ShadowPopupBorder();

	/**
	 * 投影效果是从具有8位alpha通道的PNG图像创建的。
	 */
	private static Image shadow
		= new ImageIcon(ShadowPopupBorder.class.getResource("shadow.png")).getImage();


    // 实例创建 *****************************************************

	/**
	 * 返回用于绘制所有边框的单例实例。
	 * 
	 * @return ShadowPopupBorder单例实例
	 */
	public static ShadowPopupBorder getInstance() {
		return instance;
	}


	/**
	 * 为指定组件绘制边框，具有指定的位置和大小。
	 * 
	 * @param c 组件
	 * @param g 图形上下文
	 * @param x X坐标
	 * @param y Y坐标
	 * @param width 宽度
	 * @param height 高度
	 */
	@Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		// 在重量级弹出窗口的情况下伪造投影效果
        JComponent popup = (JComponent) c;
        Image hShadowBg = (Image) popup.getClientProperty(ShadowPopupFactory.PROP_HORIZONTAL_BACKGROUND);
        if (hShadowBg != null) {
            g.drawImage(hShadowBg, x, y + height - 5, c);
        }
        Image vShadowBg = (Image) popup.getClientProperty(ShadowPopupFactory.PROP_VERTICAL_BACKGROUND);
        if (vShadowBg != null) {
            g.drawImage(vShadowBg, x + width - 5, y, c);
        }

		// 绘制投影效果
		g.drawImage(shadow, x +  5, y + height - 5, x + 10, y + height, 0, 6, 5, 11, null, c);
		g.drawImage(shadow, x + 10, y + height - 5, x + width - 5, y + height, 5, 6, 6, 11, null, c);
		g.drawImage(shadow, x + width - 5, y + 5, x + width, y + 10, 6, 0, 11, 5, null, c);
		g.drawImage(shadow, x + width - 5, y + 10, x + width, y + height - 5, 6, 5, 11, 6, null, c);
		g.drawImage(shadow, x + width - 5, y + height - 5, x + width, y + height, 6, 6, 11, 11, null, c);
	}


	/**
	 * 返回边框的内边距。
	 * 
	 * @param c 组件
	 * @return 边框内边距
	 */
	@Override
    public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, SHADOW_SIZE, SHADOW_SIZE);
	}


    /**
     * 用此边框的当前内边距重新初始化内边距参数。
     * 
     * @param c 应用此边框内边距值的组件
     * @param insets 要重新初始化的对象
     * @return {@code insets}对象
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = 0;
        insets.right = insets.bottom = SHADOW_SIZE;
        return insets;
    }

}