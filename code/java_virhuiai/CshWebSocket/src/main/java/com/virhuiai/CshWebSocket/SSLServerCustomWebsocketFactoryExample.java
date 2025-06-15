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

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.server.CustomSSLWebSocketServerFactory;

/**
 * Example for using the CustomSSLWebSocketServerFactory to allow just specific cipher suites
 *
 * 使用CustomSSLWebSocketServerFactory来仅允许特定加密套件的示例
 */
public class SSLServerCustomWebsocketFactoryExample {

  /*
   * Keystore with certificate created like so (in JKS format):
   *
   * 密钥库和证书的创建方法（JKS格式）：
   *
   *keytool -genkey -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
   */
  public static void main(String[] args) throws Exception {
    // 创建聊天服务器，端口为8887
    // Firefox仅允许通过443端口进行多个SSL连接（在FF16上测试过）
    ChatServer chatserver = new ChatServer(8887);

    // load up the key store
    // 加载密钥库
    String STORETYPE = "JKS";  // 密钥库类型
    String KEYSTORE = Paths.get("src", "test", "java", "org", "java_websocket", "keystore.jks")
        .toString();  // 密钥库文件路径
    String STOREPASSWORD = "storepassword";  // 密钥库密码
    String KEYPASSWORD = "keypassword";  // 密钥密码

    // 初始化密钥库实例
    KeyStore ks = KeyStore.getInstance(STORETYPE);
    File kf = new File(KEYSTORE);
    ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

    // 初始化密钥管理器工厂
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, KEYPASSWORD.toCharArray());
    // 初始化信任管理器工厂
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);

    // 初始化SSL上下文，使用TLS协议
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    // Lets remove some ciphers and protocols
    // 移除一些加密套件和协议
    SSLEngine engine = sslContext.createSSLEngine();
    // 获取所有启用的加密套件并转换为列表
    List<String> ciphers = new ArrayList<String>(Arrays.asList(engine.getEnabledCipherSuites()));
    // 移除特定的加密套件
    ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
    // 获取所有启用的协议并转换为列表
    List<String> protocols = new ArrayList<String>(Arrays.asList(engine.getEnabledProtocols()));
    // 移除SSLv3协议
    protocols.remove("SSLv3");
    // 创建自定义SSL WebSocket服务器工厂，使用筛选后的协议和加密套件
    CustomSSLWebSocketServerFactory factory = new CustomSSLWebSocketServerFactory(sslContext,
        protocols.toArray(new String[]{}), ciphers.toArray(new String[]{}));

    // Different example just using specific ciphers and protocols
    // 另一个示例：仅使用特定的加密套件和协议
        /*
        String[] enabledProtocols = {"TLSv1.2"};  // 仅启用TLSv1.2协议
		String[] enabledCipherSuites = {"TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"};  // 仅启用指定的加密套件
        CustomSSLWebSocketServerFactory factory = new CustomSSLWebSocketServerFactory(sslContext, enabledProtocols,enabledCipherSuites);
        */

    // 为聊天服务器设置WebSocket工厂
    chatserver.setWebSocketFactory(factory);

    // 启动聊天服务器
    chatserver.start();

  }
}
