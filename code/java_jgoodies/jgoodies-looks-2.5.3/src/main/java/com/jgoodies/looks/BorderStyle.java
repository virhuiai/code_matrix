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
 * 描述JMenuBar和JToolBar的边框样式。
 * 边框样式是外观依赖的，并且会覆盖外观独立的{@code HeaderStyle}。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.9 $
 *
 * @see HeaderStyle
 */
public final class BorderStyle {

    /** 空边框样式 - 不绘制任何边框 */
    public static final BorderStyle EMPTY     = new BorderStyle("Empty");
    
    /** 分隔符边框样式 - 绘制简单的分隔线 */
    public static final BorderStyle SEPARATOR = new BorderStyle("Separator");
    
    /** 蚀刻边框样式 - 绘制凹陷的边框效果 */
    public static final BorderStyle ETCHED    = new BorderStyle("Etched");

    /** 边框样式的名称 */
    private final String name;


    // 实例创建 ******************************************************

    /**
     * 私有构造函数，防止外部实例化
     * 
     * @param name 边框样式的名称
     */
    private BorderStyle(String name) {
        this.name = name;
    }


    // 公共方法 ************************************************************************

    /**
     * 从工具栏中查找客户端属性获取边框样式
     *
     * @param toolBar 工具栏组件
     * @param clientPropertyKey 用于查找属性的键
     * @return 用于在UI委托中选择边框的边框样式
     */
    public static BorderStyle from(JToolBar toolBar, String clientPropertyKey) {
        return from0(toolBar, clientPropertyKey);
    }

    /**
     * 从菜单栏中查找客户端属性获取边框样式
     *
     * @param menuBar 菜单栏组件
     * @param clientPropertyKey 用于查找属性的键
     * @return 用于在UI委托中选择边框的边框样式
     */
    public static BorderStyle from(JMenuBar menuBar, String clientPropertyKey) {
        return from0(menuBar, clientPropertyKey);
    }

    /**
     * 从指定的JComponent中查找客户端属性获取边框样式
     *
     * @param c 组件对象
     * @param clientPropertyKey 用于查找属性的键
     * @return 用于在UI委托中选择边框的边框样式
     */
    private static BorderStyle from0(JComponent c, String clientPropertyKey) {
        Object value = c.getClientProperty(clientPropertyKey);
        if (value instanceof BorderStyle) {
            return (BorderStyle) value;
        }

        if (value instanceof String) {
            return BorderStyle.valueOf((String) value);
        }

        return null;
    }

    /**
     * 根据名称查找对应的BorderStyle枚举值
     *
     * @param name 边框样式名称
     * @return 对应的BorderStyle枚举值
     * @throws IllegalArgumentException 当名称无效时抛出异常
     */
    private static BorderStyle valueOf(String name) {
        if (name.equalsIgnoreCase(EMPTY.name)) {
            return EMPTY;
        } else if (name.equalsIgnoreCase(SEPARATOR.name)) {
            return SEPARATOR;
        } else if (name.equalsIgnoreCase(ETCHED.name)) {
            return ETCHED;
        } else {
            throw new IllegalArgumentException("无效的BorderStyle名称: "
                    + name);
        }
    }

    /**
     * 返回边框样式的字符串表示
     *
     * @return 边框样式的名称
     */
    @Override
    public String toString() {
        return name;
    }

}