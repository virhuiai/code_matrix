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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 * 一个简单的WebSocket服务器实现。维护一个"聊天室"。
 */
public class ChatServer extends WebSocketServer {

  /**
   * 构造函数 - 通过指定端口创建服务器
   * @param port 服务器监听的端口号
   */
  public ChatServer(int port) throws UnknownHostException {
    super(new InetSocketAddress(port));
  }

  /**
   * 构造函数 - 通过指定地址创建服务器
   * @param address 服务器监听的网络地址
   */
  public ChatServer(InetSocketAddress address) {
    super(address);
  }

  /**
   * 构造函数 - 通过指定端口和WebSocket协议草案创建服务器
   * @param port 服务器监听的端口号
   * @param draft WebSocket协议草案
   */
  public ChatServer(int port, Draft_6455 draft) {
    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
  }

  /**
   * 当有新客户端连接时的处理方法
   * @param conn 新建立的WebSocket连接
   * @param handshake 客户端握手信息
   */
  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    conn.send("Welcome to the server!"); //This method sends a message to the new client
                                         //这个方法向新客户端发送一条消息
    broadcast("new connection: " + handshake
        .getResourceDescriptor()); //This method sends a message to all clients connected
                                   //这个方法向所有已连接的客户端发送一条消息
    System.out.println(
        conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        //输出客户端IP地址，表示有新用户进入聊天室
  }

  /**
   * 当客户端连接关闭时的处理方法
   * @param conn 关闭的WebSocket连接
   * @param code 关闭状态码
   * @param reason 关闭原因
   * @param remote 是否由远程端发起关闭
   */
  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    broadcast(conn + " has left the room!"); //向所有客户端广播有用户离开的消息
    System.out.println(conn + " has left the room!"); //在服务器控制台输出有用户离开的消息
  }

  /**
   * 接收到客户端文本消息时的处理方法
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的文本消息
   */
  @Override
  public void onMessage(WebSocket conn, String message) {
    broadcast(message); //将收到的消息广播给所有客户端
    System.out.println(conn + ": " + message); //在服务器控制台输出消息内容
  }

  /**
   * 接收到客户端二进制消息时的处理方法
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的二进制消息
   */
  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    broadcast(message.array()); //将收到的二进制消息广播给所有客户端
    System.out.println(conn + ": " + message); //在服务器控制台输出消息内容
  }

  /**
   * 程序入口方法
   * @param args 命令行参数，第一个参数可以指定端口号
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    int port = 8887; // 843 flash policy port
                     // 默认端口8887，843是Flash策略端口
    try {
      port = Integer.parseInt(args[0]); //尝试从命令行参数获取端口号
    } catch (Exception ex) {
      //如果发生异常，使用默认端口
    }
    ChatServer s = new ChatServer(port); //创建聊天服务器实例
    s.start(); //启动服务器
    System.out.println("ChatServer started on port: " + s.getPort()); //输出服务器启动信息

    BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in)); //用于从控制台读取输入
    while (true) {
      String in = sysin.readLine(); //读取控制台输入
      s.broadcast(in); //将控制台输入广播给所有客户端
      if (in.equals("exit")) { //如果输入"exit"，关闭服务器
        s.stop(1000); //给予1000毫秒的关闭时间
        break;
      }
    }
  }

  /**
   * 发生错误时的处理方法
   * @param conn 发生错误的WebSocket连接，可能为null
   * @param ex 异常对象
   */
  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace(); //打印异常堆栈
    if (conn != null) {
      // some errors like port binding failed may not be assignable to a specific websocket
      // 一些错误如端口绑定失败可能不是特定WebSocket连接导致的
    }
  }

  /**
   * 服务器启动成功时的处理方法
   */
  @Override
  public void onStart() {
    System.out.println("Server started!"); //输出服务器启动成功的消息
    setConnectionLostTimeout(0); //设置连接丢失超时时间为0
    setConnectionLostTimeout(100); //设置连接丢失超时时间为100毫秒
  }

}
