package com.virhuiai.CshWebSocket;/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

// 导入必要的Java类库
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocket聊天客户端类，继承自JFrame并实现ActionListener接口
 * 提供基于WebSocket的聊天功能图形界面
 */
public class ChatClient extends JFrame implements ActionListener {

  // 序列化ID
  private static final long serialVersionUID = -6056260699202978657L;

  // 界面组件声明
  private final JTextField uriField; // 用于输入WebSocket服务器URI的文本框
  private final JButton connect;     // 连接按钮
  private final JButton close;       // 关闭连接按钮
  private final JTextArea ta;        // 显示聊天消息的文本区域
  private final JTextField chatField; // 输入聊天消息的文本框
  private final JComboBox draft;     // WebSocket协议草案选择下拉框
  private WebSocketClient cc;        // WebSocket客户端对象

  /**
   * 构造函数，初始化聊天客户端界面
   * @param defaultlocation 默认的WebSocket服务器地址
   */
  public ChatClient(String defaultlocation) {
    super("WebSocket Chat Client"); // 设置窗口标题
    Container c = getContentPane(); // 获取内容面板
    GridLayout layout = new GridLayout(); // 创建网格布局
    layout.setColumns(1); // 设置列数为1
    layout.setRows(6);    // 设置行数为6
    c.setLayout(layout);  // 应用布局

    // 初始化WebSocket协议草案选择框
    Draft[] drafts = {new Draft_6455()};
    draft = new JComboBox(drafts);
    c.add(draft);

    // 初始化URI输入框，并设置默认值
    uriField = new JTextField();
    uriField.setText(defaultlocation);
    c.add(uriField);

    // 初始化连接按钮
    connect = new JButton("Connect");
    connect.addActionListener(this); // 添加动作监听器
    c.add(connect);

    // 初始化关闭连接按钮，默认禁用
    close = new JButton("Close");
    close.addActionListener(this);
    close.setEnabled(false);
    c.add(close);

    // 初始化带滚动条的文本区域，用于显示聊天消息
    JScrollPane scroll = new JScrollPane();
    ta = new JTextArea();
    scroll.setViewportView(ta);
    c.add(scroll);

    // 初始化聊天输入框
    chatField = new JTextField();
    chatField.setText("");
    chatField.addActionListener(this); // 添加动作监听器
    c.add(chatField);

    // 设置窗口尺寸
    java.awt.Dimension d = new java.awt.Dimension(300, 400);
    setPreferredSize(d);
    setSize(d);

    // 添加窗口关闭事件处理
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if (cc != null) {
          cc.close(); // 关闭WebSocket连接
        }
        dispose(); // 释放窗口资源
      }
    });

    // 设置窗口位置并显示
    setLocationRelativeTo(null); // 窗口居中
    setVisible(true); // 显示窗口
  }

  /**
   * 处理界面各组件的动作事件
   * @param e 动作事件对象
   */
  public void actionPerformed(ActionEvent e) {

    // 处理聊天输入框的动作
    if (e.getSource() == chatField) {
      if (cc != null) {
        cc.send(chatField.getText()); // 发送聊天消息
        chatField.setText(""); // 清空输入框
        chatField.requestFocus(); // 聚焦回输入框
      }

    // 处理连接按钮点击事件
    } else if (e.getSource() == connect) {
      try {
        // cc = new ChatClient(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
        // 创建并初始化WebSocket客户端，使用匿名内部类实现WebSocketClient抽象类
        cc = new WebSocketClient(new URI(uriField.getText()), (Draft) draft.getSelectedItem()) {

          /**
           * 接收到消息时的回调方法
           * @param message 接收到的消息内容
           */
          @Override
          public void onMessage(String message) {
            ta.append("got: " + message + "\n"); // 在文本区域显示收到的消息
            ta.setCaretPosition(ta.getDocument().getLength()); // 滚动到最新消息
          }

          /**
           * 连接打开时的回调方法
           * @param handshake 服务器握手信息
           */
          @Override
          public void onOpen(ServerHandshake handshake) {
            ta.append("You are connected to ChatServer: " + getURI() + "\n"); // 显示连接成功信息
            ta.setCaretPosition(ta.getDocument().getLength()); // 滚动到最新消息
          }

          /**
           * 连接关闭时的回调方法
           * @param code 关闭代码
           * @param reason 关闭原因
           * @param remote 是否由远程端关闭
           */
          @Override
          public void onClose(int code, String reason, boolean remote) {
            ta.append(
                "You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason
                    + "\n"); // 显示连接关闭信息
            ta.setCaretPosition(ta.getDocument().getLength()); // 滚动到最新消息
            // 重置界面状态
            connect.setEnabled(true);
            uriField.setEditable(true);
            draft.setEditable(true);
            close.setEnabled(false);
          }

          /**
           * 发生错误时的回调方法
           * @param ex 异常对象
           */
          @Override
          public void onError(Exception ex) {
            ta.append("Exception occurred ...\n" + ex + "\n"); // 显示异常信息
            ta.setCaretPosition(ta.getDocument().getLength()); // 滚动到最新消息
            ex.printStackTrace(); // 打印异常堆栈
            // 重置界面状态
            connect.setEnabled(true);
            uriField.setEditable(true);
            draft.setEditable(true);
            close.setEnabled(false);
          }
        };

        // 更新界面组件状态
        close.setEnabled(true);
        connect.setEnabled(false);
        uriField.setEditable(false);
        draft.setEditable(false);
        cc.connect(); // 建立WebSocket连接
      } catch (URISyntaxException ex) {
        ta.append(uriField.getText() + " is not a valid WebSocket URI\n"); // 显示URI格式错误
      }
    // 处理关闭连接按钮点击事件
    } else if (e.getSource() == close) {
      cc.close(); // 关闭WebSocket连接
    }
  }

  /**
   * 程序入口点
   * @param args 命令行参数，可指定默认的WebSocket服务器地址
   */
  public static void main(String[] args) {
    String location;
    // 判断是否提供了命令行参数
    if (args.length != 0) {
      location = args[0]; // 使用提供的服务器地址
      System.out.println("Default server url specified: \'" + location + "\'");
    } else {
      location = "ws://localhost:8887"; // 使用默认服务器地址
      System.out.println("Default server url not specified: defaulting to \'" + location + "\'");
    }
    new ChatClient(location); // 创建并启动聊天客户端
  }

}
