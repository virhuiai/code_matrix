# 1. 创建项目

```
pnpm init vite
```

framework选择vue   

使用typescript

Done. Now run:

```
cd le-latex-tool
pnpm install
```  


# 启动项目

```
pnpm run dev
```

# 安装 Element Plus：

```
pnpm install element-plus
```

# 在项目中配置 Element Plus

对于使用 Vite 和 Vue 3 的项目，您需要在主入口文件中配置 Element Plus

（通常是 [main.ts](file:///Volumes/THAWSPACE/CshProject/code_matrix.git/code/vue3/le-latex-tool/src/main.ts) 或 `main.js`）：

```typescript
// main.ts
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)

app.use(ElementPlus)
app.mount('#app')
```

这样就全局引入了 Element Plus 及其样式。

# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).
