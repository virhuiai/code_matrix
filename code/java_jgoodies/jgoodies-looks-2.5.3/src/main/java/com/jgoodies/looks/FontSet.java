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

import javax.swing.plaf.FontUIResource;

/**
 * 返回外观和感觉或主题使用的字体集合。
 * 这些字体必须实现UIResource标记接口。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.9 $
 *
 * @since 2.0
 */
public interface FontSet {

    /**
     * 返回用于所有对话框组件的字体。
     *
     * @return 用于所有对话框组件的字体
     */
    FontUIResource getControlFont();


    /**
     * 返回用于菜单的字体。
     *
     * @return 用于菜单的字体
     */
    FontUIResource getMenuFont();


    /**
     * 返回用于TitledBorders中标题标签的字体。
     * 此字体也用于JGoodies Forms标题和带标题的分隔符。
     *
     * @return 用于TitledBorder标题的字体
     */
    FontUIResource getTitleFont();


    /**
     * 返回用于内部框架标题的字体。
     *
     * @return 用于内部框架标题的字体
     */
    FontUIResource getWindowTitleFont();


    /**
     * 返回用于工具提示的字体。
     *
     * @return 工具提示字体
     */
    FontUIResource getSmallFont();


    /**
     * 返回用于消息对话框的字体。
     *
     * @return 用于消息对话框的字体
     */
    FontUIResource getMessageFont();

}