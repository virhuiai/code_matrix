package com.virhuiai.StaticWebServer;

import com.sun.net.httpserver.HttpExchange;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SendUtils {
    private static Log LOGGER = CshLogUtils.createLogExtended(SendUtils.class);

    /**
     * 发送一个带有自定义状态码和响应消息的HTTP响应。
     *
     * @param exchange    HTTP交换对象，用于发送响应。
     * @param statusCode   HTTP状态码，例如404。
     * @param statusMessage HTTP状态消息，例如"文件未找到"。
     * @throws IOException 如果写入响应体时发生I/O错误。
     */
    public static void sendCustomResponse(HttpExchange exchange, int statusCode, String statusMessage) throws IOException {
        // 将状态消息转换为字节，并计算字节长度
        byte[] responseBytes = statusMessage.getBytes(StandardCharsets.UTF_8);
        int length = responseBytes.length;

        // 设置响应头，包括状态码、响应体的长度和字符集
        // 当在HTTP响应中发送中文内容时，浏览器可能会因为编码问题而显示乱码。
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        // 发送HTTP响应头，包括状态码和响应体的长度
        exchange.sendResponseHeaders(statusCode, length);

        // 使用 try-with-resources 语句创建一个输出流，并确保在响应发送后该流被正确关闭
        try (OutputStream responseBody = exchange.getResponseBody()) {
            // 将响应消息的字节写入响应体
            responseBody.write(responseBytes);
        }catch (Exception e){
            // 捕捉处理响应时可能发生的IO异常，并通过日志记录错误信息
            LOGGER.error("错误:" + e.getMessage(), e);
        }
    }

    public static void sendCustomResponse(HttpExchange exchange, HttpStatus status) throws IOException {
        sendCustomResponse(exchange,status.getCode(), status.getChineseMessage());
    }


    /**
     * 这段代码是一个 Java 方法，用于处理 HTTP 请求时，如果遇到不被允许的请求方法（例如，客户端发送了一个 POST 请求到只接受 GET 请求的端点），则发送一个 HTTP 405 状态码（Method Not Allowed）响应。
     *
     * 以下是此方法的详细解释：
     * 1。设置响应头:方法首先通过 sendResponseHeaders 方法设置HTTP响应的状态码为 405，这表示客户端使用了一个不被服务器允许或不支持的方法来发起请求。
     * 响应体的长度设置为错误消息字符串“不被允许的请求方法”的长度，以确保客户端知道将要接收的内容大小。
     * 2。发送响应体:使用 getResponseBody 方法获取用于发送响应内容的 OutputStream。
     * 在 try 块中，错误信息被转换成字节（使用 UTF-8 编码），然后写入到输出流中。使用 try-with-resources 语法确保在响应体内容被发送后，输出流会被正确关闭，这是处理 I/O 流时的一种最佳实践。
     * 3。异常处理: 方法签名中声明了 throws IOException，这表示如果在处理 HTTP 交换或写入响应时发生 I/O 异常，异常将被抛出到方法的调用者。这种方式要求调用者处理或进一步抛出这个异常。
     *
     * 使用场景：这个方法通常在处理 HTTP 请求时检查到非法或不支持的 HTTP 方法（如 PUT, POST, DELETE 等）时调用，以通知客户端当前请求的 HTTP 方法是不被允许的。例如，如果你的服务器设计为只接受 GET 请求，当接收到 POST 请求时，就可以使用这个方法发送 405 响应。
     * @param exchange
     * @throws IOException
     */
    public static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendCustomResponse(exchange, HttpStatus.BAD_METHOD_405);
    }

    public static void sendBadRequest(HttpExchange exchange) throws IOException {
        sendCustomResponse(exchange, HttpStatus.BAD_REQUEST_400);
    }

    /**
     * 定义一个公共静态方法，用于发送404未找到响应
     *
     * 这段Java代码定义了一个名为 sendNotFound 的方法，用于处理 HTTP 请求中文件未找到的情况。当服务器无法找到请求的文件时，该方法会发送一个状态码为404的HTTP响应，通知客户端文件未找到。以下是代码的详细解释
     * 1。构造响应内容: 方法首先构造一个字符串 response，其中包含了错误信息和未找到的文件路径。
     * 2。发送响应头: 使用 exchange.sendResponseHeaders(404, response.length()) 发送 HTTP 响应状态码 404（表示请求的资源未找到）和响应体的长度。这里的长度是响应字符串的长度。
     * 3。发送响应体: 使用 OutputStream 将错误信息的字节写入响应体。这里使用 StandardCharsets.UTF_8 将字符串转换为字节，确保在不同的系统和环境中字符编码的正确性。
     * 4。资源管理: 使用 try-with-resources 语句来自动管理输出流的资源，确保无论响应成功与否，输出流都将被正确关闭，防止资源泄漏。
     * @param exchange 用于处理一个 HTTP 交换的对象，包括接收请求和发送响应。
     * @param filePath 请求的未找到文件的路径。
     * @throws IOException
     */
    public static void sendNotFound(HttpExchange exchange, String filePath) throws IOException {
        // 构造响应体内容，包括错误信息和未找到的文件路径
//        String response = "文件末找到: " + filePath;
        LOGGER.error("文件末找到:" + filePath);
        sendCustomResponse(exchange, HttpStatus.NOT_FOUND_404);
    }

    /**
     * 定义一个公共静态方法，通过 HttpExchange 发送一个文件给客户端
     * 以下Java代码定义了一个名为 sendFile 的方法，用于通过 HTTP 交换发送文件。方法使用了 java.nio.file.Files 类来读取文件内容，并通过 HttpExchange 对象发送 HTTP 响应。这个方法非常适合在 HTTP 服务器实现中使用，用于处理文件传输的请求。
     *
     * 下面是方法的详细解释：
     * 1.读取文件: 首先，方法使用 Files.readAllBytes 从给定的路径 resourcePath 读取全部文件内容到 fileBytes 数组。
     * 2.设置 MIME 类型: 使用 getContentType(resourcePath.toString()) 方法来确定文件的 MIME 类型，并设置在 HTTP 响应的 Content-Type 头部。
     * 3.发送响应头: 通过 exchange.sendResponseHeaders(200, fileBytes.length) 发送 HTTP 响应状态码 200（表示请求成功）和响应体的长度。
     * 4.发送响应体: 使用 OutputStream 将文件字节写入响应体。使用了 try-with-resources 语句来自动管理资源，确保无论响应成功与否，输出流都将被正确关闭，防止资源泄漏。
     * @param exchange 这是一个用于处理一个 HTTP 交换的对象，包括接收请求和发送响应。
     * @param resourcePath 指向要发送的文件的路径。
     * @throws IOException
     */
    public static void sendFile(HttpExchange exchange, Path resourcePath) throws IOException {
        // 读取文件所有字节到一个字节数组中
        byte[] fileBytes = Files.readAllBytes(resourcePath);

        // 设置HTTP响应头中的Content-Type，调用前面定义的getContentType方法来获取文件的MIME类型
        exchange.getResponseHeaders().set("Content-Type", ServerUtils.getContentType(resourcePath.toString()));

        // 发送HTTP响应头，状态码设置为200（OK），并设置响应体的长度
        exchange.sendResponseHeaders(HttpStatus.OK_200.getCode(), fileBytes.length);

        // 使用 try-with-resources 语句创建一个输出流，确保在响应发送后该流被正确关闭
        try (OutputStream responseBody = exchange.getResponseBody()) {
            // 将文件字节写入响应体
            responseBody.write(fileBytes);
        }
    }


    /**
     * 发送内部错误响应
     * 用于在 HTTP 服务器中处理和发送内部服务器错误（HTTP 500）响应的功能
     *
     * 功能解释
     *
     * 这个方法sendInternalError的主要作用是向客户端发送一个包含自定义错误消息的 HTTP 500 响应。方法接收两个参数：HttpExchange 对象和一个字符串 errorMessage。HttpExchange 对象提供了必要的方法来构造和发送 HTTP 响应。
     *
     * 1。设置响应头：方法首先通过 sendResponseHeaders 设置响应的状态码为 500（表示服务器内部错误），并将响应内容长度设置为错误消息字符串的长度。这是告诉客户端接下来的响应体有多大。
     * 2。发送响应体：随后，方法使用 getResponseBody 获取响应体的 OutputStream，并将错误消息作为响应体发送。这是实际向客户端传输错误信息的部分。
     * 3。异常处理：如果在处理响应中出现 IOException（输入输出异常），则通过日志系统记录这个异常，通常这表示在网络层面或文件系统中发生了问题，使得服务器无法正常发送响应。
     * @param exchange
     * @param errorMessage
     */
    public static void sendInternalError(HttpExchange exchange, String errorMessage) throws IOException {
        LOGGER.error(HttpStatus.INTERNAL_ERROR_500.getChineseMessage() + ":" +  errorMessage);
        SendUtils.sendCustomResponse(exchange,HttpStatus.INTERNAL_ERROR_500);
    }
}
