<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElRadioGroup, ElRadioButton, ElCheckbox } from 'element-plus'

const props = defineProps<{
  modelValue: {
    documentClass: string
    options: Record<string, boolean>
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { documentClass: string; options: Record<string, boolean> }): void
  (e: 'codeChange', value: string): void
}>()

interface DocumentClassConfig {
  className: string
  label: string
  description: string
}

// 文档类选项配置
interface ClassOptionConfig {
  key: string
  label: string
}

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
  { key: 'a4paper', label: 'a4paper' },
  { key: 'oneside', label: 'oneside' },
  { key: 'zihao=-4', label: 'zihao=-4' },
  { key: 'space', label: 'space' },
  { key: 'scheme=chinese', label: 'scheme=chinese' },
  { key: 'heading=true', label: 'heading=true' },
  { key: 'hyperref', label: 'hyperref' },
  { key: 'fntef', label: 'fntef' },
  { key: 'fancyhdr', label: 'fancyhdr' },
  { key: 'fontset=none', label: 'fontset=none' }
]

const selectedClass = computed({
  get: () => props.modelValue.documentClass,
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

const computedLatexCode = computed(() => {
  if (!selectedClass.value) return ''
  
  // 获取选中的选项
  const selectedOptions = classOptions
    .filter(option => optionValues.value[option.key])
    .map(option => option.key)
  
  return `\\documentclass[${selectedOptions.join(',')}]{${selectedClass.value}}`
})

watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`DocumentClassSelector component loaded successfully with ID: ${props.componentId}`)
  }
})
</script>

<template>
  <el-card shadow="hover">
    <div>
      <strong>文档类选择</strong>
      <p>选择适合的中文文档类</p>
      
      <!-- 将原来的 radio group 替换为 select -->
      <el-select 
        v-model="selectedClass" 
        placeholder="请选择文档类"
      >
        <el-option
          v-for="docClass in documentClasses"
          :key="docClass.className"
          :label="docClass.label"
          :value="docClass.className"
        >
          <span>{{ docClass.label }}</span>
          <span>{{ docClass.description }}</span>
        </el-option>
      </el-select>
      
      <div>
        <strong>文档类选项</strong>
        <el-checkbox
          v-for="option in classOptions"
          :key="option.key"
          v-model="optionValues[option.key]"
          :label="option.label"
        />
      </div>
    </div>
  </el-card>
</template>