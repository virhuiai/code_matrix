package com.virhuiai.jcef109.v2;


import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * CEF客户端自定义协议处理器 - 用于处理file://协议的请求
 *
 * 用途说明：
 * 该类继承自CefResourceHandlerAdapter，实现了对file://协议的自定义处理。
 * 主要功能包括：
 * 1. 拦截并处理file://开头的URL请求
 * 2. 将file://协议的URL映射到本地文件系统路径
 * 3. 读取本地文件内容并返回给CEF浏览器
 * 4. 根据文件扩展名自动设置正确的MIME类型
 *
 * 使用场景：
 * - 加载本地HTML、CSS、JavaScript等资源文件
 * - 显示本地图片、视频等多媒体文件
 * - 实现离线应用的资源加载
 *
 * 依赖说明：
 * 本类依赖于jcefmaven库（版本109.1.11），使用了CEF的资源处理器机制
 *
 * @see org.cef.handler.CefResourceHandlerAdapter
 */
public class CefVClientSchemeHandler extends CefResourceHandlerAdapter {
    /**
     * 处理的协议名称 - "file"
     * 表示本处理器负责处理file://协议的URL
     */
    public static final String scheme = "file";

    /**
     */
    public static final String other = "";

    /**
     * 文件输入流
     * 用于读取本地文件的内容
     */
    private InputStream data_;

    /**
     * 文件路径
     * 存储解析后的本地文件系统路径
     */
    private String filePath;

    /**
     * MIME类型
     * 存储文件对应的MIME类型，用于告知浏览器如何处理该文件
     */
    private String mime_type_;

    /**
     * 当前读取偏移量
     * 记录文件已经读取的字节数，用于支持分块读取
     */
    private int offset_ = 0;

    /**
     * 文件总长度（字节）
     * 存储文件的总字节数，用于响应头和读取控制
     */
    private int fileLength = 0;

