/**
 * Playwright2 接口文件
 * 该文件定义了 Playwright2 接口，用于扩展 Playwright 功能
 * 包路径：com.microsoft.playwright.impl
 *
 * @author Microsoft Playwright 团队
 * @version 2.0
 */
package com.microsoft.playwright.impl;

// 导入 Playwright 基础接口
import com.microsoft.playwright.Playwright;

/**
 * Playwright2 接口
 *
 * 该接口继承自 Playwright 接口，提供了创建 Playwright 实例的静态工厂方法。
 * 这是 Playwright 库的核心接口之一，用于自动化浏览器操作。
 *
 * 主要功能：
 * 1. 提供带参数的创建方法
 * 2. 提供无参数的便捷创建方法
 *
 * 使用场景：
 * - 自动化测试
 * - 网页爬虫
 * - 自动化任务执行
 */
public interface Playwright2 extends Playwright {

    /**
     * 创建 Playwright 实例（带配置选项）
     *
     * 该方法用于创建一个新的 Playwright 实例，可以通过 CreateOptions 参数
     * 来自定义 Playwright 的行为和配置。
     *
     * @param options 创建选项配置对象，包含各种初始化参数
     *                可以配置：
     *                - 浏览器类型（Chrome、Firefox、Safari等）
     *                - 代理设置
     *                - 超时时间
     *                - 其他高级选项
     * @return 返回创建的 Playwright 实例，用于后续的浏览器自动化操作
     * @throws RuntimeException 当创建失败时可能抛出运行时异常
     */
    static Playwright create(CreateOptions options) {
        // 调用具体实现类 PlaywrightImpl2 的创建方法
        // 该实现类包含了实际的创建逻辑
        return PlaywrightImpl2.create(options);
    }

    /**
     * 创建 Playwright 实例（使用默认配置）
     *
     * 这是一个便捷方法，用于快速创建使用默认配置的 Playwright 实例。
     * 适用于不需要特殊配置的场景，简化了调用过程。
     *
     * 内部实现：
     * 该方法会调用带参数的 create 方法，并传入 null 作为参数，
     * 这样实现类会使用默认的配置选项。
     *
     * @return 返回使用默认配置创建的 Playwright 实例
     * @see #create(CreateOptions) 带参数的创建方法
     */
    static Playwright create() {
        // 调用带参数的 create 方法，传入 null 使用默认配置
        // 强制类型转换确保类型安全
        return create((CreateOptions)null);
    }
}
