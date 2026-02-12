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
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

import com.jgoodies.looks.Options;

/**
 * 具有最小尺寸的{@code Icon}，最小尺寸从
 * {@code UIManager}的{@code defaultIconSize}键读取。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.9 $
 */

public class MinimumSizedIcon implements Icon {

	/** 图标对象 */
	private final Icon icon;
	
	/** 图标宽度 */
	private final int  width;
	
	/** 图标高度 */
	private final int  height;
	
	/** X轴偏移量 */
	private final int  xOffset;
	
	/** Y轴偏移量 */
	private final int  yOffset;


	/**
	 * 构造一个新的最小尺寸图标，不包含具体图标
	 */
	public MinimumSizedIcon() {
		this(null);
	}

	/**
	 * 构造一个新的最小尺寸图标
	 * 
	 * @param icon 原始图标
	 */
	public MinimumSizedIcon(Icon icon) {
		Dimension minimumSize = Options.getDefaultIconSize();
		this.icon      = icon;
		int iconWidth  = icon == null ? 0 : icon.getIconWidth();
		int iconHeight = icon == null ? 0 : icon.getIconHeight();
		width   = Math.max(iconWidth,  Math.max(20, minimumSize.width));
		height  = Math.max(iconHeight, Math.max(20, minimumSize.height));
		xOffset = Math.max(0, (width  - iconWidth)  / 2);
		yOffset = Math.max(0, (height - iconHeight) / 2);
	}


	/**
	 * 获取图标的高度
	 * 
	 * @return 图标高度
	 */
	@Override
	public int getIconHeight() {  return height;	}
	
	/**
	 * 获取图标的宽度
	 * 
	 * @return 图标宽度
	 */
	@Override
	public int getIconWidth()	{  return width;	}


	/**
	 * 绘制图标
	 * 
	 * @param c 组件
	 * @param g 图形上下文
	 * @param x X坐标
	 * @param y Y坐标
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (icon != null) {
            icon.paintIcon(c, g, x + xOffset, y + yOffset);
        }
	}


}