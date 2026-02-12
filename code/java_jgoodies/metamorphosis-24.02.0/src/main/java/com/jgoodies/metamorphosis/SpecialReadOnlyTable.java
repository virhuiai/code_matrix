/*
 * Copyright (c) 2024, JGoodies Software GmbH.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of JGoodies Software GmbH nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.metamorphosis;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * 特殊只读表格类。
 * 扩展JTable，提供只读功能和自定义的外观设置，包括表头对齐和列宽配置。
 */
final class SpecialReadOnlyTable extends JTable {
    
    /** 表头对齐方式数组，用于指定每列表头的对齐方式 */
    private int[] headerAlignments = null;
    
    /** 列宽提示数组，用于指定每列的推荐宽度 */
    private int[] columnSizeHints;

    /**
     * 构造特殊只读表格实例。
     * 初始化表格的各种属性，使其成为只读状态。
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public SpecialReadOnlyTable() {
        // 如果当前样式不是FRANKEN，则设置自动调整模式为OFF
        if (Style.getCurrent() != Style.FRANKEN) {
            setAutoResizeMode(0);  // 不自动调整列宽
        }
        
        setRowSelectionAllowed(false);      // 禁止行选择
        setColumnSelectionAllowed(false);  // 禁止列选择
        setCellSelectionEnabled(false);    // 禁止单元格选择
        setSelectionMode(0);              // 设置选择模式为单选
        setEnabled(false);                // 设置表格为禁用状态（只读）
    }

    /**
     * 创建左对齐的默认表头渲染器。
     * 
     * @return 左对齐的表头渲染器
     */
    private static TableCellRenderer createLeftAlignedDefaultHeaderRenderer() {
        // 创建自定义的表头渲染器
        DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
            /**
             * 获取表格单元格的渲染组件。
             * 设置渲染器的颜色、字体和边框，使其与表头一致。
             * 
             * @param table 表格组件
             * @param value 单元格的值
             * @param isSelected 是否选中
             * @param hasFocus 是否获得焦点
             * @param row 行索引
             * @param column 列索引
             * @return 渲染组件
             */
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JTableHeader header;
                // 如果表格有效且有表头，则设置渲染器的外观
                if (table != null && (header = table.getTableHeader()) != null) {
                    setForeground(header.getForeground());  // 设置前景色
                    setBackground(header.getBackground());  // 设置背景色
                    setFont(header.getFont());              // 设置字体
                }
                // 设置文本，如果值为空则显示空字符串
                setText(value == null ? "" : value.toString());
                // 设置单元格边框
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };
        // 设置水平对齐方式为左对齐
        label.setHorizontalAlignment(2);
        return label;
    }

    /**
     * 将指定列的表头设置为左对齐。
     * 
     * @param column 要左对齐的表格列
     */
    private static void leftAlignColumn(TableColumn column) {
        // 获取当前列的表头渲染器
        DefaultTableCellRenderer headerRenderer = column.getHeaderRenderer();
        
        // 如果渲染器是DefaultTableCellRenderer的实例，则直接设置对齐方式
        if (headerRenderer instanceof DefaultTableCellRenderer) {
            headerRenderer.setHorizontalAlignment(2);  // 左对齐
        } else {
            // 否则使用自定义的左对齐渲染器
            column.setHeaderRenderer(createLeftAlignedDefaultHeaderRenderer());
        }
    }

    /**
     * 配置表头对齐方式。
     * 根据headerAlignments数组设置每列的对齐方式。
     */
    private void configureColumnHeaders() {
        // 如果没有设置对齐方式，则直接返回
        if (null == this.headerAlignments) {
            return;
        }
        
        // 遍历所有列
        for (int col = 0; col < getModel().getColumnCount(); col++) {
            // 如果该列需要左对齐
            if (this.headerAlignments[col] == 2) {
                // 将该列的表头设置为左对齐
                leftAlignColumn(getColumnModel().getColumn(col));
            }
        }
    }

    /**
     * 配置列宽。
     * 根据columnSizeHints数组设置每列的宽度。
     */
    private void configureColumnSizes() {
        // 遍历所有列
        for (int col = 0; col < getModel().getColumnCount(); col++) {
            // 获取该列的宽度提示
            int sizeHint = this.columnSizeHints[col];
            // 如果宽度提示不为-1（表示有具体宽度要求）
            if (sizeHint != -1) {
                // 设置该列的首选宽度
                getColumnModel().getColumn(col).setPreferredWidth(sizeHint);
            }
        }
    }

    /**
     * 设置列宽提示数组。
     * 
     * @param columnSizeHints 列宽提示数组
     */
    public void setColumnSizes(int[] columnSizeHints) {
        this.columnSizeHints = columnSizeHints;
    }

    /**
     * 设置表头对齐方式数组。
     * 
     * @param alignments 对齐方式数组
     */
    public void setHeaderAlignments(int[] alignments) {
        this.headerAlignments = alignments;
    }

    /**
     * 设置表格模型。
     * 当设置新模型时，会自动配置表头对齐和列宽。
     * 
     * @param model 表格模型
     */
    public void setModel(TableModel model) {
        super.setModel(model);  // 调用父类方法设置模型
        
        // 如果当前样式不是FRANKEN，则配置表头和列宽
        if (Style.getCurrent() != Style.FRANKEN) {
            configureColumnHeaders();  // 配置表头对齐
            configureColumnSizes();    // 配置列宽
        }
        revalidate();  // 重新验证组件
    }

    /**
     * 更新UI组件。
     * 重新配置嵌入的滚动窗格，并设置行高。
     */
    public void updateUI() {
        super.updateUI();  // 调用父类方法更新UI
        
        configureEnclosingScrollPane();  // 配置嵌入的滚动窗格
        
        // 设置行高为树的行高和16的最大值减1
        setRowHeight(Math.max(UIManager.getInt("Tree.rowHeight"), 16) - 1);
    }
}