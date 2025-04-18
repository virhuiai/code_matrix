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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 *
 * 这个示例演示了如何创建与服务器的WebSocket连接。
 * 只重写了最重要的回调方法。
 */
public class ExampleClient extends WebSocketClient {

  /**
   * 构造函数 - 使用服务器URI和WebSocket协议草案
   * @param serverUri 服务器URI
   * @param draft WebSocket协议草案
   */
  public ExampleClient(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  /**
   * 构造函数 - 仅使用服务器URI
   * @param serverURI 服务器URI
   */
  public ExampleClient(URI serverURI) {
    super(serverURI);
  }

  /**
   * 构造函数 - 使用服务器URI和HTTP头信息
   * @param serverUri 服务器URI
   * @param httpHeaders HTTP头信息映射
   */
  public ExampleClient(URI serverUri, Map<String, String> httpHeaders) {
    super(serverUri, httpHeaders);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    send("Hello, it is me. Mario :)");
    System.out.println("opened connection");
    // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    // 如果你计划基于IP或HTTP字段拒绝连接，可以重写onWebsocketHandshakeReceivedAsClient方法
  }

  @Override
  public void onMessage(String message) {
    System.out.println("received: " + message);
    // 接收到消息时的回调方法，打印接收到的消息
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    // The close codes are documented in class org.java_websocket.framing.CloseFrame
    // 关闭代码在org.java_websocket.framing.CloseFrame类中有文档说明
    System.out.println(
        "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
            + reason);
    // 连接关闭时的回调方法，打印关闭信息，包括是由远程端还是本地关闭，关闭代码和原因
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
    // if the error is fatal then onClose will be called additionally
    // 如果错误是致命的，那么onClose方法也会被调用
  }

  /**
   * 主方法 - 创建WebSocket客户端并连接到服务器
   * @param args 命令行参数
   * @throws URISyntaxException URI语法异常
   */
  public static void main(String[] args) throws URISyntaxException {
    ExampleClient c = new ExampleClient(new URI(
        "ws://localhost:8887")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        // 关于WebSocket协议草案的更多信息请参见: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
    c.connect();
    // 连接到WebSocket服务器
  }

}
