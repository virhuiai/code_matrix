package com.virhuiai.CshPlaywright;

import com.microsoft.playwright.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 登录状态管理类
 */
public class LoginStateManager {
    private final String stateFilePath;
    private final int maxStateAgeHours;

    public LoginStateManager(String stateFilePath, int maxStateAgeHours) {
        this.stateFilePath = stateFilePath;
        this.maxStateAgeHours = maxStateAgeHours;

        // 确保状态文件存在
        ensureStateFileExists();
    }

    /**
     * 确保状态文件存在，如果不存在则创建空文件
     */
    private void ensureStateFileExists() {
        Path statePath = Paths.get(stateFilePath);

        try {
            // 如果文件不存在，创建空文件
            if (!Files.exists(statePath)) {
                // 确保父目录存在
                Path parentDir = statePath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }

                // 创建空文件
                Files.createFile(statePath);
                System.out.println("已创建状态文件: " + stateFilePath);
            }
        } catch (IOException e) {
            System.err.println("创建状态文件失败: " + e.getMessage());
            // 可以选择抛出运行时异常或继续执行
            throw new RuntimeException("无法创建状态文件: " + stateFilePath, e);
        }
    }

    /**
     * 检查保存的状态是否仍然有效
     */
    public boolean isStateValid() {
        Path statePath = Paths.get(stateFilePath);

        if (!Files.exists(statePath)) {
            return false;
        }

        try {
            // 检查文件是否为空（空文件表示没有保存的状态）
            if (Files.size(statePath) == 0) {
                return false;
            }

            // 检查文件修改时间
            long lastModified = Files.getLastModifiedTime(statePath).toMillis();
            long currentTime = System.currentTimeMillis();
            long ageInHours = (currentTime - lastModified) / (1000 * 60 * 60);

            return ageInHours < maxStateAgeHours;
        } catch (Exception e) {
            System.err.println("检查状态文件时发生错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 创建带有已保存状态的浏览器上下文
     */
    public BrowserContext createContextWithState(Browser browser) {
        if (isStateValid()) {
            try {
                return browser.newContext(new Browser.NewContextOptions()
                    .setStorageStatePath(Paths.get(stateFilePath)));
            } catch (Exception e) {
                System.out.println("加载保存的状态失败: " + e.getMessage());
            }
        }
        return browser.newContext();
    }

    /**
     * 保存当前浏览器上下文的状态
     */
    public void saveState(BrowserContext context) {
        try {
            context.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(Paths.get(stateFilePath)));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("状态已保存 [" + timestamp + "]: " + stateFilePath);
        } catch (Exception e) {
            System.err.println("保存状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除保存的状态文件
     */
    public void clearState() {
        try {
            Path statePath = Paths.get(stateFilePath);
            if (Files.exists(statePath)) {
                Files.delete(statePath);
                System.out.println("已清除保存的状态");

                // 重新创建空文件
                ensureStateFileExists();
            }
        } catch (Exception e) {
            System.err.println("清除状态失败: " + e.getMessage());
        }
    }

    /**
     * 检查状态文件是否存在且不为空
     */
    public boolean hasValidStateFile() {
        Path statePath = Paths.get(stateFilePath);
        try {
            return Files.exists(statePath) && Files.size(statePath) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取状态文件的信息
     */
    public String getStateFileInfo() {
        Path statePath = Paths.get(stateFilePath);

        if (!Files.exists(statePath)) {
            return "状态文件不存在: " + stateFilePath;
        }

        try {
            long size = Files.size(statePath);
            String lastModified = Files.getLastModifiedTime(statePath)
                .toString().substring(0, 19).replace("T", " ");

            return String.format("状态文件: %s, 大小: %d bytes, 最后修改: %s",
                stateFilePath, size, lastModified);
        } catch (Exception e) {
            return "无法获取状态文件信息: " + e.getMessage();
        }
    }
}