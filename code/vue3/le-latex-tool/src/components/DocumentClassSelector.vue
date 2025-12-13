<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElRadioGroup, ElRadioButton, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'
import { DocumentClassConfig, ClassOptionConfig, DocumentClassInfo } from '../types/document-class-selector-types';
import { generateCodeFromDocumentClassInfo } from '../utils/document-class-selector-utils';
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    documentClass: string
    options: Record<string, boolean>
  }
  componentId?: number
  externalTrigger?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { documentClass: string; options: Record<string, boolean> }): void
  (e: 'codeChange', value: string, documentClassInfo?: DocumentClassInfo): void
}>()

// 控制弹窗显示
const isDialogOpen = ref(false)

// 文档类选项配置
const documentClasses: DocumentClassConfig[] = [
  {
    className: 'ctexart',
    label: 'ctexart',
    description: '对应英文article，用于短篇中文论文/报告'
  },
  {
    className: 'ctexrep',
    label: 'ctexrep',
    description: '对应英文report，支持章节结构，适合长篇报告'
  },
  {
    className: 'ctexbook',
    label: 'ctexbook',
    description: '对应英文book，含完整书籍结构（如前言、目录）'
  },
  {
    className: 'ctexbeamer',
    label: 'ctexbeamer',
    description: '对应英文beamer，专用于中文幻灯片'
  }
]

// 文档类可选选项
const classOptions: ClassOptionConfig[] = [
  { key: 'a4paper', label: 'a4paper', desc: '设置纸张大小为 A4', },
  { key: 'oneside', label: 'oneside', desc: '单面排版模式' },
  { key: 'zihao=-4', label: 'zihao=-4', desc: '中文字号设置为小四' },
  { key: 'space', label: 'space', desc: '启用空格相关优化' }, 
  { key: 'scheme=chinese', label: 'scheme=chinese', desc: '中文排版方案' },
  { key: 'heading=true', label: 'heading=true', desc: '启用中文标题格式支持' },
  { key: 'hyperref', label: 'hyperref', desc: '与超链接设置兼容' },
  { key: 'fntef', label: 'fntef', desc: '与中文字体特效兼容' },
  { key: 'fancyhdr', label: 'fancyhdr', desc: '自动加载 fancyhdr 并启用中文页眉支持' },
  { key: 'fontset=none', label: 'fontset=none', desc: '不使用默认字体集，便于自定义字体' }
]

const selectedClass = computed({
  get: () => props.modelValue.documentClass || 'ctexbook', // 默认使用 ctexbook
  set: (value) => emit('update:modelValue', {
    documentClass: value,
    options: props.modelValue.options
  })
})

const optionValues = computed({
  get: () => props.modelValue.options,
  set: (value) => emit('update:modelValue', {
    documentClass: props.modelValue.documentClass,
    options: value
  })
})

// 生成文档类信息列表
const generateDocumentClassInfo = (): DocumentClassInfo => {
  // 获取选中的选项
  const selectedOptions = classOptions
    .filter(option => optionValues.value[option.key])
    .map(option => option.key)
    .join(',')
  
  return {
    command: 'documentclass',
    options: selectedOptions,
    documentClass: selectedClass.value
  }
}

const latexCode = computed(() => {
  const documentClassInfo = generateDocumentClassInfo();
  return generateCodeFromDocumentClassInfo(documentClassInfo);
})

const exampleForOption = (opt: string) => `\\documentclass[${opt}]{$${selectedClass.value}}`.replace('$','')

// 更新选项值
const updateOptionValue = (key: string, value: boolean) => {
  emit('update:modelValue', {
    documentClass: props.modelValue.documentClass,
    options: { ...optionValues.value, [key]: value }
  })
}

if (!props.modelValue.documentClass) {
  emit('update:modelValue', {
    documentClass: 'ctexbook',
    options: props.modelValue.options || {}
  })
}

setupCodeEmission(latexCode, emit as any, props.componentId, 'DocumentClassSelector')

// 弹窗控制方法
const openDialog = () => {
  isDialogOpen.value = true
}

const closeDialog = () => {
  isDialogOpen.value = false
}

defineExpose({
  openDialog,
  closeDialog
})
</script>

<template>
  <div>
    <!-- 触发弹窗的按钮 -->
    <el-button v-if="!props.externalTrigger" type="primary" size="small" round @click="openDialog">Document Class 文档类</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="isDialogOpen"
      title="Document Class 文档类"
      :before-close="closeDialog"
      
    >
      <el-card shadow="hover">
        <div>
          <strong>文档类选择</strong>
          <p>选择适合的中文文档类</p>
          
          <div class="document-class-selector-container">
            <!-- 左栏：选项 -->
            <div class="document-class-selector-left">
              <el-divider />
              
              <el-radio-group v-model="selectedClass" class="document-class-selector-section">
                <el-radio-button
                  v-for="docClass in documentClasses"
                  :key="docClass.className"
                  :label="docClass.className"
                >
                  {{ docClass.label }}
                </el-radio-button>
              </el-radio-group>
              
              <div class="document-class-selector-description">
                <div v-for="docClass in documentClasses" :key="docClass.className" v-show="selectedClass === docClass.className">
                  {{ docClass.description }}
                </div>
              </div>
              
              <div class="document-class-selector-options">
                <strong class="document-class-selector-options-title">文档类选项</strong>
              <div class="document-class-selector-options-list">
                <div v-for="opt in classOptions" :key="opt.key">
                  <el-checkbox
                    :model-value="optionValues[opt.key]"
                    @update:model-value="(val: boolean | string | number) => updateOptionValue(opt.key, Boolean(val))"
                    :label="opt.label"
                    class="document-class-selector-option-item"
                  />
                  <div v-if="optionValues[opt.key]">
                    <div>{{ opt.desc }}</div>
                    <pre class="code-preview-content">{{ exampleForOption(opt.key) }}</pre>
                  </div>
                </div>
              </div>
              </div>
            </div>
            
            <!-- 右栏：代码预览 -->
            <div class="document-class-selector-right">
              <div class="document-class-selector-code-preview">
                <pre class="document-class-selector-code-preview-content">{{ latexCode }}</pre>
              </div>
            </div>
          </div>
        </div>
      </el-card>
      
      <template #footer>
        
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>
