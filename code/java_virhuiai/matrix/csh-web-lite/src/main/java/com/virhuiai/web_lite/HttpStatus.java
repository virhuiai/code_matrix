package com.virhuiai.web_lite;

// java.net.HttpURLConnection.HTTP_OK 和 sun.net.httpserver.Code
public enum HttpStatus {
    CONTINUE_100(100, "Continue", "继续"),
    OK_200(200, "OK", "成功"),
    CREATED_201(201, "Created", "已创建"),
    ACCEPTED_202(202, "Accepted", "已接受"),
    NOT_AUTHORITATIVE_203(203, "Non-Authoritative Information", "非权威信息"),
    NO_CONTENT_204(204, "No Content", "无内容"),
    RESET_CONTENT_205(205, "Reset Content", "重置内容"),
    PARTIAL_CONTENT_206(206, "Partial Content", "部分内容"),
    MULTIPLE_CHOICES_300(300, "Multiple Choices", "多重选择"),
    MOVED_PERMANENTLY_301(301, "Moved Permanently", "永久重定向"),
    MOVED_TEMPORARILY_302(302, "Found", "临时重定向"), // 注意：HTTP状态码302的标准描述是"Found"
    SEE_OTHER_303(303, "See Other", "查看其它"),
    NOT_MODIFIED_304(304, "Not Modified", "未修改"),
    USE_PROXY_305(305, "Use Proxy", "使用代理"),
    BAD_REQUEST_400(400, "Bad Request", "错误请求"),
    UNAUTHORIZED_401(401, "Unauthorized", "未授权"),
    PAYMENT_REQUIRED_402(402, "Payment Required", "需要付费"),
    FORBIDDEN_403(403, "Forbidden", "禁止访问"),
    NOT_FOUND_404(404, "Not Found", "未找到"),
    BAD_METHOD_405(405, "Method Not Allowed", "方法不允许"),
    NOT_ACCEPTABLE_406(406, "Not Acceptable", "不可接受"),
    PROXY_AUTHENTICATION_REQUIRED_407(407, "Proxy Authentication Required", "代理服务器认证"),
    CLIENT_TIMEOUT_408(408, "Request Timeout", "客户端超时"),
    CONFLICT_409(409, "Conflict", "冲突"),
    GONE_410(410, "Gone", "已移除"),
    LENGTH_REQUIRED_411(411, "Length Required", "需要长度"),
    PRECONDITION_FAILED_412(412, "Precondition Failed", "前提条件失败"),
    ENTITY_TOO_LARGE_413(413, "Request Entity Too Large", "请求实体过大"),
    REQ_TOO_LONG_414(414, "Request-URI Too Long", "请求URI过长"),
    UNSUPPORTED_TYPE_415(415, "Unsupported Media Type", "不支持的媒体类型"),
    INTERNAL_ERROR_500(500, "Internal Server Error", "内部服务器错误"),
    NOT_IMPLEMENTED_501(501, "Not Implemented", "未实现"),
    BAD_GATEWAY_502(502, "Bad Gateway", "网关错误"),
    UNAVAILABLE_503(503, "Service Unavailable", "服务不可用"),
    GATEWAY_TIMEOUT_504(504, "Gateway Timeout", "网关超时"),
    VERSION_NOT_SUPPORTED_505(505, "HTTP Version Not Supported", "不支持的HTTP版本");

    private final int code;
    private final String englishMessage;
    private final String chineseMessage;

    HttpStatus(int code, String englishMessage, String chineseMessage) {
        this.code = code;
        this.englishMessage = englishMessage;
        this.chineseMessage = chineseMessage;
    }

    public int getCode() {
        return code;
    }

    public String getEnglishMessage() {
        return englishMessage;
    }

    public String getChineseMessage() {
        return chineseMessage;
    }

    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching code found: " + code);
    }

    public static HttpStatus fromEnglishMessage(String message) {
        for (HttpStatus status : values()) {
            if (status.getEnglishMessage().equalsIgnoreCase(message)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching message found: " + message);
    }

    @Override
    public String toString() {
        return String.format("%s (%d) - English: %s, 中文: %s", name(), getCode(), getEnglishMessage(), getChineseMessage());
    }
}