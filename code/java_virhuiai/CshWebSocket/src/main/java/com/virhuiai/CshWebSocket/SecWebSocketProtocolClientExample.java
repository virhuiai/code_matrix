package com.virhuiai.CshWebSocket;/*
 *  Copyright (c) 2010-2020 Nathan Rajlich
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
import java.util.ArrayList;
import java.util.Collections;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;

/**
 * This example demonstrates how to use a specific Sec-WebSocket-Protocol for your connection.
 * 该示例演示了如何为WebSocket连接使用特定的Sec-WebSocket-Protocol协议。
 */
public class SecWebSocketProtocolClientExample {

  public static void main(String[] args) throws URISyntaxException {
    // This draft only allows you to use the specific Sec-WebSocket-Protocol without a fallback.
    // 这个draft配置只允许使用特定的Sec-WebSocket-Protocol协议，没有回退机制。
    Draft_6455 draft_ocppOnly = new Draft_6455(Collections.<IExtension>emptyList(),
        Collections.<IProtocol>singletonList(new Protocol("ocpp2.0")));

    // This draft allows the specific Sec-WebSocket-Protocol and also provides a fallback, if the other endpoint does not accept the specific Sec-WebSocket-Protocol
    // 这个draft配置允许使用特定的Sec-WebSocket-Protocol协议，并且提供回退机制，如果对端不接受特定的协议，则使用默认协议。
    ArrayList<IProtocol> protocols = new ArrayList<IProtocol>();
    // 添加OCPP 2.0协议（开放充电点协议，用于电动车充电站通信）
    protocols.add(new Protocol("ocpp2.0"));
    // 添加空协议作为回退选项
    protocols.add(new Protocol(""));
    // 创建包含协议列表的Draft_6455对象
    Draft_6455 draft_ocppAndFallBack = new Draft_6455(Collections.<IExtension>emptyList(),
        protocols);

    // 创建一个WebSocket客户端，连接到echo.websocket.org服务器，使用带有回退机制的draft配置
    ExampleClient c = new ExampleClient(new URI("ws://echo.websocket.org"), draft_ocppAndFallBack);
    // 建立WebSocket连接
    c.connect();
  }
}
