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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.Opcode;

/**
 * This example shows how to send fragmented frames.<br> For information on when to used fragmented
 * frames see http://tools.ietf.org/html/rfc6455#section-5.4<br> Fragmented and normal messages can
 * not be mixed. One is however allowed to mix them with control messages like ping/pong.
 *
 * @see WebSocket#sendFragmentedFrame(Opcode, ByteBuffer, boolean)
 **/
/**
 * 此示例展示如何发送分片帧。<br> 关于何时使用分片帧的信息，请参见
 * http://tools.ietf.org/html/rfc6455#section-5.4<br>
 * 分片消息和普通消息不能混用。但是，允许将它们与控制消息（如ping/pong）混合使用。
 *
 * @see WebSocket#sendFragmentedFrame(Opcode, ByteBuffer, boolean)
 **/
public class FragmentedFramesExample {

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    // WebSocketImpl.DEBUG = true; // will give extra output
    // WebSocketImpl.DEBUG = true; // 将提供额外的输出信息

    WebSocketClient websocket = new ExampleClient(new URI("ws://localhost:8887"));
    if (!websocket.connectBlocking()) {
      System.err.println("Could not connect to the server.");
      // 无法连接到服务器
      return;
    }

    System.out.println("This example shows how to send fragmented(continuous) messages.");
    // 此示例展示如何发送分片（连续）消息

    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    while (websocket.isOpen()) {
      System.out
          .println("Please type in a loooooong line(which then will be send in 2 byte fragments):");
      // 请输入一个很长的行（随后将以2字节的片段发送）
      String longline = stdin.readLine();
      ByteBuffer longelinebuffer = ByteBuffer.wrap(longline.getBytes());
      longelinebuffer.rewind();
      // 将输入的字符串转换为ByteBuffer并重置位置指针

      for (int position = 2; ; position += 2) {
        if (position < longelinebuffer.capacity()) {
          longelinebuffer.limit(position);
          websocket.sendFragmentedFrame(Opcode.TEXT, longelinebuffer,
              false);// when sending binary data one should use Opcode.BINARY
              // 发送二进制数据时应使用Opcode.BINARY
          assert (longelinebuffer.remaining() == 0);
          // after calling sendFragmentedFrame one may reuse the buffer given to the method immediately
          // 调用sendFragmentedFrame后，可以立即重用传递给该方法的缓冲区
        } else {
          longelinebuffer.limit(longelinebuffer.capacity());
          websocket
              .sendFragmentedFrame(Opcode.TEXT, longelinebuffer, true);// sending the last frame
              // 发送最后一个帧
          break;
        }

      }
      System.out.println("You can not type in the next long message or press Ctr-C to exit.");
      // 您现在可以输入下一条长消息，或按Ctrl-C退出
    }
    System.out.println("FragmentedFramesExample terminated");
    // FragmentedFramesExample已终止
  }
}
