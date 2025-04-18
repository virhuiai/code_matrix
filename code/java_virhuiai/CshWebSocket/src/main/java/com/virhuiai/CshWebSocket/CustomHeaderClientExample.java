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
import java.util.HashMap;
import java.util.Map;

/**
 * This class shows how to add additional http header like "Origin" or "Cookie".
 * <p>
 * To see it working, start ServerRejectHandshakeExample and then start this example.
 *
 * 这个类展示了如何添加额外的HTTP头信息，如"Origin"或"Cookie"。
 * <p>
 * 要查看其工作原理，请先启动ServerRejectHandshakeExample，然后启动此示例。
 */
public class CustomHeaderClientExample {

  public static void main(String[] args) throws URISyntaxException, InterruptedException {
    // 创建一个HashMap用于存储HTTP头信息
    Map<String, String> httpHeaders = new HashMap<String, String>();

    // 添加一个测试Cookie
    httpHeaders.put("Cookie", "test");
    // 创建一个WebSocket客户端，连接到本地8887端口，并传入自定义的HTTP头信息
    ExampleClient c = new ExampleClient(new URI("ws://localhost:8887"), httpHeaders);
    //We expect no successful connection
    //我们预期这个连接不会成功，因为Cookie值不符合服务器要求
    c.connectBlocking();

    // 修改Cookie值为"username=nemo"
    httpHeaders.put("Cookie", "username=nemo");
    // 创建一个新的客户端实例
    c = new ExampleClient(new URI("ws://localhost:8887"), httpHeaders);
    //Wer expect a successful connection
    //我们预期这个连接会成功，因为Cookie值符合服务器要求
    c.connectBlocking();
    // 关闭连接
    c.closeBlocking();

    // 添加跨域请求头信息
    httpHeaders.put("Access-Control-Allow-Origin", "*");
    // 创建一个新的客户端实例
    c = new ExampleClient(new URI("ws://localhost:8887"), httpHeaders);
    //We expect no successful connection
    //我们预期这个连接不会成功，因为Access-Control-Allow-Origin应该由服务器设置，而不是客户端
    c.connectBlocking();
    // 关闭连接
    c.closeBlocking();

    // 清除之前的所有头信息
    httpHeaders.clear();
    // 添加Origin和Cookie头信息
    httpHeaders.put("Origin", "localhost:8887");
    httpHeaders.put("Cookie", "username=nemo");
    // 创建一个新的客户端实例
    c = new ExampleClient(new URI("ws://localhost:8887"), httpHeaders);
    //We expect a successful connection
    //我们预期这个连接会成功，因为Origin和Cookie都符合服务器要求
    c.connectBlocking();
    // 关闭连接
    c.closeBlocking();

    // 清除之前的所有头信息
    httpHeaders.clear();
    // 添加不同的Origin值和cookie（注意此处cookie是小写）
    httpHeaders.put("Origin", "localhost");
    httpHeaders.put("cookie", "username=nemo");
    // 创建一个新的客户端实例
    c = new ExampleClient(new URI("ws://localhost:8887"), httpHeaders);
    //We expect no successful connection
    //我们预期这个连接不会成功，因为Origin值不符合服务器要求，或者cookie的键名是小写
    c.connectBlocking();
  }
}
