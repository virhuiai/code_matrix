import { createRouter, createWebHistory } from 'vue-router'

const HomeDashboard = () => import('../pages/HomeDashboard.vue')
const HomeNews = () => import('../pages/HomeNews.vue')
const ToolsText = () => import('../pages/ToolsText.vue')
const ToolsGenerator = () => import('../pages/ToolsGenerator.vue')
const ToolsAnalyzer = () => import('../pages/ToolsAnalyzer.vue')
const SettingsProfile = () => import('../pages/SettingsProfile.vue')
const SettingsPreferences = () => import('../pages/SettingsPreferences.vue')

const routes = [
  { path: '/', redirect: '/home/dashboard' },
  { path: '/home/dashboard', component: HomeDashboard },
  { path: '/home/news', component: HomeNews },
  { path: '/tools/text', component: ToolsText },
  { path: '/tools/generator', component: ToolsGenerator },
  { path: '/tools/analyzer', component: ToolsAnalyzer },
  { path: '/settings/profile', component: SettingsProfile },
  { path: '/settings/preferences', component: SettingsPreferences },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
