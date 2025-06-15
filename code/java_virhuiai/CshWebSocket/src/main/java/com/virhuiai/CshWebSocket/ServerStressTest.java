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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

/**
 * 服务器压力测试工具
 * 这个类提供了一个图形界面，用于对WebSocket服务器进行压力测试
 * 它允许用户控制客户端数量、消息发送间隔和客户端加入速率
 */
public class ServerStressTest extends JFrame {

  // GUI组件声明
  private JSlider clients; // 客户端数量滑块
  private JSlider interval; // 消息发送间隔滑块
  private JSlider joinrate; // 客户端加入速率滑块
  private JButton start, stop, reset; // 开始、停止和重置按钮
  private JLabel joinratelabel = new JLabel(); // 显示加入速率的标签
  private JLabel clientslabel = new JLabel(); // 显示客户端数量的标签
  private JLabel intervallabel = new JLabel(); // 显示消息间隔的标签
  private JTextField uriinput = new JTextField("ws://localhost:8887"); // WebSocket服务器地址输入框
  private JTextArea text = new JTextArea("payload"); // 消息内容输入区
  private Timer timer = new Timer(true); // 定时器，用于定期发送消息
  private Thread adjustthread; // 调整客户端数量的线程

  // 未连接的客户端计数
  private int notyetconnected = 0;

