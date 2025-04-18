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
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

/**
 * This example shows how to reject a handshake as a server from a client.
 * <p>
 * For this you have to override onWebsocketHandshakeReceivedAsServer in your WebSocketServer class
 *
 * 这个示例展示了服务器如何拒绝客户端的握手请求。
 * <p>
 * 为了实现这个功能，你需要在WebSocketServer类中重写onWebsocketHandshakeReceivedAsServer方法
 */
public class ServerRejectHandshakeExample extends ChatServer {

  /**
   * 构造函数，初始化WebSocket服务器并绑定指定端口
   * @param port 服务器监听的端口
   */
  public ServerRejectHandshakeExample(int port) {
    super(new InetSocketAddress(port));
  }

  /**
   * 重写父类的握手处理方法，用于验证和筛选客户端连接请求
   * 当接收到客户端的WebSocket握手请求时，此方法被调用
   */
  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
      ClientHandshake request) throws InvalidDataException {
    ServerHandshakeBuilder builder = super
        .onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    //In this example we don't allow any resource descriptor ( "ws://localhost:8887/?roomid=1 will be rejected but ws://localhost:8887 is fine)
    //在这个例子中，我们不允许任何资源描述符（"ws://localhost:8887/?roomid=1"会被拒绝，但"ws://localhost:8887"可以接受）
    if (!request.getResourceDescriptor().equals("/")) {
      throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Not accepted!");
    }
    //If there are no cookies set reject it as well.
    //如果请求中没有设置Cookie，也拒绝连接
    if (!request.hasFieldValue("Cookie")) {
      throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Not accepted!");
    }
    //If the cookie does not contain a specific value
    //如果Cookie的值不是特定的"username=nemo"，拒绝连接
    if (!request.getFieldValue("Cookie").equals("username=nemo")) {
      throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Not accepted!");
    }
    //If there is a Origin Field, it has to be localhost:8887
    //如果请求中包含Origin字段，它必须是"localhost:8887"，否则拒绝连接
    if (request.hasFieldValue("Origin")) {
      if (!request.getFieldValue("Origin").equals("localhost:8887")) {
        throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Not accepted!");
      }
    }
    return builder;
  }


  /**
   * 主方法，启动WebSocket服务器
   * @param args 命令行参数，第一个参数可以指定端口号
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    int port = 8887; // 843 flash policy port
    // 尝试从命令行参数获取端口号
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception ex) {
      // 如果解析失败，使用默认端口8887
    }
    // 创建并启动服务器实例
    ServerRejectHandshakeExample s = new ServerRejectHandshakeExample(port);
    s.start();
    System.out.println("Server started on port: " + s.getPort());
    // 服务器已启动，显示端口信息

    // 创建控制台输入流，用于接收命令
    BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      // 读取控制台输入
      String in = sysin.readLine();
      // 向所有连接的客户端广播消息
      s.broadcast(in);
      // 如果输入"exit"，则停止服务器并退出程序
      if (in.equals("exit")) {
        s.stop(1000);
        break;
      }
    }
  }
}
