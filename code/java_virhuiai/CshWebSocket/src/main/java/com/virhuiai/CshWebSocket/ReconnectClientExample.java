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

/**
 * Simple example to reconnect blocking and non-blocking.
 * 简单示例展示阻塞式和非阻塞式重连功能。
 *
 * 这段代码是一个WebSocket客户端重连示例，展示了如何使用阻塞式和非阻塞式方法进行连接、断开和重连操作。
 */
public class ReconnectClientExample {

  public static void main(String[] args) throws URISyntaxException, InterruptedException {
    // 创建一个ExampleClient实例，连接到本地8887端口的WebSocket服务器
    ExampleClient c = new ExampleClient(new URI("ws://localhost:8887"));

    //Connect to a server normally
    //正常连接到服务器
    c.connectBlocking();

    // 发送消息"hi"
    c.send("hi");

    // 线程休眠10毫秒
    Thread.sleep(10);

    // 关闭连接（阻塞式）
    c.closeBlocking();

    //Disconnect manually and reconnect blocking
    //手动断开连接并使用阻塞方式重新连接
    c.reconnectBlocking();

    // 发送消息"it's"
    c.send("it's");

    // 线程休眠10000毫秒（10秒）
    Thread.sleep(10000);

    // 关闭连接（阻塞式）
    c.closeBlocking();

    //Disconnect manually and reconnect
    //手动断开连接并重新连接（非阻塞方式）
    c.reconnect();

    // 线程休眠100毫秒
    Thread.sleep(100);

    // 发送消息"me"
    c.send("me");

    // 线程休眠100毫秒
    Thread.sleep(100);

    // 关闭连接（阻塞式）
    c.closeBlocking();
  }
}
