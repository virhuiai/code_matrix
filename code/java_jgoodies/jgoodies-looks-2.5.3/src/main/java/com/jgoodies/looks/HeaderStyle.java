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

package com.jgoodies.looks;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/**
 * 描述JMenuBar和JToolBar的标题样式。
 * 标题样式是外观独立的，可以被外观依赖的{@code BorderStyle}所覆盖。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.9 $
 *
 * @see	BorderStyle
 */
public final class HeaderStyle {

    /** 单侧样式 - 只在一侧绘制装饰 */
    public static final HeaderStyle SINGLE = new HeaderStyle("Single");
    
    /** 双侧样式 - 在两侧都绘制装饰 */
    public static final HeaderStyle BOTH   = new HeaderStyle("Both");

    /** 标题样式的名称 */
    private final String name;


    /**
     * 私有构造函数，防止外部实例化
     * 
     * @param name 标题样式的名称
     */
    private HeaderStyle(String name) {
        this.name = name;
    }


    /**
     * 从菜单栏中查找客户端属性获取标题样式
     *
     * @param menuBar 菜单栏组件
     * @return 菜单栏的标题样式
     */
    public static HeaderStyle from(JMenuBar menuBar) {
        return from0(menuBar);
    }


    /**
     * 从工具栏中查找客户端属性获取标题样式
     *
     * @param toolBar 工具栏组件
     * @return 工具栏的标题样式
     */
    public static HeaderStyle from(JToolBar toolBar) {
        return from0(toolBar);
    }


    /**
     * 从指定的JComponent中查找客户端属性获取标题样式
     *
     * @param c 组件对象
     * @return 给定组件的标题样式
     */
    private static HeaderStyle from0(JComponent c) {
        Object value = c.getClientProperty(Options.HEADER_STYLE_KEY);
        if (value instanceof HeaderStyle) {
            return (HeaderStyle) value;
        }

        if (value instanceof String) {
            return HeaderStyle.valueOf((String) value);
        }

        return null;
    }


    /**
     * 查找并返回具有指定名称的{@code HeaderStyle}
     *
     * @param name 要查找的HeaderStyle对象名称
     * @return 相关联的HeaderStyle
     * @throws IllegalArgumentException 当名称无效时抛出异常
     */
    private static HeaderStyle valueOf(String name) {
        if (name.equalsIgnoreCase(SINGLE.name)) {
            return SINGLE;
        } else if (name.equalsIgnoreCase(BOTH.name)) {
            return BOTH;
        } else {
            throw new IllegalArgumentException("无效的HeaderStyle名称: "
                    + name);
        }
    }


    /**
     * 返回标题样式的字符串表示
     *
     * @return 标题样式的名称
     */
    @Override
    public String toString() {
        return name;
    }

}