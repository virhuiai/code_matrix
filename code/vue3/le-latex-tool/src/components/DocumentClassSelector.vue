<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElRadioGroup, ElRadioButton } from 'element-plus'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'codeChange', value: string): void
}>()

interface DocumentClassConfig {
  className: string
  label: string
  description: string
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

const selectedClass = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const computedLatexCode = computed(() => {
  if (!selectedClass.value) return ''
  
  const commonOptions = [
    'a4paper',
    'oneside',
    'zihao=-4',
    'space',
    'scheme=chinese',
    'heading=true',
    'hyperref',
    'fntef',
    'fancyhdr',
    'fontset=none'
  ]
  
  return `\\documentclass[${commonOptions.join(',')}]{${selectedClass.value}}`
})

watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

onMounted(() => {
  emit('codeChange', computedLatexCode.value)
})
</script>

<template>
  <el-card shadow="hover" class="document-class-card">
    <div class="document-class">
      <strong class="document-class__title">文档类选择</strong>
      <p class="document-class__description">选择适合的中文文档类</p>
      
      <el-radio-group 
        v-model="selectedClass" 
        class="document-class__radio-group"
      >
        <div 
          v-for="docClass in documentClasses" 
          :key="docClass.className" 
          class="document-class__item"
        >
          <el-radio-button :label="docClass.className">
            {{ docClass.label }}
          </el-radio-button>
          <p class="document-class__item-description">{{ docClass.description }}</p>
        </div>
      </el-radio-group>
    </div>
  </el-card>
</template>

<style scoped>
/* 所有样式已移至 src/style.css 文件中 */
</style>