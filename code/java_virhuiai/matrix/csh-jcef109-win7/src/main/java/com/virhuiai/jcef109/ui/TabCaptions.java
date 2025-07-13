/*

 */

package com.virhuiai.jcef109.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;

/**
 * 多个tab
 * Multiple tabs // 多个标签页
 *
 */
public class TabCaptions extends JPanel {

    // 当前选中的标签页
    private TabCaption selectedTab;

    // 存放标签页的面板
    private JPanel tabsPane;
    // 存放按钮的面板
    private JPanel buttonsPane;

    /**
     * 构造函数,初始化UI
     */
    public TabCaptions() {
        createUI();
    }

    /**
     * 创建界面UI
     */
    private void createUI() {
        // 设置布局为水平BoxLayout
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // 设置背景颜色为浅灰色
        setBackground(new Color(231,234,237));
        // 添加标签页面板
        add(createItemsPane());
        // 添加按钮面板
        add(createButtonsPane());
        // 添加水平胶水组件,用于自动填充剩余空间
        add(Box.createHorizontalGlue());
    }

    /**
     * 创建标签页面板
     */
    private JComponent createItemsPane() {
        tabsPane = new JPanel();
        // 设置为透明
        tabsPane.setOpaque(false);
        // 设置水平BoxLayout布局
        tabsPane.setLayout(new BoxLayout(tabsPane, BoxLayout.X_AXIS));
        return tabsPane;
    }

    /**
     * 创建按钮面板
     */
    private JComponent createButtonsPane() {
        buttonsPane = new JPanel();
        // 设置为透明
        buttonsPane.setOpaque(false);
        // 设置水平BoxLayout布局
        buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
        return buttonsPane;
    }

    /**
     * 添加标签页
     * @param item 要添加的标签页
     */
    public void addTab(TabCaption item) {
        tabsPane.add(item);
    }

    /**
     * 移除标签页
     * @param item 要移除的标签页
     */
    public void removeTab(TabCaption item) {
        tabsPane.remove(item);
    }

    /**
     * 添加标签页按钮
     * @param button 要添加的按钮
     */
    public void addTabButton(TabButton button) {
        buttonsPane.add(button);
    }

    /**
     * 获取当前选中的标签页
     * @return 当前选中的标签页
     */
    public TabCaption getSelectedTab() {
        return selectedTab;
    }

    /**
     * 设置选中的标签页
     * @param selectedTab 要设置为选中状态的标签页
     */
    public void setSelectedTab(TabCaption selectedTab) {
        this.selectedTab = selectedTab;
        // 设置标签页为选中状态
        this.selectedTab.setSelected(true);
    }
}