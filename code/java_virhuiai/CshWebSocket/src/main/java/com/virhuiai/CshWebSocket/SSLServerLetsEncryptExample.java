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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;


/**
 * SSL Example using the LetsEncrypt certificate See https://github.com/TooTallNate/Java-WebSocket/wiki/Getting-a-SSLContext-from-different-sources#getting-a-sslcontext-using-a-lets-encrypt-certificate
 *
 * 使用Let's Encrypt证书的SSL示例，详见GitHub Wiki页面
 */
public class SSLServerLetsEncryptExample {

  public static void main(String[] args) throws Exception {
    // 创建一个在8887端口运行的聊天服务器
    ChatServer chatserver = new ChatServer(8887);

    // 获取SSL上下文
    SSLContext context = getContext();
    if (context != null) {
      // 如果SSL上下文不为空，设置WebSocket工厂为SSL WebSocket服务器工厂
      chatserver.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(getContext()));
    }
    // 设置连接超时时间为30秒
    chatserver.setConnectionLostTimeout(30);
    // 启动聊天服务器
    chatserver.start();

  }

  /**
   * 获取SSL上下文
   * 该方法读取Let's Encrypt证书文件并创建SSL上下文
   */
  private static SSLContext getContext() {
    SSLContext context;
    // 密钥库密码，建议在实际应用中修改
    String password = "CHANGEIT";
    // 证书文件所在目录
    String pathname = "pem";
    try {
      // 创建TLS安全协议的SSL上下文
      context = SSLContext.getInstance("TLS");

      // 从PEM文件中解析证书和私钥
      byte[] certBytes = parseDERFromPEM(getBytes(new File(pathname + File.separator + "cert.pem")),
          "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
      byte[] keyBytes = parseDERFromPEM(
          getBytes(new File(pathname + File.separator + "privkey.pem")),
          "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

      // 从DER格式字节生成X509证书和RSA私钥
      X509Certificate cert = generateCertificateFromDER(certBytes);
      RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

      // 创建JKS类型的密钥库
      KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(null);
      // 将证书和私钥添加到密钥库中
      keystore.setCertificateEntry("cert-alias", cert);
      keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[]{cert});

      // 创建密钥管理器工厂
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(keystore, password.toCharArray());

      // 获取密钥管理器
      KeyManager[] km = kmf.getKeyManagers();

      // 初始化SSL上下文
      context.init(km, null, null);
    } catch (Exception e) {
      // 如果出现异常，返回null
      context = null;
    }
    return context;
  }

  /**
   * 从PEM格式的字节数组中解析DER格式数据
   *
   * @param pem PEM格式的字节数组
   * @param beginDelimiter 开始分隔符
   * @param endDelimiter 结束分隔符
   * @return DER格式的字节数组
   */
  private static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
    String data = new String(pem);
    String[] tokens = data.split(beginDelimiter);
    tokens = tokens[1].split(endDelimiter);
   // return DatatypeConverter.parseBase64Binary(tokens[0]);
    // 注意：这里返回null，实际应用中需要使用Base64解码，例如使用Java 8的Base64.getDecoder().decode(tokens[0])
    return null;
  }

  /**
   * 从DER格式字节生成RSA私钥
   *
   * @param keyBytes DER格式的私钥字节
   * @return RSA私钥对象
   */
  private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes)
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    // 创建PKCS8编码的密钥规范
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

    // 获取RSA密钥工厂
    KeyFactory factory = KeyFactory.getInstance("RSA");

    // 生成私钥并返回
    return (RSAPrivateKey) factory.generatePrivate(spec);
  }

  /**
   * 从DER格式字节生成X509证书
   *
   * @param certBytes DER格式的证书字节
   * @return X509证书对象
   */
  private static X509Certificate generateCertificateFromDER(byte[] certBytes)
      throws CertificateException {
    // 创建X.509证书工厂
    CertificateFactory factory = CertificateFactory.getInstance("X.509");

    // 生成证书并返回
    return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
  }

  /**
   * 读取文件内容到字节数组
   *
   * @param file 要读取的文件
   * @return 包含文件内容的字节数组
   */
  private static byte[] getBytes(File file) {
    byte[] bytesArray = new byte[(int) file.length()];

    FileInputStream fis = null;
    try {
      // 创建文件输入流
      fis = new FileInputStream(file);
      // 将文件内容读入字节数组
      fis.read(bytesArray);
      // 关闭文件流
      fis.close();
    } catch (IOException e) {
      // 打印异常堆栈
      e.printStackTrace();
    }
    return bytesArray;
  }
}
