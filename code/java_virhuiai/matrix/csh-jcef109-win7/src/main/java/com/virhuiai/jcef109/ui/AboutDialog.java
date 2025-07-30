
package com.virhuiai.jcef109.ui;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;

/**
 *
 * 关于对话框类
 */
public class AboutDialog extends JDialog {
    /**
     * 构造函数
     * @param owner 父窗口
     */
    public AboutDialog(Frame owner) {
        super(owner, "关于", true);
        initContent(); // 初始化对话框内容

//        ShortcutKeyUtils.bindEscapeKey(this, null);
        ShortcutKeyUtils.bindEscapeKey(this,e ->{
                    dispose();
                }
        );

//        initKeyStroke(); // 初始化按键监听
        setResizable(false); // 设置对话框不可调整大小
        pack(); // 调整对话框大小以适应内容
        setLocationRelativeTo(owner); // 设置对话框位置相对于父窗口居中
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // 设置关闭操作为释放资源
    }
    /**
     * 初始化对话框内容
     * 创建并配置显示"关于"信息的文本面板
     */
    private void initContent() {
        // 创建一个JTextPane文本面板组件，它支持富文本显示
        JTextPane aboutText = new JTextPane();

        // 设置文本面板的内容类型为HTML，这样可以支持HTML格式的文本显示
        aboutText.setContentType("text/html");

        // 设置文本面板中显示的文本内容
//        aboutText.setText("开源浏览器Jcef");
        // 使用HTML格式设置文本内容，包含超链接
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; font-size: 12px;'>" +
                "<h2>开源浏览器Jcef</h2>" +
                "<p>这是一个基于JCEF的开源浏览器项目</p>" +
                "<p>项目地址：<a href='https://github.com/virhuiai/code_matrix/tree/main/code'>GitHub的地址</a></p>" +
                "</body>" +
                "</html>";

        aboutText.setText(htmlContent); // 设置HTML格式的文本内容

        // 设置文本面板的边距，参数依次为：上、左、下、右各20像素的空白边距
        aboutText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 设置文本面板为不可编辑，用户只能查看不能修改文本内容
        aboutText.setEditable(false);

        // 添加超链接事件监听器，用于处理用户点击超链接的事件
        aboutText.addHyperlinkListener(new HyperlinkListener() {
            // 压制参数名称与重写方法不同的警告
            @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                // 当超链接被激活（即被点击）时执行以下代码
                if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        // 获取系统桌面环境对象
                        Desktop desktop = Desktop.getDesktop();

                        // 打印将要打开的URL地址（转换为URI格式）
                        System.out.println("event.getURL().toURI()：" + event.getURL().toURI());

                        // 使用系统默认浏览器打开超链接对应的URL
                        desktop.browse(event.getURL().toURI());
                    } catch (Exception e) {
                        // 如果发生异常，将其转换为运行时异常并抛出
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // 将配置好的文本面板添加到当前容器的中央位置
        add(aboutText, BorderLayout.CENTER);
    }
//    /**
//     * 初始化按键监听
//     * 设置ESC键关闭对话框的功能
//     * 这个方法通过两种方式实现ESC键关闭窗口：
//     * 1. 键盘事件监听器
//     * 2. 全局按键绑定
//     */
//    private void initKeyStroke() {
////    // 第一种方式：使用KeyAdapter添加键盘事件监听[传统的键盘事件监听方式]
////    addKeyListener(new KeyAdapter() {
////        @Override
////        public void keyPressed(KeyEvent e) {
////            // 当检测到按下的键是ESC键时
////            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
////                dispose(); // 调用dispose()方法关闭当前对话框
////            }
////        }
////    });
//
//        // 第二种方式：使用按键绑定（Key Bindings）[更现代的按键绑定方式，可以避免焦点问题]
//        // 获取对话框的根面板，用于设置按键绑定
//        JRootPane rootPane = getRootPane();
//
//        // 创建ESC键的按键触发器
//        // 参数说明：
//        // - KeyEvent.VK_ESCAPE: 表示ESC键的虚拟键码
//        // - 0: 表示没有组合键（如Ctrl、Alt等）
//        // - false: 表示按键按下时触发，而不是释放时触发
//        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
//
//        // 将按键绑定添加到输入映射中
//        // WHEN_IN_FOCUSED_WINDOW表示即使组件没有焦点也能响应按键
//        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "ESCAPE");
//
//        // 创建并绑定按键触发后要执行的动作
//        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
//            // 为实现序列化接口定义的序列化版本ID
//            private static final long serialVersionUID = 6693635607417495802L;
//
//            // 定义按键触发后的具体行为
//            public void actionPerformed(ActionEvent e) {
//                dispose(); // 关闭对话框
//            }
//        });
//    }
}