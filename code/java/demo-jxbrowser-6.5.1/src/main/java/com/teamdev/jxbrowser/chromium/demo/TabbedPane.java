/*
 * 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
 * TeamDev 专有和机密。
 * 使用需遵循许可条款。
 */

package com.teamdev.jxbrowser.chromium.demo;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TeamDev Ltd.
 */
public class TabbedPane extends JPanel {
    private final List<Tab> tabs;                    // 标签页列表
    private final TabCaptions captions;              // 标签页标题管理器
    private final JComponent contentContainer;        // 内容容器

    public TabbedPane() {
        this.captions = new TabCaptions();
        this.tabs = new ArrayList<Tab>();
        this.contentContainer = new JPanel(new BorderLayout());

        setLayout(new BorderLayout());
        add(captions, BorderLayout.NORTH);           // 标题栏放在北部
        add(contentContainer, BorderLayout.CENTER);   // 内容区放在中央
    }

    // 关闭所有标签页
    public void disposeAllTabs() {
        for (Tab tab : getTabs()) {
            disposeTab(tab);
        }
    }

    // 关闭单个标签页
    private void disposeTab(Tab tab) {
        tab.getCaption().setSelected(false);
        tab.getContent().dispose();
        removeTab(tab);
        if (hasTabs()) {
            // 如果还有标签页，选中第一个
            Tab firstTab = getFirstTab();
            firstTab.getCaption().setSelected(true);
        } else {
            // 如果没有标签页，关闭窗口
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.setVisible(false);
                window.dispose();
            }
        }
    }

    // 根据标签页标题查找标签页
    private Tab findTab(TabCaption item) {
        for (Tab tab : getTabs()) {
            if (tab.getCaption().equals(item)) {
                return tab;
            }
        }
        return null;
    }

    // 添加新标签页
    public void addTab(final Tab tab) {
        TabCaption caption = tab.getCaption();
        caption.addPropertyChangeListener("CloseButtonPressed", new TabCaptionCloseTabListener());
        caption.addPropertyChangeListener("TabSelected", new SelectTabListener());

        TabContent content = tab.getContent();
        content.addPropertyChangeListener("TabClosed", new TabContentCloseTabListener());

        captions.addTab(caption);
        tabs.add(tab);
        validate();
        repaint();
    }

    private boolean hasTabs() {
        return !tabs.isEmpty();
    }

    private Tab getFirstTab() {
        return tabs.get(0);
    }

    private List<Tab> getTabs() {
        return new ArrayList<Tab>(tabs);
    }

    // 移除标签页
    public void removeTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        captions.removeTab(tabCaption);
        tabs.remove(tab);
        validate();
        repaint();
    }

    // 添加标签页按钮（如新建标签页按钮）
    public void addTabButton(TabButton button) {
        captions.addTabButton(button);
    }

    // 选择标签页
    public void selectTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        TabCaption selectedTab = captions.getSelectedTab();
        if (selectedTab != null && !selectedTab.equals(tabCaption)) {
            selectedTab.setSelected(false);
        }
        captions.setSelectedTab(tabCaption);
    }

    // 标签页关闭按钮监听器
    private class TabCaptionCloseTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            disposeTab(tab);
        }
    }

    // 标签页选择监听器
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

    // 标签页内容关闭监听器
    private class TabContentCloseTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabContent content = (TabContent) evt.getSource();
            Tab tab = findTab(content);
            disposeTab(tab);
        }

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