    /**
     * MIME类型映射表
     * 根据文件扩展名映射到对应的MIME类型
     * TODO: 考虑将此映射表抽取为独立的配置类或使用现有的MIME类型库（如Apache Tika）
     * TODO: 考虑支持更多的文件类型，如.json -> application/json, .css -> text/css
     * TODO: 可以考虑使用ConcurrentHashMap以提高线程安全性
     */
    final HashMap<String, String> MIME_MapTable = new HashMap<String, String>() {
        private static final long serialVersionUID = 1;

        {
            // 视频文件类型
            put(".3gp", "video/3gpp");  // 3GP视频格式
            put(".asf", "video/x-ms-asf");  // ASF视频格式
            put(".avi", "video/x-msvideo");  // AVI视频格式
            put(".mov", "video/quicktime");  // QuickTime视频格式
            put(".mp4", "video/mp4");  // MP4视频格式
            put(".m4u", "video/vnd.mpegurl");  // M4U播放列表
            put(".m4v", "video/x-m4v");  // M4V视频格式
            put(".mpe", "video/mpeg");  // MPEG视频格式
            put(".mpeg", "video/mpeg");  // MPEG视频格式
            put(".mpg", "video/mpeg");  // MPEG视频格式
            put(".mpg4", "video/mp4");  // MPEG4视频格式
            put(".wmv", "video/x-ms-wmv");  // WMV视频格式

            // 音频文件类型
            put(".m3u", "audio/x-mpegurl");  // M3U播放列表
            put(".m4a", "audio/mp4a-latm");  // M4A音频格式
            put(".m4b", "audio/mp4a-latm");  // M4B音频格式（有声书）
            put(".m4p", "audio/mp4a-latm");  // M4P音频格式（受保护）
            put(".mp2", "audio/x-mpeg");  // MP2音频格式
            put(".mp3", "audio/x-mpeg");  // MP3音频格式
            put(".mpga", "audio/mpeg");  // MPEG音频格式
            put(".ogg", "audio/ogg");  // OGG音频格式
            put(".wav", "audio/x-wav");  // WAV音频格式
            put(".wma", "audio/x-ms-wma");  // WMA音频格式
            put(".rmvb", "video/x-pn-realaudio");  // RMVB格式

            // 图片文件类型
            put(".bmp", "image/bmp");  // BMP位图格式
            put(".gif", "image/gif");  // GIF动画格式
            put(".jpeg", "image/jpeg");  // JPEG图片格式
            put(".jpg", "image/jpeg");  // JPG图片格式
            put(".png", "image/png");  // PNG图片格式

            // 应用程序文件类型
            put(".apk", "application/vnd.android.package-archive");  // Android应用包
            put(".bin", "application/octet-stream");  // 二进制文件
            put(".class", "application/octet-stream");  // Java类文件
            put(".exe", "application/octet-stream");  // Windows可执行文件
            put(".jar", "application/java-archive");  // Java归档文件
            put(".mpc", "application/vnd.mpohun.certificate");  // 证书文件
            put(".pdf", "application/pdf");  // PDF文档
            put(".rtf", "application/rtf");  // RTF富文本格式

            // Microsoft Office文件类型
            put(".doc", "application/msword");  // Word文档（旧版）
            put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");  // Word文档（新版）
            put(".xls", "application/vnd.ms-excel");  // Excel表格（旧版）
            put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");  // Excel表格（新版）
            put(".pps", "application/vnd.ms-powerpoint");  // PowerPoint幻灯片
            put(".ppt", "application/vnd.ms-powerpoint");  // PowerPoint演示文稿（旧版）
            put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");  // PowerPoint演示文稿（新版）
            put(".msg", "application/vnd.ms-outlook");  // Outlook邮件
            put(".wps", "application/vnd.ms-works");  // Works文档

            // 文本文件类型
            put(".c", "text/plain");  // C源代码文件
            put(".conf", "text/plain");  // 配置文件
            put(".cpp", "text/plain");  // C++源代码文件
            put(".h", "text/plain");  // C/C++头文件
            put(".htm", "text/html");  // HTML文件
            put(".html", "text/html");  // HTML文件
            put(".java", "text/plain");  // Java源代码文件
            put(".js", "application/x-javascript");  // JavaScript文件
            put(".log", "text/plain");  // 日志文件
            put(".prop", "text/plain");  // 属性文件
            put(".rc", "text/plain");  // 资源脚本文件
            put(".sh", "text/plain");  // Shell脚本文件
            put(".txt", "text/plain");  // 纯文本文件
            put(".xml", "application/xml");  // XML文件 或text/xml

            // 压缩文件类型
            put(".gtar", "application/x-gtar");  // GNU tar归档
            put(".gz", "application/x-gzip");  // GZIP压缩文件
            put(".tar", "application/x-tar");  // TAR归档文件
            put(".tgz", "application/x-compressed");  // TAR.GZ压缩文件
            put(".z", "application/x-compress");  // Z压缩文件
            put(".zip", "application/x-zip-compressed");  // ZIP压缩文件

            // 默认类型 - 用于未知文件类型
            put(CefVClientSchemeHandler.other, "*/*");
        }
    };

    /**
     * 处理请求方法
     * 当CEF遇到file://协议的请求时，会调用此方法
     *
     * @param request CEF请求对象，包含URL等请求信息
     * @param callback 回调对象，用于通知CEF继续处理
     * @return true表示请求被处理，false表示未处理
     *
     * TODO: 考虑添加更详细的错误处理和日志记录
     * TODO: 考虑支持URL编码的文件路径
     * TODO: 路径转换逻辑可能需要根据不同操作系统进行调整
     */
    public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
        boolean handled = false;  // 标记请求是否被处理
        String url = request.getURL();  // 获取请求的URL

