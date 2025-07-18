package com.virhuiai.log.jcef109.ui;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * 这是 处理快捷键的工具类
 */
public class ShortcutKeyUtils {
    private ShortcutKeyUtils() {
        // 私有构造函数防止实例化
    }

    /**
     * 为窗口组件绑定ESC键关闭功能
     *
     * @param window 需要绑定ESC键的窗口组件（如JDialog、JFrame等）
     * @param customAction 可选的自定义操作，如果为null则默认调用dispose()
     */
    public static void bindEscapeKey(Window window, ActionListener customAction) {
        if (!(window instanceof RootPaneContainer)) {
            throw new IllegalArgumentException("窗口组件必须实现RootPaneContainer接口");
        }

        // 获取根面板
        JRootPane rootPane = ((RootPaneContainer) window).getRootPane();

        // 创建ESC键的KeyStroke
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        // 创建动作
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (customAction != null) {
                    // 执行自定义操作
                    customAction.actionPerformed(e);
                } else {
                    // 默认关闭窗口
                    window.dispose();
                }
            }
        };

        // 绑定ESC键
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", escapeAction);
    }

}
