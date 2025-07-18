package com.virhuiai.log.jcef109.jcef;


import com.google.gson.Gson;
import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to download the native packages from GitHub or central repository.
 * Central repository is only used as fallback.
 * 用于从GitHub或中央仓库下载原生包的类。
 * 中央仓库仅作为备用选项。
 *
 * @author Fritz Windisch
 */
public class PackageDownloaderV {
    // Gson对象用于JSON解析
    private static final Gson GSON = new Gson();
    // 日志记录器
    private static final Logger LOGGER = Logger.getLogger(me.friwi.jcefmaven.impl.step.fetch.PackageDownloader.class.getName());

    // 缓冲区大小：16KB
    private static final int BUFFER_SIZE = 16 * 1024;

    /**
     * 下载原生包的主方法
     * @param info CEF构建信息
     * @param platform 目标平台
     * @param destination 目标文件
     * @param progressConsumer 进度回调消费者
     * @param mirrors 镜像源集合
     */
    public static void downloadNatives(CefBuildInfo info, EnumPlatform platform, File destination,
                                       Consumer<Float> progressConsumer, Set<String> mirrors) throws IOException {
        // 参数非空检查
        Objects.requireNonNull(info, "info cannot be null");
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(destination, "destination cannot be null");
        Objects.requireNonNull(progressConsumer, "progressConsumer cannot be null");
        Objects.requireNonNull(mirrors, "mirrors can not be null");
        if (mirrors.isEmpty()) {
            throw new RuntimeException("mirrors can not be empty");
        }

        // 创建目标文件
        if (!destination.createNewFile()) {
            throw new IOException("Could not create target file " + destination.getAbsolutePath());
        }
        // 加载Maven版本
        String mvn_version = loadJCefMavenVersion();

        // 尝试所有镜像源
        Exception lastException = null;
        for (String mirror : mirrors) {
            // 替换镜像URL中的变量
            String m = mirror
                    .replace("{platform}", platform.getIdentifier())
                    .replace("{tag}", info.getReleaseTag())
                    .replace("{mvn_version}", mvn_version);
            try {
                // 打开到镜像的连接
                URL url = new URL(m);
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                try (InputStream in = uc.getInputStream()) {
                    // 检查响应码
                    if (uc.getResponseCode() != 200) {
                        LOGGER.log(Level.WARNING, "Request to mirror failed with code " + uc.getResponseCode()
                                + " from server: " + m);
                        continue;
                    }
                    long length = uc.getContentLengthLong();
                    // 传输数据
                    try (FileOutputStream fos = new FileOutputStream(destination)) {
                        long progress = 0;
                        progressConsumer.accept(0f);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        long transferred = 0;
                        int r;
                        // 读取数据并写入文件
                        while ((r = in.read(buffer)) > 0) {
                            fos.write(buffer, 0, r);
                            transferred += r;
                            // 计算并更新下载进度
                            long newprogress = transferred * 100 / length;
                            if (newprogress > progress) {
                                progress = newprogress;
                                progressConsumer.accept((float) progress);
                            }
                        }
                        fos.flush();
                    }
                    // 清理连接
                    uc.disconnect();
                    return;
                } catch (IOException e) {
                    // 忽略错误，将在后续代码中尝试备用镜像
                    lastException = e;
                    LOGGER.log(Level.WARNING, "Request failed with exception on mirror: " + m
                            + " (" + e.getClass().getSimpleName()
                            + (e.getMessage() == null ? "" : (": " + e.getMessage())) + ")");
                    uc.disconnect();
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Request failed with exception on mirror: " + m, e);
                lastException = e;
            }
        }
        // 如果没有下载成功则抛出异常
        if (lastException != null) {
            throw new IOException("None of the supplied mirrors were working", lastException);
        } else {
            throw new IOException("None of the supplied mirrors were working");
        }
    }

    /**
     * 从配置文件加载JCef Maven版本
     */
    private static String loadJCefMavenVersion() throws IOException {
        Map object;
        try (InputStream in = me.friwi.jcefmaven.impl.step.fetch.PackageDownloader.class.getResourceAsStream("/jcefmaven_build_meta.json")) {
            if (in == null) {
                throw new IOException("/jcefmaven_build_meta.json not found on class path");
            }
            object = GSON.fromJson(new InputStreamReader(in), Map.class);
        } catch (Exception e) {
            throw new IOException("Invalid json content in jcefmaven_build_meta.json", e);
        }
        return (String) object.get("version");
    }

    /**
     * 构建下载URL
     * @param info
     * @param platform
     * @param mirrors
     * @return
     * @throws IOException
     */
    public static String buildDownloadUrl(CefBuildInfo info, EnumPlatform platform,
                                          Set<String> mirrors) throws IOException {
        // 参数非空检查
        Objects.requireNonNull(info, "info cannot be null");
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(mirrors, "mirrors can not be null");
        if (mirrors.isEmpty()) {
            throw new RuntimeException("mirrors can not be empty");
        }


        // 加载Maven版本
        String mvn_version = loadJCefMavenVersion();

        // 尝试所有镜像源

        for (String mirror : mirrors) {
            // 替换镜像URL中的变量  109.1.11
            String m = mirror
                    .replace("{platform}", platform.getIdentifier())
                    .replace("{tag}", info.getReleaseTag())
                    .replace("{mvn_version}", mvn_version);

            return m;
        }

        return null;
    }
}
