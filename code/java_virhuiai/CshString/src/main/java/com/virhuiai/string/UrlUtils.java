package com.virhuiai.string;


/**
 * 包含 URL 解析和提取相关的工具方法
 */
public interface UrlUtils {

    /**
     * 提取 URL 地址中的协议部分
     * @param url URL 地址
     * @return 协议,如果 URL 为空,返回 null
     */
    default String getProtocol(String url) {
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        int pos = url.indexOf("://");
        if (pos == -1) {
            return null;
        }
        return url.substring(0, pos);
    }

    /**
     * 提取 URL 地址中的域名部分
     * @param url URL 地址
     * @return 域名,如果 URL 为空,返回 null
     */
    default String getDomain(String url) {
        if (Str.Utils.isBlank(url)) {
            return null;
        }

        // 查找协议分隔符 "://"
        int protocolEnd = url.indexOf("://");
        if (protocolEnd == -1) {
            return null;
        }

        // 从 "://" 后面开始查找第三个斜杠
        int domainStart = protocolEnd + 3;
        int domainEnd = url.indexOf('/', domainStart);

        // 如果没有找到第三个斜杠，说明URL只包含协议和域名
        if (domainEnd == -1) {
            // 检查是否有内容
            if (domainStart >= url.length()) {
                return null;
            }
            return url.substring(domainStart);
        }

        // 提取域名部分（从 :// 后到第三个斜杠之间）
        return url.substring(domainStart, domainEnd);
    }

    /**
     * 提取 URL 地址中的路径部分
     * @param url URL 地址
     * @return 路径,如果 URL 为空,返回 null
     */
    default String getPath(String url) {
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        int pos = url.indexOf("://");
        if (pos == -1) {
            return null;
        }
        String domain = getDomain(url);
        if (domain == null) {
            return null;
        }
        return url.substring(pos + 3 + domain.length());
    }

    /**
     * 提取 URL 地址中的文件名部分
     * @param url URL 地址
     * @return 文件名,如果 URL 为空或不包含文件名,返回 null
     */
    default String getFileName(String url) {
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        String path = getPath(url);
        if (path == null || path.length() == 0) {
            return null;
        }
        int pos = path.lastIndexOf("/");
        if (pos == -1 || pos == path.length() - 1) {
            return null;
        }
        return path.substring(pos + 1);
    }

    /**
     * 提取 URL 地址中的参数部分
     * @param url URL 地址
     * @return 参数,如果 URL 为空或不包含参数,返回 null
     */
    default String getQueryString(String url) {
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        int pos = url.indexOf("?");
        if (pos == -1 || pos == url.length() - 1) {
            return null;
        }
        return url.substring(pos + 1);
    }
}