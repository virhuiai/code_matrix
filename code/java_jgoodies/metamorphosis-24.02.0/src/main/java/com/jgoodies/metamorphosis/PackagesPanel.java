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
import com.jgoodies.metamorphosis.Style;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * 包面板类，用于创建和管理包树形结构的面板。
 * 该面板显示项目和包的层次结构，类似于Eclipse IDE中的包浏览器。
 */
final class PackagesPanel {
    
    /**
     * 私有构造方法，防止实例化此类。
     * 该类仅作为构建包面板的工具类使用。
     */
    PackagesPanel() {
    }

    /**
     * 构建包面板。
     * 创建一个Eclipse风格的面板，包含一个显示项目和包层次结构的树形控件。
     * 
     * @return 构建好的包面板组件
     */
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Component build() {
        // 创建Eclipse风格的面板，标题为"Packages"
        JPanel panel = FakeclipseFactory.createEclipsePanel(
            Utils.getIcon("package.gif"),      // 左侧图标
            "Packages",                        // 标签文本
            Utils.getIcon("package_tools.gif"), // 右侧图标
            false                              // 非选中状态
        );
        
        // 创建树形控件，使用项目树数据
        JTree tree = new JTree(createProjectTree());
        tree.setRootVisible(false);    // 隐藏根节点
        tree.setShowsRootHandles(true); // 显示根节点手柄
        
        // 展开某些节点以显示初始内容
        tree.expandRow(4);
        tree.expandRow(5);
        tree.expandRow(0);
        
        // 如果当前样式使用WIN图标风格，则设置特殊的渲染器
        if (Style.getCurrent().iconStyle() == Style.IconStyle.WIN) {
            tree.setCellRenderer(new LabeledNodeRenderer());
        }
        
        // 创建滚动面板包装树形控件
        JScrollPane scrollPane = FakeclipseFactory.createScrollPane(tree);
        panel.add(scrollPane, "Center");  // 将滚动面板添加到面板中央
        
        // 设置面板的最小和首选大小
        panel.setMinimumSize(new Dimension(140, DelayedPropertyChangeHandler.DEFAULT_DELAY));
        panel.setPreferredSize(new Dimension(140, DelayedPropertyChangeHandler.DEFAULT_DELAY));
        
        return panel;
    }

