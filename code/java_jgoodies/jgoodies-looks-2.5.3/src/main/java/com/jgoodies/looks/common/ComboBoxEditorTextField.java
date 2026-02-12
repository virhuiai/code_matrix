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

import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * 专为用作组合框编辑器而设计的文本字段。
 * 当文本等于内容时不设置文本；
 * 这解决了Java问题#4530952。
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.7 $
 */
public final class ComboBoxEditorTextField extends JTextField {

    /**
     * 构造一个新的组合框编辑器文本字段
     * 
     * @param isTableCellEditor 是否为表格单元格编辑器
     */
    public ComboBoxEditorTextField(boolean isTableCellEditor) {
        super("", UIManager.getInt("ComboBox.editorColumns"));
        // 为表格使用特殊内边距，否则使用文本字段默认值
        if (isTableCellEditor) {
            setMargin(UIManager.getInsets("ComboBox.tableEditorInsets"));
        }
        setBorder(UIManager.getBorder("ComboBox.editorBorder"));
    }

    // 解决Java问题4530952的变通方法
    /**
     * 设置文本内容，如果文本等于当前内容则不执行设置操作
     * 
     * @param s 要设置的文本
     */
    @Override
    public void setText(String s) {
        if (getText().equals(s)) {
            return;
        }
        super.setText(s);
    }

}
