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

import java.awt.Insets;

import javax.swing.plaf.InsetsUIResource;

/**
 * 描述外观和感觉或主题使用的内边距和外边距。
 * 定义了各种UI组件的布局参数。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.8 $
 *
 * @since 2.1
 */
public final class MicroLayout {

    /** 文本组件的内边距 */
    private final InsetsUIResource textInsets;
    
    /** 换行文本组件的内边距 */
    private final InsetsUIResource wrappedTextInsets;
    
    /** 组合框编辑器的内边距 */
    private final InsetsUIResource comboBoxEditorInsets;
    
    /** 按钮边框的内边距 */
    private final Insets           buttonBorderInsets;
    
    /** 标准按钮的外边距 */
    private final InsetsUIResource buttonMargin;
    
    /** 提交按钮的外边距 */
    private final InsetsUIResource commitButtonMargin;
    
    /** 组合框边框大小 */
    private final int comboBorderSize;
    
    /** 组合框弹出边框大小 */
    private final int comboPopupBorderSize;
    
    /** 复选框的外边距 */
    private final InsetsUIResource checkBoxMargin;
    
    /** 菜单项的外边距 */
    private final InsetsUIResource menuItemMargin;
    
    /** 菜单的外边距 */
    private final InsetsUIResource menuMargin;
    
    /** 弹出菜单分隔符的外边距 */
    private final InsetsUIResource popupMenuSeparatorMargin;


    // 实例创建 ******************************************************

    /**
     * 构造一个新的微布局对象
     *
     * @param textInsets 文本内边距
     * @param wrappedTextInsets 换行文本内边距
     * @param comboBoxEditorInsets 组合框编辑器内边距
     * @param comboBorderSize 组合框边框大小
     * @param comboPopupBorderSize 组合框弹出边框大小
     * @param buttonBorderInsets 按钮边框内边距
     * @param buttonMargin 按钮外边距
     * @param commitButtonMargin 提交按钮外边距
     * @param checkBoxMargin 复选框外边距
     * @param menuItemMargin 菜单项外边距
     * @param menuMargin 菜单外边距
     * @param popupMenuSeparatorMargin 弹出菜单分隔符外边距
     */
    public MicroLayout(
            InsetsUIResource textInsets,
            InsetsUIResource wrappedTextInsets,
            InsetsUIResource comboBoxEditorInsets,
            int comboBorderSize,
            int comboPopupBorderSize,
            Insets           buttonBorderInsets,
            InsetsUIResource buttonMargin,
            InsetsUIResource commitButtonMargin,
            InsetsUIResource checkBoxMargin,
            InsetsUIResource menuItemMargin,
            InsetsUIResource menuMargin,
            InsetsUIResource popupMenuSeparatorMargin) {
        this.textInsets = textInsets;
        this.wrappedTextInsets = wrappedTextInsets;
        this.comboBoxEditorInsets = comboBoxEditorInsets;
        this.buttonBorderInsets = buttonBorderInsets;
        this.buttonMargin = buttonMargin;
        this.commitButtonMargin = commitButtonMargin;
        this.comboBorderSize = comboBorderSize;
        this.comboPopupBorderSize = comboPopupBorderSize;
        this.checkBoxMargin = checkBoxMargin;
        this.menuItemMargin = menuItemMargin;
        this.menuMargin = menuMargin;
        this.popupMenuSeparatorMargin = popupMenuSeparatorMargin;
    }


    // Getter方法 ****************************************************************

    /**
     * 返回用于按钮边框的内边距。
     *
     * @return 用于按钮边框的内边距
     */
    public Insets getButtonBorderInsets() {
        return buttonBorderInsets;
    }


    /**
     * 返回用于标准按钮的外边距。这些内边距描述了在表单行中
     * 与其他组件排列的按钮。标准按钮的<em>高度</em>通常与
     * 文本字段、组合框和其他按行排列的组件相同。<p>
     *
     * 工具栏按钮可能有不同的高度，以及放置在特殊命令栏区域的
     * 提交按钮也是如此，例如确定、取消、应用。
     *
     * @return 标准按钮的外边距
     *
     * @see #getCommitButtonMargin()
     */
    public InsetsUIResource getButtonMargin() {
        return buttonMargin;
    }

    /**
     * 返回用于命令区域中提交按钮的外边距。
     * 这样的命令区域通常位于对话框或面板的底部或侧面；
     * 经常使用的标签有确定、取消、应用、是、否、重试。
     * 提交按钮的<em>高度</em>可能与表单中按行排列的
     * 其他组件按钮的高度不同。
     *
     * @return 命令区域中提交按钮的外边距
     *
     * @see #getButtonMargin()
     */
    public InsetsUIResource getCommitButtonMargin() {
        return commitButtonMargin;
    }

    /**
     * 获取组合框边框大小
     *
     * @return 组合框边框大小
     */
    public int getComboBorderSize() {
        return comboBorderSize;
    }

    /**
     * 获取组合框弹出边框大小
     *
     * @return 组合框弹出边框大小
     */
    public int getComboPopupBorderSize() {
        return comboPopupBorderSize;
    }

    /**
     * 获取组合框编辑器内边距
     *
     * @return 组合框编辑器内边距
     */
    public InsetsUIResource getComboBoxEditorInsets() {
        return comboBoxEditorInsets;
    }

    /**
     * 获取复选框外边距
     *
     * @return 复选框外边距
     */
    public InsetsUIResource getCheckBoxMargin() {
        return checkBoxMargin;
    }


    /**
     * 获取菜单项外边距
     *
     * @return 菜单项外边距
     */
    public InsetsUIResource getMenuItemMargin() {
        return menuItemMargin;
    }

    /**
     * 获取菜单外边距
     *
     * @return 菜单外边距
     */
    public InsetsUIResource getMenuMargin() {
        return menuMargin;
    }

    /**
     * 获取弹出菜单分隔符外边距
     *
     * @return 弹出菜单分隔符外边距
     */
    public InsetsUIResource getPopupMenuSeparatorMargin() {
        return popupMenuSeparatorMargin;
    }


    /**
     * 获取文本内边距
     *
     * @return 文本内边距
     */
    public InsetsUIResource getTextInsets() {
        return textInsets;
    }


    /**
     * 获取换行文本内边距
     *
     * @return 换行文本内边距
     */
    public InsetsUIResource getWrappedTextInsets() {
        return wrappedTextInsets;
    }

}