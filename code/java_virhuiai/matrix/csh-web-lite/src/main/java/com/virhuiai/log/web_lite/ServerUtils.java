package com.virhuiai.log.web_lite;

import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerUtils {
    public static HttpServer initServer() throws IOException {
        // 使用0作为端口号让系统自动分配一个可用端口
        // Backlog 参数详解
        // backlog 参数指的是操作系统可以保持挂起状态的最大入站连接数。
        // 这个数值定义了在服务器尝试接受新的连接之前，可以排队等待接受的TCP连接请求的最大数量。
        // 这个队列包括了已经完成三次握手过程，等待应用程序接受的连接。
        // 当在 HttpServer.create 方法中将 backlog 设置为 0 时，
        // 实际的行为可能取决于底层的操作系统和 Java 虚拟机的实现。
        // 一般来说，如果指定 backlog 为 0，系统将使用一个默认值。这个默认值因操作系统和 JVM 的实现而异，
        // 但通常被设计为能够合理地平衡性能和资源使用。
        return HttpServer.create(new InetSocketAddress(0), 0);
    }

    public static int  getPort(HttpServer staticServer){
        return staticServer.getAddress().getPort();
    }

    /**
     * 添加 getResourcePath 方法来获取资源路径，以确保路径拼接的安全性。
     * @param requestPath
     * @return
     */
    public static Path getResourcePath(String requestPath) {
        System.out.println("requestPath:" + requestPath);
        String path;
        if ("/".equals(requestPath)) {
            path = "index.html"; // 默认文件
        }else{
            // getResource 是 Java 中 Class 类的方法，用于加载类路径下的资源。
            //  资源路径规则:
            //            如果 name 以 / 开头：则从类路径的根目录查找资源。
            //            如果 name 不以 / 开头：则从调用该方法的类的包路径开始查找资源。
            // 示例
            //            1. 从类路径的根目录查找资源
            //            假设项目结构如下：
            //            src/main/resources
            //            ├── config.properties
            //                        XXX.class.getResource("/config.properties");
            //
            //            2.从类的包路径查找资源
            //            假设项目结构如下：
            //            src/main/java/com/example
            //            ├── Main.java
            //            ├── resources
            //            │   ├── data.txt
            //                        XXX.class.getResource("resources/data.txt");

            //  注意点:
            //    name 表示的资源文件需要在类路径中（即 src/main/resources 或编译后的 target/classes 等目录中）。
            //    返回的 URL 是可以进一步用来读取资源内容的。

            // 常用于访问与应用打包在一起的资源文件，特别是在 Java 应用被打包成 JAR 或 WAR 时依然能正确找到资源文件。
            // 这个方法是获取打包后Jar/War文件中资源的一种方式，在开发时和部署后都能正常工作。
            // 注意：资源如果不存在，getResource(requestPath) 会返回 null
            URL resourceUrl = ServerUtils.class.getResource(requestPath);
            System.out.println("resourceUrl:" + resourceUrl);
            if (null ==  resourceUrl) {
//                throw new FileNotFoundException("Resource not found: " + requestPath);
                 return null;
            }
            // getPath() 是 URL 对象的一个方法，用于返回 URL 的路径部分（即资源的绝对路径）。
            path = resourceUrl.getPath();
            
        }

        // return Paths.get("src/main/resources", requestPath);

        System.out.println("path:" + path);
        System.out.println("Paths.get(path):" + Paths.get(path));
        //getPath() 返回的路径可能包含操作系统特定的格式，比如在 Windows 上可能会包含反斜杠（\）。
        //需要跨平台兼容，建议使用 Paths.get() 或 new File() 来处理路径。
        return Paths.get(path);///Volumes/THAWSPACE/CshProject/Csh_2_StaticWebServer/target/classes/index.html
    }

    /**
     * 定义一个公共的静态方法，根据文件路径返回相应的MIME类型字符串
     * 这段代码定义了一个 Java 方法 getContentType，其目的是根据提供的文件路径（通过其扩展名）确定该文件的正确 MIME 类型。这个方法非常有用，例如在 HTTP 服务器中，当你需要根据文件类型设置正确的 Content-Type 响应头时。
     *
     * 下面是代码的详细解释：
     * 1.提取文件扩展名:方法首先从传入的 filePath 字符串中提取文件的扩展名。这通过查找最后一个点（.）字符的位置，并从该位置到字符串末尾的子字符串完成，这部分通常代表文件的扩展名。
     * 2.确定MIME类型:使用 switch 语句根据文件的扩展名决定返回的 MIME 类型。对于常见的 web 文件类型（如 HTML, CSS, JavaScript），方法返回相应的标准 MIME 类型。
     * 对于不是这几种类型的文件，方法返回 "application/octet-stream"。这是一个通用的 MIME 类型，用于表示任意的二进制数据，通常用于客户端不应该尝试解释为任何已知文件格式的文件。这样做增加了传输未知文件类型时的安全性和灵活性。
     *
     * 使用场景:
     * 这个方法可以在任何需要根据文件扩展名来确定 MIME 类型的场合使用，特别是在实现 web 服务器或处理 HTTP 响应时。正确的 Content-Type 非常重要，因为它告诉浏览器或其他客户端如何处理接收到的数据。例如，在发送 HTML 文件时，正确设置 Content-Type 为 text/html 可以确保浏览器将其作为网页正确显示，而不是作为文本或其他格式处理。
     * @param filePath
     * @return
     */
    public static String getContentType(String filePath) {
        // 从文件路径中获取文件扩展名，假定文件名中最后一个点后面的部分为扩展名
        String extension = filePath.substring(filePath.lastIndexOf('.'));
        // 使用switch语句根据不同的文件扩展名返回相应的MIME类型
        switch (extension) {
            case ".html":
                // 如果扩展名是.html，则返回"text/html"
                return "text/html";
            case ".js":
                // 如果扩展名是.js，则返回"application/javascript"
                // 注意这里使用了"application/javascript"代替旧的"text/javascript"
                return "application/javascript";
            case ".css":
                // 如果扩展名是.css，则返回"text/css"
                return "text/css";
            default:
                // 如果扩展名不是上述任何一种，使用"application/octet-stream"
                // 这种类型表示二进制数据流，通常用于未知的文件类型
                return "application/octet-stream"; // 更安全的默认值，避免文本解释错误
        }
    }


    public static String readStringFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }


}
