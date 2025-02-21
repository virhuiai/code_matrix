// 版权声明 (c) 2000-2015 TeamDev Ltd. 保留所有权利
// TeamDev 专有和机密
// 使用须遵守许可条款

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.demo.resources.Resources;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author TeamDev Ltd.
 *
 * 这是一个JavaScript控制台的实现，主要功能和结构如下：
 *
 * 1. 主要组件：
 *    - 标题栏：显示"JavaScript 控制台"标题和关闭按钮
 *    - 输出区域：显示JavaScript代码执行结果的文本区域
 *    - 输入区域：用于输入JavaScript代码的文本框
 * 2. 核心功能：
 *    - 允许用户输入JavaScript代码
 *    - 执行输入的代码并显示执行结果
 *    - 使用线程池处理代码执行
 *    - 保持执行历史记录
 * 3. 界面特点：
 *    - 使用Swing组件构建用户界面
 *    - 支持文本换行
 *    - 提供滚动条
 *    - 包含关闭按钮
 * 4. 关键方法：
 *    - 创建控制台输入()：创建输入区域
 *    - 创建控制台输出()：创建输出显示区域
 *    - 更新控制台输出()：处理并显示执行结果
 *    - 创建关闭按钮()：创建具有图标的关闭按钮
 *
 * 这个控制台可以用于调试和测试JavaScript代码，类似于浏览器的开发者工具中的控制台功能。
 */
public class JS控制台 extends JPanel {

    private static final String 换行符 = "\n";
    private static final String 查询行开始符 = ">> ";

    private JTextArea 控制台;
    private final Browser 浏览器;
    private final ExecutorService 执行器;

    public JS控制台(Browser browser) {
        this.浏览器 = browser;
        this.执行器 = Executors.newCachedThreadPool();
        setLayout(new BorderLayout());
        add(创建标题(), BorderLayout.NORTH);
        add(创建控制台输出(), BorderLayout.CENTER);
        add(创建控制台输入(), BorderLayout.SOUTH);
    }

    private JComponent 创建控制台输入() {
        JPanel 结果 = new JPanel(new BorderLayout());
        结果.setBackground(Color.WHITE);

        JLabel 标签 = new JLabel(查询行开始符);
        标签.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));

        final JTextField 控制台输入 = new JTextField();
        控制台输入.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        控制台输入.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                执行器.submit(new Runnable() {
                    public void run() {
                        final String 脚本 = 控制台输入.getText();
                        JSValue js值 = 浏览器.executeJavaScriptAndReturnValue(脚本);
                        final String 执行结果 = js值.toString();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                更新控制台输出(脚本, 执行结果);
                                控制台输入.setText("");
                            }
                        });
                    }
                });
            }
        });
        结果.add(标签, BorderLayout.WEST);
        结果.add(控制台输入, BorderLayout.CENTER);
        return 结果;
    }

    private JComponent 创建控制台输出() {
        控制台 = new JTextArea();
        控制台.setFont(new Font("Consolas", Font.PLAIN, 12));
        控制台.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        控制台.setEditable(false);
        控制台.setWrapStyleWord(true);
        控制台.setLineWrap(true);
        控制台.setText("");
        JScrollPane 滚动面板 = new JScrollPane(控制台);
        滚动面板.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return 滚动面板;
    }

    private JComponent 创建标题() {
        JPanel 面板 = new JPanel(new BorderLayout());
        面板.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        面板.add(创建标题标签(), BorderLayout.WEST);
        面板.add(创建关闭按钮(), BorderLayout.EAST);
        return 面板;
    }

    private static JComponent 创建标题标签() {
        return new JLabel("JavaScript 控制台");
    }

    private JComponent 创建关闭按钮() {
        JButton 关闭按钮 = new JButton();
        关闭按钮.setOpaque(false);
        关闭按钮.setToolTipText("关闭 JavaScript 控制台");
        关闭按钮.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        关闭按钮.setPressedIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/close-pressed.png"));
        关闭按钮.setIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/close.png"));
        关闭按钮.setContentAreaFilled(false);
        关闭按钮.setFocusable(false);
        关闭按钮.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                firePropertyChange("JS控制台已关闭", false, true);
            }
        });
        return 关闭按钮;
    }

    private void 更新控制台输出(String 脚本, String 执行结果) {
        显示脚本(脚本);
        显示执行结果(执行结果);
        控制台.setCaretPosition(控制台.getText().length());
    }

    private void 显示执行结果(String 结果) {
        控制台.append(结果);
        控制台.append(换行符);
    }

    private void 显示脚本(String 脚本) {
        控制台.append(查询行开始符);
        控制台.append(脚本);
        控制台.append(换行符);
    }
}