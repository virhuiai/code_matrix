

package com.virhuiai.jcef109.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 标签页面板类,实现标签页功能的主要容器组件
 */
public class TabbedPane extends JPanel {

    // 存储所有标签页对象的列表
    private final List<Tab> tabs;
    // 标签页标题栏组件
    private final TabCaptions captions;
    // 标签页内容容器
    private final JComponent contentContainer;

    /**
     * 构造函数,初始化标签页面板
     */
    public TabbedPane() {
        this.captions = new TabCaptions();
        this.tabs = new ArrayList<Tab>();
        this.contentContainer = new JPanel(new BorderLayout());

        // 设置布局为边界布局
        setLayout(new BorderLayout());
        // 将标签标题栏添加到北部
        add(captions, BorderLayout.NORTH);
        // 将内容容器添加到中央
        add(contentContainer, BorderLayout.CENTER);
    }

    /**
     * 关闭并释放所有标签页
     */
    public void disposeAllTabs() {
        for (Tab tab : getTabs()) {
            disposeTab(tab);
        }
    }

    /**
     * 关闭并释放指定标签页
     * @param tab 要关闭的标签页
     */
    private void disposeTab(Tab tab) {
        // 取消选中状态
        tab.getCaption().setSelected(false);
        // 释放内容组件资源
        tab.getContent().dispose();
        // 移除标签页
        removeTab(tab);
        if (hasTabs()) {
            // 如果还有其他标签页,选中第一个
            Tab firstTab = getFirstTab();
            firstTab.getCaption().setSelected(true);
        } else {
            // 如果没有标签页了,关闭窗口
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.setVisible(false);
                window.dispose();
            }
        }
    }

    /**
     * 根据标签标题查找对应的标签页对象
     * @param item 标签标题对象
     * @return 对应的标签页对象,如果未找到返回null
     */
    private Tab findTab(TabCaption item) {
        for (Tab tab : getTabs()) {
            if (tab.getCaption().equals(item)) {
                return tab;
            }
        }
        return null;
    }

    /**
     * 添加新的标签页
     * @param tab 要添加的标签页对象
     */
    public void addTab(final Tab tab) {
        TabCaption caption = tab.getCaption();
        // 添加关闭按钮事件监听
        caption.addPropertyChangeListener("CloseButtonPressed", new TabCaptionCloseTabListener());
        // 添加标签选中事件监听
        caption.addPropertyChangeListener("TabSelected", new SelectTabListener());

        TabContent content = tab.getContent();
        // 添加内容关闭事件监听
        content.addPropertyChangeListener("TabClosed", new TabContentCloseTabListener());

        // 添加标签到组件中
        captions.addTab(caption);
        tabs.add(tab);
        validate();
        repaint();
    }

    /**
     * 判断是否有标签页
     * @return 有标签页返回true,否则返回false
     */
    private boolean hasTabs() {
        return !tabs.isEmpty();
    }

    /**
     * 获取第一个标签页
     * @return 第一个标签页对象
     */
    private Tab getFirstTab() {
        return tabs.get(0);
    }

    /**
     * 获取所有标签页列表的副本
     * @return 标签页列表的副本
     */
    private List<Tab> getTabs() {
        return new ArrayList<Tab>(tabs);
    }

    /**
     * 移除指定标签页
     * @param tab 要移除的标签页
     */
    public void removeTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        captions.removeTab(tabCaption);
        tabs.remove(tab);
        validate();
        repaint();
    }

    /**
     * 添加标签页按钮
     * @param button 要添加的按钮
     */
    public void addTabButton(TabButton button) {
        captions.addTabButton(button);
    }

    /**
     * 选中指定标签页
     * @param tab 要选中的标签页
     */
    public void selectTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        TabCaption selectedTab = captions.getSelectedTab();
        if (selectedTab != null && !selectedTab.equals(tabCaption)) {
            selectedTab.setSelected(false);
        }
        captions.setSelectedTab(tabCaption);
    }

    /**
     * 标签页关闭按钮点击事件监听器
     */
    private class TabCaptionCloseTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            disposeTab(tab);
        }
    }

    /**
     * 标签页选中状态改变事件监听器
     */
    private class SelectTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            if (caption.isSelected()) {
                selectTab(tab);
            }
            if (!caption.isSelected()) {
                // 移除未选中标签页的内容
                TabContent content = tab.getContent();
                contentContainer.remove(content);
                contentContainer.validate();
                contentContainer.repaint();
            } else {
                // 显示选中标签页的内容
                final TabContent content = tab.getContent();
                contentContainer.add(content, BorderLayout.CENTER);
                contentContainer.validate();
                contentContainer.repaint();
            }
        }
    }

    /**
     * 标签页内容关闭事件监听器
     */
    private class TabContentCloseTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabContent content = (TabContent) evt.getSource();
            Tab tab = findTab(content);
            disposeTab(tab);
        }

        /**
         * 根据内容组件查找对应的标签页
         * @param content 内容组件
         * @return 对应的标签页对象,如果未找到返回null
         */
        private Tab findTab(TabContent content) {
            for (Tab tab : getTabs()) {
                if (tab.getContent().equals(content)) {
                    return tab;
                }
            }
            return null;
        }
    }
}