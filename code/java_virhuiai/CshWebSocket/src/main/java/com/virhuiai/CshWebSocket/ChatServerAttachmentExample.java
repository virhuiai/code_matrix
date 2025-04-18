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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 * <p>
 * Shows how to use the attachment for a WebSocket. This example just uses a simple integer as ID.
 * Setting an attachment also works in the WebSocketClient
 */
/**
 * 一个简单的WebSocket服务器实现。用于管理一个"聊天室"。
 * <p>
 * 展示了如何为WebSocket使用附件功能。本例中仅使用简单的整数作为ID。
 * 设置附件的功能在WebSocketClient中同样适用
 */
public class ChatServerAttachmentExample extends WebSocketServer {

  // 索引计数器，用于为每个连接分配唯一ID
  Integer index = 0;

  /**
   * 构造函数，通过端口号创建服务器
   * @param port 服务器监听的端口号
   */
  public ChatServerAttachmentExample(int port) throws UnknownHostException {
    super(new InetSocketAddress(port));
  }

  /**
   * 构造函数，通过InetSocketAddress创建服务器
   * @param address 包含IP地址和端口的套接字地址
   */
  public ChatServerAttachmentExample(InetSocketAddress address) {
    super(address);
  }

  @Override
  /**
   * 当新的WebSocket连接建立时调用
   * @param conn 新建立的WebSocket连接
   * @param handshake 客户端握手信息
   */
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    conn.setAttachment(index); // Set the attachment to the current index
                               // 将当前索引值设置为此连接的附件
    index++;
    // Get the attachment of this connection as Integer
    // 获取此连接的附件值（作为Integer类型）
    System.out.println(
        conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room! ID: "
            + conn.<Integer>getAttachment());
  }

  @Override
  /**
   * 当WebSocket连接关闭时调用
   * @param conn 关闭的WebSocket连接
   * @param code 关闭代码
   * @param reason 关闭原因
   * @param remote 是否由远程端关闭
   */
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    // Get the attachment of this connection as Integer
    // 获取此连接的附件值（作为Integer类型）
    System.out.println(conn + " has left the room! ID: " + conn.<Integer>getAttachment());
  }

  @Override
  /**
   * 当收到文本消息时调用
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的文本消息
   */
  public void onMessage(WebSocket conn, String message) {
    System.out.println(conn + ": " + message);
  }

  @Override
  /**
   * 当收到二进制消息时调用
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的二进制消息
   */
  public void onMessage(WebSocket conn, ByteBuffer message) {
    System.out.println(conn + ": " + message);
  }

  /**
   * 主方法，程序入口点
   * @param args 命令行参数，第一个参数可以指定端口号
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    int port = 8887; // 843 flash policy port
                     // 843是Flash策略端口
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception ex) {
      // 如果解析命令行参数失败，则使用默认端口
    }
    ChatServerAttachmentExample s = new ChatServerAttachmentExample(port);
    s.start();
    System.out.println("ChatServer started on port: " + s.getPort());

    // 创建控制台输入流，用于读取管理员输入的消息
    BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String in = sysin.readLine();
      s.broadcast(in);
      if (in.equals("exit")) {
        s.stop(1000);
        break;
      }
    }
  }

  @Override
  /**
   * 当发生错误时调用
   * @param conn 相关的WebSocket连接，如果错误不是特定于某个连接则为null
   * @param ex 发生的异常
   */
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
    if (conn != null) {
      // some errors like port binding failed may not be assignable to a specific websocket
      // 某些错误（如端口绑定失败）可能不是针对特定WebSocket连接的
    }
  }

  @Override
  /**
   * 当服务器成功启动时调用
   */
  public void onStart() {
    System.out.println("Server started!");
  }

}
