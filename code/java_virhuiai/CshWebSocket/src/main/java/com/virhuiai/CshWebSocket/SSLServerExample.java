package com.virhuiai.CshWebSocket;

/*
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

// 导入所需的Java类库
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

/**
 * SSL服务器示例类
 * 演示如何创建支持SSL/TLS的WebSocket服务器
 */
public class SSLServerExample {

  /*
   * Keystore with certificate created like so (in JKS format):
   *
   *keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
   */

  /*
   * 密钥库及证书的创建方式（JKS格式）：
   * 使用keytool工具生成RSA算法的密钥，有效期10年，存储在keystore.jks文件中
   * 存储密码为"storepassword"，密钥密码为"keypassword"，别名为"default"
   * 证书信息包含常用名称、组织单位、组织、城市、地区和国家信息
   */
  public static void main(String[] args) throws Exception {
    // 创建聊天服务器实例，监听8887端口
    // 注意：Firefox浏览器只允许通过443端口建立多个SSL连接（在FF16版本上测试过）
    ChatServer chatserver = new ChatServer(
        8887); // Firefox does allow multible ssl connection only via port 443 //tested on FF16

    // 加载密钥库
    // 设置密钥库相关参数
    String STORETYPE = "JKS";  // 密钥库类型
    String KEYSTORE = Paths.get("src", "test", "java", "org", "java_websocket", "keystore.jks")
        .toString();  // 密钥库文件路径
    String STOREPASSWORD = "storepassword";  // 密钥库密码
    String KEYPASSWORD = "keypassword";  // 密钥密码

    // 初始化密钥库实例
    KeyStore ks = KeyStore.getInstance(STORETYPE);
    File kf = new File(KEYSTORE);
    ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

    // 创建并初始化密钥管理器工厂
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, KEYPASSWORD.toCharArray());

    // 创建并初始化信任管理器工厂
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);

    // 创建并初始化SSL上下文
    SSLContext sslContext = null;
    sslContext = SSLContext.getInstance("TLS");  // 使用TLS协议
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    // 为聊天服务器设置WebSocket工厂，启用SSL支持
    chatserver.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));

    // 启动聊天服务器
    chatserver.start();

  }
}
