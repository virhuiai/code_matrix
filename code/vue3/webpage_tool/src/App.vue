<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const topMenus = [
  { key: 'home', label: '主页' },
  { key: 'tools', label: '工具' },
  { key: 'settings', label: '设置' },
]

const subMenuMap = {
  home: [
    { key: 'dashboard', label: '仪表盘', path: '/home/dashboard' },
    { key: 'news', label: '更新', path: '/home/news' },
  ],
  tools: [
    { key: 'text', label: '文本处理', path: '/tools/text' },
    { key: 'generator', label: '生成器', path: '/tools/generator' },
    { key: 'analyzer', label: '分析器', path: '/tools/analyzer' },
  ],
  settings: [
    { key: 'profile', label: '个人设置', path: '/settings/profile' },
    { key: 'preferences', label: '偏好', path: '/settings/preferences' },
  ],
}

const activeTop = computed(() => {
  const seg = route.path.split('/')[1] || 'home'
  return ['home', 'tools', 'settings'].includes(seg) ? seg : 'home'
})
const subMenus = computed(() => subMenuMap[activeTop.value] || [])

function handleTopSelect(key) {
  const list = subMenuMap[key]
  const first = list && list[0] ? list[0].path : '/home/dashboard'
  router.push(first)
}
</script>

<template>
  <el-container class="layout-container">
    <el-header height="56px" class="layout-header">
      <el-menu mode="horizontal" :default-active="activeTop" @select="handleTopSelect">
        <el-menu-item v-for="m in topMenus" :key="m.key" :index="m.key">{{ m.label }}</el-menu-item>
      </el-menu>
    </el-header>
    <el-container class="layout-body">
      <el-aside width="260px" class="layout-aside">
        <el-scrollbar>
          <el-menu :default-active="$route.path" router>
            <el-menu-item v-for="m in subMenus" :key="m.key" :index="m.path">{{ m.label }}</el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-main class="layout-main">
        <el-scrollbar>
          <router-view />
        </el-scrollbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.layout-header {
  background-color: #409eff;
  padding: 0;
}

.layout-body {
  flex: 1;
  overflow: hidden;
}

.layout-aside {
  background-color: #f5f5f5;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.layout-main {
  padding: 0;
  overflow: hidden;
  background-color: #ffffff;
}

.el-menu {
  border-right: none;
}

:deep(.el-scrollbar) {
  flex: 1;
  overflow: hidden;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
</style>