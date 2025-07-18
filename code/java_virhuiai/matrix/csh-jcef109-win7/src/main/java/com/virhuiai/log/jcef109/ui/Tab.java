

package com.virhuiai.log.jcef109.ui;

/**
 *
 *
 * 标签页类
 * 用于实现标签页组件
 * 包含标签页标题和内容两个主要部分
 */
public class Tab {

    // 标签页标题组件
    private final TabCaption caption;

    // 标签页内容组件
    private final TabContent content;

    /**
     * 构造函数
     * @param caption 标签页标题
     * @param content 标签页内容
     */
    public Tab(TabCaption caption, TabContent content) {
        this.caption = caption;
        this.content = content;
    }

    /**
     * 获取标签页标题
     * @return 标签页标题组件
     */
    public TabCaption getCaption() {
        return caption;
    }

    /**
     * 获取标签页内容
     * @return 标签页内容组件
     */
    public TabContent getContent() {
        return content;
    }
}