# webpage_tool 开发笔记

> 更新时间：2025-12-14（基于当前仓库状态）

## 项目概览

- 技术栈：Vue 3 + Vite 5
- UI：Element Plus（通过自动按需引入配置）
- 入口：`src/main.js:1`，根组件：`src/App.vue:1`
- 构建工具与插件：`vite.config.js:1-17` 配置了 `@vitejs/plugin-vue`、`unplugin-auto-import`、`unplugin-vue-components` 并启用 `ElementPlusResolver`

## 当前项目状态

- 代码结构为 Vite 官方 Vue 模板的基础形态：
  - 启动挂载：`src/main.js:1-5`
  - 示例组件：`src/components/HelloWorld.vue:1-43`
  - 页面样式：`src/style.css:1-79`
- 已安装并配置 Element Plus 自动导入（见 `vite.config.js:8-16`），但当前页面尚未使用任何 Element Plus 组件。
- 项目脚本：`package.json:6-10`
  - `pnpm run dev`：本地开发
  - `pnpm run build`：生产构建
  - `pnpm run preview`：本地预览构建产物
- Node 版本建议：Vite 5 要求 Node >= 18。

## 目录结构（摘录）

```
webpage_tool/
├─ index.html
├─ vite.config.js
├─ package.json
├─ src/
│  ├─ main.js
│  ├─ App.vue
│  ├─ components/HelloWorld.vue
│  └─ style.css
└─ .gitignore
```

## 本地开发

- 安装依赖：`pnpm i`
- 启动开发：`pnpm run dev`
- 构建产物：`pnpm run build`
- 本地预览：`pnpm run preview`

## 开发约定

- 组件统一使用 `<script setup>` 与 Composition API（参见 `App.vue:1`、`HelloWorld.vue:1`）。
- Element Plus 通过自动注册使用，无需手动 `import` 组件；样式由解析器默认策略处理。
- 保持文件命名与目录结构简洁，页面从 `App.vue` 下开始搭建。

## 后续计划（建议）

- 引入实际业务页面并开始使用 Element Plus 组件库搭建 UI。
- 视需要添加 `vue-router` 与 `pinia` 进行路由与状态管理。
- 增加代码质量工具（如 ESLint、Prettier）与类型支持（如将入口迁移到 `main.ts`）。
- 补充单元测试与基础 CI 流程。

## 参考位置

- 入口挂载：`src/main.js:1-5`
- 根组件：`src/App.vue:1-30`
- 示例组件：`src/components/HelloWorld.vue:1-43`
- Vite 插件配置：`vite.config.js:1-17`
- pnpm 脚本与依赖：`package.json:1-21`
