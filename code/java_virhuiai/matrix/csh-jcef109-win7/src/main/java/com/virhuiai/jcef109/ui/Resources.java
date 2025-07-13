

package com.virhuiai.jcef109.ui;

// 导入 Swing 的图标组件

import javax.swing.ImageIcon;

/**
 * 资源工具类
 * 用于加载和管理应用程序的图标等资源文件
 */
public class Resources {

    /**
     * 获取图标资源
     *
     * @param fileName 图标文件名
     * @return 返回 ImageIcon 对象
     *
     * 此方法根据传入的文件名从类路径中加载图标资源
     * 图标文件应位于项目的资源根目录下
     */
    public static ImageIcon getIcon(String fileName) {
        // 从类路径加载图标资源并返回 ImageIcon 对象
        // Resources.class.getResource() 用于获取类路径下的资源
        return new ImageIcon(Resources.class.getResource("/" + fileName));
    }
}