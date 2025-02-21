// 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
// TeamDev 专有和机密。
// 使用须遵守许可条款。

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.EditorCommand;
import javax.swing.JMenuItem;

/**
 * @author Artem Trofimov
 *
 * 这段代码的主要功能说明：
 *
 * 1. 这是一个自定义的菜单项类(CommandMenuItem)，继承自Swing的JMenuItem类
 * 2. 该类主要用于在图形界面中创建带有编辑器命令功能的菜单项
 * 3. 类中包含:
 *    - 一个final类型的EditorCommand成员变量，用于存储编辑器命令
 *    - 一个构造函数，接收命令名称和编辑器命令作为参数
 *    - 一个getter方法用于获取存储的编辑器命令
 * 4. 这个类的设计目的是将编辑器命令与菜单项进行关联，使用户可以通过菜单操作触发相应的编辑命令
 */
public class CommandMenuItem extends JMenuItem {

    // 存储编辑器命令的私有成员变量
    private final EditorCommand command;

    /**
     * 构造函数
     * @param commandName 命令显示的名称
     * @param command 对应的编辑器命令
     */
    public CommandMenuItem(String commandName, EditorCommand command) {
        super(commandName);
        this.command = command;
    }

    /**
     * 获取编辑器命令
     * @return 返回存储的编辑器命令
     */
    public EditorCommand getCommand() {
        return command;
    }
}