        // 检查是否为file://协议
        if (url.startsWith("file://")) {
            handled = true;// 这儿的前后处理不错
            try {
                // 根据URL获取MIME类型
                this.mime_type_ = GetMimeTypeName(url);

                // 解析文件路径
                // 移除"file://"前缀
                this.filePath = url.substring("file://".length());
                // 将路径格式从"/C:/path"转换为"C:/path"（Windows路径格式）
                // TODO: 这种路径转换只适用于Windows，需要考虑跨平台兼容性
                this.filePath = this.filePath.replaceFirst("/", ":/");

                // 创建文件对象并验证文件
                File f = new File(this.filePath);
                if (f.canRead() && f.exists() && f.isFile()) {
                    // 文件存在且可读，记录文件长度并创建输入流
                    this.fileLength = (int) f.length();  // TODO: 使用int可能会导致大文件溢出问题
                    this.data_ = new FileInputStream(f);
                } else {
                    // 文件不存在或不可读，抛出异常
                    throw new Exception("invalid client resource:" + url);
                }
            } catch (Exception e) {
                // 处理过程中出错，标记为未处理
                handled = false;
                e.printStackTrace();  // TODO: 应该使用日志框架而不是直接打印堆栈
            }
        }

        // 如果请求被成功处理，通知CEF继续
        if (handled) {
            callback.Continue();
            return true;
        }
        return false;
    }

    /**
     * 获取响应头信息
     * CEF调用此方法获取响应的HTTP头信息
     *
     * @param response CEF响应对象，用于设置响应头
     * @param response_length 响应内容长度的引用
     * @param redirectUrl 重定向URL的引用（本实现中未使用）
     *
     * TODO: 可以考虑添加更多的响应头，如Cache-Control、Last-Modified等
     */
    public void getResponseHeaders(CefResponse response, IntRef response_length, StringRef redirectUrl) {
        // 设置MIME类型
        response.setMimeType(this.mime_type_);
        // 设置HTTP状态码为200（成功）
        response.setStatus(200);
        // 设置响应内容长度
        response_length.set(this.fileLength);
    }

    /**
     * 读取响应数据
     * CEF会多次调用此方法来分块读取文件内容
     *
     * @param data_out 输出缓冲区，用于存储读取的数据
     * @param bytes_to_read 请求读取的字节数
     * @param bytes_read 实际读取的字节数的引用
     * @param callback 回调对象（本实现中未使用）
     * @return true表示还有数据可读，false表示数据读取完毕
     *
     * TODO: 考虑添加读取进度的监控和日志
     * TODO: 异常处理可以更细化，区分不同的错误类型
     */
    public synchronized boolean readResponse(byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
        boolean has_data = false;  // 标记是否还有数据可读

        try {
            if (this.offset_ < this.fileLength) {
                // 还有数据未读取
                // 计算本次实际读取的字节数（不能超过剩余字节数）
                int transfer_size = Math.min(bytes_to_read, this.fileLength - this.offset_);
                // 从输入流读取数据到输出缓冲区
                this.data_.read(data_out, 0, transfer_size);
                // 更新偏移量
                this.offset_ += transfer_size;
                // 设置实际读取的字节数
                bytes_read.set(transfer_size);
                has_data = true;
            } else {
                // 数据已全部读取完毕
                this.offset_ = 0;  // 重置偏移量
                bytes_read.set(0);  // 设置读取字节数为0
                this.data_.close();  // 关闭输入流，释放资源
            }
        } catch (Exception e) {
            // 读取过程中出错，重置文件长度并返回无数据
            this.fileLength = 0;
            has_data = false;
            // TODO: 应该记录错误日志，而不是静默失败
        }

        return has_data;
    }

    /**
     * 根据URL获取MIME类型名称
     * 通过文件扩展名查找对应的MIME类型
     *
     * @param url 文件URL
     * @return 对应的MIME类型字符串
     *
     * TODO: 考虑处理没有扩展名的文件
     * TODO: 考虑处理URL中包含查询参数的情况
     */
    private String GetMimeTypeName(String url) {
        // 获取文件扩展名（包含点号）
        String key = url.substring(url.lastIndexOf("."));

        // 如果映射表中不包含该扩展名，使用默认类型
        if (!this.MIME_MapTable.containsKey(key)) {
            key = other;  // TODO:  建议使用专门的默认MIME类型常量
        }

        return this.MIME_MapTable.get(key);
    }
}