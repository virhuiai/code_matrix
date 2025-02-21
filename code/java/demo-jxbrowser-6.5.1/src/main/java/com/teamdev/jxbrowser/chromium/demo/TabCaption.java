/*
 * Copyright (c) 2000-2015 TeamDev Ltd. 保留所有权利。
 * TeamDev 专有和机密。
 * 使用需遵守许可条款。
 */

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author TeamDev Ltd.
 * 这是一个标签页标题栏的实现，用于浏览器标签页显示。
 *
 * 主要特点：
 *
 * - 包含标题文本和关闭按钮
 * - 支持选中/未选中状态的显示
 * - 具有渐变背景效果
 * - 支持鼠标点击事件处理
 *
 * 核心组件：
 *
 * - TabCaption：主要的标签容器类
 * - TabCaptionComponent：实际的标签组件实现
 *
 * 主要功能：
 *
 * - 创建标签页标题组件
 * - 处理标签的选中状态
 * - 处理关闭按钮事件
 * - 支持标题文本的设置
 * - 实现渐变背景绘制
 *
 * 交互功能：
 *
 * - 左键点击选中标签
 * - 中键点击关闭标签
 * - 点击关闭按钮关闭标签
 *
 * 视觉效果：
 *
 * - 使用渐变背景
 * - 支持抗锯齿渲染
 * - 标签选中状态有不同的背景色显示
 */
public class TabCaption extends JPanel {

    private boolean selected;
    private TabCaptionComponent component;

    public TabCaption() {
        setLayout(new BorderLayout());
        setOpaque(false);
        add(createComponent(), BorderLayout.CENTER);
        add(Box.createHorizontalStrut(1), BorderLayout.EAST);
    }

    private JComponent createComponent() {
        component = new TabCaptionComponent();
        component.addPropertyChangeListener("CloseButtonPressed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("CloseButtonPressed", evt.getOldValue(), evt.getNewValue());
            }
        });
        component.addPropertyChangeListener("TabClicked", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setSelected(true);
            }
        });
        return component;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(155, 26);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(50, 26);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void setTitle(String title) {
        component.setTitle(title);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        component.setSelected(selected);
        firePropertyChange("TabSelected", oldValue, selected);
    }

    private static class TabCaptionComponent extends JPanel {

        private JLabel label;
        private final Color defaultBackground;

        private TabCaptionComponent() {
            defaultBackground = getBackground();
            setLayout(new BorderLayout());
            setOpaque(false);
            add(createLabel(), BorderLayout.CENTER);
            add(createCloseButton(), BorderLayout.EAST);
        }

        private JComponent createLabel() {
            label = new JLabel();
            label.setOpaque(false);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        firePropertyChange("TabClicked", false, true);
                    }
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        firePropertyChange("CloseButtonPressed", false, true);
                    }
                }
            });
            return label;
        }

        private JComponent createCloseButton() {
            JButton closeButton = new JButton();
            closeButton.setOpaque(false);
            closeButton.setToolTipText("Close");
            closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            closeButton.setPressedIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/close-pressed.png"));
            closeButton.setIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/close.png"));
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusable(false);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    firePropertyChange("CloseButtonPressed", false, true);
                }
            });
            return closeButton;
        }

        public void setTitle(final String title) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    label.setText(title);
                    label.setToolTipText(title);
                }
            });
        }

        public void setSelected(boolean selected) {
            setBackground(selected ? defaultBackground : new Color(150, 150, 150));
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, getHeight(), getBackground()));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paint(g);
        }
    }
}
