# 开发日志

> 日期：2025-12-14

## 本次更新内容

- 建立路由结构，支持顶部菜单与左侧子菜单导航。
- 重构入口布局为 Element Plus 组件（`el-container`/`el-header`/`el-aside`/`el-main`）。
- 新增子页面：
  - `HomeDashboard`、`HomeNews`
  - `ToolsText`（包含 `el-input` 文本域，`autosize`，按钮实现空格转下划线）
  - `ToolsGenerator`、`ToolsAnalyzer`
  - `SettingsProfile`、`SettingsPreferences`
- 引入并配置 `vue-router`，在 `src/router/index.js` 中进行路由注册。
- 在 `src/main.js` 中挂载路由，并引入 Element Plus 样式。
- 使用 `pnpm install` 并运行 `pnpm dev` 验证本地开发。

## 相关文件

- 入口组件：`src/App.vue`
- 路由配置：`src/router/index.js`
- 页面目录：`src/pages/*`
- 应用入口：`src/main.js`

## 后续计划

- 扩展工具页的具体功能，实现更多文本与数据处理能力。
- 引入 ESLint/Prettier 等质量工具，规范代码风格。
- 增加基础测试与 CI 流程。