    /**
     * 创建项目树的数据模型。
     * 构建一个包含多个项目及其包结构的树形数据结构。
     * 
     * @return 根节点，包含所有项目的树形结构
     */
    private static TreeNode createProjectTree() {
        // 加载各种图标资源
        ImageIcon icon = Utils.getIcon("project.gif");         // 项目图标
        ImageIcon icon2 = Utils.getIcon("fldr_obj.gif");       // 文件夹图标
        ImageIcon icon3 = Utils.getIcon("packagefolder_obj.gif"); // 包文件夹图标
        ImageIcon icon4 = Utils.getIcon("package.gif");        // 包图标
        ImageIcon icon5 = Utils.getIcon("javasrc.gif");        // Java源文件图标
        ImageIcon icon6 = Utils.getIcon("file_obj.gif");       // 一般文件图标
        
        // 创建根节点
        DefaultMutableTreeNode root = new LabeledNode(null, null);
        
        // 创建第一个项目：Chartster
        DefaultMutableTreeNode project = new LabeledNode(icon, "Chartster");
        DefaultMutableTreeNode folder0 = new LabeledNode(icon3, "src");
        project.add(folder0);
        folder0.add(new LabeledNode(icon4, "com.jgoodies.chartster"));
        DefaultMutableTreeNode folder02 = new LabeledNode(icon2, "build");
        folder02.add(new LabeledNode(icon6, "manifest.txt"));
        project.add(folder02);
        root.add(project);
        
        // 创建第二个项目：JDiskReport
        DefaultMutableTreeNode project2 = new LabeledNode(icon, "JDiskReport");
        DefaultMutableTreeNode folder03 = new LabeledNode(icon3, "src");
        project2.add(folder03);
        folder03.add(new LabeledNode(icon4, "com.jgoodies.jdiskreport"));
        project2.add(new LabeledNode(icon2, "build"));
        root.add(project2);
        
        // 创建第三个项目：JGoodies Animations
        DefaultMutableTreeNode project3 = new LabeledNode(icon, "JGoodies Animations");
        project3.add(new LabeledNode(icon3, "src"));
        project3.add(new LabeledNode(icon2, "build"));
        root.add(project3);
        
        // 创建第四个项目：JGoodies Charts
        DefaultMutableTreeNode project4 = new LabeledNode(icon, "JGoodies Charts");
        project4.add(new LabeledNode(icon3, "src"));
        project4.add(new LabeledNode(icon2, "build"));
        root.add(project4);
        
        // 创建第五个项目：JGoodies Looks
        DefaultMutableTreeNode project5 = new LabeledNode(icon, "JGoodies Looks");
        DefaultMutableTreeNode folder04 = new LabeledNode(icon3, "src");
        project5.add(folder04);
        DefaultMutableTreeNode folder1 = new LabeledNode(icon4, "com.jgoodies.looks.plastic");
        folder1.add(new LabeledNode(icon5, "PlasticLookAndFeel.java"));
        folder04.add(folder1);
        DefaultMutableTreeNode folder12 = new LabeledNode(icon4, "com.jgoodies.looks.windows");
        folder12.add(new LabeledNode(icon5, "WindowsLookAndFeel.java"));
        folder04.add(folder12);
        DefaultMutableTreeNode folder13 = new LabeledNode(icon2, "com.jgoodies.looks.plastic.icons");
        folder13.add(new LabeledNode(icon6, "resource.properties"));
        folder04.add(folder13);
        DefaultMutableTreeNode folder14 = new LabeledNode(icon2, "com.jgoodies.looks.windows.icons");
        folder14.add(new LabeledNode(icon6, "resource.properties"));
        folder04.add(folder14);
        DefaultMutableTreeNode folder05 = new LabeledNode(icon2, "build");
        folder05.add(new LabeledNode(icon6, "manifest.txt"));
        project5.add(folder05);
        root.add(project5);
        
        // 创建第六个项目：JGoodies Looks Demo
        DefaultMutableTreeNode project6 = new LabeledNode(icon, "JGoodies Looks Demo");
        project6.add(new LabeledNode(icon3, "src"));
        project6.add(new LabeledNode(icon2, "build"));
        root.add(project6);
        
        // 创建第七个项目：JGoodies Smart Client
        DefaultMutableTreeNode project7 = new LabeledNode(icon, "JGoodies Smart Client");
        project7.add(new LabeledNode(icon3, "src"));
        project7.add(new LabeledNode(icon2, "build"));
        root.add(project7);
        
        return root;  // 返回根节点
    }

    /**
     * 带标签的节点内部类。
     * 扩展DefaultMutableTreeNode，添加图标和标签属性。
     */
    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/metamorphosis/PackagesPanel$LabeledNode.class */
    public static class LabeledNode extends DefaultMutableTreeNode {
        
        /** 节点图标 */
        private final Icon icon;
        
        /** 节点标签文本 */
        private final String label;

        /**
         * 构造带标签的节点。
         * 
         * @param icon 节点图标
         * @param label 节点标签文本
         */
        LabeledNode(Icon icon, String label) {
            super(label);  // 调用父类构造函数
            this.icon = icon;
            this.label = label;
        }

        /**
         * 获取节点图标。
         * 
         * @return 节点图标
         */
        public Icon icon() {
            return this.icon;
        }

        /**
         * 获取节点标签文本。
         * 
         * @return 节点标签文本
         */
        public String label() {
            return this.label;
        }
    }

    /**
     * 带标签节点的渲染器内部类。
     * 自定义树形控件单元格的渲染方式，显示节点的图标和标签。
     */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/metamorphosis/PackagesPanel$LabeledNodeRenderer.class */
    private static class LabeledNodeRenderer extends DefaultTreeCellRenderer {
        
        /**
         * 私有构造方法，防止外部实例化。
         */
        private LabeledNodeRenderer() {
        }

        /**
         * 获取树形控件单元格的渲染组件。
         * 设置单元格的图标和文本为节点的图标和标签。
         * 
         * @param tree 树形控件
         * @param value 节点值
         * @param sel 是否选中
         * @param expanded 是否展开
         * @param leaf 是否为叶子节点
         * @param row 行号
         * @param focused 是否获得焦点
         * @return 渲染组件
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focused) {
            // 调用父类方法获取基础渲染组件
            JLabel label = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
            
            // 将节点值转换为LabeledNode类型
            LabeledNode node = (LabeledNode) value;
            
            // 设置标签的图标和文本
            label.setIcon(node.icon());
            label.setText(node.label());
            
            return label;
        }
    }
}