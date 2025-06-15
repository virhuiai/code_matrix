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
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

/**
 * This example shows how to add additional headers to your server handshake response
 * <p>
 * For this you have to override onWebsocketHandshakeReceivedAsServer in your WebSocketServer class
 * <p>
 * We are simple adding the additional header "Access-Control-Allow-Origin" to our server response
 *
 * 本示例展示了如何向服务器握手响应中添加额外的头信息
 * <p>
 * 为此，您需要在WebSocketServer类中重写onWebsocketHandshakeReceivedAsServer方法
 * <p>
 * 我们简单地向服务器响应中添加了额外的头信息"Access-Control-Allow-Origin"，设置为"*"，允许跨域访问
 */
public class ServerAdditionalHeaderExample extends ChatServer {

  /**
   * 构造函数，创建一个在指定端口上运行的服务器
   * @param port 服务器监听的端口号
   */
  public ServerAdditionalHeaderExample(int port) {
    super(new InetSocketAddress(port));
  }

  /**
   * 重写的方法，当服务器收到WebSocket握手请求时被调用
   * 在此方法中，我们向响应添加额外的头信息
   *
   * @param conn WebSocket连接对象
   * @param draft WebSocket协议草案
   * @param request 客户端握手请求
   * @return 修改后的服务器握手响应构建器
   * @throws InvalidDataException 如果请求数据无效
   */
  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
      ClientHandshake request) throws InvalidDataException {
    ServerHandshakeBuilder builder = super
        .onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    // 添加CORS头信息，允许所有来源的跨域请求
    builder.put("Access-Control-Allow-Origin", "*");
    return builder;
  }


  /**
   * 主方法，启动WebSocket服务器
   *
   * @param args 命令行参数，可以指定端口号
   * @throws InterruptedException 如果线程被中断
   * @throws IOException 如果I/O操作失败
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    int port = 8887; // 843 flash policy port
    // 默认端口为8887，如果在命令行参数中提供了端口号，则使用该端口
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception ex) {
      // 如果解析端口号失败，使用默认端口
    }
    // 创建并启动服务器实例
    ServerAdditionalHeaderExample s = new ServerAdditionalHeaderExample(port);
    s.start();
    System.out.println("Server started on port: " + s.getPort());
    // 服务器已在指定端口启动

    // 创建输入流读取器，用于从控制台读取命令
    BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      // 读取控制台输入
      String in = sysin.readLine();
      // 将输入内容广播给所有连接的客户端
      s.broadcast(in);
      // 如果输入"exit"，则停止服务器并退出循环
      if (in.equals("exit")) {
        s.stop(1000);
        s.stop(1000); // 停止服务器，等待1000毫秒完成关闭
        break;
      }
    }
  }
}
