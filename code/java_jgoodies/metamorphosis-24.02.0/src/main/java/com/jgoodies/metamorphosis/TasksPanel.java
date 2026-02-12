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

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 * 任务面板类，用于显示错误和警告的任务列表。
 * 该面板展示项目中的错误和警告信息，类似于Eclipse IDE中的问题视图。
 */
final class TasksPanel {
    
    /**
     * 私有构造方法，防止实例化此类。
     * 该类仅作为构建任务面板的工具类使用。
     */
    TasksPanel() {
    }

    /**
     * 构建任务面板。
     * 创建一个Eclipse风格的面板，包含一个显示任务列表的表格。
     * 
     * @return 构建好的任务面板组件
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Component build() {
        // 定义各列的对齐方式：0为左对齐，2为右对齐
        int[] alignments = {0, 0, 0, 2, 2, 2, 2, 2};
        
        // 定义各列的初始大小
        int[] columnSizes = {19, 19, 19, 195, 87, 135, 60, 10};
        
        // 创建Eclipse风格的面板，标题为"Tasks (2 items)"，选中状态为true
        JPanel panel = FakeclipseFactory.createEclipsePanel(
            Utils.getIcon("tasks.gif"),         // 左侧图标
            "Tasks (2 items)",                  // 标签文本
            Utils.getIcon("tasks_tools.gif"),   // 右侧图标
            true                                // 选中状态
        );
        
        // 创建特殊只读表格
        SpecialReadOnlyTable table = new SpecialReadOnlyTable();
        
        // 设置默认单元格渲染器
        table.setDefaultRenderer(Object.class, new MyDefaultTableCellRenderer());
        
        // 设置表头对齐方式和列宽
        table.setHeaderAlignments(alignments);
        table.setColumnSizes(columnSizes);
        
        // 设置表格模型
        table.setModel(createTasksTableModel());
        
        // 设置表格最小尺寸
        table.setMinimumSize(new Dimension(DelayedPropertyChangeHandler.DEFAULT_DELAY, 100));
        
        // 如果当前样式使用扩展外观且强制使用系统外观，则设置网格颜色
        if (Style.getCurrent().useExtendedLookAndFeel() && Style.getCurrent().forceSystemLookAndFeel()) {
            table.setGridColor(UIManager.getColor("controlHighlight"));
        }
        
        // 创建滚动面板包装表格
        JScrollPane scrollPane = FakeclipseFactory.createScrollPane(table);
        panel.add(scrollPane, "Center");  // 将滚动面板添加到面板中央
        
        // 设置面板首选大小
        panel.setPreferredSize(new Dimension(360, 115));
        
        return panel;
    }

    /**
     * 创建任务表格的数据模型。
     * 创建一个包含错误和警告信息的表格模型。
     * 
     * @return 任务表格模型
     */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.String[], java.lang.String[][]] */
    private static TableModel createTasksTableModel() {
        // 加载错误图标
        final ImageIcon icon = Utils.getIcon("error_tsk.gif");
        
        // 定义列名
        final String[] columnNames = {"", "C.", "!", "Description", "Resource", "In Folder", "Location", ""};
        
        // 定义任务数据（错误信息）
        final String[][] tasksData = {
            {"org.cdc.Channel cannot be resolved", "Launcher.java", "Peek-a-Booty Client/src/com/jgoodies/peekaboo/", "line 42 in Launcher.java", ""},
            {"org.cdc.Broadcast cannot be resolved", "Launcher.java", "Peek-a-Booty Client/src/com/jgoodies/peekaboo/", "line 44 in MainModel.registerListeners()", ""},
            {"", "", "", "", ""}
        };
        
        // 返回匿名内部类实现的表格模型
        return new AbstractTableModel() {
            /**
             * 获取列数
             * 
             * @return 列的数量
             */
            public int getColumnCount() {
                return columnNames.length;
            }

            /**
             * 获取列名
             * 
             * @param col 列索引
             * @return 指定列的名称
             */
            public String getColumnName(int col) {
                return columnNames[col];
            }

            /**
             * 获取行数
             * 
             * @return 行的数量
             */
            public int getRowCount() {
                return tasksData.length;
            }

            /**
             * 获取指定单元格的值
             * 
             * @param row 行索引
             * @param col 列索引
             * @return 指定单元格的值
             */
            public Object getValueAt(int row, int col) {
                // 如果是第一列（错误图标列）且在前两行，则返回图标
                if (col == 0 && row < 2) {
                    return icon;
                }
                // 如果是前三列（图标、C、!列），返回空字符串
                if (col < 3) {
                    return "";
                }
                // 否则返回数据数组中的值
                return tasksData[row][col - 3];
            }
        };
    }

    /**
     * 自定义单元格渲染器内部类。
     * 用于定制表格单元格的外观，特别是处理图标和文本的显示。
     */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/metamorphosis/TasksPanel$MyDefaultTableCellRenderer.class */
    private static class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
        
        /** 带水平边距的边框 */
        private static final Border HPAD_BORDER = BorderFactory.createEmptyBorder(0, 4, 0, 4);
        
        /** 空边框 */
        private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);

        /**
         * 私有构造方法，防止外部实例化。
         */
        private MyDefaultTableCellRenderer() {
        }

        /**
         * 获取表格单元格的渲染组件。
         * 根据单元格的值类型设置相应的图标和边框。
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
            // 调用父类方法获取基础渲染组件
            JLabel label = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            
            // 如果值是图标类型，则设置图标并清除文本
            if (value instanceof Icon) {
                label.setIcon((Icon) value);
                label.setText("");                 // 清除文本
                label.setBorder(EMPTY_BORDER);     // 设置空边框
            } else {
                label.setIcon((Icon) null);        // 清除图标
                label.setBorder(HPAD_BORDER);      // 设置带水平边距的边框
            }
            
            return label;
        }
    }
}