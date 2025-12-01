<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElRadioGroup, ElRadioButton, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'

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

// 控制弹窗显示
const dialogVisible = ref(false)

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

const computedLatexCode = computed(() => {
  if (!selectedClass.value) return ''
  
  // 获取选中的选项
  const selectedOptions = classOptions
    .filter(option => optionValues.value[option.key])
    .map(option => option.key)
  
  return `\\documentclass[${selectedOptions.join(',')}]{${selectedClass.value}}`
})

// 更新选项值
const updateOptionValue = (key: string, value: boolean) => {
  emit('update:modelValue', {
    documentClass: props.modelValue.documentClass,
    options: { ...optionValues.value, [key]: value }
  })
}

watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

onMounted(() => {
  // 如果没有设置文档类，则默认使用 ctexbook
  if (!props.modelValue.documentClass) {
    emit('update:modelValue', {
      documentClass: 'ctexbook',
      options: props.modelValue.options || {}
    })
  }
  
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`DocumentClassSelector component loaded successfully with ID: ${props.componentId}`)
  }
})

// 弹窗控制方法
const openDialog = () => {
  dialogVisible.value = true
}

const closeDialog = () => {
  dialogVisible.value = false
}

defineExpose({
  openDialog,
  closeDialog
})
</script>

<template>
  <div>
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Document Class 文档类</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Document Class 文档类"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>文档类选择</strong>
          <p>选择适合的中文文档类</p>
          
          <el-radio-group v-model="selectedClass" style="margin-bottom: 20px;">
            <el-radio-button
              v-for="docClass in documentClasses"
              :key="docClass.className"
              :label="docClass.className"
            >
              {{ docClass.label }}
            </el-radio-button>
          </el-radio-group>
          
          <div style="margin-top: 10px; color: #666; font-size: 14px; margin-bottom: 20px;">
            <div v-for="docClass in documentClasses" :key="docClass.className" v-show="selectedClass === docClass.className">
              {{ docClass.description }}
            </div>
          </div>
          
          <el-divider />
          
          <div style="margin-top: 20px;">
            <strong>文档类选项</strong>
            <div style="margin-top: 10px; margin-left: 20px;">
              <el-checkbox
                v-for="option in classOptions"
                :key="option.key"
                :model-value="optionValues[option.key]"
                @update:model-value="(val) => updateOptionValue(option.key, Boolean(val))"
                :label="option.label"
                style="display: block; margin-bottom: 8px;"
              />
            </div>
          </div>
          
          <div style="margin-top: 20px;">
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ computedLatexCode }}</pre>
          </div>
        </div>
      </el-card>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="closeDialog">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>