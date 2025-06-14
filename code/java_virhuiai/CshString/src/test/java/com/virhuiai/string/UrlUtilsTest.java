package com.virhuiai.string;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UrlUtilsTest {

    private UrlUtils urlUtils;

    // 创建UrlUtils的实现类用于测试
    private static class UrlUtilsImpl implements UrlUtils {
        // 使用默认实现
    }

    @Before
    public void setUp() {
        urlUtils = new UrlUtilsImpl();
    }

    // ==================== getProtocol 方法测试 ====================

    @Test
    public void testGetProtocol_normalHttpUrl() {
        // 测试正常的HTTP协议
        String url = "http://www.example.com/path/file.html";
        assertEquals("http", urlUtils.getProtocol(url));
    }

    @Test
    public void testGetProtocol_normalHttpsUrl() {
        // 测试正常的HTTPS协议
        String url = "https://www.example.com/path/file.html";
        assertEquals("https", urlUtils.getProtocol(url));
    }

    @Test
    public void testGetProtocol_ftpUrl() {
        // 测试FTP协议
        String url = "ftp://ftp.example.com/files/document.pdf";
        assertEquals("ftp", urlUtils.getProtocol(url));
    }

    @Test
    public void testGetProtocol_customProtocol() {
        // 测试自定义协议
        String url = "custom://custom.example.com/resource";
        assertEquals("custom", urlUtils.getProtocol(url));
    }

    @Test
    public void testGetProtocol_nullUrl() {
        // 测试null输入
        assertNull(urlUtils.getProtocol(null));
    }

    @Test
    public void testGetProtocol_emptyUrl() {
        // 测试空字符串
        assertNull(urlUtils.getProtocol(""));
    }

    @Test
    public void testGetProtocol_blankUrl() {
        // 测试空白字符串
        assertNull(urlUtils.getProtocol("   "));
    }

    @Test
    public void testGetProtocol_noProtocol() {
        // 测试没有协议的URL
        String url = "www.example.com/path/file.html";
        assertNull(urlUtils.getProtocol(url));
    }

    @Test
    public void testGetProtocol_onlyProtocol() {
        // 测试只有协议部分
        String url = "http://";
        assertEquals("http", urlUtils.getProtocol(url));
    }

    // ==================== getDomain 方法测试 ====================

    @Test
    public void testGetDomain_normalUrl() {
        // 测试正常的URL
        String url = "http://www.example.com/path/file.html";
        assertEquals("www.example.com", urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_urlWithPort() {
        // 测试带端口的URL
        String url = "http://www.example.com:8080/path/file.html";
        assertEquals("www.example.com:8080", urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_ipAddress() {
        // 测试IP地址作为域名
        String url = "http://192.168.1.1/admin/index.html";
        assertEquals("192.168.1.1", urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_ipAddressWithPort() {
        // 测试带端口的IP地址
        String url = "http://192.168.1.1:8080/admin/index.html";
        assertEquals("192.168.1.1:8080", urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_nullUrl() {
        // 测试null输入
        assertNull(urlUtils.getDomain(null));
    }

    @Test
    public void testGetDomain_emptyUrl() {
        // 测试空字符串
        assertNull(urlUtils.getDomain(""));
    }

    @Test
    public void testGetDomain_noDomain() {
        // 测试没有域名的URL
        String url = "http://";
        assertNull(urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_invalidUrl() {
        // 测试无效的URL格式
        String url = "not-a-url";
        assertNull(urlUtils.getDomain(url));
    }

    @Test
    public void testGetDomain_onlyDomain() {
        // 测试只有域名没有路径
        String url = "http://www.example.com";
        assertEquals("www.example.com", urlUtils.getDomain(url));
    }

    // ==================== getPath 方法测试 ====================

    @Test
    public void testGetPath_normalUrl() {
        // 测试正常的URL路径
        String url = "http://www.example.com/path/to/file.html";
        assertEquals("/path/to/file.html", urlUtils.getPath(url));
    }

    @Test
    public void testGetPath_rootPath() {
        // 测试根路径
        String url = "http://www.example.com/";
        assertEquals("/", urlUtils.getPath(url));
    }

    @Test
    public void testGetPath_noPath() {
        // 测试没有路径的URL
        String url = "http://www.example.com";
        assertEquals("", urlUtils.getPath(url));
    }

    @Test
    public void testGetPath_withQueryString() {
        // 测试带查询参数的路径
        String url = "http://www.example.com/search?q=test&page=1";
        assertEquals("/search?q=test&page=1", urlUtils.getPath(url));
    }

    @Test
    public void testGetPath_withAnchor() {
        // 测试带锚点的路径
        String url = "http://www.example.com/page.html#section1";
        assertEquals("/page.html#section1", urlUtils.getPath(url));
    }

    @Test
    public void testGetPath_nullUrl() {
        // 测试null输入
        assertNull(urlUtils.getPath(null));
    }

    @Test
    public void testGetPath_emptyUrl() {
        // 测试空字符串
        assertNull(urlUtils.getPath(""));
    }

    @Test
    public void testGetPath_noProtocol() {
        // 测试没有协议的URL
        String url = "www.example.com/path/file.html";
        assertNull(urlUtils.getPath(url));
    }

    // ==================== getFileName 方法测试 ====================

    @Test
    public void testGetFileName_normalFile() {
        // 测试正常的文件名
        String url = "http://www.example.com/path/to/file.html";
        assertEquals("file.html", urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_fileWithQueryString() {
        // 测试带查询参数的文件名
        String url = "http://www.example.com/download/document.pdf?version=2";
        assertEquals("document.pdf?version=2", urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_fileWithAnchor() {
        // 测试带锚点的文件名
        String url = "http://www.example.com/docs/manual.html#chapter3";
        assertEquals("manual.html#chapter3", urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_noExtension() {
        // 测试没有扩展名的文件
        String url = "http://www.example.com/api/users";
        assertEquals("users", urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_rootPath() {
        // 测试根路径（没有文件名）
        String url = "http://www.example.com/";
        assertNull(urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_directoryPath() {
        // 测试目录路径（以/结尾）
        String url = "http://www.example.com/path/to/directory/";
        assertNull(urlUtils.getFileName(url));
    }

    @Test
    public void testGetFileName_nullUrl() {
        // 测试null输入
        assertNull(urlUtils.getFileName(null));
    }

    @Test
    public void testGetFileName_emptyUrl() {
        // 测试空字符串
        assertNull(urlUtils.getFileName(""));
    }

    @Test
    public void testGetFileName_noPath() {
        // 测试没有路径的URL
        String url = "http://www.example.com";
        assertNull(urlUtils.getFileName(url));
    }

    // ==================== getQueryString 方法测试 ====================

    @Test
    public void testGetQueryString_singleParam() {
        // 测试单个参数
        String url = "http://www.example.com/search?q=test";
        assertEquals("q=test", urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_multipleParams() {
        // 测试多个参数
        String url = "http://www.example.com/search?q=test&page=1&size=10";
        assertEquals("q=test&page=1&size=10", urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_withAnchor() {
        // 测试带锚点的查询参数
        String url = "http://www.example.com/page?param=value#section";
        assertEquals("param=value#section", urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_emptyQuery() {
        // 测试空查询（只有问号）
        String url = "http://www.example.com/page?";
        assertNull(urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_noQuery() {
        // 测试没有查询参数
        String url = "http://www.example.com/page.html";
        assertNull(urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_encodedParams() {
        // 测试URL编码的参数
        String url = "http://www.example.com/search?q=hello%20world&lang=zh-CN";
        assertEquals("q=hello%20world&lang=zh-CN", urlUtils.getQueryString(url));
    }

    @Test
    public void testGetQueryString_nullUrl() {
        // 测试null输入
        assertNull(urlUtils.getQueryString(null));
    }

    @Test
    public void testGetQueryString_emptyUrl() {
        // 测试空字符串
        assertNull(urlUtils.getQueryString(""));
    }

    @Test
    public void testGetQueryString_multipleQuestionMarks() {
        // 测试多个问号的情况
        String url = "http://www.example.com/page?param1=value1?param2=value2";
        assertEquals("param1=value1?param2=value2", urlUtils.getQueryString(url));
    }

    // ==================== 综合测试 ====================

    @Test
    public void testComplexUrl() {
        // 测试复杂的URL，包含所有部分
        String url = "https://subdomain.example.com:8443/path/to/resource/file.json?param1=value1&param2=value2#section";

        assertEquals("https", urlUtils.getProtocol(url));
        assertEquals("subdomain.example.com:8443", urlUtils.getDomain(url));
        assertEquals("/path/to/resource/file.json?param1=value1&param2=value2#section", urlUtils.getPath(url));
        assertEquals("file.json?param1=value1&param2=value2#section", urlUtils.getFileName(url));
        assertEquals("param1=value1&param2=value2#section", urlUtils.getQueryString(url));
    }

    @Test
    public void testSpecialCharactersInUrl() {
        // 测试URL中的特殊字符
        String url = "http://example.com/path/文件.html?参数=值";

        assertEquals("http", urlUtils.getProtocol(url));
        assertEquals("example.com", urlUtils.getDomain(url));
        assertEquals("/path/文件.html?参数=值", urlUtils.getPath(url));
        assertEquals("文件.html?参数=值", urlUtils.getFileName(url));
        assertEquals("参数=值", urlUtils.getQueryString(url));
    }
}
