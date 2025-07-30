
package com.virhuiai.jcef109.ui; // 包声明

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * 自定义标签页按钮类,继承自JButton
 */
public class TabButton extends JButton {

    /**
     * 构造函数
     * @param icon 按钮图标
     * @param toolTipText 按钮提示文本
     */
    public TabButton(Icon icon, String toolTipText) {
        setIcon(icon); // 设置按钮图标
        setToolTipText(toolTipText); // 设置鼠标悬停提示文本
        setOpaque(false); // 设置按钮背景透明
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); // 设置按钮四周留白边框
        setContentAreaFilled(false); // 不填充按钮内容区域
        setFocusable(false); // 设置按钮不可获取焦点
    }

}