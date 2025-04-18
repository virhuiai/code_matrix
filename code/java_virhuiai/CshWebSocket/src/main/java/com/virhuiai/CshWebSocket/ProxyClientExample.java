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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
// 导入必要的Java网络相关类
import java.net.InetSocketAddress;  // 用于表示IP套接字地址（IP地址+端口号）
import java.net.Proxy;              // 用于配置网络连接的代理设置
import java.net.URI;                // 统一资源标识符类
import java.net.URISyntaxException; // URI语法异常类

/**
 * 代理客户端示例类
 * 演示如何通过HTTP代理连接到WebSocket服务器
 */
public class ProxyClientExample {

  /**
   * 主方法，程序入口点
   * @param args 命令行参数
   * @throws URISyntaxException 当URI格式不正确时抛出此异常
   */
  public static void main(String[] args) throws URISyntaxException {
    // 创建一个示例客户端，连接到echo.websocket.org提供的WebSocket服务
    ExampleClient c = new ExampleClient(new URI("ws://echo.websocket.org"));

    // 设置HTTP代理，指定代理服务器地址为"proxyaddress"，端口为80
    c.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxyaddress", 80)));

    // 建立与WebSocket服务器的连接
    c.connect();
  }
}
