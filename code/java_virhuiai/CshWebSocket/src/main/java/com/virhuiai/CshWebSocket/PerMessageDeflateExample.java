package com.virhuiai.CshWebSocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.permessage_deflate.PerMessageDeflateExtension;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * This class only serves the purpose of showing how to enable PerMessageDeflateExtension for both
 * server and client sockets.<br> Extensions are required to be registered in
 *
 * 本类仅用于展示如何为服务器和客户端套接字启用 PerMessageDeflateExtension。<br>
 * 扩展需要在 Draft 对象中注册。
 *
 * @see Draft objects and both
 * @see WebSocketClient and
 * @see WebSocketServer accept a
 * @see Draft object in their constructors. This example shows how to achieve it for both server and
 * client sockets. Once the connection has been established, PerMessageDeflateExtension will be
 * enabled and any messages (binary or text) will be compressed/decompressed automatically.<br>
 * Since no additional code is required when sending or receiving messages, this example skips those
 * parts.
 *
 * WebSocketClient 和 WebSocketServer 在它们的构造函数中都接受 Draft 对象。
 * 本示例展示了如何为服务器和客户端套接字实现这一点。
 * 一旦连接建立，PerMessageDeflateExtension 将被启用，任何消息（二进制或文本）都将自动压缩/解压缩。<br>
 * 由于发送或接收消息时不需要额外的代码，本示例省略了这些部分。
 */
public class PerMessageDeflateExample {

  // 创建一个使用 PerMessageDeflateExtension 的 Draft_6455 对象
  private static final Draft perMessageDeflateDraft = new Draft_6455(
      new PerMessageDeflateExtension());
  // 定义服务器监听端口
  private static final int PORT = 8887;

  /**
   * 支持压缩的 WebSocket 客户端实现类
   */
  private static class DeflateClient extends WebSocketClient {

    /**
     * 构造函数，连接到本地 WebSocket 服务器
     */
    public DeflateClient() throws URISyntaxException {
      super(new URI("ws://localhost:" + PORT), perMessageDeflateDraft);
    }

    /**
     * 连接打开时的回调方法
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    /**
     * 接收消息时的回调方法
     */
    @Override
    public void onMessage(String message) {
    }

    /**
     * 连接关闭时的回调方法
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    /**
     * 发生错误时的回调方法
     */
    @Override
    public void onError(Exception ex) {
    }
  }

  /**
   * 支持压缩的 WebSocket 服务器实现类
   */
  private static class DeflateServer extends WebSocketServer {

    /**
     * 构造函数，创建一个监听指定端口的服务器
     */
    public DeflateServer() {
      super(new InetSocketAddress(PORT), Collections.singletonList(perMessageDeflateDraft));
    }

    /**
     * 客户端连接打开时的回调方法
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    /**
     * 客户端连接关闭时的回调方法
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    /**
     * 接收到客户端消息时的回调方法
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
    }

    /**
     * 发生错误时的回调方法
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    /**
     * 服务器启动时的回调方法
     */
    @Override
    public void onStart() {
    }
  }
}
