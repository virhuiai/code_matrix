

package com.virhuiai.jcef109.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 单一tab
 * Single tab
 *
 *
 */
public class TabCaption extends JPanel {

    // 选中状态标志
    private boolean selected;
    // Tab组件
    private TabCaptionComponent component;

    public TabCaption() {
        // 设置布局为边界布局
        setLayout(new BorderLayout());
        // 设置为透明
        setOpaque(false);
        // 添加组件到中心位置
        add(createComponent(), BorderLayout.CENTER);
        // 在右侧添加1个像素的水平间距
        add(Box.createHorizontalStrut(1), BorderLayout.EAST);
    }

    private JComponent createComponent() {
        // 创建Tab组件
        component = new TabCaptionComponent();
        // 添加关闭按钮事件监听器
        component.addPropertyChangeListener("CloseButtonPressed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("CloseButtonPressed", evt.getOldValue(), evt.getNewValue());
            }
        });
        // 添加Tab点击事件监听器
        component.addPropertyChangeListener("TabClicked", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setSelected(true);
            }
        });
        return component;
    }

    @Override
    public Dimension getPreferredSize() {
        // 返回首选大小 155x26
        return new Dimension(155, 26);
    }

    @Override
    public Dimension getMinimumSize() {
        // 返回最小大小 50x26
        return new Dimension(50, 26);
    }

    @Override
    public Dimension getMaximumSize() {
        // 返回最大大小，与首选大小相同
        return getPreferredSize();
    }

    // 设置标题
    public void setTitle(String title) {
        component.setTitle(title);
    }

    // 获取选中状态
    public boolean isSelected() {
        return selected;
    }

    // 设置选中状态
    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        component.setSelected(selected);
        firePropertyChange("TabSelected", oldValue, selected);
    }

    // Tab标题组件内部类
    private static class TabCaptionComponent extends JPanel {

        // 标签控件
        private JLabel label;
        // 默认背景色
        private final Color defaultBackground;

        private TabCaptionComponent() {
            defaultBackground = getBackground();
            // 设置布局为边界布局
            setLayout(new BorderLayout());
            // 设置为透明
            setOpaque(false);
            // 添加标签到中心
            add(createLabel(), BorderLayout.CENTER);
            // 添加关闭按钮到右侧
            add(createCloseButton(), BorderLayout.EAST);
        }

        private JComponent createLabel() {
            // 创建标签
            label = new JLabel();
            // 设置为透明
            label.setOpaque(false);
            // 设置边距
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            // 添加鼠标事件监听器
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // 左键点击选中Tab
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        firePropertyChange("TabClicked", false, true);
                    }
                    // 中键点击关闭Tab
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        firePropertyChange("CloseButtonPressed", false, true);
                    }
                }
            });
            return label;
        }

        private JComponent createCloseButton() {
            // 创建关闭按钮
            JButton closeButton = new JButton();
            // 设置为透明
            closeButton.setOpaque(false);
            // 设置提示文本
            closeButton.setToolTipText("Close");
            // 设置边距
            closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            // 设置按下时的图标
            closeButton.setPressedIcon(Resources.getIcon("close-pressed.png"));
            // 设置默认图标
            closeButton.setIcon(Resources.getIcon("close.png"));
            // 不填充内容区域
            closeButton.setContentAreaFilled(false);
            // 不可获得焦点
            closeButton.setFocusable(false);
            // 添加点击事件监听器
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    firePropertyChange("CloseButtonPressed", false, true);
                }
            });
            return closeButton;
        }

        // 设置标题
        public void setTitle(final String title) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    label.setText(title);
                    label.setToolTipText(title);
                }
            });
        }

        // 设置选中状态
        public void setSelected(boolean selected) {
            setBackground(selected ? Color.WHITE : new Color(231, 234, 237));
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            // 创建2D图形对象
            Graphics2D g2d = (Graphics2D) g.create();
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置渐变填充
            g2d.setPaint(new GradientPaint(0, 0, getBackground(), 0, getHeight(), getBackground()));
            // 填充矩形区域
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paint(g);
        }
    }
}
