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

import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.framework.builder.MenuBuilder;
import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

/**
 * 菜单栏视图类。
 * 创建和管理应用程序的菜单栏，包括文件、编辑、源代码等菜单项。
 */
final class MenuBarView {
    
    /** 菜单栏组件 */
    private JMenuBar menuBar;

    /**
     * 获取菜单栏实例。
     * 如果菜单栏尚未创建，则先构建它。
     * 
     * @return 菜单栏组件
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public JMenuBar getMenuBar() {
        if (this.menuBar == null) {
            this.menuBar = buildMenuBar();  // 构建菜单栏
        }
        return this.menuBar;
    }

    /**
     * 构建菜单栏。
     * 创建包含所有菜单项的完整菜单栏。
     * 
     * @return 构建好的菜单栏
     */
    private static JMenuBar buildMenuBar() {
        // 获取组件工厂
        JGComponentFactory factory = JGComponentFactory.getCurrent();
        
        // 创建菜单栏
        JMenuBar bar = new JMenuBar();
        
        // 设置头部样式和边框样式
        bar.putClientProperty(HeaderStyle.KEY, HeaderStyle.BOTH);
        bar.putClientProperty(BorderStyle.WINDOWS_KEY, BorderStyle.SEPARATOR);
        
        // 添加文件菜单
        bar.add(buildFileMenu());
        
        // 添加编辑菜单并添加单选菜单项
        JMenu menu = factory.createMenu("_Edit");
        addRadioMenuItems(menu);
        bar.add(menu);
        
        // 添加源代码菜单并添加复选菜单项
        JMenu menu2 = factory.createMenu("_Source");
        addCheckMenuItems(menu2);
        bar.add(menu2);
        
        // 添加其他占位菜单
        bar.add(buildStubMenu("Refac_tor"));
        bar.add(buildStubMenu("_Navigate"));
        bar.add(buildStubMenu("Se_arch"));
        bar.add(buildStubMenu("_Project"));
        bar.add(buildStubMenu("_Run"));
        bar.add(buildStubMenu("_Window"));
        bar.add(buildStubMenu("_Help"));
        
        return bar;
    }

    /**
     * 构建文件菜单。
     * 创建包含文件操作命令的完整菜单。
     * 
     * @return 构建好的文件菜单
     */
    static JMenu buildFileMenu() {
        // 使用MenuBuilder构建文件菜单，包含新建、保存、打印等操作
        return new MenuBuilder()
            .label("_File")  // 设置菜单标签
            .item(new MenuBuilder()  // 新建子菜单
                .label("New")
                .noIcons()  // 不显示图标
                .item("Project…")  // 项目
                .item("Folder…")   // 文件夹
                .item("Document…") // 文档
                .separator()       // 分隔线
                .item("This submenu has no icons")  // 说明文本
                .build())
            .separator()  // 分隔线
            .item("Close", "ctrl F4")                    // 关闭
            .item("Close All", "ctrl shift F4")          // 全部关闭
            .separator()                                 // 分隔线
            .item("Save description.txt", (Icon) Utils.getIcon("save_edit.gif"), "ctrl S")  // 保存
            .item("Save description.txt As…", (Icon) Utils.getIcon("saveas_edit.gif"))      // 另存为
            .item("Save All", (Icon) Utils.getIcon("saveall_edit.gif"), "ctrl shift S")     // 全部保存
            .item("Revert")                              // 恢复
            .separator()                                 // 分隔线
            .item("Move…")                               // 移动
            .item("Rename…")                             // 重命名
            .separator()                                 // 分隔线
            .item("Refresh")                             // 刷新
            .separator()                                 // 分隔线
            .item("Print", (Icon) Utils.getIcon("print.gif"), "ctrl P")  // 打印
            .separator()                                 // 分隔线
            .item("Import", (Icon) Utils.getIcon("import_wiz.gif"))       // 导入
            .item("Export", (Icon) Utils.getIcon("export_wiz.gif"))       // 导出
            .separator()                                 // 分隔线
            .item("Properties", "alt ENTER")             // 属性
            .separator()                                 // 分隔线
            .item("1 WinXPMenuItemUI.java")              // 示例文件
            .item("2 WinXPUtils.java")
            .item("3 WinXPBorders.java")
            .item("4 WinXPLookAndFeel.java")
            .separator()                                 // 分隔线
            .item("Exit")                                // 退出
            .build();                                    // 构建菜单
    }

    /**
     * 构建占位菜单。
     * 创建一个简单的菜单，用于占位，只有一个"Fake"项。
     * 
     * @param markedText 菜单标签文本
     * @return 构建好的占位菜单
     */
    private static JMenu buildStubMenu(String markedText) {
        return new MenuBuilder()
            .label(markedText)  // 设置菜单标签
            .item("Fake")       // 添加假项
            .build();
    }

