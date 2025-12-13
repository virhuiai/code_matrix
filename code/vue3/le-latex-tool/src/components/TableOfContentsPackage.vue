<script setup lang="ts">
import { ref, computed, defineEmits, defineProps } from 'vue'
import { ElCard, ElCheckbox, ElDialog, ElButton, ElDivider } from 'element-plus'
import { setupCodeEmission } from '../utils/code-emitter'

const props = defineProps<{
  modelValue: {
    titletocEnabled?: boolean
    multitocEnabled?: boolean
  }
  componentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: { titletocEnabled?: boolean, multitocEnabled?: boolean }): void
  (e: 'codeChange', value: string): void
}>()

// 控制弹窗显示
const dialogVisible = ref(false)

// LaTeX 代码模板
const titletocTemplate = `\\usepackage{titletoc}
\\titlecontents{chapter}[4em]{\\addvspace{2.3mm}\\bfseries}{%
                       \\contentslabel{4.0em}}{}{\\titlerule*[5pt]{$\\cdot$}\\contentspage}
\\titlecontents{section}[4em]{}{\\contentslabel{2.5em}}{}{%
                       \\titlerule*[5pt]{$\\cdot$}\\contentspage}
\\titlecontents{subsection}[7.2em]{}{\\contentslabel{3.3em}}{}{%
                          \\titlerule*[5pt]{$\\cdot$}\\contentspage}`

const multitocTemplate = '\\usepackage[toc]{multitoc}'

// 计算属性：控制启用状态
const titletocEnabled = computed({
  get: () => props.modelValue.titletocEnabled ?? true,
  set: (value) => emit('update:modelValue', { 
    titletocEnabled: value, 
    multitocEnabled: props.modelValue.multitocEnabled ?? false 
  })
})

const multitocEnabled = computed({
  get: () => props.modelValue.multitocEnabled ?? false,
  set: (value) => emit('update:modelValue', { 
    titletocEnabled: props.modelValue.titletocEnabled ?? true, 
    multitocEnabled: value 
  })
})

// 计算属性：生成 LaTeX 代码
const computedLatexCode = computed(() => {
  const codes = []
  if (titletocEnabled.value) {
    codes.push(titletocTemplate)
  }
  if (multitocEnabled.value) {
    codes.push(multitocTemplate)
  }
  return codes.join('\n')
})

if (props.modelValue.titletocEnabled === undefined || props.modelValue.multitocEnabled === undefined) {
  emit('update:modelValue', { 
    titletocEnabled: true, 
    multitocEnabled: false 
  })
}

setupCodeEmission(computedLatexCode, emit, props.componentId, 'TableOfContentsPackage')

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
  <div class="package-options-dialog">
    <!-- 触发弹窗的按钮 -->
    <el-button type="primary" size="small" round @click="openDialog">目录格式设置</el-button>
    
    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="目录格式设置"
      :before-close="closeDialog"
    >
      <el-card shadow="hover">
        <div>
          <div class="package-options-container">
            <!-- 左栏：选项 -->
            <div class="package-options-left">
              <strong>目录格式设置</strong>
              <p>设置章节目录的格式，包括标题样式和页码指引线</p>

              <div style="margin-top: 20px;">
                <el-checkbox 
                  :model-value="titletocEnabled" 
                  @update:model-value="(val: boolean | string | number) => titletocEnabled = Boolean(val)"
                  label="启用 titletoc 宏包（用于自定义目录样式）" 
                />
                <div v-if="titletocEnabled" style="margin-top: 10px; margin-left: 20px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ titletocTemplate }}</pre>
                </div>
              </div>

              <el-divider />

              <div style="margin-top: 20px;">
                <el-checkbox 
                  :model-value="multitocEnabled" 
                  @update:model-value="(val: boolean | string | number) => multitocEnabled = Boolean(val)"
                  label="启用 multitoc 宏包（用于多栏目录）" 
                />
                <div v-if="multitocEnabled" style="margin-top: 10px; margin-left: 20px;">
                  <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; font-family: monospace; font-size: 12px;">{{ multitocTemplate }}</pre>
                </div>
              </div>
            </div>

            <!-- 右栏：代码预览 -->
            <div class="package-options-right">
              <div class="code-preview">
                <pre class="code-preview-content">{{ computedLatexCode }}</pre>
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
