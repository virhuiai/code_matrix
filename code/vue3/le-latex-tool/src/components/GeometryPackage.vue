<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import PackageDialogLayout from './PackageDialogLayout.vue'
import { generateCodeFromPackageInfos } from '../utils/generic-packages-utils'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    paperWidth: string
    paperHeight: string
    textWidth: string
    textHeight: string
    leftMargin: string
    topMargin: string
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()


// 默认配置值
const defaultValues = {
  paperWidth: '185mm',
  paperHeight: '260mm',
  textWidth: '148mm',
  textHeight: '220mm',
  leftMargin: '21mm',
  topMargin: '25.5mm'
}

type GeometryConfig = {
  enabled: boolean
  paperWidth: string
  paperHeight: string
  textWidth: string
  textHeight: string
  leftMargin: string
  topMargin: string
}

// 从modelValue中提取除enabled以外的属性
const getModelValueWithoutEnabled = (): Partial<Omit<GeometryConfig, 'enabled'>> => {
  if (!props.modelValue) return {}
  const { enabled: _, ...rest } = props.modelValue
  return rest
}

// geometry 配置数据
const geometryConfig = ref<GeometryConfig>({
  enabled: props.modelValue?.enabled ?? false,
  ...defaultValues,
  ...getModelValueWithoutEnabled()
})

// 表单项配置
const formItems = [
  { key: 'paperWidth', label: '纸张宽度 (paperwidth)' },
  { key: 'paperHeight', label: '纸张高度 (paperheight)' },
  { key: 'textWidth', label: '正文宽度 (text width)' },
  { key: 'textHeight', label: '正文高度 (text height)' },
  { key: 'leftMargin', label: '左边距 (left)' },
  { key: 'topMargin', label: '上边距 (top)' }
] as const

type FormItemKey = typeof formItems[number]['key']

// 计算属性：生成 LaTeX 代码（使用通用工具）
const latexCode = computed(() => {
  if (!geometryConfig.value.enabled) return ''
  const opts: string[] = []
  if (geometryConfig.value.paperWidth) opts.push(`paperwidth=${geometryConfig.value.paperWidth}`)
  if (geometryConfig.value.paperHeight) opts.push(`paperheight=${geometryConfig.value.paperHeight}`)
  if (geometryConfig.value.textWidth && geometryConfig.value.textHeight) {
    opts.push(`text={${geometryConfig.value.textWidth},${geometryConfig.value.textHeight}}`)
  } else {
    if (geometryConfig.value.textWidth) opts.push(`textwidth=${geometryConfig.value.textWidth}`)
    if (geometryConfig.value.textHeight) opts.push(`textheight=${geometryConfig.value.textHeight}`)
  }
  if (geometryConfig.value.leftMargin) opts.push(`left=${geometryConfig.value.leftMargin}`)
  if (geometryConfig.value.topMargin) opts.push(`top=${geometryConfig.value.topMargin}`)
return generateCodeFromPackageInfos([{ package: 'geometry', options: opts }])
})

// 更新配置
const updateConfig = <T extends keyof GeometryConfig>(field: T, value: GeometryConfig[T]) => {
  geometryConfig.value = { ...geometryConfig.value, [field]: value }
  emit('update:modelValue', { ...geometryConfig.value })
}

// 重置选项操作
const handleOptionAction = (field: FormItemKey, action: 'reset' | 'clear') => {
  const newValue = action === 'reset' 
    ? defaultValues[field] || '' 
    : ''
  updateConfig(field, newValue)
}

// 重置所有配置
const resetAll = () => {
  geometryConfig.value = {
    enabled: geometryConfig.value.enabled,
    ...defaultValues
  }
  emit('update:modelValue', { ...geometryConfig.value })
}

setupCodeEmission(latexCode, emit, props.componentId, 'GeometryPackage')

</script>

<template>
  <PackageDialogLayout 
    button-label="Geometry 版面设置"
    dialog-title="Geometry 版面设置"
    :code="latexCode"
  >
    <template #left>
      <strong>Geometry 版面设置</strong>
      <p>设置页面尺寸、边距等版面参数</p>

      <el-checkbox 
        :model-value="geometryConfig.enabled" 
        @update:model-value="(val: boolean | string | number) => updateConfig('enabled', !!val)"
        label="启用 Geometry 版面设置宏包"
      />

      <div v-if="geometryConfig.enabled" style="margin-top: 20px;">
        <el-divider />

        <div 
          v-for="item in formItems"
          :key="item.key"
          style="margin-bottom: 15px;"
        >
          <el-form-item :label="item.label">
            <div style="display: flex; gap: 10px; align-items: center;">
              <el-input 
                :model-value="geometryConfig[item.key]" 
                @input="(val: string) => updateConfig(item.key, val)"
                size="small"
                style="flex: 1;"
              />
              <el-button @click="() => handleOptionAction(item.key, 'reset')" size="small">重置</el-button>
              <el-button @click="() => handleOptionAction(item.key, 'clear')" size="small">清空</el-button>
            </div>
          </el-form-item>
        </div>

        <div style="margin-top: 20px; text-align: right;">
          <el-button @click="resetAll" type="warning">重置所有设置</el-button>
        </div>
      </div>
    </template>
  </PackageDialogLayout>
</template>