  /**
   * 构造函数，初始化GUI界面
   */
  public ServerStressTest() {
    setTitle("ServerStressTest"); // 设置窗口标题
    setDefaultCloseOperation(EXIT_ON_CLOSE); // 设置关闭操作

    // 初始化开始按钮
    start = new JButton("Start");
    start.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // 开始按钮点击事件处理
        start.setEnabled(false); // 禁用开始按钮
        stop.setEnabled(true); // 启用停止按钮
        reset.setEnabled(false); // 禁用重置按钮
        interval.setEnabled(false); // 禁用消息间隔滑块
        clients.setEnabled(false); // 禁用客户端数量滑块

        // 停止之前的调整线程
        stopAdjust();
        // 创建新的调整线程
        adjustthread = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              adjust(); // 调整客户端数量并启动消息发送
            } catch (InterruptedException e) {
              System.out.println("adjust chanced"); // 线程被中断时输出信息
            }
          }
        });
        adjustthread.start(); // 启动调整线程

      }
    });

    // 初始化停止按钮
    stop = new JButton("Stop");
    stop.setEnabled(false); // 初始时禁用停止按钮
    stop.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // 停止按钮点击事件处理
        timer.cancel(); // 取消定时器
        stopAdjust(); // 停止调整线程
        start.setEnabled(true); // 启用开始按钮
        stop.setEnabled(false); // 禁用停止按钮
        reset.setEnabled(true); // 启用重置按钮
        joinrate.setEnabled(true); // 启用加入速率滑块
        interval.setEnabled(true); // 启用消息间隔滑块
        clients.setEnabled(true); // 启用客户端数量滑块
      }
    });

    // 初始化重置按钮
    reset = new JButton("reset");
    reset.setEnabled(true); // 初始时启用重置按钮
    reset.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // 重置按钮点击事件处理：关闭所有WebSocket连接
        while (!websockets.isEmpty()) {
          websockets.remove(0).close();
        }

      }
    });

    // 初始化加入速率滑块
    joinrate = new JSlider(0, 5000); // 范围：0-5000毫秒
    joinrate.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        // 更新加入速率标签显示
        joinratelabel.setText("Joinrate: " + joinrate.getValue() + " ms ");
      }
    });

    // 初始化客户端数量滑块
    clients = new JSlider(0, 10000); // 范围：0-10000个客户端
    clients.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        // 更新客户端数量标签显示
        clientslabel.setText("Clients: " + clients.getValue());

      }
    });

    // 初始化消息间隔滑块
    interval = new JSlider(0, 5000); // 范围：0-5000毫秒
    interval.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        // 更新消息间隔标签显示
        intervallabel.setText("Interval: " + interval.getValue() + " ms ");

      }
    });

    // 设置窗口大小和布局
    setSize(300, 400);
    setLayout(new GridLayout(10, 1, 10, 10));

    // 添加组件到窗口
    add(new JLabel("URI")); // URI标签
    add(uriinput); // URI输入框
    add(joinratelabel); // 加入速率标签
    add(joinrate); // 加入速率滑块
    add(clientslabel); // 客户端数量标签
    add(clients); // 客户端数量滑块
    add(intervallabel); // 消息间隔标签
    add(interval); // 消息间隔滑块

    // 底部按钮面板
    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
    add(text); // 消息内容输入区
    add(south); // 底部按钮面板

    // 添加按钮到底部面板
    south.add(start);
    south.add(stop);
    south.add(reset);

    // 设置初始值
    joinrate.setValue(200); // 默认加入速率：200毫秒
    interval.setValue(1000); // 默认消息间隔：1000毫秒
    clients.setValue(1); // 默认客户端数量：1个

  }

  // 存储所有WebSocket客户端的列表，使用同步列表保证线程安全
  List<WebSocketClient> websockets = Collections
      .synchronizedList(new LinkedList<WebSocketClient>());
  URI uri; // WebSocket服务器URI

  /**
   * 调整客户端数量并启动消息发送
   * 此方法根据用户设置的参数创建或关闭WebSocket客户端，并启动定时发送消息
   */
  public void adjust() throws InterruptedException {
    System.out.println("Adjust"); // 输出调整开始信息

    // 解析WebSocket服务器URI
    try {
      uri = new URI(uriinput.getText());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    int totalclients = clients.getValue(); // 获取目标客户端数量

    // 如果当前客户端数量少于目标数量，创建新的客户端
    while (websockets.size() < totalclients) {
      // 创建一个新的WebSocket客户端
      WebSocketClient cl = new ExampleClient(uri) {
        @Override
        public void onClose(int code, String reason, boolean remote) {
          // 连接关闭时的处理
          System.out.println("Closed duo " + code + " " + reason);
          clients.setValue(websockets.size()); // 更新客户端数量显示
          websockets.remove(this); // 从列表中移除此客户端
        }
      };

      cl.connect(); // 连接到服务器
      clients.setValue(websockets.size()); // 更新客户端数量显示
      websockets.add(cl); // 添加到客户端列表
      Thread.sleep(joinrate.getValue()); // 等待指定的加入间隔时间
    }

    // 如果当前客户端数量多于目标数量，关闭多余的客户端
    while (websockets.size() > clients.getValue()) {
      websockets.remove(0).close();
    }

    // 创建定时器，定期发送消息
    timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        send(); // 发送消息到所有客户端
      }
    }, 0, interval.getValue()); // 按设定的间隔时间执行

  }

  /**
   * 停止调整线程
   * 中断并等待调整线程结束
   */
  public void stopAdjust() {
    if (adjustthread != null) {
      adjustthread.interrupt(); // 中断线程
      try {
        adjustthread.join(); // 等待线程结束
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 向所有WebSocket客户端发送消息
   * 统计发送成功和失败的数量并输出信息
   */
  public void send() {
    notyetconnected = 0; // 重置未连接计数器
    String payload = text.getText(); // 获取要发送的消息内容
    long time1 = System.currentTimeMillis(); // 记录开始时间

    // 向所有客户端发送消息
    synchronized (websockets) {
      for (WebSocketClient cl : websockets) {
        try {
          cl.send(payload); // 发送消息
        } catch (WebsocketNotConnectedException e) {
          notyetconnected++; // 如果客户端未连接，增加计数
        }
      }
    }

    // 输出发送结果和耗时
    System.out.println(
        websockets.size() + "/" + notyetconnected + " clients sent \"" + payload + "\"" + (
            System.currentTimeMillis() - time1));
  }

  /**
   * 主方法，启动应用程序
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    new ServerStressTest().setVisible(true); // 创建并显示主窗口
  }

}
