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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;


/**
 * 渲染对齐的{@code JRadioButtonMenuItem}s。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.9 $
 */
public class ExtBasicRadioButtonMenuItemUI extends ExtBasicMenuItemUI {

	/**
	 * 获取属性前缀
	 * 
	 * @return 属性前缀字符串
	 */
	@Override
    protected String getPropertyPrefix() { return "RadioButtonMenuItem"; }


	/**
	 * 创建UI实例
	 * 
	 * @param b 组件
	 * @return 组件UI
	 */
	public static ComponentUI createUI(JComponent b) {
		return new ExtBasicRadioButtonMenuItemUI();
	}


	// RadioButtonMenuItems和CheckBoxMenuItems将覆盖此方法
	/**
	 * 判断图标边框是否启用
	 * 
	 * @return 总是返回true
	 */
	@Override
    protected boolean iconBorderEnabled() { return true; }


	/**
	 * 处理鼠标事件
	 * 
	 * @param item 菜单项
	 * @param e 鼠标事件
	 * @param path 菜单元素路径
	 * @param manager 菜单选择管理器
	 */
	public void processMouseEvent(JMenuItem item, MouseEvent e,
								   MenuElement[] path, MenuSelectionManager manager) {
		Point p = e.getPoint();
		if (p.x >= 0 && p.x < item.getWidth() &&
		    p.y >= 0 && p.y < item.getHeight()) {
			if (e.getID() == MouseEvent.MOUSE_RELEASED) {
				manager.clearSelectedPath();
				item.doClick(0);
				item.setArmed(false);
			} else {
                manager.setSelectedPath(path);
            }
		} else if (item.getModel().isArmed()) {
			MenuElement[] newPath = new MenuElement[path.length - 1];
			int i, c;
			for (i = 0, c = path.length - 1; i < c; i++) {
                newPath[i] = path[i];
            }
			manager.setSelectedPath(newPath);
		}
	}

}
