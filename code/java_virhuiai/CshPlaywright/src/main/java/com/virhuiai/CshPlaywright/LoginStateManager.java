package com.virhuiai.CshPlaywright;

// 导入 Playwright 浏览器相关类
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;

// 导入 Java IO 和文件操作相关类
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// 导入时间处理相关类
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 登录状态管理类
 * Login state management class for handling browser session persistence
 *
 * 该类负责管理浏览器登录状态的持久化存储，包括：
 * - 状态文件的创建和管理
 * - 登录状态的有效性验证
 * - 浏览器上下文的状态保存和恢复
 */
public class LoginStateManager {
    // 状态文件存储路径
    private final String stateFilePath;
    // 状态文件最大有效期（单位：小时）
    private final int maxStateAgeHours;

    /**
     * 构造函数 - 初始化登录状态管理器
     * Constructor - Initialize the login state manager
     *
     * @param stateFilePath 状态文件的存储路径
     * @param maxStateAgeHours 状态文件的最大有效期（小时）
     */
    public LoginStateManager(String stateFilePath, int maxStateAgeHours) {
        // 初始化状态文件路径
        this.stateFilePath = stateFilePath;
        // 初始化最大状态有效期
        this.maxStateAgeHours = maxStateAgeHours;

        // 确保状态文件存在
        // TODO: 可以考虑添加配置验证，确保路径格式正确
        ensureStateFileExists();
    }

    /**
     * 确保状态文件存在，如果不存在则创建空文件
     * Ensure state file exists, create empty file if it doesn't exist
     *
     * 该方法会检查状态文件是否存在，如果不存在则：
     * 1. 创建必要的父目录
     * 2. 创建空的状态文件
     */
    private void ensureStateFileExists() {
        // 获取状态文件的路径对象
        Path statePath = Paths.get(stateFilePath);

        try {
            // 如果文件不存在，创建空文件
            if (!Files.exists(statePath)) {
                // 确保父目录存在
                Path parentDir = statePath.getParent();
                // 检查父目录是否为null且不存在
                if (parentDir != null && !Files.exists(parentDir)) {
                    // 递归创建所有必要的父目录
                    Files.createDirectories(parentDir);
                }

                // 创建空文件
                Files.createFile(statePath);
                // 输出创建成功的消息
                System.out.println("已创建状态文件: " + stateFilePath);
            }
        } catch (IOException e) {
            // 打印错误信息到标准错误输出
            System.err.println("创建状态文件失败: " + e.getMessage());
            // 可以选择抛出运行时异常或继续执行
            // TODO: 可以考虑添加重试机制或备用路径
            throw new RuntimeException("无法创建状态文件: " + stateFilePath, e);
        }
    }

    /**
     * 检查保存的状态是否仍然有效
     * Check if the saved state is still valid
     *
     * 验证逻辑包括：
     * 1. 检查文件是否存在
     * 2. 检查文件是否为空
     * 3. 检查文件是否在有效期内
     *
     * @return true 如果状态有效，false 如果状态无效或过期
     */
    public boolean isStateValid() {
        // 获取状态文件路径
        Path statePath = Paths.get(stateFilePath);

        // 如果文件不存在，直接返回无效
        if (!Files.exists(statePath)) {
            return false;
        }

        try {
            // 检查文件是否为空（空文件表示没有保存的状态）
            if (Files.size(statePath) == 0) {
                return false;
            }

            // 检查文件修改时间
            // 获取文件最后修改时间（毫秒）
            long lastModified = Files.getLastModifiedTime(statePath).toMillis();
            // 获取当前时间（毫秒）
            long currentTime = System.currentTimeMillis();
            // 计算文件年龄（小时）
            long ageInHours = (currentTime - lastModified) / (1000 * 60 * 60);

            // 返回文件是否在有效期内
            // TODO: 可以考虑添加更灵活的时间验证策略，比如分钟级别的精度
            return ageInHours < maxStateAgeHours;
        } catch (Exception e) {
            // 发生异常时打印错误信息
            System.err.println("检查状态文件时发生错误: " + e.getMessage());
            // 异常情况下返回无效
            return false;
        }
    }

