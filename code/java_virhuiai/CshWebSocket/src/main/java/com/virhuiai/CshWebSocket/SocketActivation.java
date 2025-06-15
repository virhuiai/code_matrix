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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * This is a "smart" chat server which will exit when no more clients are left, in order to demonstrate socket activation
 * 这是一个"智能"聊天服务器，当没有客户端连接时会自动退出，用于演示套接字激活功能
 */
public class SocketActivation extends WebSocketServer {

  // 原子整数，用于跟踪连接的客户端数量
  AtomicInteger clients = new AtomicInteger(0);

  /**
   * 构造函数，接收一个ServerSocketChannel作为参数
   * @param chan 服务器套接字通道
   */
  public SocketActivation(ServerSocketChannel chan) {
    super(chan);
  }

  /**
   * 当新的WebSocket连接建立时调用
   * @param conn 新建立的WebSocket连接
   * @param handshake 客户端握手信息
   */
  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    conn.send("Welcome to the server!"); //This method sends a message to the new client
                                         //这个方法向新客户端发送消息
    broadcast("new connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
                                                                       //这个方法向所有已连接的客户端发送消息
    if(clients.get() == 0) {
      broadcast("You are the first client to join");  // 如果这是第一个客户端，发送特殊欢迎消息
    }
    System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");  // 打印客户端IP地址
    clients.incrementAndGet();  // 增加客户端计数
  }

  /**
   * 当WebSocket连接关闭时调用
   * @param conn 关闭的WebSocket连接
   * @param code 关闭代码
   * @param reason 关闭原因
   * @param remote 是否由远程端关闭
   */
  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    broadcast(conn + " has left the room!");  // 向所有客户端广播某用户离开的消息
    System.out.println(conn + " has left the room!");  // 在服务器控制台打印用户离开信息
    if(clients.decrementAndGet() <= 0) {  // 减少客户端计数，如果没有客户端了，则退出程序
      System.out.println("No more clients left, exiting");  // 打印没有客户端剩余的信息
      System.exit(0);  // 退出程序
    }
  }

  /**
   * 当收到文本消息时调用
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的文本消息
   */
  @Override
  public void onMessage(WebSocket conn, String message) {
    broadcast(message);  // 广播接收到的消息给所有客户端
    System.out.println(conn + ": " + message);  // 在服务器控制台打印消息内容
  }

  /**
   * 当收到二进制消息时调用
   * @param conn 发送消息的WebSocket连接
   * @param message 接收到的二进制消息
   */
  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    broadcast(message.array());  // 广播二进制消息给所有客户端
    System.out.println(conn + ": " + message);  // 在服务器控制台打印消息内容
  }

  /**
   * 程序入口点
   * @param args 命令行参数
   * @throws InterruptedException 如果线程被中断
   * @throws IOException 如果发生I/O错误
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    if(System.inheritedChannel() == null) {  // 检查是否有继承的通道
      System.err.println("System.inheritedChannel() is null, make sure this program is started with file descriptor zero being a listening socket");
      // 上面的错误信息表示：System.inheritedChannel()为空，请确保该程序启动时文件描述符0是一个监听套接字
      System.exit(1);  // 如果没有继承的通道，退出程序
    }
    SocketActivation s = new SocketActivation((ServerSocketChannel)System.inheritedChannel());  // 创建服务器实例
    s.start();  // 启动服务器
    System.out.println(">>>> SocketActivation started on port: " + s.getPort() + " <<<<");  // 打印服务器启动信息和端口
  }

  /**
   * 当发生错误时调用
   * @param conn 发生错误的WebSocket连接，可能为null
   * @param ex 异常对象
   */
  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();  // 打印异常堆栈
  }

  /**
   * 当服务器成功启动时调用
   */
  @Override
  public void onStart() {
    System.out.println("Server started!");  // 打印服务器启动成功信息
  }

}
