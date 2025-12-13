<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElContainer, ElHeader, ElAside, ElMain, ElFooter, ElTabs, ElTabPane, ElInput, ElSpace, ElButton, ElDivider } from 'element-plus'
import PackageOptions from './components/PackageOptions.vue'
import DocumentClassSelector from './components/DocumentClassSelector.vue'
import FontSettings from './components/FontSettings.vue'
import EnglishFontSettings from './components/EnglishFontSettings.vue'
import MoreWritesPackage from './components/MoreWritesPackage.vue'
import BoxPackages from './components/BoxPackages.vue'
import DocumentContent from './components/DocumentContent.vue'
import LettrinePackage from './components/LettrinePackage.vue'
import CodeListingPackage from './components/CodeListingPackage.vue'
import DocumentLayoutPackage from './components/DocumentLayoutPackage.vue'
import GeometryPackage from './components/GeometryPackage.vue'
import FancyhdrPackage from './components/FancyhdrPackage.vue'
import TitleFormatPackage from './components/TitleFormatPackage.vue'
import TableOfContentsPackage from './components/TableOfContentsPackage.vue'
import HyperlinkIndexPackage from './components/HyperlinkIndexPackage.vue'
import ListSymbolPackage from './components/ListSymbolPackage.vue'
import ParallelTextPackage from './components/ParallelTextPackage.vue'
import CommentPackage from './components/CommentPackage.vue'
import FigureColorPackage from './components/FigureColorPackage.vue'
import TablePackage from './components/TablePackage.vue'

const activeTab = ref('tab1')


// 创建一个生成递增编号的函数
let componentCounter = 0
const getNextComponentId = () => {
  return componentCounter++
}

const componentIds = {
  packageOptions: getNextComponentId(),
  documentClass: getNextComponentId(),
  fontSettings: getNextComponentId(),
  englishFontSettings: getNextComponentId(),
  moreWritesPackage: getNextComponentId(),
  boxPackages: getNextComponentId(),
  lettrinePackage: getNextComponentId(),
  fancyhdrPackage: getNextComponentId(),
  codeListingPackage: getNextComponentId(),
  documentLayoutPackage: getNextComponentId(),
  geometryPackage: getNextComponentId(),
  titleFormatPackage: getNextComponentId(),
  tableOfContentsPackage: getNextComponentId(),
  hyperlinkIndexPackage: getNextComponentId(),
  listSymbolPackage: getNextComponentId(),
  parallelTextPackage: getNextComponentId(),
  commentPackage: getNextComponentId(),
  figureColorPackage: getNextComponentId(),
  tablePackage: getNextComponentId(),
  documentContent: getNextComponentId()
}

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
  documentClass: 'ctexbook',
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

// FancyhdrPackage 的默认值
const fancyhdrPackage = ref({
  enabled: true
})

// TitleFormatPackage 的默认值
const titleFormatPackage = ref({
  enabled: true
})

// TableOfContentsPackage 的默认值
const tableOfContentsPackage = ref({
  titletocEnabled: true,
  multitocEnabled: false
})

// HyperlinkIndexPackage 的默认值
const hyperlinkIndexPackage = ref({
  variorefEnabled: true,
  imakeidxEnabled: false,
  splitidxEnabled: false,
  hyperrefEnabled: true,
  urlEnabled: true,
  pdfTitle: ''
})

// ListSymbolPackage 的默认值
const listSymbolPackage = ref({
  enabled: true
})

// ParallelTextPackage 的默认值
const parallelTextPackage = ref({
  enabled: true
})

// CommentPackage 的默认值
const commentPackage = ref({
  enabled: true
})

// FigureColorPackage 的默认值
const figureColorPackage = ref({
  enabled: true
})

