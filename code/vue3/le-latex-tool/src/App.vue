<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElContainer, ElHeader, ElAside, ElMain, ElFooter, ElTabs, ElTabPane, ElCard, ElInput } from 'element-plus'
import PackageOptions from './components/PackageOptions.vue'
import DocumentClassSelector from './components/DocumentClassSelector.vue'
import FontSettings from './components/FontSettings.vue'
import EnglishFontSettings from './components/EnglishFontSettings.vue'
import MoreWritesPackage from './components/MoreWritesPackage.vue'
import BoxPackages from './components/BoxPackages.vue'
import DocumentContent from './components/DocumentContent.vue'
import LettrinePackage from './components/LettrinePackage.vue'
import CodeListingPackage from './components/CodeListingPackage.vue'

const activeTab = ref('tab1')

// 定义选项的默认值
const packageOptions = ref({
  autoFakeBold: true,
  autoFakeSlant: true,
  noMath: true,
  prologue: true,
  dvipsnames: true
})

// 修改文档类选项的默认值结构
const documentClass = ref({
  documentClass: 'ctexart',
  options: {
    'a4paper': true,
    'oneside': true,
    'zihao=-4': true,
    'space': true,
    'scheme=chinese': true,
    'heading=true': true,
    'hyperref': true,
    'fntef': true,
    'fancyhdr': true,
    'fontset=none': true
  }
})

// 字体设置的默认值
const fontSettings = ref({
  fonts: [
    {
      type: 'main',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengShuSong-GBK-1.ttf'
    },
    {
      type: 'sans',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengHeiTi-GBK-1.ttf'
    },
    {
      type: 'mono',
      path: '/Volumes/THAWSPACE/CshProject/code_matrix.git/code/LaTex/fontFiles/方正/',
      filename: 'FangZhengFangSong-GBK-1.ttf'
    }
  ]
})

// 英文字体设置的默认值
const englishFontSettings = ref({
  enabled: true
})

// MoreWritesPackage 的默认值
const moreWritesPackage = ref({
  enabled: true
})

// BoxPackages 的默认值 - 所有选项默认选中
const boxPackages = ref({
  fancybox: true,
  boxedminipage: true,
  tikz: true,
  tcolorbox: {
    enabled: true,
    raster: true,
    listings: true,
    theorems: true,
    skins: true,
    xparse: true,
    breakable: true,
  },
  awesomebox: true,
  mdframed: true,
  framed: true,
  changepage: true
})

// LettrinePackage 的默认值
const lettrinePackage = ref({
  enabled: true,
  lines: 1,
  lhang: 0.1,
  loversize: 0.1
})

// CodeListingPackage 的默认值
const codeListingPackage = ref({
  xcolor: {
    enabled: true,
    dvipsnames: true
  },
  cprotect: true,
  spverbatim: true,
  fancyvrb: true,
  fancyvrbEx: true,
  xparse: true,
  minted: {
    enabled: true,
    newfloat: true,
    cache: false
  },
  listings: true,
  accsupp: true,
  tcolorbox: {
    enabled: true,
    listings: true,
    skins: true,
    breakable: true,
    xparse: true
  }
})

// DocumentContent 的默认值
const documentContent = ref({
  enabled: true
})

// 从子组件接收的LaTeX代码
const latexCodeFromChild = ref('')
const documentClassCode = ref('')
const fontSettingsCode = ref('')
const englishFontSettingsCode = ref('')
const moreWritesPackageCode = ref('')
const boxPackagesCode = ref('')
const lettrinePackageCode = ref('')
const codeListingPackageCode = ref('')
const documentContentCode = ref('')

// 合并多个组件传来的代码
const combinedLatexCode = computed(() => {
  return [latexCodeFromChild.value, documentClassCode.value, fontSettingsCode.value, englishFontSettingsCode.value, moreWritesPackageCode.value, boxPackagesCode.value, lettrinePackageCode.value, codeListingPackageCode.value, documentContentCode.value]
    .filter(code => code.trim() !== '')
    .join('\n')
})
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
            <div class="app__left_tab-content">
              <!-- 使用新创建的组件 -->
              <PackageOptions 
                v-model="packageOptions" 
                @code-change="(code) => latexCodeFromChild = code"
              />

              <!-- 修改DocumentClassSelector组件的使用 -->
              <DocumentClassSelector
                v-model="documentClass"
                @code-change="(code) => documentClassCode = code"
              />
              
              <!-- 添加FontSettings组件 -->
              <FontSettings
                v-model="fontSettings"
                @code-change="(code) => fontSettingsCode = code"
              />

              <!-- 添加EnglishFontSettings组件 -->
              <EnglishFontSettings
                v-model="englishFontSettings"
                @code-change="(code) => englishFontSettingsCode = code"
              />

              <!-- 添加MoreWritesPackage组件 morewrites -->
              <MoreWritesPackage
                v-model="moreWritesPackage"
                @code-change="(code) => moreWritesPackageCode = code"
              />

              <!-- 添加BoxPackages组件 2_2_盒子设置 -->
              <BoxPackages
                v-model="boxPackages"
                @code-change="(code: string) => boxPackagesCode = code"
              />

              <!-- 添加LettrinePackage组件 2_3_首行放大 -->
              <LettrinePackage
                v-model="lettrinePackage"
                @code-change="(code: string) => lettrinePackageCode = code"
              />

              <!-- 添加CodeListingPackage组件 -->
              <CodeListingPackage
                v-model="codeListingPackage"
                @code-change="(code: string) => codeListingPackageCode = code"
              />

              <!-- 添加DocumentContent组件 -->
              <DocumentContent
                v-model="documentContent"
                @code-change="(code: string) => documentContentCode = code"
              />

              <el-card shadow="hover" class="card card--hover">
                <div class="card">
                  <strong class="card__title">文档设置</strong>
                  <p class="card__description">设置文档类型、页面大小等</p>
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
            v-model="combinedLatexCode"
            type="textarea"
            :autosize="{ minRows: 10}"
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