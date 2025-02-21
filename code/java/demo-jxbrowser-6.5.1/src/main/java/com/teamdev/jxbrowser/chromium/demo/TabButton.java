// 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
// TeamDev 专有和机密。
// 使用需遵循许可条款。

package com.teamdev.jxbrowser.chromium.demo;

import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.BorderFactory;

/**
 * @author TeamDev Ltd.
 *
这是一个自定义的标签页按钮类(TabButton)，继承自Swing的JButton类

构造方法接收两个参数：

- icon: 按钮显示的图标
- toolTipText: 鼠标悬停时显示的提示文本

在构造方法中设置了按钮的多个属性：

- setIcon(): 设置按钮上显示的图标
- setToolTipText(): 设置鼠标悬停时的提示文本
- setOpaque(): 设置按钮是否透明，这里设为透明
- setBorder(): 设置按钮的边距，这里在四周都设置了4个像素的空白边距
- setContentAreaFilled(): 设置是否填充按钮的内容区域，这里设为不填充
- setFocusable(): 设置按钮是否可以获得焦点，这里设为不可获得焦点

这个类主要用于创建一个自定义外观的标签页按钮，通常用在浏览器标签页等界面中，具有简洁的外观和特定的交互行为。

import语句采用了完整写法，而不是使用通配符(*)，这样可以明确知道使用了哪些具体的类。
 */
public class TabButton extends JButton {

    public TabButton(Icon icon, String toolTipText) {
        setIcon(icon);                                                 // 设置按钮图标
        setToolTipText(toolTipText);                                  // 设置鼠标悬停提示文本
        setOpaque(false);                                             // 设置按钮背景透明
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));      // 设置按钮四周空白边距
        setContentAreaFilled(false);                                  // 不填充按钮内容区域
        setFocusable(false);                                         // 设置按钮不可获得焦点
    }
}