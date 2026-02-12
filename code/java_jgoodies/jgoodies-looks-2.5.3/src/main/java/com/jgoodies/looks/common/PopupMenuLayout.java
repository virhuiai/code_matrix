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

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.plaf.UIResource;


/**
 * JGoodies弹出菜单的布局管理器实现。
 * 与JDK实现相比，它在方法{@link #invalidateLayout(Container)}中而不是在方法
 * {@link #preferredLayoutSize(Container)}中刷新客户端属性
 * {@code maxTextWidth}和{@code maxAccWidth}的值。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.8 $
 */
public final class PopupMenuLayout extends BoxLayout implements UIResource {


    /**
     * 创建一个沿给定轴布置组件的布局管理器。
     *
     * @param target 需要布置的容器
     * @param axis 沿其布置组件的轴
     */
    public PopupMenuLayout(Container target, int axis) {
        super(target, axis);
    }


    /**
     * 表示子组件已更改其布局相关信息，
     * 因此应刷新任何缓存的计算结果。
     * <p>
     * 如果目标是JPopupMenu的实例，则刷新客户端属性
     * {@code maxTextWidth}和{@code maxAccWidth}的值。
     *
     * @param target 受影响的容器
     */
    @Override
    public synchronized void invalidateLayout(Container target) {
        if (target instanceof JPopupMenu) {
            JPopupMenu menu = (JPopupMenu) target;
            menu.putClientProperty(MenuItemRenderer.MAX_TEXT_WIDTH, null);
            menu.putClientProperty(MenuItemRenderer.MAX_ACC_WIDTH,  null);
        }
        super.invalidateLayout(target);
    }


}
