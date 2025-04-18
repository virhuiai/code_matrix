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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;

/**
 * This example demonstrates how to use a specific Sec-WebSocket-Protocol for your connection.
 * 这个示例演示了如何为你的连接使用特定的Sec-WebSocket-Protocol协议。
 */
public class SecWebSocketProtocolServerExample {

  public static void main(String[] args) throws URISyntaxException {
    // This draft only allows you to use the specific Sec-WebSocket-Protocol without a fallback.
    // 这个草案只允许你使用特定的Sec-WebSocket-Protocol协议，没有后备方案。
    Draft_6455 draft_ocppOnly = new Draft_6455(Collections.<IExtension>emptyList(),
        Collections.<IProtocol>singletonList(new Protocol("ocpp2.0")));

    // This draft allows the specific Sec-WebSocket-Protocol and also provides a fallback, if the other endpoint does not accept the specific Sec-WebSocket-Protocol
    // 这个草案允许使用特定的Sec-WebSocket-Protocol协议，并且还提供了一个后备方案，以防另一个端点不接受特定的Sec-WebSocket-Protocol协议
    ArrayList<IProtocol> protocols = new ArrayList<IProtocol>();
    // 添加ocpp2.0协议
    protocols.add(new Protocol("ocpp2.0"));
    // 添加空协议作为后备方案
    protocols.add(new Protocol(""));
    Draft_6455 draft_ocppAndFallBack = new Draft_6455(Collections.<IExtension>emptyList(),
        protocols);

    // 创建一个聊天服务器，使用端口8887和只支持ocpp协议的草案
    ChatServer chatServer = new ChatServer(8887, draft_ocppOnly);
    // 启动聊天服务器
    chatServer.start();
  }
}
