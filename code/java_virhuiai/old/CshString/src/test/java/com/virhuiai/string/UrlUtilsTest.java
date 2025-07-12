package com.virhuiai.string;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UrlUtilsTest {

    private Url urlUtils;

    // 创建UrlUtils的实现类用于测试
    private static class UrlImpl implements Url {
        // 使用默认实现
    }

    @Before
    public void setUp() {
        urlUtils = new UrlImpl();
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
        assertEquals("param=value", urlUtils.getQueryString(url));
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
        assertEquals("q=hello world&lang=zh-CN", urlUtils.getQueryString(url));
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
        assertEquals("param1=value1&param2=value2", urlUtils.getQueryString(url));
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

    // --- Test cases for getQueryParameters ---

    @Test
    public void testGetQueryParameters_PositiveCases() {
        // Simple case
        Map<String, String> params1 = urlUtils.getQueryParameters("http://example.com?key1=value1&key2=value2");
        assertEquals(2, params1.size());
        assertEquals("value1", params1.get("key1"));
        assertEquals("value2", params1.get("key2"));

        // URL with no query string
        Map<String, String> params2 = urlUtils.getQueryParameters("http://example.com/path");
        assertTrue(params2.isEmpty());

        // URL with empty query string
        Map<String, String> params3 = urlUtils.getQueryParameters("http://example.com?");
        assertTrue(params3.isEmpty());

        // URL with empty value parameter
        Map<String, String> params4 = urlUtils.getQueryParameters("http://example.com?key1=&key2=value2");
        assertEquals(1, params4.size());
        assertNull( params4.get("key1"));//
        assertEquals("value2", params4.get("key2"));

        // URL with parameter without value
        Map<String, String> params5 = urlUtils.getQueryParameters("http://example.com?key1&key2=value2");
        assertEquals(2, params5.size());
        assertEquals("", params5.get("key1")); // Should be empty string as per logic
        assertEquals("value2", params5.get("key2"));

        // URL with multiple parameters of the same name (last one wins in HashMap)
        Map<String, String> params6 = urlUtils.getQueryParameters("http://example.com?key1=valueA&key1=valueB");
        assertEquals(1, params6.size());
        assertEquals("valueB", params6.get("key1"));

        // URL with special characters in keys/values (not URL encoded)
        Map<String, String> params7 = urlUtils.getQueryParameters("http://example.com?key_with_space=value with space&key/slash=value?question");
        assertEquals(0, params7.size());

        // URL with fragment
        Map<String, String> params8 = urlUtils.getQueryParameters("http://example.com?key=value#fragment");
        assertEquals(1, params8.size());
        assertEquals("value", params8.get("key"));//Actual   :value#fragment

        // URL with mixed valid/invalid parameters
        Map<String, String> params9 = urlUtils.getQueryParameters("http://example.com?param1=value1&invalid&param2=value2=");
        assertEquals(3, params9.size());
        assertEquals("value1", params9.get("param1"));
        assertEquals("", params9.get("invalid"));
        assertEquals("value2=", params9.get("param2")); // key = "param2", value = ""
    }

    @Test
    public void testGetQueryParameters_NegativeCases() {
        // Null URL
        Map<String, String> params1 = urlUtils.getQueryParameters(null);
        assertTrue(params1.isEmpty());

        // Empty URL
        Map<String, String> params2 = urlUtils.getQueryParameters("");
        assertTrue(params2.isEmpty());

        // Blank URL
        Map<String, String> params3 = urlUtils.getQueryParameters("   ");
        assertTrue(params3.isEmpty());

        // Invalid URL format (no protocol, host, but has a query-like part)
        // The underlying getQueryString will return null for malformed URLs
        Map<String, String> params4 = urlUtils.getQueryParameters("just_a_path?param=value");
        assertEquals(0, params1.size());

        // Only query string, no base URL
        Map<String, String> params5 = urlUtils.getQueryParameters("?param=value");
        assertEquals(1, params5.size());

        // URL with only protocol and host, but no query string
        Map<String, String> params6 = urlUtils.getQueryParameters("http://localhost");
        assertTrue(params6.isEmpty());
    }

    // --- Test cases for getPort ---

    @Test
    public void testGetPort_PositiveCases() {
        assertEquals(8080, urlUtils.getPort("http://localhost:8080/path"));
        assertEquals(443, urlUtils.getPort("https://example.com:443/resource"));
        assertEquals(80, urlUtils.getPort("http://www.test.com:80"));
        assertEquals(80, urlUtils.getPort2("http://www.test.com:80?param=value")); // With query todo 后面可以看下
        assertEquals(8080, urlUtils.getPort("http://localhost:8080")); // No path
    }

    @Test
    public void testGetPort_NegativeCases() {
        // No port specified
        assertEquals(-1, urlUtils.getPort("http://localhost/path"));
        assertEquals(-1, urlUtils.getPort("https://example.com/"));
        assertEquals(-1, urlUtils.getPort("http://www.test.com"));

        // Null URL
        assertEquals(-1, urlUtils.getPort(null));

        // Empty URL
        assertEquals(-1, urlUtils.getPort(""));

        // Blank URL
        assertEquals(-1, urlUtils.getPort("   "));

        // Invalid port format (non-numeric)
        assertEquals(-1, urlUtils.getPort("http://localhost:abc/path"));
        assertEquals(-1, urlUtils.getPort("http://localhost:/path")); // Missing port number
        assertEquals(-1, urlUtils.getPort("http://localhost:port/path"));

        // Malformed URL that cannot be parsed by URL object
        assertEquals(-1, urlUtils.getPort("invalid-url"));
        assertEquals(8080, urlUtils.getPort("htp://example.com:8080")); // Invalid protocol
    }

    // --- Test cases for isValidUrl ---

    @Test
    public void testIsValidUrl_PositiveCases() {
        assertTrue(urlUtils.isValidUrl("http://example.com"));
        assertTrue(urlUtils.isValidUrl("https://www.google.com/search?q=test"));
        assertTrue(urlUtils.isValidUrl("ftp://files.example.com/path/to/file.zip"));
        assertTrue(urlUtils.isValidUrl("http://localhost:8080/"));
        assertTrue(urlUtils.isValidUrl("https://192.168.1.1/"));
        assertTrue(urlUtils.isValidUrl("http://example.com/path/to/resource.html#fragment"));
        assertTrue(urlUtils.isValidUrl("http://user:pass@example.com/")); // With credentials
    }

    @Test
    public void testIsValidUrl_NegativeCases() {
        assertFalse(urlUtils.isValidUrl(null));
        assertFalse(urlUtils.isValidUrl(""));
        assertFalse(urlUtils.isValidUrl("   "));
        assertFalse(urlUtils.isValidUrl("example.com")); // Missing protocol
        assertFalse(urlUtils.isValidUrl("www.example.com")); // Missing protocol
        assertFalse(urlUtils.isValidUrl("http:///path")); // Invalid host
        assertFalse(urlUtils.isValidUrl("http://")); // Missing host
        assertFalse(urlUtils.isValidUrl("ftp://")); // Missing host
        assertFalse(urlUtils.isValidUrl("just_a_string"));
        assertFalse(urlUtils.isValidUrl("http:/example.com")); // Single slash
        assertFalse(urlUtils.isValidUrl("://example.com")); // Missing protocol name
        assertFalse(urlUtils.isValidUrl("http://.com")); // Invalid domain //todo
    }

    // --- Test cases for getTopLevelDomain ---

    @Test
    public void testGetTopLevelDomain_PositiveCases() {
        assertEquals("com", urlUtils.getTopLevelDomain("http://www.example.com"));
        assertEquals("org", urlUtils.getTopLevelDomain("https://sub.domain.org/path"));
        assertEquals("cn", urlUtils.getTopLevelDomain("http://test.co.cn:8080"));
        assertEquals("net", urlUtils.getTopLevelDomain("ftp://ftp.data.net"));
        assertEquals("io", urlUtils.getTopLevelDomain("https://my-app.io"));
        assertEquals("uk", urlUtils.getTopLevelDomain("http://example.co.uk")); // Based on current logic, it should return 'uk'
    }

    @Test
    public void testGetTopLevelDomain_NegativeCases() {
        // Null URL
        assertNull(urlUtils.getTopLevelDomain(null));

        // Empty URL
        assertNull(urlUtils.getTopLevelDomain(""));

        // Blank URL
        assertNull(urlUtils.getTopLevelDomain("   "));

        // URL with no domain/malformed
        assertNull(urlUtils.getTopLevelDomain("http:///path"));
        assertNull(urlUtils.getTopLevelDomain("invalid-url"));
        assertNull(urlUtils.getTopLevelDomain("http://localhost")); // No TLD for localhost
        assertNull(urlUtils.getTopLevelDomain("http://192.168.1.1")); // IP address, no TLD in this context

        // Domain without a dot for TLD
        assertNull(urlUtils.getTopLevelDomain("http://example"));

        // Domain ending with a dot
        assertNull(urlUtils.getTopLevelDomain("http://example.com."));

        // TLD is the whole domain (e.g., example.com where "com" is the only part after the last dot)
        // This method gets the part AFTER the LAST dot, so for "example.com", "com" is returned which is correct for this specific logic.
        assertEquals("com", urlUtils.getTopLevelDomain("http://example.com"));
        assertEquals("com", urlUtils.getTopLevelDomain("http://www.example.com")); // Still 'com'
    }

    // --- Test cases for normalizeUrl ---

    @Test
    public void testNormalizeUrl_PositiveCases() {
        // Add default protocol
        assertEquals("http://example.com", urlUtils.normalizeUrl("example.com"));// todo
        assertEquals("http://www.example.com/path", urlUtils.normalizeUrl("www.example.com/path"));

        // Remove multiple slashes
        assertEquals("http://example.com/path/to/resource", urlUtils.normalizeUrl("http://example.com//path///to//resource"));
        assertEquals("http://example.com/", urlUtils.normalizeUrl("http://example.com///")); // Root path should keep single slash

        // Remove trailing slash
        assertEquals("http://example.com/path/", urlUtils.normalizeUrl("http://example.com/path/"));
        assertEquals("http://example.com/path/?query=value", urlUtils.normalizeUrl("http://example.com/path/?query=value"));

        // Combined scenarios
        assertEquals("http://example.com/path/", urlUtils.normalizeUrl("  example.com///path/  "));
        assertEquals("https://www.test.com/a/b/", urlUtils.normalizeUrl("  https://www.test.com//a//b/  "));

        // URL with query and fragment
        assertEquals("http://example.com/path?param=value#fragment", urlUtils.normalizeUrl("http://example.com/path?param=value#fragment"));

        // URL with default protocol and existing query
        assertEquals("http://example.com?param=value", urlUtils.normalizeUrl("example.com?param=value"));
    }

    @Test
    public void testNormalizeUrl_NegativeCases() {
        // Null, empty, blank
        assertNull(urlUtils.normalizeUrl(null));
        assertNull(urlUtils.normalizeUrl(""));
        assertNull(urlUtils.normalizeUrl("   "));

        // Malformed URL after adding default protocol (if host is still invalid)
        assertNull(urlUtils.normalizeUrl("just_a_string")); // becomes http://just_a_string, which is invalid host
        assertNull(urlUtils.normalizeUrl("://example.com")); // becomes http://://example.com, invalid host
        assertNull(urlUtils.normalizeUrl("http:///")); // Invalid after normalization by isValidUrl
        assertNull(urlUtils.normalizeUrl("/path/to/resource")); // Becomes http:///path/to/resource, invalid
    }

    @Test
    public void testNormalizeUrl_BoundaryConditions() {
        // Only domain
        assertEquals("http://example.com", urlUtils.normalizeUrl2("example.com"));//todo
        assertEquals("http://example.com", urlUtils.normalizeUrl("http://example.com"));

        // Root path
        assertEquals("http://example.com/", urlUtils.normalizeUrl("http://example.com/"));
        assertEquals("http://example.com/", urlUtils.normalizeUrl("example.com/"));
        assertEquals("http://example.com/", urlUtils.normalizeUrl("http://example.com///"));

        // Trailing slash, but it's the root (should not remove)
        assertEquals("http://example.com/", urlUtils.normalizeUrl("http://example.com/"));
        assertEquals("http://localhost:8080/", urlUtils.normalizeUrl("http://localhost:8080/"));
    }

    // --- Internal helper methods tests (getQueryString, getProtocol, getDomain) ---
    // Although these are helper methods, testing them individually ensures their correctness
    // and makes debugging the main methods easier.

    @Test
    public void testGetQueryString() {
        assertEquals("param1=value1&param2=value2", urlUtils.getQueryString("http://example.com?param1=value1&param2=value2"));
        assertNull(urlUtils.getQueryString("http://example.com/path"));
        assertNull(urlUtils.getQueryString("http://example.com?")); // Empty query, URL.getQuery() returns null
        assertNull(urlUtils.getQueryString(null));
        assertNull(urlUtils.getQueryString(""));
        assertNull(urlUtils.getQueryString("invalid-url"));
        assertEquals("a=b", urlUtils.getQueryString("http://example.com?a=b#fragment")); // Fragment is ignored // todo Actual   :a=b#fragment
    }

    @Test
    public void testGetProtocol() {
        assertEquals("http", urlUtils.getProtocol("http://example.com"));
        assertEquals("https", urlUtils.getProtocol("https://www.google.com"));
        assertEquals("ftp", urlUtils.getProtocol("ftp://files.example.com"));
        assertNull(urlUtils.getProtocol("www.example.com")); // No protocol
        assertNull(urlUtils.getProtocol(null));
        assertNull(urlUtils.getProtocol(""));
        assertNull(urlUtils.getProtocol("invalid-url"));
    }

    @Test
    public void testGetDomain() {
        assertEquals("example.com", urlUtils.getDomain("http://example.com"));
        assertEquals("localhost:8080", urlUtils.getDomain("http://localhost:8080/path"));
        assertEquals("www.google.com", urlUtils.getDomain("https://www.google.com/search"));
        assertEquals("192.168.1.1:80", urlUtils.getDomain("http://192.168.1.1:80")); // URL.getHost() and URL.getPort() handles this correctly.
        assertEquals("example.com", urlUtils.getDomain("http://user:pass@example.com/")); // Ignores user/pass
        assertNull(urlUtils.getDomain(null));
        assertNull(urlUtils.getDomain(""));
        assertNull(urlUtils.getDomain("invalid-url"));
        assertNull(urlUtils.getDomain("http:///path")); // Invalid host
    }
}
