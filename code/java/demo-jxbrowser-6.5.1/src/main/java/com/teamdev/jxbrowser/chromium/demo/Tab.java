// 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
// TeamDev 专有和机密。
// 使用须遵守许可条款。

package com.teamdev.jxbrowser.chromium.demo;

/**
 * 标签页类
 * @author TeamDev Ltd.
 *
 * 1. 这是一个名为`Tab`的类，用于表示浏览器中的标签页。
 * 2. 类中包含两个私有的final字段：
 *    - `caption`: 类型为TabCaption，表示标签页的标题
 *    - `content`: 类型为TabContent，表示标签页的内容
 * 3. 构造函数：
 *    - 接收两个参数：标题(caption)和内容(content)
 *    - 将参数值分别赋给对应的成员变量
 * 4. 两个公共的getter方法：
 *    - `getCaption()`: 返回标签页标题
 *    - `getContent()`: 返回标签页内容
 * 5. 这是一个典型的Java Bean结构，使用了不可变设计模式（成员变量都是final的）
 *
 * 这个类是JxBrowser（一个Java Chromium嵌入式浏览器框架）示例代码的一部分，用于管理浏览器标签页的基本信息。
 *
 * 需要说明的是，这个类依赖于`TabCaption`和`TabContent`两个类，这些类在代码中并未显示，但它们应该分别用于处理标签页的标题和内容相关功能。
 */
public class Tab {
    // 标签页标题
    private final TabCaption caption;
    // 标签页内容
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
     * @return 标签页标题
     */
    public TabCaption getCaption() {
        return caption;
    }

    /**
     * 获取标签页内容
     * @return 标签页内容
     */
    public TabContent getContent() {
        return content;
    }
}