// TablePackage 的默认值
const tablePackage = ref({
  enabled: true
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

// DocumentLayoutPackage 的默认值
const documentLayoutPackage = ref({
  parskip: {
    enabled: true,
    linespread: {
      enabled: true,
      value: 1.245
    }
  },
  xspace: {
    enabled: true
  }
})

// GeometryPackage 的默认值
const geometryPackage = ref({
  enabled: true,
  paperWidth: '185mm',
  paperHeight: '260mm',
  textWidth: '148mm',
  textHeight: '220mm',
  leftMargin: '21mm',
  topMargin: '25.5mm'
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
const fancyhdrPackageCode = ref('')
const titleFormatPackageCode = ref('')
const tableOfContentsPackageCode = ref('')
const hyperlinkIndexPackageCode = ref('')
const listSymbolPackageCode = ref('')
const parallelTextPackageCode = ref('')
const commentPackageCode = ref('')
const figureColorPackageCode = ref('')
const tablePackageCode = ref('')
const codeListingPackageCode = ref('')
const documentLayoutPackageCode = ref('')
const geometryPackageCode = ref('')
const documentContentCode = ref('')

// 合并多个组件传来的代码
const combinedLatexCode = computed(() => {
  const codes = [
    latexCodeFromChild.value,
    documentClassCode.value,
    fontSettingsCode.value,
    englishFontSettingsCode.value,
    moreWritesPackageCode.value,
    boxPackagesCode.value,
    lettrinePackageCode.value,
    fancyhdrPackageCode.value,
    codeListingPackageCode.value,
    documentLayoutPackageCode.value,
    geometryPackageCode.value,
    titleFormatPackageCode.value,
    tableOfContentsPackageCode.value,
    hyperlinkIndexPackageCode.value,
    listSymbolPackageCode.value,
    parallelTextPackageCode.value,
    commentPackageCode.value,
    figureColorPackageCode.value,
    tablePackageCode.value,
    
    
  ]

  

  codes.push(documentContentCode.value)
  

  return codes.filter(code => code.trim() !== '').join('\n\n')
})

// 外部触发：组件引用
const compRefs = {
  packageOptions: ref<any>(null),
  documentClass: ref<any>(null),
  fontSettings: ref<any>(null),
  englishFontSettings: ref<any>(null),
  moreWritesPackage: ref<any>(null),
  boxPackages: ref<any>(null),
  lettrinePackage: ref<any>(null),
  fancyhdrPackage: ref<any>(null),
  codeListingPackage: ref<any>(null),
  documentLayoutPackage: ref<any>(null),
  geometryPackage: ref<any>(null),
  titleFormatPackage: ref<any>(null),
  tableOfContentsPackage: ref<any>(null),
  hyperlinkIndexPackage: ref<any>(null),
  listSymbolPackage: ref<any>(null),
  parallelTextPackage: ref<any>(null),
  commentPackage: ref<any>(null),
  figureColorPackage: ref<any>(null),
  tablePackage: ref<any>(null),
  documentContent: ref<any>(null)
}

const openPanel = (key: keyof typeof compRefs) => {
  const inst = compRefs[key].value as any
  if (!inst) return
  if (typeof inst.openDialog === 'function') inst.openDialog()
  else if (typeof inst.showDialog === 'function') inst.showDialog()
}
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
              <el-space direction="vertical" size="small">
                <div>
                  <el-button size="small" round @click="openPanel('packageOptions')">宏包选项</el-button>
                  <el-button size="small" round @click="openPanel('documentClass')">文档类</el-button>
                  <el-button size="small" round @click="openPanel('fontSettings')">中文字体</el-button>
                  <el-button size="small" round @click="openPanel('englishFontSettings')">英文字体</el-button>
                  <el-button size="small" round @click="openPanel('moreWritesPackage')">MoreWrites</el-button>
                  <el-button size="small" round @click="openPanel('boxPackages')">盒子宏包</el-button>
                  <el-button size="small" round @click="openPanel('lettrinePackage')">首字下沉</el-button>
                  <el-button size="small" round @click="openPanel('fancyhdrPackage')">版式包</el-button>
                  <el-button size="small" round @click="openPanel('codeListingPackage')">代码抄录</el-button>
                  <el-button size="small" round @click="openPanel('documentLayoutPackage')">行距与空格</el-button>
                  <el-button size="small" round @click="openPanel('geometryPackage')">版面设置</el-button>
                  <el-button size="small" round @click="openPanel('titleFormatPackage')">标题格式</el-button>
                  <el-button size="small" round @click="openPanel('tableOfContentsPackage')">目录格式</el-button>
                  <el-button size="small" round @click="openPanel('hyperlinkIndexPackage')">链接与索引</el-button>
                  <el-button size="small" round @click="openPanel('listSymbolPackage')">列表与符号</el-button>
                  <el-button size="small" round @click="openPanel('parallelTextPackage')">对译环境</el-button>
                  <el-button size="small" round @click="openPanel('commentPackage')">注释设置</el-button>
                  <el-button size="small" round @click="openPanel('figureColorPackage')">插图与颜色</el-button>
                  <el-button size="small" round @click="openPanel('tablePackage')">表格设置</el-button>
                  <el-button size="small" round @click="openPanel('documentContent')">文档内容</el-button>
                </div>
                <el-divider />
                <!-- 使用新创建的组件 -->
                <PackageOptions 
                  v-model="packageOptions" 
                  :component-id="componentIds.packageOptions"
                  @code-change="(code) => {latexCodeFromChild = code;}"
                  :external-trigger="true"
                  :ref="el => compRefs.packageOptions.value = el"
                />

              <!-- 修改DocumentClassSelector组件的使用 -->
              <DocumentClassSelector
                v-model="documentClass"
                :component-id="componentIds.documentClass"
                @code-change="(code) => documentClassCode = code"
                :external-trigger="true"
                :ref="el => compRefs.documentClass.value = el"
              />
              
              <!-- 添加FontSettings组件 -->
              <FontSettings
                v-model="fontSettings"
                :component-id="componentIds.fontSettings"
                @code-change="(code) => fontSettingsCode = code"
                :external-trigger="true"
                :ref="el => compRefs.fontSettings.value = el"
              />

              <!-- 添加EnglishFontSettings组件 -->
              <EnglishFontSettings
                v-model="englishFontSettings"
                :component-id="componentIds.englishFontSettings"
                @code-change="(code) => englishFontSettingsCode = code"
                :external-trigger="true"
                :ref="el => compRefs.englishFontSettings.value = el"
              />

              <!-- 添加MoreWritesPackage组件 morewrites -->
              <MoreWritesPackage
                v-model="moreWritesPackage"
                :component-id="componentIds.moreWritesPackage"
                @code-change="(code) => moreWritesPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.moreWritesPackage.value = el"
              />

              <!-- 添加BoxPackages组件 2_2_盒子设置 -->
              <BoxPackages
                v-model="boxPackages"
                :component-id="componentIds.boxPackages"
                @code-change="(code: string) => boxPackagesCode = code"
                :external-trigger="true"
                :ref="el => compRefs.boxPackages.value = el"
              />

              <!-- 添加LettrinePackage组件 2_3_首行放大 -->
              <LettrinePackage
                v-model="lettrinePackage"
                :component-id="componentIds.lettrinePackage"
                @code-change="(code: string) => lettrinePackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.lettrinePackage.value = el"
              />

              <!-- 添加FancyhdrPackage组件 版式设置 -->
              <FancyhdrPackage
                v-model="fancyhdrPackage"
                :component-id="componentIds.fancyhdrPackage"
                @code-change="(code: string) => fancyhdrPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.fancyhdrPackage.value = el"
              />


              <!-- 添加CodeListingPackage组件 3_抄录设置 -->
              <CodeListingPackage
                v-model="codeListingPackage"
                :component-id="componentIds.codeListingPackage"
                @code-change="(code) => codeListingPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.codeListingPackage.value = el"
              />

              <!-- 添加DocumentLayoutPackage组件 行距和空格设置-->
              <DocumentLayoutPackage
                v-model="documentLayoutPackage"
                :component-id="componentIds.documentLayoutPackage"
                @code-change="(code) => documentLayoutPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.documentLayoutPackage.value = el"
              />

              <!-- 添加GeometryPackage组件 版面设置16K -->
              <GeometryPackage
                v-model="geometryPackage"
                :component-id="componentIds.geometryPackage"
                @code-change="(code: string) => geometryPackageCode = code"
                :ref="el => compRefs.geometryPackage.value = el"
              />


              <!-- 添加TitleFormatPackage组件 标题格式设置  \input{6_标题格式设置.tex}-->
              <TitleFormatPackage
                v-model="titleFormatPackage"
                :component-id="componentIds.titleFormatPackage"
                @code-change="(code: string) => titleFormatPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.titleFormatPackage.value = el"
              />

              <!-- 添加TableOfContentsPackage组件 目录格式设置  8_目录格式设置.tex -->
              <TableOfContentsPackage
                v-model="tableOfContentsPackage"
                :component-id="componentIds.tableOfContentsPackage"
                @code-change="(code: string) => tableOfContentsPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.tableOfContentsPackage.value = el"
              />


              <!-- 添加HyperlinkIndexPackage组件 链接_索引设置 10_链接_索引设置.tex -->
              <HyperlinkIndexPackage
                v-model="hyperlinkIndexPackage"
                :component-id="componentIds.hyperlinkIndexPackage"
                @code-change="(code: string) => hyperlinkIndexPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.hyperlinkIndexPackage.value = el"
              />


              <!-- 添加ListSymbolPackage组件 列表_符号设置 11_列表_符号设置.tex -->
              <ListSymbolPackage
                v-model="listSymbolPackage"
                :component-id="componentIds.listSymbolPackage"
                @code-change="(code: string) => listSymbolPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.listSymbolPackage.value = el"
              />


              <!-- 添加ParallelTextPackage组件 对译环境 12_对译环境.tex -->
              <ParallelTextPackage
                v-model="parallelTextPackage"
                :component-id="componentIds.parallelTextPackage"
                @code-change="(code: string) => parallelTextPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.parallelTextPackage.value = el"
              />


              <!-- 添加CommentPackage组件 注释 13_注释.tex -->
              <CommentPackage
                v-model="commentPackage"
                :component-id="componentIds.commentPackage"
                @code-change="(code: string) => commentPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.commentPackage.value = el"
              />


              <!-- 添加FigureColorPackage组件 插图_颜色设置 14_插图_颜色设置.tex -->
              <FigureColorPackage
                v-model="figureColorPackage"
                :component-id="componentIds.figureColorPackage"
                @code-change="(code: string) => figureColorPackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.figureColorPackage.value = el"
              />


              <!-- 添加TablePackage组件 表格设置 15_表格设置.tex -->
              <TablePackage
                v-model="tablePackage"
                :component-id="componentIds.tablePackage"
                @code-change="(code: string) => tablePackageCode = code"
                :external-trigger="true"
                :ref="el => compRefs.tablePackage.value = el"
              />


              <!-- todo  -->


              <!-- 添加DocumentContent组件 -->
              <DocumentContent
                v-model="documentContent"
                :component-id="componentIds.documentContent"
                @code-change="(code: string) => documentContentCode = code"
                :external-trigger="true"
                :ref="el => compRefs.documentContent.value = el"
              />
              </el-space>
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
