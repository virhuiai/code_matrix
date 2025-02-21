// 版权所有 (c) 2000-2015 TeamDev Ltd. 保留所有权利。
// TeamDev 专有和机密。
// 使用须遵守许可条款。

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.ProductInfo;
import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Frame;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

/**
 * @author TeamDev Ltd.
 *
 */
public class AboutDialog extends JDialog {
    public AboutDialog(Frame owner) {
        super(owner, "关于 JxBrowser 演示", true);
        初始化内容();
        初始化快捷键();
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void 初始化内容() {
        JLabel 图标 = new JLabel();
        图标.setIcon(Resources.getIcon("/com/teamdev/jxbrowser/chromium/demo/resources/jxbrowser32x32.png"));
        JLabel 应用名称 = new JLabel("JxBrowser 演示");
        JLabel 版本号 = new JLabel("版本: " + ProductInfo.getVersion());
        JLabel 公司名 = new JLabel("\u00A9 "
                + Calendar.getInstance().get(Calendar.YEAR) + " TeamDev Ltd.");
        JLabel 版权声明 = new JLabel("保留所有权利。");

        图标.setAlignmentX(CENTER_ALIGNMENT);
        应用名称.setAlignmentX(CENTER_ALIGNMENT);
        应用名称.setFont(应用名称.getFont().deriveFont(Font.BOLD));
        版本号.setAlignmentX(CENTER_ALIGNMENT);
        公司名.setAlignmentX(CENTER_ALIGNMENT);
        版权声明.setAlignmentX(CENTER_ALIGNMENT);

        JPanel 内容面板 = new JPanel();
        内容面板.setBorder(new EmptyBorder(30, 70, 30, 70));
        内容面板.setLayout(new BoxLayout(内容面板, BoxLayout.Y_AXIS));
        内容面板.add(图标);
        内容面板.add(Box.createVerticalStrut(16));
        内容面板.add(应用名称);
        内容面板.add(Box.createVerticalStrut(8));
        内容面板.add(版本号);
        内容面板.add(Box.createVerticalStrut(8));
        内容面板.add(公司名);
        内容面板.add(Box.createVerticalStrut(4));
        内容面板.add(版权声明);
        setContentPane(内容面板);
    }

    private void 初始化快捷键() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        JRootPane 根面板 = getRootPane();
        KeyStroke 按键 = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        根面板.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(按键, "ESCAPE");
        根面板.getActionMap().put("ESCAPE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}