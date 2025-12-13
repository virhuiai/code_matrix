# 1. 创建项目

初始化 Vite 项目：

```
npm init vite
```

按照提示进行以下选择：

- Project name: le-latex-tool
- Framework: Vue
- Variant: TypeScript

初始化完成后，进入项目目录并安装依赖：

```
cd le-latex-tool
pnpm install
```  


# 启动项目

```
pnpm run dev
```

项目将在 http://localhost:5173 上运行。

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

# 在 HelloWorld.vue 组件中使用 Element Plus 组件

修改您的 `HelloWorld.vue` 文件，开始使用 Element Plus 组件：
   
```vue
<script setup lang="ts">
import { ref } from 'vue'
import { ElButton, ElMessage } from 'element-plus'

defineProps<{ msg: string }>()

const count = ref(0)

const handleClick = () => {
    ElMessage.success('按钮被点击了!')
}
</script>

<template>
    <h1>{{ msg }}</h1>

    <div class="card">
    <!-- 使用 Element Plus 按钮组件 -->
    <el-button type="primary" @click="handleClick">
        count is {{ count }}
    </el-button>
    <p>
        编辑
        <code>components/HelloWorld.vue</code> 来测试 HMR
    </p>
    </div>

    <!-- 其他内容保持不变 -->
    <p>
    查看
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
        >create-vue</a
    >，官方的 Vue + Vite 起步模板
    </p>
    <p>
    在
    <a
        href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
        target="_blank"
        >Vue 文档扩展指南</a
    > 中了解更多关于 Vue 的 IDE 支持
    </p>
    <p class="read-the-docs">点击 Vite 和 Vue 标志了解更多</p>
</template>
```

# 可选：按需导入配置


 如果想要通过只导入使用的组件来减小打包体积，可以配置自动按需导入：

 ```bash
 pnpm install -D unplugin-vue-components unplugin-auto-import
 ```

 然后修改您的 `vite.config.ts` 文件：
 
 ```typescript
 // vite.config.ts
 import { defineConfig } from 'vite'
 import vue from '@vitejs/plugin-vue'
 import AutoImport from 'unplugin-auto-import/vite'
 import Components from 'unplugin-vue-components/vite'
 import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
 
 export default defineConfig({
   plugins: [
     vue(),
     AutoImport({
       resolvers: [ElementPlusResolver()],
     }),
     Components({
       resolvers: [ElementPlusResolver()],
     }),
   ],
 })
 ```

完成这些配置后，您就可以在整个 Vue 3 应用程序中使用 Element Plus 提供的各种组件了。

# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).
