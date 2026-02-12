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

import java.awt.KeyEventPostProcessor;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ComboPopup;

import com.sun.java.swing.plaf.windows.WindowsRootPaneUI;

/**
 * 处理Alt键以选择菜单栏中的第一个菜单（如果有的话）。
 * 有助于让非Windows外观（如Plastic）感觉更像Windows。
 *
 * @see WindowsRootPaneUI
 */
public final class MenuSelectionProcessor implements KeyEventPostProcessor {

    /** Alt键是否被按下 */
    private boolean altKeyPressed = false;
    
    /** 菜单是否在按下时被取消 */
    private boolean menuCanceledOnPress = false;

    /**
     * 后处理键盘事件
     * 
     * @param ev 键盘事件
     * @return 如果事件被处理则返回true，否则返回false
     */
    @Override
	public boolean postProcessKeyEvent(KeyEvent ev) {
        if (ev.isConsumed()) {
            return false;
        }
        if (ev.getKeyCode() == KeyEvent.VK_ALT) {
            if (ev.getID() == KeyEvent.KEY_PRESSED) {
                if (!altKeyPressed) {
                    altPressed(ev);
                }
                altKeyPressed = true;
                return true;
            } else if (ev.getID() == KeyEvent.KEY_RELEASED) {
                if (altKeyPressed) {
                    altReleased(ev);
                }
                altKeyPressed = false;
            }
        } else {
            altKeyPressed = false;
        }
        return false;
    }


    /**
     * 处理Alt键按下事件
     * 
     * @param ev 键盘事件
     */
    private void altPressed(KeyEvent ev) {
        MenuSelectionManager msm =
            MenuSelectionManager.defaultManager();
        MenuElement[] path = msm.getSelectedPath();
        if (path.length > 0 && ! (path[0] instanceof ComboPopup)) {
            msm.clearSelectedPath();
            menuCanceledOnPress = true;
            ev.consume();
        } else if (path.length > 0) { // 我们在组合框中
            menuCanceledOnPress = false;
            ev.consume();
        } else {
            menuCanceledOnPress = false;
            JMenuBar mbar = getMenuBar(ev);
            JMenu menu = mbar != null ? mbar.getMenu(0) : null;
            if (menu != null) {
                ev.consume();
            }
        }
    }


    /**
     * 处理Alt键释放事件
     * 
     * @param ev 键盘事件
     */
    private void altReleased(KeyEvent ev) {
        if (menuCanceledOnPress) {
            return;
        }
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        if (msm.getSelectedPath().length == 0) {
            // 如果没有激活的菜单，我们尝试激活菜单栏
            JMenuBar mbar = getMenuBar(ev);
            JMenu menu = mbar != null ? mbar.getMenu(0) : null;
            if (menu != null) {
                MenuElement[] path = new MenuElement[2];
                path[0] = mbar;
                path[1] = menu;
                msm.setSelectedPath(path);
            }
        }
    }


    /**
     * 获取菜单栏
     * 
     * @param ev 键盘事件
     * @return 菜单栏组件
     */
    private static JMenuBar getMenuBar(KeyEvent ev) {
        JRootPane root = SwingUtilities.getRootPane(ev.getComponent());
        Window winAncestor = root == null ? null : SwingUtilities.getWindowAncestor(root);
        JMenuBar mbar = root != null ? root.getJMenuBar() : null;
        if (mbar == null && winAncestor instanceof JFrame) {
            mbar = ((JFrame)winAncestor).getJMenuBar();
        }
        return mbar;
    }

}