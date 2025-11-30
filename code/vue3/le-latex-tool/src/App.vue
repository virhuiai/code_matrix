<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElContainer, ElHeader, ElAside, ElMain, ElFooter, ElTabs, ElTabPane, ElCard, ElInput } from 'element-plus'
import PackageOptions from './components/PackageOptions.vue'

const activeTab = ref('tab1')

// 定义选项的默认值
const packageOptions = ref({
  autoFakeBold: true,
  autoFakeSlant: true,
  noMath: true,
  prologue: true,
  dvipsnames: true
})

// 从子组件接收的LaTeX代码
const latexCodeFromChild = ref('')
</script>

<template>
  <el-container class="app">
    <!-- 顶部标题区域 -->
    <el-header class="app__header">
      <h1 class="app__header-title">LE LaTeX 工具</h1>
    </el-header>
    
    <!-- 下方主要内容区域 -->
    <el-container class="app__container"> 
      <!-- 左侧选项卡 -->
      <el-aside class="app__aside">
        <el-tabs v-model="activeTab" type="border-card" class="tabs">
          <el-tab-pane label="选项" name="tab1">
            <div class="app__tab-content">
              <!-- 使用新创建的组件 -->
              <PackageOptions 
                v-model="packageOptions" 
                @code-change="(code) => latexCodeFromChild = code"
              />

              <el-card shadow="hover" class="card card--hover">
                <div class="card">
                  <strong class="card__title">文档设置</strong>
                  <p class="card__description">设置文档类型、页面大小等</p>
                </div>
              </el-card>
              
              <el-card shadow="hover" class="card card--hover">
                <div class="card">
                  <strong class="card__title">公式编辑</strong>
                  <p class="card__description">插入和编辑数学公式</p>
                </div>
              </el-card>
              
              <el-card shadow="hover" class="card card--hover">
                <div class="card">
                  <strong class="card__title">图表生成</strong>
                  <p class="card__description">创建和自定义图表</p>
                </div>
              </el-card>
              
            </div>
          </el-tab-pane>
          <el-tab-pane label="常见模板" name="tab2"></el-tab-pane>
          <!-- <el-tab-pane label="选项三" name="tab3"></el-tab-pane>
          <el-tab-pane label="选项四" name="tab4"></el-tab-pane> -->
        </el-tabs>
      </el-aside>
      
      <!-- 中间主要内容显示区域 -->
      <el-main class="app__main">
        <div class="app__main-content">
          <!-- 显示从子组件传递过来的LaTeX代码 -->
          <el-input
            v-model="latexCodeFromChild"
            type="textarea"
            :rows="10"
            readonly
            class="latex-output"
          />
        </div>
      </el-main>
      
      <!-- 右侧属性设置区域 -->
      <el-aside class="app__property-panel">
        <div class="app__property-panel-title">
          <h3>属性设置</h3>
          <div v-if="activeTab === 'tab1'">
            <p>选项一的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab2'">
            <p>选项二的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab3'">
            <p>选项三的属性设置</p>
          </div>
          <div v-else-if="activeTab === 'tab4'">
            <p>选项四的属性设置</p>
          </div>
        </div>
      </el-aside>
    </el-container>
    
    <!-- 底部 -->
    <el-footer class="app__footer">
      <span>LE LaTeX 工具 ©2023</span>
    </el-footer>
  </el-container>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>