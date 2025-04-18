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

// 导入必要的Java库
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocketChatClient类 - 继承自WebSocketClient
 * 实现WebSocket客户端通信功能
 */
class WebSocketChatClient extends WebSocketClient {

  /**
   * 构造函数
   * @param serverUri 服务器URI地址
   */
  public WebSocketChatClient(URI serverUri) {
    super(serverUri);
  }

  /**
   * 当WebSocket连接成功打开时调用
   * @param handshakedata 握手数据
   */
  @Override
  public void onOpen(ServerHandshake handshakedata) {
    System.out.println("Connected");  // 输出"已连接"消息

  }

  /**
   * 当收到服务器消息时调用
   * @param message 接收到的消息
   */
  @Override
  public void onMessage(String message) {
    System.out.println("got: " + message);  // 输出接收到的消息

  }

  /**
   * 当WebSocket连接关闭时调用
   * @param code 关闭状态码
   * @param reason 关闭原因
   * @param remote 是否由远程端关闭
   */
  @Override
  public void onClose(int code, String reason, boolean remote) {
    System.out.println("Disconnected");  // 输出"已断开连接"消息

  }

  /**
   * 当发生错误时调用
   * @param ex 异常对象
   */
  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();  // 打印异常堆栈信息

  }

}

/**
 * SSLClientExample类 - 主类
 * 演示如何创建一个基于SSL的WebSocket客户端
 */
public class SSLClientExample {

  /*
   * Keystore with certificate created like so (in JKS format):
   *
   *keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
   */
  /*
   * 密钥库和证书的创建方式（JKS格式）：
   * 使用keytool命令生成RSA算法的密钥，有效期3650天，存储在"keystore.jks"文件中，
   * 存储密码为"storepassword"，密钥密码为"keypassword"，别名为"default"
   */
  /**
   * 主方法
   * @param args 命令行参数
   * @throws Exception 可能抛出的异常
   */
  public static void main(String[] args) throws Exception {
    // 创建WebSocket客户端，连接到安全的WebSocket服务器
    WebSocketChatClient chatclient = new WebSocketChatClient(new URI("wss://localhost:8887"));

    // 加载密钥库
    // load up the key store
    String STORETYPE = "JKS";  // 密钥库类型
    String KEYSTORE = Paths.get("src", "test", "java", "org", "java_websocket", "keystore.jks")
        .toString();  // 密钥库路径
    String STOREPASSWORD = "storepassword";  // 存储密码
    String KEYPASSWORD = "keypassword";  // 密钥密码

    // 初始化密钥库
    KeyStore ks = KeyStore.getInstance(STORETYPE);
    File kf = new File(KEYSTORE);
    ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

    // 初始化密钥管理器和信任管理器
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, KEYPASSWORD.toCharArray());
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);

    // 创建并初始化SSL上下文
    SSLContext sslContext = null;
    sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
    // 上面注释行：使用Java默认的密钥和信任库，除非处理自签名证书，否则这通常就足够了

    // 获取SSL套接字工厂
    SSLSocketFactory factory = sslContext
        .getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

    // 设置客户端的套接字工厂
    chatclient.setSocketFactory(factory);

    // 阻塞连接，直到连接建立
    chatclient.connectBlocking();

    // 从控制台读取输入，发送消息或执行命令
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String line = reader.readLine();  // 读取一行输入
      if (line.equals("close")) {  // 如果输入"close"则关闭连接
        chatclient.closeBlocking();
      } else if (line.equals("open")) {  // 如果输入"open"则重新连接
        chatclient.reconnect();
      } else {  // 否则发送输入的消息
        chatclient.send(line);
      }
    }

  }
}
