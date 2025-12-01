<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElFormItem, ElInput, ElDivider } from 'element-plus'

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

// 控制弹窗显示
const dialogVisible = ref(false)

// 默认配置值
const defaultValues = {
  paperWidth: '185mm',
  paperHeight: '260mm',
  textWidth: '148mm',
  textHeight: '220mm',
  leftMargin: '21mm',
  topMargin: '25.5mm'
}

// geometry 配置数据
const geometryConfig = ref({
  enabled: props.modelValue.enabled ?? false,
  ...defaultValues,
  ...(props.modelValue || {})
})

// LaTeX 代码模板
const generateLatexCode = (options: typeof geometryConfig.value) => {
  if (!options.enabled) return ''

  const optionsArray = []
  
  if (options.paperWidth) optionsArray.push(`paperwidth=${options.paperWidth}`)
  if (options.paperHeight) optionsArray.push(`paperheight=${options.paperHeight}`)
  
  if (options.textWidth && options.textHeight) {
    optionsArray.push(`text={${options.textWidth},${options.textHeight}}`)
  } else {
    if (options.textWidth) optionsArray.push(`textwidth=${options.textWidth}`)
    if (options.textHeight) optionsArray.push(`textheight=${options.textHeight}`)
  }
  
  if (options.leftMargin) optionsArray.push(`left=${options.leftMargin}`)
  if (options.topMargin) optionsArray.push(`top=${options.topMargin}`)
  
  return `\\usepackage[${optionsArray.join(', ')}]{geometry}`
}

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => generateLatexCode(geometryConfig.value))

// 更新配置
const updateConfig = (field: string, value: any) => {
  geometryConfig.value = { ...geometryConfig.value, [field]: value }
  emit('update:modelValue', { ...geometryConfig.value })
}

// 重置单个选项
const resetOption = (field: string) => {
  updateConfig(field, defaultValues[field as keyof typeof defaultValues] || '')
}

// 清空单个选项
const clearOption = (field: string) => {
  updateConfig(field, '')
}

// 重置所有配置
const resetAll = () => {
  geometryConfig.value = {
    enabled: geometryConfig.value.enabled,
    ...defaultValues
  }
  emit('update:modelValue', { ...geometryConfig.value })
}

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`GeometryPackage component loaded successfully with ID: ${props.componentId}`)
  }
})

// 打开弹窗
const openDialog = () => {
  dialogVisible.value = true
}

// 关闭弹窗
const closeDialog = () => {
  dialogVisible.value = false
}

// 表单项配置
const formItems = [
  { key: 'paperWidth', label: '纸张宽度 (paperwidth)' },
  { key: 'paperHeight', label: '纸张高度 (paperheight)' },
  { key: 'textWidth', label: '正文宽度 (text width)' },
  { key: 'textHeight', label: '正文高度 (text height)' },
  { key: 'leftMargin', label: '左边距 (left)' },
  { key: 'topMargin', label: '上边距 (top)' }
]

defineExpose({
  openDialog,
  closeDialog
})
</script>

<template>
  <div>
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">Geometry 版面设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Geometry 版面设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>Geometry 版面设置</strong>
          <p>设置页面尺寸、边距等版面参数</p>
          
          <el-checkbox 
            v-model="geometryConfig.enabled" 
            @change="(val) => updateConfig('enabled', val)"
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
                    v-model="geometryConfig[item.key]" 
                    @input="(val) => updateConfig(item.key, val)"
                    size="small"
                    style="flex: 1;"
                  />
                  <el-button @click="resetOption(item.key)" size="small">重置</el-button>
                  <el-button @click="clearOption(item.key)" size="small">清空</el-button>
                </div>
              </el-form-item>
            </div>
            
            <div style="margin-top: 20px; text-align: right;">
              <el-button @click="resetAll" type="warning">重置所有设置</el-button>
            </div>
            
            <div style="margin-top: 20px;">
              <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ computedLatexCode }}</pre>
            </div>
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