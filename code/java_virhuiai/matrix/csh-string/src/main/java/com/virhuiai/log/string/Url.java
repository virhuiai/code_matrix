package com.virhuiai.log.string;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 包含 URL 解析和提取相关的工具方法
 */
public interface Url {

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
        String trimmed = url.trim();
//        if (!trimmed.contains("://")) {
//            trimmed = "http://" + trimmed;
//        }

        try {
            URI uri = new URI(trimmed);
            // Normalize the path components:
            URI normalizedUri = uri.normalize();
            if (normalizedUri.getHost() != null) {
                if(normalizedUri.getPort() != -1){
                    return normalizedUri.getHost().toLowerCase() + ":" + normalizedUri.getPort();
                }else{
                    return normalizedUri.getHost().toLowerCase();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }

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
//    default String getQueryString(String url) {
//        if (Str.Utils.isBlank(url)) {
//            return null;
//        }
//        int pos = url.indexOf("?");
//        if (pos == -1 || pos == url.length() - 1) {
//            return null;
//        }
//        return url.substring(pos + 1);
//    }

    // Assuming this method exists or you need to implement it.
    // This is the core fix: correctly extracting the query string without the fragment.
    default String getQueryString(String urlString) {
        if(Str.Utils.isBlank(urlString)){
            return  null;
        }
        try {
            URI uri = new URI(urlString);
            String rs = uri.getQuery(); // URI.getQuery() correctly handles fragments
            if(Str.Utils.isBlank(rs)){
                return  null;
            }else {
                return rs;
            }
        } catch (URISyntaxException e) {
            // Handle malformed URL string, perhaps log an error or return null

            return null;
        }
    }

    ///  todo test
    /**
     * 解析 URL 中的查询参数为键值对
     * @param url URL 地址
     * @return 参数键值对 Map,如果 URL 为空或无参数,返回空 Map
     */
    default Map<String, String> getQueryParameters(String url) {
        Map<String, String> params = new HashMap<>();
        String query = getQueryString(url);
        if (query == null || query.isEmpty()) { // Added check for empty query string
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            // if (idx != -1) { // If an equals sign exists
            if (idx != -1 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                params.put(key, value);
            } else if (idx == -1 && !pair.isEmpty()) {
                params.put(pair, "");
            }
        }
        return params;
    }



    /**
     * 提取 URL 地址中的端口号
     * @param url URL 地址
     * @return 端口号,如果 URL 为空或无端口号,返回 -1
     */
    default int getPort(String url) {
        if (Str.Utils.isBlank(url)) {
            return -1;
        }
        String domain = getDomain(url);
        if (domain == null) {
            return -1;
        }
        int portStart = domain.indexOf(":");
        if (portStart == -1) {
            return -1;
        }
        int portEnd = domain.indexOf("/", portStart);
        String portStr = (portEnd == -1) ? domain.substring(portStart + 1) : domain.substring(portStart + 1, portEnd);
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    default int getPort2(String url) {
        if (Str.Utils.isBlank(url)) {
            return -1;
        }

        try {
            URL u = new URL(url);
            int port = u.getPort();
            if (port == -1) {
                // If the port is not explicitly specified, get the default port for the protocol
                return u.getDefaultPort();
            }
            return port;
        } catch (MalformedURLException e) {
            return -1; // Invalid URL
        }
    }



    /**
     * 检查 URL 是否有效（简单验证协议和域名）
     * @param url URL 地址
     * @return 是否有效
     */
    default boolean isValidUrl(String url) {
        if (Str.Utils.isBlank(url)) {
            return false;
        }
        String protocol = getProtocol(url);
        String domain = getDomain(url);
        return protocol != null && !protocol.isEmpty() && domain != null && !domain.isEmpty();
    }


    /**
     * 提取顶级域名（例如 example.com 中的 com）
     * @param url URL 地址
     * @return 顶级域名,如果 URL 为空或无法解析,返回 null
     */
    default String getTopLevelDomain(String url) {
        String domain = getDomain(url);
        if (domain == null) {
            return null;
        }
        // 移除端口号
        int portIndex = domain.indexOf(":");
        if (portIndex != -1) {
            domain = domain.substring(0, portIndex);
        }
        int lastDot = domain.lastIndexOf(".");
        if (lastDot == -1 || lastDot == domain.length() - 1) {
            return null;
        }
        String rs = domain.substring(lastDot + 1);
        if(Str.Utils.isNumeric(rs)){
            return null;
        }else{
            return rs;
        }
    }



    /**
     * 规范化 URL（去除多余斜杠、添加默认协议等）
     * @param url URL 地址
     * @return 规范化后的 URL,如果 URL 无效,返回 null
     */
    default String normalizeUrl(String url) {
        // Using StrUtil.isBlank from Hutool based on the original snippet
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        String trimmed = url.trim();

        // Add default protocol if missing.
        // This is a basic check; for more robust parsing, java.net.URL might be used,
        // but URI.parse() can also infer scheme from context or throw if it's truly malformed.
        // For simplicity, we'll stick to the original logic for adding "http://".
        if (!trimmed.contains("://")) {
            trimmed = "http://" + trimmed;
        }

        try {
            URI uri = new URI(trimmed);

            // Normalize the URI:
            // 1. Resolve relative paths (e.g., "a/../b" becomes "b").
            // 2. Remove redundant dot segments (".", "..").
            // 3. Lowercase the host (common practice for normalization).
            // 4. Potentially handle default ports (e.g., remove :80 for http).
            //    URI.normalize() does path normalization. Other normalizations need manual handling.

            // Build a new URI to ensure consistency.
            // This also handles removing empty path components or redundant slashes
            // to some extent when converting back to string.
            // Example: "http://example.com//path" might become "http://example.com/path"
            // depending on the URI implementation and its toString().
            // For explicit path normalization, you'd call uri.normalize().

            // Normalize the path components:
            URI normalizedUri = uri.normalize();

            // Additional common normalization: lowercase the host
            // Note: URI.normalize() doesn't lowercase the host.
            // We need to rebuild the URI with a lowercased host if desired.
            if (normalizedUri.getHost() != null) {
                normalizedUri = new URI(
                        normalizedUri.getScheme(),
                        normalizedUri.getUserInfo(),
                        normalizedUri.getHost().toLowerCase(), // Lowercase the host
                        normalizedUri.getPort(),
                        normalizedUri.getPath(),
                        normalizedUri.getQuery(),
                        normalizedUri.getFragment()
                );
            }

            // You might want to remove the default port (e.g., :80 for HTTP, :443 for HTTPS)
            // if that's part of your normalization definition.
            // This is more complex as it involves checking the scheme.
            // For now, let's keep it simple and just return the string.

            String rs = normalizedUri.toASCIIString(); // Use toASCIIString() for proper encoding
            if(!rs.contains(".") && !rs.contains("localhost")){
                return null;
            }else{
                return rs;
            }
        } catch (URISyntaxException e) {
            // Handle invalid URI format
            // e.g., log the error: System.err.println("Invalid URL syntax: " + url + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            // Catch any other unexpected exceptions during normalization
            // e.g., System.err.println("Error normalizing URL: " + url + " - " + e.getMessage());
            return null;
        }
    }

    default String normalizeUrl2(String url) {// todo 测试
        if (Str.Utils.isBlank(url)) {
            return null;
        }
        String trimmed = url.trim();
        // Add default protocol (if no protocol)
        if (!trimmed.contains("://")) {
            trimmed = "http://" + trimmed;
        }
        // Replace multiple slashes
        // This regex needs to be careful not to replace the // in http://
        // A better approach is to replace multiple slashes ONLY after the protocol part.
        // For simplicity for this specific fix, let's just make sure it doesn't affect http://
        trimmed = trimmed.replaceAll("(?<!:)//+", "/"); // Replaces // or more, but not if preceded by a colon (:)

        // Remove trailing slash (if not root path)
        // This condition `trimmed.indexOf("/", trimmed.indexOf("://") + 3) != trimmed.length() - 1`
        // tries to ensure we don't remove the slash for root paths like "http://example.com/"
        // and also correctly handles cases like "http://example.com/path/"
        if (trimmed.endsWith("/") && !trimmed.equals("http://") && !trimmed.equals("https://")) {
            int protocolEndIndex = trimmed.indexOf("://");
            if (protocolEndIndex != -1) {
                // Find the first slash after the host part
                int firstSlashAfterHost = trimmed.indexOf("/", protocolEndIndex + 3);
                if (firstSlashAfterHost != -1 && firstSlashAfterHost == trimmed.length() - 1) {
                    // It's a trailing slash after the host, and it's the only one
                    // e.g., "http://example.com/" should be kept as "http://example.com/" for root
                    // However, the test case expects "http://example.com"
                    // So, if it's just the host with a trailing slash, remove it.
                    // This part of the original logic seems a bit complex for just removing a trailing slash.
                    // Let's simplify for the test case.
                    // If it's "http://example.com/", and the intent is "http://example.com", then remove it.
                    // The original logic: trimmed.indexOf("/", trimmed.indexOf("://") + 3) != trimmed.length() - 1
                    // This means if the *only* slash after the protocol is the trailing one, don't remove it.
                    // Which seems counter-intuitive if "http://example.com" is the desired output.

                    // Let's adjust the trailing slash removal logic.
                    // Remove trailing slash unless it's just the protocol and host, e.g., "http://localhost/"
                    // If the URL is just "http://example.com/", we want "http://example.com"
                    // If the URL is "http://example.com/path/", we want "http://example.com/path"
                    // If the URL is "http://example.com", we want "http://example.com"
                    // So, if it ends with a slash and is not just "http://" or "https://", remove it.
                    // And also, if it's just "http://domain.com/", remove the slash.

                    // More robust trailing slash removal:
                    // If the URL ends with a slash and it's not simply the root path (like "http://")
                    // then remove it.
                    // The original condition was quite specific:
                    // `trimmed.indexOf("/", trimmed.indexOf("://") + 3) != trimmed.length() - 1`
                    // This means "if the slash is NOT the *first* slash after the host *and* it's at the end".
                    // This is complicated.
                    // A simpler rule for "http://example.com" vs "http://example.com/":
                    // If the URL ends with a slash AND it's not just "http://" or "https://", AND there's
                    // more than just the host part or it's a domain with a single trailing slash.

                    // Let's refine for the specific test case: "example.com" -> "http://example.com"
                    // The trailing slash removal part is the one that might be problematic for cases like "http://example.com/" -> "http://example.com"
                    // Your current code for trailing slash removal works as follows for "http://example.com/":
                    // trimmed.endsWith("/") is true.
                    // trimmed.indexOf("/", trimmed.indexOf("://") + 3) will be 18 (the slash after .com)
                    // trimmed.length() - 1 will be 18.
                    // So `18 != 18` is false. The condition is false, and the slash is NOT removed.
                    // This means "http://example.com/" would remain "http://example.com/".
                    // But the test case `assertEquals("http://example.com", urlUtils.normalizeUrl("example.com"))` doesn't test this.
                    // It seems your existing trailing slash logic might actually be preventing "http://example.com/" from becoming "http://example.com".
                    // However, the test input is "example.com", which becomes "http://example.com" and then the trailing slash logic is skipped.

                    // The most likely issue remains `isValidUrl`.

                    // Let's assume the trailing slash removal logic is intended to remove for paths but keep for root domains
                    // like http://example.com/ (if it was the only slash).
                    // This seems to be the current behavior based on `trimmed.indexOf("/", trimmed.indexOf("://") + 3) != trimmed.length() - 1`
                    // This condition means: "if there's a slash at the end AND that slash is NOT the *first* slash after the host".
                    // So, "http://example.com/" -> the first slash after host is the last char, so the condition is false. The slash is kept.
                    // "http://example.com/path/" -> the first slash after host is after "com", the last slash is after "path".
                    // The condition evaluates to true, and the last slash is removed.
                    // This means: "http://example.com/" remains "http://example.com/", while "http://example.com/path/" becomes "http://example.com/path".

                    // If the goal is strictly "http://example.com" for both "example.com" and "example.com/",
                    // then the trailing slash logic needs to be adjusted.
                    // Let's modify the trailing slash removal to handle the common case.
                }
            }
        }

        // Revised trailing slash removal:
        // If the URL ends with a slash and it's not just the protocol part (e.g., "http://")
        // and it's not just the domain root (e.g., "http://example.com/")
        // For the test case "example.com" -> "http://example.com" (no trailing slash),
        // this section should not cause an issue.
        if (trimmed.endsWith("/")) {
            // Check if it's more than just "http://" or "https://"
            int protocolEnd = trimmed.indexOf("://");
            if (protocolEnd != -1) {
                // If the string is just "http://domain.com/" and we want "http://domain.com"
                // The length check helps differentiate between http://example.com/ (remove)
                // and http://example.com/path/ (remove)
                // If it's only a single slash after the protocol and domain, and it's at the end, remove it.
                if (trimmed.length() > protocolEnd + 3 + 1) { // Checks if there's content after "http://"
                    String path = trimmed.substring(protocolEnd + 3); // Get the part after http://
                    if (path.indexOf('/') == path.length() - 1 && path.indexOf('/') != -1) {
                        // This means the only slash after the protocol and host is the trailing one.
                        // Example: "example.com/" -> path is "example.com/". The only slash is the last one.
                        // We want to remove this.
                        trimmed = trimmed.substring(0, trimmed.length() - 1);
                    } else if (path.indexOf('/') != path.length() - 1 && path.indexOf('/') != -1) {
                        // This means there are other slashes before the trailing one, e.g., "example.com/path/"
                        trimmed = trimmed.substring(0, trimmed.length() - 1);
                    }
                }
            }
        }


        // Simplified trailing slash logic to achieve "http://example.com" for "example.com/"
        // If it ends with a slash and is not just "http://" or "https://" (which are root paths)
        // and is not "http://localhost/" (if you consider that a root path that should keep the slash)
        // A common normalization is to remove the trailing slash unless it's the root of the domain.
        // For "http://example.com", no slash to remove.
        // For "http://example.com/", it becomes "http://example.com".
        // For "http://example.com/path/", it becomes "http://example.com/path".

        // Let's refine the trailing slash removal for the desired behavior:
        // Remove trailing slash if it's not just the protocol and if there's no path component
        // or if there is a path component and it ends with a slash.

        String originalTrimmed = trimmed; // Store the current state
        if (trimmed.endsWith("/")) {
            int protocolEndIndex = trimmed.indexOf("://");
            if (protocolEndIndex != -1) {
                String domainAndPath = trimmed.substring(protocolEndIndex + 3); // e.g., "example.com/" or "localhost:8080/path/"
                // If there's only one slash and it's at the end, and it's the host's root
                // e.g., "example.com/" -> domainAndPath = "example.com/"
                // If there are other slashes, it's a path, e.g., "example.com/path/"
                if (domainAndPath.indexOf('/') == domainAndPath.length() - 1) { // The last char is a slash
                    // Check if it's just the root domain with a trailing slash
                    // e.g. "example.com/" -> slash at index 11.
                    // This is where "http://example.com/" should become "http://example.com"
                    // And "http://example.com/path/" should become "http://example.com/path"

                    // If the path part (after host) is empty before the slash, then it's a root domain.
                    // This means `trimmed` could be `http://example.com/`
                    // In this case, `domainAndPath` is `example.com/`
                    // We remove the trailing slash.
                    trimmed = trimmed.substring(0, trimmed.length() - 1);
                }
            }
        }


        return Str.Utils.isValidUrl(trimmed) ? trimmed : null;
    }
}