    /**
     * 为菜单添加单选菜单项。
     * 创建包含多个单选按钮的菜单项组。
     * 
     * @param menu 目标菜单
     */
    static void addRadioMenuItems(JMenu menu) {
        // 创建第一组单选按钮
        ButtonGroup group1 = new ButtonGroup();
        JRadioButtonMenuItem item = createRadioItem(true, false);  // 启用且未选中
        group1.add(item);
        menu.add(item);
        
        JRadioButtonMenuItem item2 = createRadioItem(true, true);  // 启用且选中
        group1.add(item2);
        menu.add(item2);
        
        menu.addSeparator();  // 添加分隔符
        
        // 添加其他单选按钮
        menu.add(createRadioItem(false, false));  // 禁用且未选中
        menu.add(createRadioItem(false, true));   // 禁用且选中
        menu.addSeparator();                      // 添加分隔符
        
        // 创建第二组单选按钮
        ButtonGroup group2 = new ButtonGroup();
        JRadioButtonMenuItem item3 = createRadioItem(true, false);  // 启用且未选中
        item3.setIcon(Utils.getIcon("pie_mode.gif"));  // 设置饼图模式图标
        group2.add(item3);
        menu.add(item3);
        
        JRadioButtonMenuItem item4 = createRadioItem(true, true);  // 启用且选中
        item4.setIcon(Utils.getIcon("bar_mode.gif"));  // 设置柱状图模式图标
        group2.add(item4);
        menu.add(item4);
        
        menu.addSeparator();  // 添加分隔符
        
        // 添加带图标的单选按钮
        JRadioButtonMenuItem item5 = createRadioItem(false, false);  // 禁用且未选中
        item5.setIcon(Utils.getIcon("alphab_sort.png"));  // 设置字母排序图标
        menu.add(item5);
        
        JRadioButtonMenuItem item6 = createRadioItem(false, true);  // 禁用且选中
        item6.setIcon(Utils.getIcon("size_sort.png"));     // 设置大小排序图标
        item6.setDisabledIcon(Utils.getIcon("size_sort_gray.png"));  // 设置禁用时的图标
        menu.add(item6);
    }

    /**
     * 为菜单添加复选菜单项。
     * 创建包含多个复选框的菜单项。
     * 
     * @param menu 目标菜单
     */
    static void addCheckMenuItems(JMenu menu) {
        // 添加两个复选框
        menu.add(createCheckItem(true, false));  // 启用且未选中
        menu.add(createCheckItem(true, true));   // 启用且选中
        menu.addSeparator();                     // 添加分隔符
        
        menu.add(createCheckItem(false, false));  // 禁用且未选中
        menu.add(createCheckItem(false, true));   // 禁用且选中
        menu.addSeparator();                      // 添加分隔符
        
        // 添加带选中和未选中图标的第一组复选框
        JCheckBoxMenuItem item = createCheckItem(true, false);  // 启用且未选中
        item.setIcon(Utils.getIcon("check.gif"));               // 设置普通图标
        item.setSelectedIcon(Utils.getIcon("check_selected.gif"));  // 设置选中图标
        menu.add(item);
        
        JCheckBoxMenuItem item2 = createCheckItem(true, true);  // 启用且选中
        item2.setIcon(Utils.getIcon("check.gif"));              // 设置普通图标
        item2.setSelectedIcon(Utils.getIcon("check_selected.gif"));  // 设置选中图标
        menu.add(item2);
        menu.addSeparator();  // 添加分隔符
        
        // 添加带选中和未选中图标的第二组复选框
        JCheckBoxMenuItem item3 = createCheckItem(false, false);  // 禁用且未选中
        item3.setIcon(Utils.getIcon("check.gif"));                // 设置普通图标
        item3.setSelectedIcon(Utils.getIcon("check_selected.gif"));  // 设置选中图标
        menu.add(item3);
        
        JCheckBoxMenuItem item4 = createCheckItem(false, true);  // 禁用且选中
        item4.setIcon(Utils.getIcon("check.gif"));               // 设置普通图标
        item4.setSelectedIcon(Utils.getIcon("check_selected.gif"));  // 设置选中图标
        item4.setDisabledSelectedIcon(Utils.getIcon("check_disabled_selected.gif"));  // 设置禁用时的选中图标
        menu.add(item4);
    }

    /**
     * 创建单选菜单项。
     * 
     * @param enabled 是否启用
     * @param selected 是否选中
     * @return 创建的单选菜单项
     */
    private static JRadioButtonMenuItem createRadioItem(boolean enabled, boolean selected) {
        // 创建单选菜单项，标签基于启用和选中状态
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(getToggleLabel(enabled, selected), selected);
        item.setEnabled(enabled);  // 设置启用状态
        
        // 添加状态改变监听器，当状态改变时更新标签文本
        item.addChangeListener(e -> {
            JRadioButtonMenuItem source = (JRadioButtonMenuItem) e.getSource();
            // 根据当前状态更新标签文本
            source.setText(getToggleLabel(source.isEnabled(), source.isSelected()));
        });
        
        return item;
    }

    /**
     * 创建复选菜单项。
     * 
     * @param enabled 是否启用
     * @param selected 是否选中
     * @return 创建的复选菜单项
     */
    private static JCheckBoxMenuItem createCheckItem(boolean enabled, boolean selected) {
        // 创建复选菜单项，标签基于启用和选中状态
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(getToggleLabel(enabled, selected), selected);
        item.setEnabled(enabled);  // 设置启用状态
        
        // 添加状态改变监听器，当状态改变时更新标签文本
        item.addChangeListener(e -> {
            JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
            // 根据当前状态更新标签文本
            source.setText(getToggleLabel(source.isEnabled(), source.isSelected()));
        });
        
        return item;
    }

    /**
     * 获取开关标签文本。
     * 根据启用和选中状态生成标签文本。
     * 
     * @param enabled 是否启用
     * @param selected 是否选中
     * @return 标签文本
     */
    private static String getToggleLabel(boolean enabled, boolean selected) {
        String prefix = enabled ? "Enabled" : "Disabled";   // 前缀
        String suffix = selected ? "Selected" : "Deselected";  // 后缀
        return prefix + " and " + suffix;  // 组合前缀和后缀
    }
}