    /**
     * 创建带有已保存状态的浏览器上下文
     * Create browser context with saved state
     *
     * 该方法会：
     * 1. 检查是否有有效的保存状态
     * 2. 如果有效，使用保存的状态创建上下文
     * 3. 如果无效，创建新的空白上下文
     *
     * @param browser 浏览器实例
     * @return 浏览器上下文对象（可能包含或不包含保存的状态）
     */
    public BrowserContext createContextWithState(Browser browser) {
        // 检查状态是否有效
        if (isStateValid()) {
            try {
                // 使用保存的状态创建新的浏览器上下文
                return browser.newContext(new Browser.NewContextOptions()
                    .setStorageStatePath(Paths.get(stateFilePath)));
            } catch (Exception e) {
                // 加载状态失败时输出警告信息
                System.out.println("加载保存的状态失败: " + e.getMessage());
                // TODO: 可以考虑添加状态文件损坏时的自动修复机制
            }
        }
        // 状态无效或加载失败时，创建新的空白上下文
        return browser.newContext();
    }

    /**
     * 保存当前浏览器上下文的状态
     * Save current browser context state
     *
     * 该方法将当前浏览器上下文的状态（包括cookies、localStorage等）
     * 保存到指定的状态文件中
     *
     * @param context 要保存状态的浏览器上下文
     */
    public void saveState(BrowserContext context) {
        try {
            // 将浏览器上下文的存储状态保存到文件
            context.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(Paths.get(stateFilePath)));

            // 生成当前时间戳
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // 输出保存成功的消息，包含时间戳
            System.out.println("状态已保存 [" + timestamp + "]: " + stateFilePath);
        } catch (Exception e) {
            // 保存失败时输出错误信息
            System.err.println("保存状态失败: " + e.getMessage());
            // TODO: 可以考虑添加备份保存机制，避免状态丢失
        }
    }

    /**
     * 删除保存的状态文件
     * Clear saved state file
     *
     * 该方法会：
     * 1. 删除现有的状态文件
     * 2. 重新创建一个空的状态文件
     */
    public void clearState() {
        try {
            // 获取状态文件路径
            Path statePath = Paths.get(stateFilePath);
            // 检查文件是否存在
            if (Files.exists(statePath)) {
                // 删除状态文件
                Files.delete(statePath);
                // 输出清除成功的消息
                System.out.println("已清除保存的状态");

                // 重新创建空文件
                // TODO: 可以考虑添加选项来决定是否重新创建空文件
                ensureStateFileExists();
            }
        } catch (Exception e) {
            // 清除失败时输出错误信息
            System.err.println("清除状态失败: " + e.getMessage());
            // TODO: 可以考虑强制删除或提供手动清理指导
        }
    }

    /**
     * 检查状态文件是否存在且不为空
     * Check if state file exists and is not empty
     *
     * 该方法用于快速检查是否有可用的状态数据
     *
     * @return true 如果状态文件存在且包含数据，false 否则
     */
    public boolean hasValidStateFile() {
        // 获取状态文件路径
        Path statePath = Paths.get(stateFilePath);
        try {
            // 检查文件是否存在且大小大于0
            return Files.exists(statePath) && Files.size(statePath) > 0;
        } catch (Exception e) {
            // 发生异常时返回false
            // TODO: 可以考虑记录具体的异常信息用于调试
            return false;
        }
    }

    /**
     * 获取状态文件的信息
     * Get state file information
     *
     * 该方法返回状态文件的详细信息，包括：
     * - 文件路径
     * - 文件大小
     * - 最后修改时间
     *
     * @return 包含状态文件详细信息的字符串
     */
    public String getStateFileInfo() {
        // 获取状态文件路径
        Path statePath = Paths.get(stateFilePath);

        // 检查文件是否存在
        if (!Files.exists(statePath)) {
            return "状态文件不存在: " + stateFilePath;
        }

        try {
            // 获取文件大小（字节）
            long size = Files.size(statePath);
            // 获取并格式化最后修改时间
            String lastModified = Files.getLastModifiedTime(statePath)
                .toString().substring(0, 19).replace("T", " ");

            // 返回格式化的文件信息
            // TODO: 可以考虑添加更多信息，如文件权限、创建时间等
            return String.format("状态文件: %s, 大小: %d bytes, 最后修改: %s",
                stateFilePath, size, lastModified);
        } catch (Exception e) {
            // 获取信息失败时返回错误信息
            return "无法获取状态文件信息: " + e.getMessage();
        }
    }
}