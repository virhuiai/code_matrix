<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton } from 'element-plus'

const props = defineProps<{
  modelValue: {
    enabled: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { enabled: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// 包配置数据
const packageConfig = {
  // 默认启用目录格式设置
  defaultEnabled: true,
  
  // LaTeX 代码模板
  latexTemplate: `% 目录格式设置
\\usepackage{titletoc}
\\usepackage[toc]{multitoc}
\\titlecontents{chapter}[4em]{\\addvspace{2.3mm}\\bfseries}{%
                       \\contentslabel{4.0em}}{}{\\titlerule*[5pt]{$\\cdot$}\\contentspage}
\\titlecontents{section}[4em]{}{\\contentslabel{2.5em}}{}{%
                       \\titlerule*[5pt]{$\\cdot$}\\contentspage}
\\titlecontents{subsection}[7.2em]{}{\\contentslabel{3.3em}}{}{%
                          \\titlerule*[5pt]{$\\cdot$}\\contentspage}`
}

// 计算属性：控制启用状态
const isEnabled = computed({
  get: () => props.modelValue.enabled,
  set: (value) => emit('update:modelValue', { enabled: value })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  return isEnabled.value ? packageConfig.latexTemplate : ''
})

// 监听代码变化
watch(computedLatexCode, (newCode) => {
  emit('codeChange', newCode)
})

// 组件挂载时触发代码变更事件
onMounted(() => {
  emit('codeChange', computedLatexCode.value)
  if (props.componentId !== undefined) {
    console.log(`TableOfContentsPackage component loaded successfully with ID: ${props.componentId}`)
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
    <el-button type="primary" @click="openDialog" style="width: 100%; margin-top: 10px;">目录格式设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="目录格式设置"
      width="60%"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <strong>目录格式设置</strong>
          <p>设置章节目录的格式，包括标题样式和页码指引线</p>
          
          <el-checkbox v-model="isEnabled" label="启用目录格式设置" />
          
          <div v-if="isEnabled" style="margin-top: 20px;">
            <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: monospace;">{{ packageConfig.latexTemplate }}</pre>
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