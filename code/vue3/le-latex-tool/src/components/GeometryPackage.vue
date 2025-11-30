<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElFormItem, ElInput, ElDivider } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
    papersize: string
    textwidth: string
    textlines: string
    leftmargin: string
    rightmargin: string
    topmargin: string
    bottommargin: string
    headheight: string
    headsep: string
    footskip: string
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// geometry 配置数据
const geometryConfig = ref({
  enabled: props.modelValue.enabled !== undefined ? props.modelValue.enabled : true,
  paperWidth: '185mm',
  paperHeight: '260mm',
  textWidth: '148mm',
  textHeight: '220mm',
  leftMargin: '21mm',
  topMargin: '25.5mm'
} as any)

// LaTeX 代码模板
const latexTemplates = {
  geometryPackage: (options: { 
    paperWidth: string
    paperHeight: string
    textWidth: string
    textHeight: string
    leftMargin: string
    topMargin: string
  }) => {
    return `%\\usepackage{geometry}
%\\geometry{
%  a4paper,
%  total={${options.paperWidth},${options.paperHeight}},
%  text={${options.textWidth},${options.textHeight}},
%  left=${options.leftMargin},
%  top=${options.topMargin}
%}`
  }
}

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  if (!geometryConfig.value.enabled) {
    return ''
  }

  return latexTemplates.geometryPackage({
    paperWidth: geometryConfig.value.paperWidth,
    paperHeight: geometryConfig.value.paperHeight,
    textWidth: geometryConfig.value.textWidth,
    textHeight: geometryConfig.value.textHeight,
    leftMargin: geometryConfig.value.leftMargin,
    topMargin: geometryConfig.value.topMargin
  })
})

// 更新配置
const updateConfig = (field: string, value: any) => {
  (geometryConfig.value as any)[field] = value
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
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="纸张宽度 (paperwidth)">
                <el-input 
                  v-model="geometryConfig.paperWidth" 
                  @input="(val) => updateConfig('paperWidth', val)"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="纸张高度 (paperheight)">
                <el-input 
                  v-model="geometryConfig.paperHeight" 
                  @input="(val) => updateConfig('paperHeight', val)"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="正文宽度 (text width)">
                <el-input 
                  v-model="geometryConfig.textWidth" 
                  @input="(val) => updateConfig('textWidth', val)"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="正文高度 (text height)">
                <el-input 
                  v-model="geometryConfig.textHeight" 
                  @input="(val) => updateConfig('textHeight', val)"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="左边距 (left)">
                <el-input 
                  v-model="geometryConfig.leftMargin" 
                  @input="(val) => updateConfig('leftMargin', val)"
                  size="small"
                />
              </el-form-item>
            </div>
            
            <div style="margin-bottom: 15px;">
              <el-form-item label="上边距 (top)">
                <el-input 
                  v-model="geometryConfig.topMargin" 
                  @input="(val) => updateConfig('topMargin', val)"
                  size="small"
                />
              </el-form-item>
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